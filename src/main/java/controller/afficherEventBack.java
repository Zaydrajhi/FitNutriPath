package controller;

import entite.Evenement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Callback;
import service.EvenementService;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class afficherEventBack {
    @javafx.fxml.FXML
    private Label appName;
    @javafx.fxml.FXML
    private Button btnEvenements;
    @javafx.fxml.FXML
    private VBox menuVBox;
    @javafx.fxml.FXML
    private Button btnWorkouts;
    @javafx.fxml.FXML
    private Button btnProduits;
    @javafx.fxml.FXML
    private HBox headerHBox;
    @javafx.fxml.FXML
    private ImageView logo_menu;
    @javafx.fxml.FXML
    private Rectangle cadre;
    @javafx.fxml.FXML
    private Button btnForum;
    @javafx.fxml.FXML
    private TextField tf_nom;
    @javafx.fxml.FXML
    private Button ajoutEx;
    @javafx.fxml.FXML
    private Button btn_trier;
    @javafx.fxml.FXML
    private TextField tf_muscle;
    private EvenementService evenementService;
    @javafx.fxml.FXML
    private VBox detailsContainer;
    @javafx.fxml.FXML
    private GridPane gridPaneEvents;




    @javafx.fxml.FXML
    public void trier(ActionEvent actionEvent) {
    }

    @FXML
    public void initialize() {
        try {
            evenementService = new EvenementService();
            afficherEvenements();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }





    /*public void afficherEvenements() throws SQLException {
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
            card.setAlignment(Pos.TOP_LEFT);
            card.setPrefWidth(250);

            card.setStyle("""
            -fx-background-color: #ffffff;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0.3, 0, 4);
            -fx-border-color: #dddddd;
        """);

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

            card.getChildren().addAll(
                    titleLabel,
                    descriptionBox,
                    dateBox, lieuBox, statutBox,
                    typeBox, nbrPlaceBox, prixBox
            );
            // Boutons Modifier et Supprimer
            HBox buttonBox = new HBox(10);
            buttonBox.setAlignment(Pos.CENTER_RIGHT);
            buttonBox.setPadding(new Insets(10, 0, 0, 0));

            Button btnModifier = new Button("Modifier");
            btnModifier.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
            btnModifier.setOnAction(event -> {
                modifierEvenement(evenement); // Line 139
            });


            Button btnSupprimer = new Button("Supprimer");
            btnSupprimer.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-weight: bold;");
            btnSupprimer.setOnAction(event -> {
                try {
                    evenementService.supprimer(evenement.getId());
                    afficherEvenements(); // recharge les événements après suppression
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            buttonBox.getChildren().addAll(btnModifier, btnSupprimer);
            card.getChildren().add(buttonBox);


            gridPaneEvents.add(card, column, row);

            column++;
            if (column > 2) {
                column = 0;
                row++;
            }
        }
    }*/

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
            card.setPrefWidth(250);

            card.setStyle("""
            -fx-background-color: #ffffff;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0.3, 0, 4);
            -fx-border-color: #dddddd;
        """);

            // Image
            ImageView eventImageView = new ImageView();
            eventImageView.setFitWidth(200); // Ajustez la largeur selon vos besoins
            eventImageView.setFitHeight(150); // Ajustez la hauteur selon vos besoins
            eventImageView.setPreserveRatio(true);

            String imagePath = evenement.getImage();
            if(imagePath != null && !imagePath.isEmpty()) {
                try {
                    String imageURI = imagePath.replace("\\", "/"); // Assurez-vous que le chemin est valide
                    String cheminSansFile = imageURI.replace("file:", "");
                    File imageFile = new File(cheminSansFile);

                    if (imageFile.exists()) {
                        Image image = new Image(imageFile.toURI().toString());
                        eventImageView.setImage(image);
                    } else {
                        System.out.println("L'image n'existe pas à ce chemin : " + imageFile.getAbsolutePath());
                    }
                } catch (Exception e) {
                    System.out.println("Erreur de chargement de l'image : " + imagePath);
                    e.printStackTrace();
                }
            }
            // Titre centré
            Label titleLabel = new Label(evenement.getTitre());
            titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333333;");
            titleLabel.setMaxWidth(Double.MAX_VALUE);
            titleLabel.setAlignment(Pos.CENTER);

            // Description
            HBox descriptionBox = createLabelWithValue("📝", "Description :", evenement.getDescription());
            HBox dateBox = createLabelWithValue("📅", "Date :", evenement.getDate());
            HBox lieuBox = createLabelWithValue("📍", "Lieu :", evenement.getLieu());
            HBox statutBox = createLabelWithValue("🔖", "Statut :", evenement.getStatut());
            HBox typeBox = createLabelWithValue("🏷️", "Type :", evenement.getType());
            HBox nbrPlaceBox = createLabelWithValue("👥", "Places :", String.valueOf(evenement.getNbrplace()));
            HBox prixBox = createLabelWithValue("💶", "Prix :", evenement.getPrix() + " ");

            // Ajouter les éléments à la carte
            card.getChildren().addAll(
                    eventImageView, // Image en haut
                    titleLabel,
                    descriptionBox,
                    dateBox, lieuBox, statutBox,
                    typeBox, nbrPlaceBox, prixBox
            );

// Boutons Modifier et Supprimer
            HBox buttonBox = new HBox(10);
            buttonBox.setAlignment(Pos.CENTER_RIGHT);
            buttonBox.setPadding(new Insets(10, 0, 0, 0));

// Bouton Modifier
            Button btnModifier = new Button("✏️");
            btnModifier.setStyle(
                    "-fx-background-color: #4CAF50; " +
                            "-fx-text-fill: white; " +
                            "-fx-font-size: 16px; " +
                            "-fx-background-radius: 5; " +
                            "-fx-border-width: 0; " +  // Supprime le cadre noir
                            "-fx-padding: 0 5 0 5; " + // Ajustement horizontal seulement
                            "-fx-content-display: text-only; " + // Force l'affichage du texte seul
                            "-fx-cursor: hand;"
            );
            btnModifier.setOnAction(event -> modifierEvenement(evenement));

// Bouton Supprimer
            Button btnSupprimer = new Button("🗑️");
            btnSupprimer.setStyle(
                    "-fx-background-color: #F44336; " +
                            "-fx-text-fill: white; " +
                            "-fx-font-size: 16px; " +
                            "-fx-background-radius: 5; " +
                            "-fx-border-width: 0; " +  // Supprime le cadre noir
                            "-fx-padding: 0 5 0 5; " + // Ajustement horizontal seulement
                            "-fx-content-display: text-only; " + // Force l'affichage du texte seul
                            "-fx-cursor: hand;"
            );
            btnSupprimer.setOnAction(event -> {
                // Création de la boîte de dialogue de confirmation
                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                confirmation.setTitle("Confirmation de suppression");
                confirmation.setHeaderText(null);
                confirmation.setContentText("Voulez-vous vraiment supprimer cet événement ?");

                // Personnalisation des boutons
                ButtonType ouiButton = new ButtonType("Oui", ButtonBar.ButtonData.YES);
                ButtonType nonButton = new ButtonType("Non", ButtonBar.ButtonData.NO);
                confirmation.getButtonTypes().setAll(ouiButton, nonButton);

                // Affichage de la boîte de dialogue et attente de la réponse
                Optional<ButtonType> result = confirmation.showAndWait();

                if (result.isPresent() && result.get() == ouiButton) {
                    try {
                        evenementService.supprimer(evenement.getId());
                        afficherEvenements(); // Recharge la liste après suppression
                        // Optionnel: Afficher un message de succès
                        Alert success = new Alert(Alert.AlertType.INFORMATION);
                        success.setTitle("Succès");
                        success.setHeaderText(null);
                        success.setContentText("Événement supprimé avec succès !");
                        success.showAndWait();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        // Optionnel: Afficher un message d'erreur
                        Alert error = new Alert(Alert.AlertType.ERROR);
                        error.setTitle("Erreur");
                        error.setHeaderText(null);
                        error.setContentText("Une erreur est survenue lors de la suppression.");
                        error.showAndWait();
                    }
                }
            });

            buttonBox.getChildren().addAll(btnModifier, btnSupprimer);
            card.getChildren().add(buttonBox);

            gridPaneEvents.add(card, column, row);

            column++;
            if (column > 2) {
                column = 0;
                row++;
            }
        }
    }





    @Deprecated
    public void modifierEvenement(Evenement evenement) {
        try {
            // Charger le FXML de la vue de modification
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifierEvenement.fxml")); // Adjust path if needed
            if (loader.getLocation() == null) {
                throw new IOException("FXML file not found at /controller/modifierEvenement.fxml");
            }
            Parent root = loader.load();

            // Récupérer le contrôleur de la vue de modification
            modifierEvenement controller = loader.getController();

            // Passer l'événement sélectionné et une référence à ce contrôleur
            controller.initialiserChamps(evenement, this);

            // Récupérer la fenêtre actuelle (celle d'afficherEventBack)
            Stage currentStage = (Stage) gridPaneEvents.getScene().getWindow(); // Assumes gridPaneEvents is an @FXML field in afficherEventBack

            // Remplacer la scène actuelle par la nouvelle scène
            Scene newScene = new Scene(root);
            currentStage.setScene(newScene);
            currentStage.setTitle("Modifier l'événement");

            // Pas besoin de stage.show() car la fenêtre est déjà ouverte

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors du chargement de la fenêtre de modification : " + e.getMessage());
            alert.showAndWait();
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


    @FXML
    public void ajouterEvent(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterEvenement.fxml"));
            Parent root = loader.load();

            // Obtenir la scène actuelle et remplacer le contenu
            btn_trier.getScene().setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
