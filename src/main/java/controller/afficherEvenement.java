package controller;

import entite.Evenement;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import service.EvenementService;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class afficherEvenement {
    @javafx.fxml.FXML
    private TextField tf_rechercher;
    @javafx.fxml.FXML
    private VBox detailsContainer;
    @javafx.fxml.FXML
    private Button btnWorkouts;
    @javafx.fxml.FXML
    private Button btnProduits;
    @javafx.fxml.FXML
    private HBox headerHBox;
    @javafx.fxml.FXML
    private ImageView logo_menu;
    @javafx.fxml.FXML
    private GridPane gridPaneEvents;
    @javafx.fxml.FXML
    private Button btnForum;
    @javafx.fxml.FXML
    private Button btnEvenements;
    @javafx.fxml.FXML
    private VBox menuVBox;
    @javafx.fxml.FXML
    private Label appName;

    private EvenementService evenementService;
    @FXML
    private Button chercher;
    @FXML
    private Button chercher2;
    @FXML
    private Button chercher1;
    @FXML
    private DatePicker tf_filtrer;

    public afficherEvenement() {
        // Initialisation du service
        this.evenementService = new EvenementService();
    }

    @FXML
    public void initialize() {
        try {
            afficherEvenements();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void afficherEvenements() throws SQLException {
        List<Evenement> evenements = evenementService.afficher();
        gridPaneEvents.getChildren().clear();

        gridPaneEvents.setHgap(20);
        gridPaneEvents.setVgap(20);
        gridPaneEvents.setPadding(new Insets(20));
        gridPaneEvents.setAlignment(Pos.TOP_CENTER);

        int row = 0;
        int column = 0;

        for (Evenement evenement : evenements) {
            VBox card = new VBox();
            card.setSpacing(8);
            card.setPadding(new Insets(15));
            card.setAlignment(Pos.TOP_CENTER); // Centré pour l'image et le titre
            card.setPrefWidth(300); // Largeur préférée augmentée pour un meilleur ajustement
            card.setMaxWidth(400);  // Définition d'une largeur maximale pour éviter l'étirement

            card.setStyle("""
                -fx-background-color: #ffffff;
                -fx-border-radius: 10;
                -fx-background-radius: 10;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0.3, 0, 4);
                -fx-border-color: #dddddd;
            """);

            // Création de l'ImageView pour l'image de l'événement
            ImageView eventImageView = new ImageView();
            eventImageView.setFitWidth(200); // Ajustez la largeur selon vos besoins
            eventImageView.setFitHeight(150); // Ajustez la hauteur selon vos besoins
            eventImageView.setPreserveRatio(true);

            // Chargement de l'image
            String imagePath = evenement.getImage();
            boolean imageLoaded = false;

            if (imagePath != null && !imagePath.isEmpty()) {
                try {
                    String imageURI = imagePath.replace("\\", "/"); // Assurez-vous que le chemin est valide
                    String cheminSansFile = imageURI.replace("file:", "");
                    File imageFile = new File(cheminSansFile);

                    if (imageFile.exists()) {
                        Image image = new Image(imageFile.toURI().toString());
                        eventImageView.setImage(image);
                        imageLoaded = true;
                    } else {
                        System.out.println("L'image n'existe pas à ce chemin : " + imageFile.getAbsolutePath());
                    }
                } catch (Exception e) {
                    System.out.println("Erreur de chargement de l'image : " + imagePath);
                    e.printStackTrace();
                }
            }

            // Si l'image principale n'a pas été chargée, essayer de charger une image par défaut
            if (!imageLoaded) {
                try {
                    // Vérifier si l'image par défaut existe dans les ressources
                    if (getClass().getResource("/images/default_event_image.png") != null) {
                        eventImageView.setImage(new Image(getClass().getResourceAsStream("/images/default_event_image.png")));
                    } else {
                        System.out.println("Image par défaut '/images/default_event_image.png' introuvable dans les ressources.");
                        // Optionnel : Laisser l'ImageView vide ou ajouter un placeholder visuel
                    }
                } catch (Exception e) {
                    System.out.println("Erreur de chargement de l'image par défaut : " + e.getMessage());
                    e.printStackTrace();
                }
            }

            // Centrer l'image dans la carte
            HBox imageContainer = new HBox(eventImageView);
            imageContainer.setAlignment(Pos.CENTER);

            // Titre centré
            Label titleLabel = new Label(evenement.getTitre());
            titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333333;");
            titleLabel.setMaxWidth(Double.MAX_VALUE);
            titleLabel.setAlignment(Pos.CENTER);

            // Description
            HBox descriptionBox = createLabelWithValue("📝", "Description :", evenement.getDescription());

            // Autres informations
            HBox dateBox = createLabelWithValue("📅", "Date :", evenement.getDate());
            HBox lieuBox = createLabelWithValue("📍", "Lieu :", evenement.getLieu());
            HBox statutBox = createLabelWithValue("🔖", "Statut :", evenement.getStatut());
            HBox typeBox = createLabelWithValue("🏷️", "Type :", evenement.getType());
            HBox nbrPlaceBox = createLabelWithValue("👥", "Places :", String.valueOf(evenement.getNbrplace()));
            HBox prixBox = createLabelWithValue("💶", "Prix :", evenement.getPrix() + " ");

            // Bouton Réserver
            Button btnReserver = new Button("Réserver");
            btnReserver.setStyle("-fx-background-color: #00c6a9; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
            // Dans la boucle for de afficherEvenements
            btnReserver.setOnAction(event -> {
                try {
                    // Débogage : Afficher l'ID de l'événement
                    System.out.println("ID de l'événement sélectionné : " + evenement.getId());
                    if (evenement.getId() == 0) {
                        System.out.println("Erreur : L'ID de l'événement est 0. Vérifiez EvenementService.");
                        return;
                    }

                    // Charger l'interface ajouterInscriptionFront.fxml
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterInscriptionFront.fxml"));
                    if (loader.getLocation() == null) {
                        System.out.println("Erreur : Impossible de trouver ajouterInscriptionFront.fxml");
                        return;
                    }
                    Parent root = loader.load();

                    // Récupérer le contrôleur de l'interface d'inscription
                    ajouterInscription controller = loader.getController();
                    if (controller == null) {
                        System.out.println("Erreur : Contrôleur ajouterInscription non chargé.");
                        return;
                    }

                    // Passer l'ID de l'événement au contrôleur
                    controller.setEvenementId(evenement.getId());

                    // Récupérer la scène actuelle et la remplacer
                    Stage stage = (Stage) btnReserver.getScene().getWindow();
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.setTitle("Réserver un événement");
                    stage.setMaximized(true);
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            // Conteneur pour le bouton Réserver (centré)
            HBox buttonBox = new HBox(btnReserver);
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.setPadding(new Insets(10, 0, 0, 0));

            // Ajouter l'image, les informations et le bouton à la carte
            card.getChildren().addAll(
                    imageContainer, // Ajouter l'image en premier
                    titleLabel,
                    descriptionBox,
                    dateBox, lieuBox, statutBox,
                    typeBox, nbrPlaceBox, prixBox,
                    buttonBox // Ajouter le bouton Réserver en dernier
            );

            gridPaneEvents.add(card, column, row);

            column++;
            if (column > 2) { // Conserver cette logique pour correspondre à la disposition en trois colonnes
                column = 0;
                row++;
            }
        }
    }

    // Méthode utilitaire pour créer une ligne avec libellé en gras et valeur normale
    private HBox createLabelWithValue(String emoji, String labelText, String value) {
        Label emojiLabel = new Label(emoji);
        emojiLabel.setStyle("-fx-font-size: 13px;");

        Label label = new Label(labelText);
        label.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #444444;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666666;");

        HBox box = new HBox(5, emojiLabel, label, valueLabel);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    public void interfaceUser(ActionEvent actionEvent) {
        try {
            // Charger le fichier FXML de l'interface produit
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/acceuil.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène
            Scene scene = new Scene(root);

            // Obtenir la fenêtre actuelle (stage)
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();

            // Changer la scène de la fenêtre
            stage.setScene(scene);
            stage.setTitle("Gestion des Produits Sportifs");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            // Gérer les erreurs de chargement ici
        }
    }

    public void interfaceProduits(ActionEvent actionEvent) {
        try {
            // Charger le fichier FXML de l'interface produit
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/listeProduitFront.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène
            Scene scene = new Scene(root);

            // Obtenir la fenêtre actuelle (stage)
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();

            // Changer la scène de la fenêtre
            stage.setScene(scene);
            stage.setTitle("Gestion des Produits Sportifs");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            // Gérer les erreurs de chargement ici
        }
    }

    public void interfacePlanSportif(ActionEvent actionEvent) {
        try {
            // Charger le fichier FXML de l'interface produit
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterAbonnement.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène
            Scene scene = new Scene(root);

            // Obtenir la fenêtre actuelle (stage)
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();

            // Changer la scène de la fenêtre
            stage.setScene(scene);
            stage.setTitle("Gestion des Produits Sportifs");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            // Gérer les erreurs de chargement ici
        }
    }

    public void prendreRendezVous(ActionEvent event) {
        naviguerVersVue("/AjouterRendezVous.fxml", "Prendre Rendez-vous", event);
    }

    public void afficherMesRendezVous(ActionEvent event) {
        naviguerVersVue("/RendezVouslist.fxml", "Mes Rendez-vous", event);
    }

    public void predictForFree(ActionEvent event) {
        naviguerVersVue("/predict.fxml", "Predict for Free", event);
    }
    private void naviguerVersVue(String fxmlPath, String title, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Get the stage from the event source
            Stage stage = (Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();

            // Set fixed size if needed
            stage.setScene(new Scene(root, 800, 800));
            stage.setTitle(title);
            stage.centerOnScreen();

        } catch (IOException e) {
            showAlert("Erreur de navigation", "Impossible de charger " + title + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}