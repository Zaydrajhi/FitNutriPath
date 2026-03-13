package controller;

import entite.produit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import service.ProduitService;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.TableCell;

import java.io.File;
import java.io.IOException;

public class ListeProduitController {

    @FXML private TableView<produit> tableViewProduits;
    @FXML private TableColumn<produit, Integer> colId;
    @FXML private TableColumn<produit, String> colNom;
    @FXML private TableColumn<produit, Double> colPrix;
    @FXML private TableColumn<produit, Integer> colStock;
    @FXML private TableColumn<produit, String> colDescription;
    @FXML private TableColumn<produit, String> colImage;
    @FXML private TableColumn<produit, Void> colActions;

    private ProduitService produitService = new ProduitService();
    private ObservableList<produit> produitsList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadProduits();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prix"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colImage.setCellFactory(column -> new TableCell<produit, String>() {
            private final ImageView imageView = new ImageView();
            {
                imageView.setFitHeight(50);
                imageView.setFitWidth(50);
                imageView.setPreserveRatio(true);
            }

            @Override
            protected void updateItem(String imagePath, boolean empty) {
                super.updateItem(imagePath, empty);
                if (empty || imagePath == null || imagePath.isEmpty()) {
                    setGraphic(null);
                } else {
                    try {
                        // Essayer d'abord avec getResourceAsStream (chemin relatif dans le classpath)
                        try {
                            Image image = new Image(getClass().getResourceAsStream(imagePath));
                            imageView.setImage(image);
                            setGraphic(imageView);
                            return;
                        } catch (Exception e) {
                            System.out.println("Première tentative de chargement d'image échouée: " + e.getMessage());
                        }
                        
                        // Essayer ensuite avec un chemin absolu
                        try {
                            String absolutePath = "src/main/resources" + imagePath;
                            File imageFile = new File(absolutePath);
                            if (imageFile.exists()) {
                                Image image = new Image(imageFile.toURI().toString());
                                imageView.setImage(image);
                                setGraphic(imageView);
                                return;
                            } else {
                                System.out.println("Fichier d'image non trouvé: " + absolutePath);
                            }
                        } catch (Exception e) {
                            System.out.println("Deuxième tentative de chargement d'image échouée: " + e.getMessage());
                        }
                        
                        // Si toutes les tentatives échouent, afficher un message d'erreur
                        setGraphic(null);
                        System.err.println("Impossible de charger l'image: " + imagePath);
                    } catch (Exception e) {
                        setGraphic(null);
                        System.err.println("Erreur de chargement de l'image: " + imagePath + " - " + e.getMessage());
                    }
                }
            }
        });

        setupActionButtons();
    }

    private void setupActionButtons() {
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("✏️ Modifier");
            private final Button deleteButton = new Button("🗑️ Supprimer");

            {
                editButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
                deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");

                editButton.setOnAction(event -> {
                    produit p = getTableView().getItems().get(getIndex());
                    System.out.println("Modification du produit: " + p.getNom() + " (ID: " + p.getId() + ")");
                    editProduit(p);
                });

                deleteButton.setOnAction(event -> {
                    produit p = getTableView().getItems().get(getIndex());
                    System.out.println("Suppression du produit: " + p.getNom() + " (ID: " + p.getId() + ")");
                    deleteProduit(p);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, editButton, deleteButton);
                    setGraphic(buttons);
                }
            }
        });
    }

    private void loadProduits() {
        produitsList.clear();
        produitsList.addAll(produitService.readAll());
        tableViewProduits.setItems(produitsList);
        
        // Forcer un rafraîchissement de la TableView
        tableViewProduits.refresh();
        
        // Forcer un rafraîchissement de toutes les cellules
        for (TableColumn<produit, ?> column : tableViewProduits.getColumns()) {
            column.setVisible(false);
            column.setVisible(true);
        }
    }

    private void editProduit(produit p) {
        try {
            // Charger l'interface de modification
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifierProduit.fxml"));
            Parent root = loader.load();

            // Passer les données du produit à modifier
            ModifierProduitController controller = loader.getController();
            if (controller != null) {
                controller.initData(p);

                // Afficher la fenêtre de modification
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.showAndWait(); // Attend la fermeture de la fenêtre

                // Rafraîchir la liste après modification
                loadProduits();
            } else {
                showAlert("Erreur", "Impossible de charger le contrôleur de modification");
            }
        } catch (IOException e) {
            e.printStackTrace(); // Afficher la stack trace complète dans la console
            showAlert("Erreur", "Erreur lors de l'ouverture de l'éditeur: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace(); // Afficher la stack trace complète dans la console
            showAlert("Erreur", "Erreur inattendue: " + e.getMessage());
        }
    }

    private void deleteProduit(produit p) {
        System.out.println("Tentative de suppression du produit: " + p.getNom() + " (ID: " + p.getId() + ")");
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer le produit");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer le produit '" + p.getNom() + "'?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    System.out.println("Suppression confirmée pour le produit: " + p.getNom() + " (ID: " + p.getId() + ")");
                    produitService.delete(p);
                    System.out.println("Produit supprimé avec succès: " + p.getNom() + " (ID: " + p.getId() + ")");
                    showAlert("Succès", "Le produit '" + p.getNom() + "' a été supprimé avec succès.");
                    loadProduits(); // Rafraîchir la liste
                } catch (Exception e) {
                    System.err.println("Erreur lors de la suppression du produit: " + e.getMessage());
                    e.printStackTrace();
                    showAlert("Erreur", "Erreur lors de la suppression du produit: " + e.getMessage());
                }
            } else {
                System.out.println("Suppression annulée pour le produit: " + p.getNom() + " (ID: " + p.getId() + ")");
            }
        });
    }

    @FXML
    private void handleRetour() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ajouterProduit.fxml"));
            Stage stage = (Stage) tableViewProduits.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}