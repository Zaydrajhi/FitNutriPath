package controller;

import entite.panier;
import entite.panier_produit;
import entite.produit;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import service.PanierService;
import service.ProduitService;
import javafx.application.Platform;
import util.ValidationUtilss;
import util.ValidationUtilss;

public class ProductDetailsController {

    @FXML private ImageView productImage;
    @FXML private Label productName;
    @FXML private Label productPrice;
    @FXML private Label productStock;
    @FXML private Text productDescription;
    @FXML private TextField lieuField;
    @FXML private TextField numTeleField;
    @FXML private Spinner<Integer> quantiteSpinner;
    @FXML private Label totalLabel;
    @FXML private Button addToCartButton;

    private produit currentProduct;
    private panier currentPanier;
    private final PanierService panierService = new PanierService();
    private final ProduitService produitService = new ProduitService();

    public void initData(produit product) {
        this.currentProduct = product;

        try {
            // Créer ou récupérer un panier
            this.currentPanier = panierService.getOrCreateCurrentPanier();

            if (currentPanier == null) {
                showAlertAndClose("Erreur Critique", "Impossible de créer un panier");
                return;
            }

            System.out.println("Panier ID: " + currentPanier.getId() + " prêt");

            // Initialisation de l'interface
            try {
                productImage.setImage(new Image(getClass().getResourceAsStream(product.getImage())));
            } catch (Exception e) {
                System.err.println("Erreur de chargement de l'image: " + e.getMessage());
                productImage.setImage(new Image(getClass().getResourceAsStream("/images/default_product.png")));
            }

            productName.setText(product.getNom());
            productPrice.setText(String.format("%.2f DT", product.getPrix()));
            productStock.setText("Stock: " + product.getStock());
            productDescription.setText(product.getDescription() != null ? product.getDescription() : "Aucune description");

            // Configuration du spinner avec les limites (1,100)
            ValidationUtilss.configureQuantiteSpinner(quantiteSpinner, 1, Math.min(100, product.getStock()), 1);

            // Désactiver si stock insuffisant
            if (product.getStock() <= 0) {
                addToCartButton.setDisable(true);
                productStock.setText("Rupture de stock");
                productStock.setTextFill(Color.RED);
            }

            updateTotal();

            // Écouteur pour mise à jour du total
            quantiteSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
                updateTotal();
                if (newVal > currentProduct.getStock()) {
                    showAlert("Quantité invalide", "La quantité dépasse le stock disponible");
                    quantiteSpinner.getValueFactory().setValue(currentProduct.getStock());
                }
            });

            // Gestion du bouton
            addToCartButton.setOnAction(e -> addToCart());

        } catch (Exception e) {
            System.err.println("Erreur d'initialisation: " + e.getMessage());
            showAlertAndClose("Erreur", "Problème d'initialisation");
        }
    }

    private void updateTotal() {
        int quantite = quantiteSpinner.getValue();
        double total = currentProduct.getPrix() * quantite;
        totalLabel.setText(String.format("%.2f DT", total));
    }

    private void addToCart() {
        try {
            // Validation des champs
            if (!ValidationUtilss.validateLieu(lieuField.getText())) {
                return;
            }

            if (!ValidationUtilss.validateNumTele(numTeleField.getText())) {
                return;
            }

            int quantite = quantiteSpinner.getValue();
            if (quantite <= 0 || quantite > currentProduct.getStock()) {
                showAlert("Stock insuffisant", "Quantité non disponible");
                return;
            }

            if (currentProduct.getCategorie() == null || currentProduct.getCategorie().getId() <= 0) {
//                showAlert("Erreur", "Le produit n'a pas de catégorie valide");
//                return;
                currentProduct.getCategorie().setId(6);
            }

            // Création panier_produit
            panier_produit panierProduit = new panier_produit();
            panierProduit.setPanier(currentPanier);
            panierProduit.setProduit(currentProduct);
            panierProduit.setStatut("En attente");
            panierProduit.setNum_tele(Integer.parseInt(numTeleField.getText()));
            panierProduit.setLieu(lieuField.getText());
            panierProduit.setQuantite(quantite);
            panierProduit.setTotal(currentProduct.getPrix() * quantite);

            // Ajout au panier
            panierService.ajouterProduitAuPanier(panierProduit);

            // Mise à jour du stock
            produit produitPourMiseAJour = new produit();
            produitPourMiseAJour.setId(currentProduct.getId());
            produitPourMiseAJour.setNom(currentProduct.getNom());
            produitPourMiseAJour.setPrix(currentProduct.getPrix());
            produitPourMiseAJour.setStock(currentProduct.getStock() - quantite);
            produitPourMiseAJour.setDescription(currentProduct.getDescription());
            produitPourMiseAJour.setImage(currentProduct.getImage());
            produitPourMiseAJour.setCategorie(currentProduct.getCategorie());

            produitService.update(produitPourMiseAJour);

            showAlert("Succès", "Produit ajouté au panier!");
            closeWindow();

        } catch (Exception e) {
            System.err.println("Erreur lors de l'ajout: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Problème lors de l'ajout au panier: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    private void showAlertAndClose(String title, String message) {
        showAlert(title, message);
        closeWindow();
    }

    private void closeWindow() {
        if (productImage.getScene() != null && productImage.getScene().getWindow() != null) {
            productImage.getScene().getWindow().hide();
        }
    }
}