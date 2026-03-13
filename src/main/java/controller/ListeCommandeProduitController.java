package controller;

import entite.panier;
import entite.panier_produit;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import service.PanierService;
import util.ValidationUtilss;

import java.io.IOException;
import java.util.List;

public class ListeCommandeProduitController {

    @FXML
    private VBox paniersVBox;

    private final PanierService panierService = new PanierService();

    @FXML
    public void initialize() {
        loadPaniers();
        addReturnButton();
    }

    private void addReturnButton() {
        Button returnButton = new Button("Retour");
        returnButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14; -fx-padding: 10 20; -fx-background-radius: 5;");
        returnButton.setOnAction(event -> goToListeProduit());
        
        // Ajouter le bouton en haut de la VBox
        paniersVBox.getChildren().add(0, returnButton);
    }

    private void loadPaniers() {
        try {
            // Récupérer tous les paniers
            List<panier> paniers = panierService.getAllPaniers();
            paniersVBox.getChildren().clear();

            if (paniers.isEmpty()) {
                Label emptyLabel = new Label("Aucun panier trouvé.");
                emptyLabel.setStyle("-fx-font-size: 16; -fx-text-fill: #ffffff;");
                paniersVBox.getChildren().add(emptyLabel);
                return;
            }

            // Pour chaque panier, créer une carte avec TableView
            for (panier panier : paniers) {
                VBox panierCard = createPanierCard(panier);
                paniersVBox.getChildren().add(panierCard);
            }

        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des paniers : " + e.getMessage());
            showAlert("Erreur", "Impossible de charger les paniers : " + e.getMessage());
        }
    }

    private VBox createPanierCard(panier panier) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: #ffffff; -fx-padding: 15; -fx-background-radius: 10;");

        // Informations du panier
        Label idLabel = new Label("Panier ID: " + panier.getId());
        idLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");

        Label etatLabel = new Label("État: " + panier.getEtat());
        Label finalizedLabel = new Label("Finalisé: " + (panier.isIs_finalized() ? "Oui" : "Non"));
        Label totalLabel = new Label("Total du panier: " + String.format("%.2f DT", panierService.calculerTotalPanier(panier.getId())));

        // TableView pour les produits
        TableView<panier_produit> tableView = new TableView<>();
        tableView.setPrefHeight(200); // Hauteur ajustable
        tableView.setStyle("-fx-background-color: #f9f9f9;");

        // Colonne Image
        TableColumn<panier_produit, ImageView> imageColumn = new TableColumn<>("Image");
        imageColumn.setCellValueFactory(cellData -> {
            ImageView imageView = new ImageView();
            imageView.setFitHeight(50);
            imageView.setFitWidth(50);
            try {
                imageView.setImage(new Image(getClass().getResourceAsStream(cellData.getValue().getProduit().getImage())));
            } catch (Exception e) {
                imageView.setImage(new Image(getClass().getResourceAsStream("/images/default_product.png")));
            }
            return new SimpleObjectProperty<>(imageView);
        });
        imageColumn.setPrefWidth(80);

