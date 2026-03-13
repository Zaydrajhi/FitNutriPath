package controller;

import entite.produit;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import service.ProduitService;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ListeProduitFrontController implements Initializable {

    @FXML
    private GridPane productsGrid;

    private ProduitService produitService = new ProduitService();
    private static final int MAX_COLUMNS = 3;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadProducts();
    }

    private void loadProducts() {
        List<produit> produits = produitService.readAll();
        int row = 0;
        int col = 0;

        for (produit p : produits) {
            // Création de la carte pour chaque produit
            VBox productCard = createProductCard(p);

            // Ajout au GridPane
            productsGrid.add(productCard, col, row);
            GridPane.setMargin(productCard, new Insets(10));

            // Gestion des colonnes/lignes
            col++;
            if (col >= MAX_COLUMNS) {
                col = 0;
                row++;
            }
        }
    }

    private VBox createProductCard(produit p) {
        // Conteneur principal
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 15;");
        card.setPrefWidth(250);
        card.setPrefHeight(350);

        // Image du produit
        ImageView imageView = new ImageView();
        try {
            Image image = new Image(getClass().getResourceAsStream(p.getImage()));
            imageView.setImage(image);
            imageView.setFitWidth(200);
            imageView.setFitHeight(150);
            imageView.setPreserveRatio(true);

            // Arrondir les coins de l'image
            Rectangle clip = new Rectangle(200, 150);
            clip.setArcWidth(20);
            clip.setArcHeight(20);
            imageView.setClip(clip);
        } catch (Exception e) {
            System.err.println("Erreur de chargement de l'image: " + p.getImage());
        }

        // Nom du produit
        Label nameLabel = new Label(p.getNom());
        nameLabel.setStyle("-fx-text-fill: black; -fx-font-weight: bold; -fx-font-size: 14;");

        // Prix du produit
        Label priceLabel = new Label(String.format("%.2f DT 💰", p.getPrix()));
        priceLabel.setStyle("-fx-text-fill: #666666; -fx-font-weight: bold; -fx-font-size: 14;");

        // Stock disponible
        Label stockLabel = new Label("Stock: " + p.getStock() + " 📦");
        if (p.getStock() == 0) {
            stockLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 14;");
        } else {
            stockLabel.setStyle("-fx-text-fill: #00c6a9; -fx-font-weight: bold; -fx-font-size: 14;");
        }

        // Bouton Voir Détails
        Button detailsButton = new Button("Voir Détails");
        if (p.getStock() == 0) {
            detailsButton.setDisable(true);
            detailsButton.setStyle("-fx-background-color: #9e9e9e; -fx-text-fill: white; -fx-font-weight: bold;");
        } else {
            detailsButton.setStyle("-fx-background-color: #00c6a9; -fx-text-fill: white; -fx-font-weight: bold;");
            detailsButton.setOnAction(e -> showProductDetails(p));
        }

        // Ajout des éléments à la carte
        card.getChildren().addAll(imageView, nameLabel, priceLabel, stockLabel, detailsButton);

        return card;
    }

    private void showProductDetails(produit p) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProductDetails.fxml"));
            Parent root = loader.load();

            ProductDetailsController controller = loader.getController();
            controller.initData(p); // Plus besoin de passer userId

            Stage stage = new Stage();
            stage.setTitle("Détails du produit");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir les détails");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void listeDeCommande() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeCommandeProduit.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) productsGrid.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des Commandes");
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de ListeCommandeProduit.fxml : " + e.getMessage());
            e.printStackTrace();
        }
    }
    public void interfaceEvenement(ActionEvent actionEvent) {
        try {
            // Charger le fichier FXML de l'interface produit
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/afficherEvenementFront.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène
            Scene scene = new Scene(root);

            // Obtenir la fenêtre actuelle (stage)
            Stage stage = (Stage) ((javafx.scene.control.Button) actionEvent.getSource()).getScene().getWindow();

            // Changer la scène de la fenêtre
            stage.setScene(scene);
            stage.setTitle("Gestion des Produits Sportifs");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            // Gérer les erreurs de chargement ici
        }
    }

    public void interfaceUser(ActionEvent actionEvent) {
        try {
            // Charger le fichier FXML de l'interface produit
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/acceuil.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène
            Scene scene = new Scene(root);

            // Obtenir la fenêtre actuelle (stage)
            Stage stage = (Stage) ((javafx.scene.control.Button) actionEvent.getSource()).getScene().getWindow();

            // Changer la scène de la fenêtre
            stage.setScene(scene);
            stage.setTitle("Gestion des Produits Sportifs");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            // Gérer les erreurs de chargement ici
        }
    }


    public void interfaceAjouterAbonnement(ActionEvent actionEvent) {
        try {
            // Charger le fichier FXML de l'interface produit
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterAbonnement.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène
            Scene scene = new Scene(root);

            // Obtenir la fenêtre actuelle (stage)
            Stage stage = (Stage) ((javafx.scene.control.Button) actionEvent.getSource()).getScene().getWindow();

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
}