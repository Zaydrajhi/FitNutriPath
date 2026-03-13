package controller;

import entite.Evenement;
import entite.Inscription;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import service.EvenementService;
import service.InscriptionService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class modifierInscription {
    @FXML
    private MenuButton btnForum;
    @FXML
    private TextField tf_commentaire;
    @FXML
    private TextField tf_placereserve;
    @FXML
    private ComboBox<String> tf_type;
    @FXML
    private DatePicker tf_date;
    @FXML
    private Button btn_ajouter;
    @FXML
    private Button afficher;
    @FXML
    private Button btnRetourner;

    private Inscription inscription;
    private InscriptionService inscriptionService;
    private EvenementService evenementService;
    private afficherInscription parentController;

    // Définir un formateur pour le format de date "yyyy-MM-dd HH:mm:ss"
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public modifierInscription() {
        this.inscriptionService = new InscriptionService();
        this.evenementService = new EvenementService();
    }

    @FXML
    public void initialize() {
        // Initialiser les options de la ComboBox pour le type
        tf_type.getItems().addAll("Vip", "Standard");
    }

    public void setInscription(Inscription inscription) {
        this.inscription = inscription;
        try {
            // Remplir les champs avec les données de l'inscription
            // Analyser la date au format "yyyy-MM-dd HH:mm:ss" et convertir en LocalDate
            LocalDateTime dateTime = LocalDateTime.parse(inscription.getDate(), DATE_TIME_FORMATTER);
            tf_date.setValue(dateTime.toLocalDate()); // Extraire uniquement la partie LocalDate
            tf_commentaire.setText(inscription.getCommentaire());
            tf_placereserve.setText(String.valueOf(inscription.getNbrPlaceReserve()));
            tf_type.setValue(inscription.getType());
        } catch (Exception e) {
            System.out.println("Erreur lors du remplissage des champs : " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des données de l'inscription. Vérifiez le format de la date.");
            e.printStackTrace();
        }
    }

    public void setParentController(afficherInscription parentController) {
        this.parentController = parentController;
    }

    @FXML
    public void ajouterInscription() {
        System.out.println("Début de ajouterInscription (modification)");
        try {
            // Récupérer et valider les champs
            LocalDate localDate = tf_date.getValue();
            if (localDate == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez sélectionner une date !");
                return;
            }
            // Vérifier que la date est postérieure à la date actuelle
            LocalDate dateActuelle = LocalDate.now();
            if (!localDate.isAfter(dateActuelle)) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "La date de l'inscription doit être postérieure à la date actuelle (" + dateActuelle + ").");
                return;
            }
            LocalDateTime dateTime = localDate.atStartOfDay(); // Ajouter une heure par défaut (00:00:00)
            String date = dateTime.format(DATE_TIME_FORMATTER);

            String commentaire = tf_commentaire.getText() != null ? tf_commentaire.getText().trim() : "";
            if (commentaire.isEmpty() || commentaire.length() > 200) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Le commentaire ne doit pas être vide et ne doit pas dépasser 200 caractères.");
                return;
            }

            String type = tf_type.getValue();
            if (type == null || type.trim().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez sélectionner un type d'inscription !");
                return;
            }

            int nbrPlaceReserve;
            try {
                String nbrPlaceReserveStr = tf_placereserve.getText() != null ? tf_placereserve.getText().trim() : "";
                if (nbrPlaceReserveStr.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Le nombre de places réservées ne peut pas être vide.");
                    return;
                }
                nbrPlaceReserve = Integer.parseInt(nbrPlaceReserveStr);
                if (nbrPlaceReserve <= 0) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Le nombre de places réservées doit être supérieur à 0.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Le nombre de places réservées doit être un nombre valide.");
                return;
            }

            // Vérifier si le nombre de places réservées est disponible
            int evenementId = inscription.getEvenementId();
            Evenement evenement = getEvenementById(evenementId);
            if (evenement == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Événement associé non trouvé.");
                return;
            }

            int placesDisponibles = evenement.getNbrplace();
            int placesDejaReservees = getNombrePlacesDejaReservees(evenementId);
            int placesDejaReserveesSansCetteInscription = placesDejaReservees - inscription.getNbrPlaceReserve();
            int placesRestantes = placesDisponibles - placesDejaReserveesSansCetteInscription;

            if (nbrPlaceReserve > placesRestantes) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Nombre de places insuffisant. Il reste " + placesRestantes + " places disponibles.");
                return;
            }

            // Mettre à jour les données de l'inscription
            System.out.println("Mise à jour des données de l'inscription...");
            inscription.setDate(date);
            inscription.setCommentaire(commentaire);
            inscription.setNbrPlaceReserve(nbrPlaceReserve);
            inscription.setType(type);

            // Enregistrer les modifications dans la base de données
            System.out.println("Enregistrement des modifications dans la base de données...");
            inscriptionService.modifier(inscription);
            System.out.println("Inscription ID " + inscription.getId() + " modifiée avec succès.");

            // Afficher un message de succès
            System.out.println("Affichage du message de succès...");
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Inscription modifiée avec succès !");

            // Revenir à la liste des inscriptions
            System.out.println("Retour à la liste des inscriptions...");
            afficherInscriptions();

        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors de la modification : " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Erreur inattendue : " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur inattendue est survenue : " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("Fin de ajouterInscription (modification)");
    }

    // Méthode pour récupérer un événement par son ID
    private Evenement getEvenementById(int id) throws SQLException {
        List<Evenement> evenements = evenementService.afficher();
        return evenements.stream()
                .filter(e -> e.getId() == id)
                .findFirst()
                .orElse(null);
    }

    // Méthode pour calculer le nombre de places déjà réservées pour un événement
    private int getNombrePlacesDejaReservees(int evenementId) throws SQLException {
        List<Inscription> inscriptions = inscriptionService.afficher();
        return inscriptions.stream()
                .filter(i -> i.getEvenementId() == evenementId)
                .mapToInt(Inscription::getNbrPlaceReserve)
                .sum();
    }

    // Méthode pour afficher une alerte
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show(); // Utilisation de show() pour éviter les problèmes de thread
    }

    @FXML
    public void afficherInscriptions() {
        try {
            // Charger l'interface afficherInscriptionFront.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/afficherInscriptionFront.fxml"));
            if (loader.getLocation() == null) {
                System.out.println("Erreur : Impossible de trouver afficherInscriptionFront.fxml");
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du retour à la liste des inscriptions.");
                return;
            }
            Parent root = loader.load();

            // Afficher la scène
            Stage stage = (Stage) tf_commentaire.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Liste des Inscriptions");
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            System.out.println("Erreur lors du retour à la liste des inscriptions : " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du retour à la liste des inscriptions.");
            e.printStackTrace();
        }
    }

    @FXML
    public void retourner() {
        try {
            // Charger l'interface afficherInscriptionFront.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/afficherInscriptionFront.fxml"));
            if (loader.getLocation() == null) {
                System.out.println("Erreur : Impossible de trouver afficherInscriptionFront.fxml");
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du retour à la liste des inscriptions.");
                return;
            }
            Parent root = loader.load();

            // Afficher la scène
            Stage stage = (Stage) btnRetourner.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Liste des Inscriptions");
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            System.out.println("Erreur lors du retour à la liste des inscriptions : " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du retour à la liste des inscriptions.");
            e.printStackTrace();
        }
    }
}