        // Colonne Nom
        TableColumn<panier_produit, String> nomColumn = new TableColumn<>("Nom");
        nomColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProduit().getNom()));
        nomColumn.setPrefWidth(200);

        // Colonne Prix
        TableColumn<panier_produit, Double> prixColumn = new TableColumn<>("Prix");
        prixColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getProduit().getPrix()).asObject());
        prixColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.format("%.2f DT", item));
            }
        });
        prixColumn.setPrefWidth(100);

        // Colonne Quantité
        TableColumn<panier_produit, Integer> quantiteColumn = new TableColumn<>("Quantité");
        quantiteColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getQuantite()).asObject());
        quantiteColumn.setPrefWidth(100);

        // Colonne Total
        TableColumn<panier_produit, Double> totalColumn = new TableColumn<>("Total");
        totalColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getTotal()).asObject());
        totalColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.format("%.2f DT", item));
            }
        });
        totalColumn.setPrefWidth(100);

        // Colonne Lieu
        TableColumn<panier_produit, String> lieuColumn = new TableColumn<>("Lieu de livraison");
        lieuColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLieu()));
        lieuColumn.setPrefWidth(150);

        // Colonne Téléphone
        TableColumn<panier_produit, Integer> telephoneColumn = new TableColumn<>("Téléphone");
        telephoneColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getNum_tele()).asObject());
        telephoneColumn.setPrefWidth(120);

        // Colonne Actions
        TableColumn<panier_produit, Void> actionsColumn = new TableColumn<>("Actions");
        actionsColumn.setCellFactory(column -> new TableCell<>() {
            private final Button editButton = new Button("✏️");
            private final Button deleteButton = new Button("🗑️");
            private final HBox buttons = new HBox(5, editButton, deleteButton);

            {
                // Style des boutons
                editButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14; -fx-padding: 5 10; -fx-background-radius: 5;");
                deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14; -fx-padding: 5 10; -fx-background-radius: 5;");
                
                // Effets au survol
                editButton.setOnMouseEntered(e -> {
                    if (!editButton.isDisabled()) {
                        editButton.setStyle("-fx-background-color: #45a049; -fx-text-fill: white; -fx-font-size: 14; -fx-padding: 5 10; -fx-background-radius: 5;");
                    }
                });
                editButton.setOnMouseExited(e -> {
                    if (!editButton.isDisabled()) {
                        editButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14; -fx-padding: 5 10; -fx-background-radius: 5;");
                    }
                });
                
                deleteButton.setOnMouseEntered(e -> {
                    if (!deleteButton.isDisabled()) {
                        deleteButton.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-font-size: 14; -fx-padding: 5 10; -fx-background-radius: 5;");
                    }
                });
                deleteButton.setOnMouseExited(e -> {
                    if (!deleteButton.isDisabled()) {
                        deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14; -fx-padding: 5 10; -fx-background-radius: 5;");
                    }
                });

                editButton.setOnAction(event -> {
                    panier_produit pp = getTableView().getItems().get(getIndex());
                    modifierCommande(pp);
                });

                deleteButton.setOnAction(event -> {
                    panier_produit pp = getTableView().getItems().get(getIndex());
                    supprimerCommande(pp);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    buttons.setAlignment(Pos.CENTER);
                    
                    // Vérifier si le panier est finalisé
                    if (panier.isIs_finalized()) {
                        // Désactiver les boutons et changer leur style
                        editButton.setDisable(true);
                        deleteButton.setDisable(true);
                        editButton.setStyle("-fx-background-color: #9e9e9e; -fx-text-fill: white; -fx-font-size: 14; -fx-padding: 5 10; -fx-background-radius: 5;");
                        deleteButton.setStyle("-fx-background-color: #9e9e9e; -fx-text-fill: white; -fx-font-size: 14; -fx-padding: 5 10; -fx-background-radius: 5;");
                    }
                    
                    setGraphic(buttons);
                }
            }
        });
        actionsColumn.setPrefWidth(150);

        // Ajouter les colonnes au TableView
        tableView.getColumns().addAll(imageColumn, nomColumn, prixColumn, quantiteColumn, totalColumn, lieuColumn, telephoneColumn, actionsColumn);

        // Charger les produits du panier
        List<panier_produit> produits = panierService.getProduitsDuPanier(panier.getId());
        tableView.getItems().setAll(produits);

        // Bouton "Vérifier cette commande"
        Button verifierButton = new Button("Vérifier cette commande");
        
        // Vérifier si le panier est déjà finalisé
        if (panier.isIs_finalized()) {
            verifierButton.setText("Commande vérifiée");
            verifierButton.setDisable(true);
            verifierButton.setStyle("-fx-background-color: #9e9e9e; -fx-text-fill: white; -fx-font-weight: bold;");
        } else {
            verifierButton.setStyle("-fx-background-color: #00c6a9; -fx-text-fill: white; -fx-font-weight: bold;");
            verifierButton.setOnAction(event -> verifierCommande(panier));
        }

        // Ajouter les éléments à la carte
        card.getChildren().addAll(idLabel, etatLabel, finalizedLabel, totalLabel, tableView, verifierButton);

        return card;
    }

    private void modifierCommande(panier_produit pp) {
        try {
            // Créer une nouvelle fenêtre pour la modification
            Stage stage = new Stage();
            VBox root = new VBox(15);
            root.setStyle("-fx-padding: 20; -fx-background-color: white;");
            root.setAlignment(Pos.CENTER);

            // Image du produit
            ImageView imageView = new ImageView();
            imageView.setFitHeight(100);
            imageView.setFitWidth(100);
            imageView.setImage(new Image(getClass().getResourceAsStream(pp.getProduit().getImage())));

            // Informations du produit
            Label nomLabel = new Label("Nom: " + pp.getProduit().getNom());
            nomLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");
            
            Label prixLabel = new Label("Prix: " + String.format("%.2f DT", pp.getProduit().getPrix()));
            prixLabel.setStyle("-fx-font-size: 14;");

            // Champs de modification
            TextField lieuField = new TextField(pp.getLieu());
            lieuField.setPromptText("Lieu de livraison");
            lieuField.setStyle("-fx-pref-width: 250; -fx-padding: 8; -fx-background-radius: 5;");

            TextField telephoneField = new TextField(String.valueOf(pp.getNum_tele()));
            telephoneField.setPromptText("Numéro de téléphone");
            telephoneField.setStyle("-fx-pref-width: 250; -fx-padding: 8; -fx-background-radius: 5;");

            // Spinner pour la quantité
            Spinner<Integer> quantiteSpinner = new Spinner<>();
            ValidationUtilss.configureQuantiteSpinner(quantiteSpinner, 1, 100, pp.getQuantite());
            quantiteSpinner.setStyle("-fx-pref-width: 250; -fx-padding: 8; -fx-background-radius: 5;");

            // Bouton de modification
            Button modifierButton = new Button("Modifier");
            modifierButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14; -fx-padding: 10 20; -fx-background-radius: 5;");
            modifierButton.setOnAction(event -> {
                try {
                    // Validation des champs
                    if (!ValidationUtilss.validateLieu(lieuField.getText())) {
                        return;
                    }
                    if (!ValidationUtilss.validateNumTele(telephoneField.getText())) {
                        return;
                    }

                    // Mise à jour des données
                    pp.setLieu(lieuField.getText());
                    pp.setNum_tele(Integer.parseInt(telephoneField.getText()));
                    pp.setQuantite(quantiteSpinner.getValue());
                    panierService.updatePanierProduit(pp);
                    stage.close();
                    loadPaniers(); // Recharger la liste
                    showAlert("Succès", "Commande modifiée avec succès");
                } catch (Exception e) {
                    showAlert("Erreur", "Erreur lors de la modification: " + e.getMessage());
                }
            });

            // Ajouter tous les éléments à la fenêtre
            VBox formContainer = new VBox(10);
            formContainer.setAlignment(Pos.CENTER);
            formContainer.setPadding(new Insets(20));
            formContainer.setStyle("-fx-background-color: #f9f9f9; -fx-background-radius: 10;");
            
            formContainer.getChildren().addAll(
                imageView,
                nomLabel,
                prixLabel,
                new Label("Lieu de livraison:"),
                lieuField,
                new Label("Numéro de téléphone:"),
                telephoneField,
                new Label("Quantité:"),
                quantiteSpinner,
                modifierButton
            );

            root.getChildren().add(formContainer);

            stage.setScene(new Scene(root));
            stage.setTitle("Modifier la commande");
            stage.show();
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'ouverture de la fenêtre de modification: " + e.getMessage());
        }
    }

    private void supprimerCommande(panier_produit pp) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer la commande");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette commande ?");
        
        // Personnaliser le style des boutons
        ButtonType buttonTypeOk = new ButtonType("Oui", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Non", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(buttonTypeOk, buttonTypeCancel);
        
        // Personnaliser le style de l'alerte
        alert.getDialogPane().setStyle("-fx-background-color: white;");
        alert.getDialogPane().lookupButton(buttonTypeOk).setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");
        alert.getDialogPane().lookupButton(buttonTypeCancel).setStyle("-fx-background-color: #9e9e9e; -fx-text-fill: white; -fx-font-weight: bold;");

        alert.showAndWait().ifPresent(response -> {
            if (response == buttonTypeOk) {
                try {
                    panierService.deletePanierProduit(pp.getId());
                    loadPaniers(); // Recharger la liste
                    showAlert("Succès", "Commande supprimée avec succès");
                } catch (Exception e) {
                    showAlert("Erreur", "Erreur lors de la suppression: " + e.getMessage());
                }
            }
        });
    }

    private void verifierCommande(panier panier) {
        try {
            // Logique pour vérifier la commande
            showAlert("Vérification", "Commande du panier ID " + panier.getId() + " vérifiée.");
            // Exemple : Marquer le panier comme finalisé
            panierService.finaliserPanier(panier.getId());
            // Recharger les paniers pour mettre à jour l'affichage
            loadPaniers();
        } catch (Exception e) {
            System.err.println("Erreur lors de la vérification du panier : " + e.getMessage());
            showAlert("Erreur", "Impossible de vérifier la commande : " + e.getMessage());
        }
    }

    @FXML
    private void goToListeProduit() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/listeProduitFront.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) paniersVBox.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des Produits");
        } catch (IOException e) {
            System.err.println("Erreur lors du retour à la liste des produits : " + e.getMessage());
            showAlert("Erreur", "Impossible de retourner à la liste des produits : " + e.getMessage());
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