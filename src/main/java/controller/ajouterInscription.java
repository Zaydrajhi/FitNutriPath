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
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ajouterInscription {
    @FXML
    private Button afficher;
    @FXML
    private Button btn_ajouter;
    @FXML
    private TextField tf_placereserve;
    @FXML
    private TextField tf_commentaire;
    @FXML
    private DatePicker tf_date;
    @FXML
    private ComboBox<String> tf_type;
    @FXML
    private MenuButton btnForum;

    private InscriptionService inscriptionService;
    private EvenementService evenementService;
    private int evenementId;
    @FXML
    private Button btnRetourner;




    public ajouterInscription() {
        this.inscriptionService = new InscriptionService();
        this.evenementService = new EvenementService();
    }

    @FXML
    public void initialize() {
        // Remplir le ComboBox avec des options pour le type d'inscription
        tf_type.getItems().addAll("Standard", "VIP", "Premium");
    }

    // Méthode pour définir l'ID de l'événement sélectionné
    public void setEvenementId(int evenementId) {
        this.evenementId = evenementId;
    }

    @FXML
    public void ajouterInscription() {
        System.out.println("Début de ajouterInscription");
        try {
            // Vérifier si un événement a été sélectionné
            if (evenementId == 0) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun événement sélectionné. Veuillez sélectionner un événement avant de réserver.");
                return;
            }

            // Récupérer les données du formulaire
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
            String date = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

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

            // Récupérer l'événement depuis la base de données pour vérifier les places disponibles
            System.out.println("Récupération de l'événement ID " + evenementId + "...");
            Evenement evenement = getEvenementById(evenementId);
            if (evenement == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Événement non trouvé.");
                return;
            }

            // Vérifier si le nombre de places réservées est disponible
            int placesDisponibles = evenement.getNbrplace();
            int placesDejaReservees = getNombrePlacesDejaReservees(evenementId);
            int placesRestantes = placesDisponibles - placesDejaReservees;
            if (nbrPlaceReserve > placesRestantes) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Nombre de places insuffisant. Il reste " + placesRestantes + " places disponibles.");
                return;
            }

            // Créer une nouvelle inscription
            System.out.println("Création de l'inscription...");
            Inscription inscription = new Inscription(
                    evenementId,
                    date,
                    commentaire,
                    nbrPlaceReserve,
                    type
            );

            // Ajouter l'inscription via le service
            System.out.println("Ajout de l'inscription dans la base de données...");
            inscriptionService.ajouter(inscription);
            System.out.println("Inscription ajoutée avec succès.");

            // Afficher un message de succès
            System.out.println("Affichage du message de succès...");
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Inscription ajoutée avec succès !");

            // Réinitialiser le formulaire
            System.out.println("Réinitialisation du formulaire...");
            resetForm();

        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors de l'ajout de l'inscription : " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Erreur inattendue : " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur inattendue est survenue : " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("Fin de ajouterInscription");
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

    // Méthode pour réinitialiser le formulaire
    private void resetForm() {
        tf_date.setValue(null);
        tf_commentaire.clear();
        tf_placereserve.clear();
        tf_type.setValue(null);
    }

    @FXML
    public void afficherInscriptions() {
        System.out.println("Bouton 'Liste des réservations' cliqué.");
        try {
            // Charger l'interface afficherInscriptionFront.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/afficherInscriptionFront.fxml"));
            if (loader.getLocation() == null) {
                System.out.println("Erreur : Impossible de trouver afficherInscriptionFront.fxml au chemin /afficherInscriptionFront.fxml");
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement de la liste des inscriptions.");
                return;
            }
            System.out.println("Chargement de afficherInscriptionFront.fxml...");
            Parent root = loader.load();
            System.out.println("afficherInscriptionFront.fxml chargé avec succès.");

            // Récupérer le contrôleur pour vérification
            afficherInscription controller = loader.getController();
            if (controller == null) {
                System.out.println("Erreur : Contrôleur afficherInscription non chargé.");
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur : Contrôleur non chargé.");
                return;
            }

            // Récupérer la scène actuelle et la remplacer
            Stage stage = (Stage) afficher.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Liste des Inscriptions");
            stage.setMaximized(true);
            stage.show();
            System.out.println("Interface Liste des Inscriptions affichée.");
        } catch (IOException e) {
            System.out.println("Erreur lors du chargement de afficherInscriptionFront.fxml : " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement de la liste des inscriptions.");
            e.printStackTrace();
        }
    }

    @FXML
    public void retourner() {
        try {
            // Charger l'interface afficherEvenementFront.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/afficherEvenementFront.fxml"));
            if (loader.getLocation() == null) {
                System.out.println("Erreur : Impossible de trouver afficherEvenementFront.fxml");
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du retour à la liste des événements.");
                return;
            }
            Parent root = loader.load();

            // Afficher la scène
            Stage stage = (Stage) btnRetourner.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Liste des Événements");
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            System.out.println("Erreur lors du retour à la liste des événements : " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du retour à la liste des événements.");
            e.printStackTrace();
        }
    }
}