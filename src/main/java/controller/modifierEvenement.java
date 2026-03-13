package controller;

import entite.Evenement;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import service.EvenementService;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class modifierEvenement {
    @FXML
    private Label appName;
    @FXML
    private Button btnWorkouts;
    @FXML
    private Button btnProduits;
    @FXML
    private Button selectImageButton;
    @FXML
    private HBox headerHBox;
    @FXML
    private ImageView logo_menu;
    @FXML
    private ImageView imageView;
    @FXML
    private Button btnForum;
    @FXML
    private Button btnEvenements;
    @FXML
    private VBox menuVBox;
    @FXML
    private Rectangle cadre;
    @FXML
    private ImageView imageView1;
    @FXML
    private TextField tf_nbrplace;
    @FXML
    private TextField tf_prix;
    @FXML
    private TextField tf_titre;
    @FXML
    private TextField tf_desc;
    @FXML
    private TextField tf_lieu;
    private Evenement evenement;
    private afficherEventBack parentController;
    private EvenementService evenementService;
    private String imagePath;
    @FXML
    private ComboBox<String> cb_statut;
    @FXML
    private ComboBox<String> cb_type;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Button btnModifier;
    @FXML
    private Button btnAfficherListe;
    @FXML
    private Button btnRetourner;

    public modifierEvenement() {
        this.evenementService = new EvenementService();
    }

    @FXML
    public void initialize() {
        // Initialiser les ComboBox avec des valeurs prédéfinies
        cb_statut.setItems(FXCollections.observableArrayList("Actif", "Inactif", "Annulé"));
        cb_type.setItems(FXCollections.observableArrayList("Sport", "Culture", "Loisir", "Autre"));
    }

    // Initialiser les champs avec les valeurs de l'événement
    public void initialiserChamps(Evenement evenement, afficherEventBack parentController) {
        this.evenement = evenement;
        this.parentController = parentController;

        // Pré-remplir les champs
        tf_titre.setText(evenement.getTitre());
        tf_desc.setText(evenement.getDescription());
        try {
            datePicker.setValue(LocalDate.parse(evenement.getDate()));
        } catch (Exception e) {
            datePicker.setValue(null);
        }
        tf_lieu.setText(evenement.getLieu());
        cb_statut.setValue(evenement.getStatut());
        cb_type.setValue(evenement.getType());
        tf_nbrplace.setText(String.valueOf(evenement.getNbrplace()));
        tf_prix.setText(String.valueOf(evenement.getPrix()));

        // Charger l'image si disponible
        if (evenement.getImage() != null && !evenement.getImage().isEmpty()) {
            try {
                imageView.setImage(new Image(new File(evenement.getImage()).toURI().toString()));
                imageView1.setImage(new Image(new File(evenement.getImage()).toURI().toString()));
                imagePath = evenement.getImage();
            } catch (Exception e) {
                imageView.setImage(null);
                imageView1.setImage(null);
            }
        }
    }

    // Gérer la sélection d'image
    @FXML
    private void selectImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(selectImageButton.getScene().getWindow());
        if (file != null) {
            imagePath = file.getAbsolutePath();
            Image image = new Image(file.toURI().toString());
            imageView.setImage(image);
            imageView1.setImage(image);
        }
    }

    // Gérer le clic sur "Modifier Evènement"
    @FXML
    private void sauvegarderModifications() {
        System.out.println("Début de sauvegarderModifications");
        try {
            // Récupérer et valider les champs
            String titre = tf_titre.getText();
            if (titre == null || titre.trim().isEmpty() || titre.length() > 50) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Le titre ne doit pas être vide et ne doit pas dépasser 50 caractères.");
                return;
            }

            String description = tf_desc.getText();
            if (description == null || description.trim().isEmpty() || description.length() > 200) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "La description ne doit pas être vide et ne doit pas dépasser 200 caractères.");
                return;
            }

            LocalDate localDate = datePicker.getValue();
            if (localDate == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez sélectionner une date !");
                return;
            }
            // Vérifier que la date est supérieure à la date actuelle
            LocalDate dateActuelle = LocalDate.now();
            if (!localDate.isAfter(dateActuelle)) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "La date de l'événement doit être postérieure à la date actuelle (" + dateActuelle + ").");
                return;
            }
            String date = localDate.toString();

            String lieu = tf_lieu.getText();
            if (lieu == null || lieu.trim().isEmpty() || lieu.length() > 50) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Le lieu ne doit pas être vide et ne doit pas dépasser 50 caractères.");
                return;
            }

            String statut = cb_statut.getValue();
            if (statut == null || statut.trim().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez sélectionner un statut !");
                return;
            }

            String type = cb_type.getValue();
            if (type == null || type.trim().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez sélectionner un type !");
                return;
            }

            int nbrplace;
            int prix;
            try {
                nbrplace = Integer.parseInt(tf_nbrplace.getText().trim());
                if (nbrplace <= 0) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Le nombre de places doit être supérieur à 0.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Le nombre de places doit être un entier valide.");
                return;
            }

            try {
                prix = Integer.parseInt(tf_prix.getText().trim());
                if (prix <= 0) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Le prix doit être strictement positif.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Le prix doit être un entier valide.");
                return;
            }

            if (imageView.getImage() == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez sélectionner une image !");
                return;
            } else {
                imagePath = imageView.getImage().getUrl();
            }

            // Mettre à jour l'objet Evenement
            System.out.println("Mise à jour des données de l'événement...");
            evenement.setTitre(titre);
            evenement.setDescription(description);
            evenement.setDate(date);
            evenement.setLieu(lieu);
            evenement.setStatut(statut);
            evenement.setType(type);
            evenement.setNbrplace(nbrplace);
            evenement.setPrix(prix);
            if (imagePath != null) {
                evenement.setImage(imagePath);
            }

            // Sauvegarder via le service
            System.out.println("Enregistrement des modifications dans la base de données...");
            evenementService.modifier(evenement);
            System.out.println("Événement ID " + evenement.getId() + " modifié avec succès.");

            // Afficher un message de succès
            System.out.println("Affichage du message de succès...");
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Événement modifié avec succès !");

        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Erreur lors de la sauvegarde des modifications : " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Erreur inattendue : " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur inattendue est survenue : " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("Fin de sauvegarderModifications");
    }

    // Gérer le clic sur "Retourner"
    @FXML
    private void retourner() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/afficherEventBack.fxml"));
            if (loader.getLocation() == null) {
                System.out.println("Erreur : Impossible de trouver afficherEventBack.fxml");
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du retour à la liste des événements.");
                return;
            }
            Parent root = loader.load();

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

    // Gérer le clic sur "Afficher la liste des évènements"
    @FXML
    private void afficherListe() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/afficherEventBack.fxml"));
            if (loader.getLocation() == null) {
                System.out.println("Erreur : Impossible de trouver afficherEventBack.fxml");
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du retour à la liste des événements.");
                return;
            }
            Parent root = loader.load();

            Stage stage = (Stage) btnAfficherListe.getScene().getWindow();
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

    // Méthode utilitaire pour afficher des alertes
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.show(); // Utilisation de show() pour éviter les problèmes de thread
    }
}