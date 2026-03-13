package controller;

import entite.categorie;
import service.CategorieService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.layout.HBox;


import java.io.IOException;

public class ListeCategoriesController {
    @FXML private TableView<categorie> tableViewCategories;
    @FXML private TableColumn<categorie, String> colNom;
    @FXML private TableColumn<categorie, String> colDescription;
    @FXML private TableColumn<categorie, String> colImage;
    @FXML private TableColumn<categorie, Void> colActions;

    private final CategorieService categorieService = new CategorieService();

    @FXML
    public void initialize() {
        // Configuration des colonnes
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colImage.setCellValueFactory(new PropertyValueFactory<>("image"));

        // Configuration de la colonne Image
        colImage.setCellFactory(column -> new TableCell<>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(String imagePath, boolean empty) {
                super.updateItem(imagePath, empty);
                if (empty || imagePath == null || imagePath.isEmpty()) {
                    setGraphic(null);
                } else {
                    try {
                        Image image = new Image(getClass().getResource(imagePath).toString());
                        imageView.setImage(image);
                        imageView.setFitHeight(50);
                        imageView.setFitWidth(50);
                        imageView.setPreserveRatio(true);
                        setGraphic(imageView);
                    } catch (Exception e) {
                        setText(imagePath);
                        setGraphic(null);
                    }
                }
            }
        });

        // Configuration de la colonne Actions avec boutons Modifier et Supprimer
        colActions.setCellFactory(new Callback<>() {
            @Override
            public TableCell<categorie, Void> call(final TableColumn<categorie, Void> param) {
                return new TableCell<>() {
                    private final HBox boutonsContainer = new HBox(5);
                    private final Button btnModifier = new Button("Modifier");
                    private final Button btnSupprimer = new Button("Supprimer");

                    {
                        // Style des boutons
                        btnModifier.setStyle("-fx-background-color: #00c6a9; -fx-text-fill: white; -fx-font-weight: bold;");
                        btnSupprimer.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white; -fx-font-weight: bold;");

                        // Actions des boutons
                        btnModifier.setOnAction(event -> {
                            categorie cat = getTableView().getItems().get(getIndex());
                            ouvrirFenetreModification(cat);
                        });

                        btnSupprimer.setOnAction(event -> {
                            categorie cat = getTableView().getItems().get(getIndex());
                            supprimerCategorie(cat);
                        });

                        boutonsContainer.getChildren().addAll(btnModifier, btnSupprimer);
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(boutonsContainer);
                        }
                    }
                };
            }
        });

        chargerCategories();
    }

    private void supprimerCategorie(categorie cat) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Attention !");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer la catégorie \"" + cat.getNom() + "\" ?\n\n" +
                "⚠️ ATTENTION : Tous les produits associés à cette catégorie seront également supprimés !");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    categorieService.delete(cat);
                    afficherAlerte("Succès", "Catégorie et produits associés supprimés avec succès");
                    chargerCategories(); // Rafraîchir la liste
                } catch (Exception e) {
                    afficherAlerte("Erreur", "Erreur lors de la suppression : " + e.getMessage());
                }
            }
        });
    }

    private void ouvrirFenetreModification(categorie cat) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifierCategorie.fxml"));
            Parent root = loader.load();

            modifierCategorieController controller = loader.getController();
            controller.initData(cat);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.showAndWait();

            chargerCategories();
        } catch (IOException e) {
            afficherAlerte("Erreur", "Erreur lors de l'ouverture de la fenêtre de modification");
        }
    }

    private void chargerCategories() {
        ObservableList<categorie> categories = FXCollections.observableArrayList(categorieService.readAll());
        tableViewCategories.setItems(categories);
    }

    @FXML
    private void handleRetour(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AjouterCategorie.fxml"));
            Stage stage = (Stage) tableViewCategories.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            afficherAlerte("Erreur", "Erreur lors du retour à l'interface précédente");
        }
    }

    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}