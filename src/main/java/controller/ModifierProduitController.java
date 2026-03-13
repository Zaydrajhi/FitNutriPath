package controller;

import entite.categorie;
import entite.produit;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import service.CategorieService;
import service.ProduitService;
import util.ValidationUtilss;
import javafx.event.ActionEvent;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ResourceBundle;

public class ModifierProduitController implements Initializable {

    @FXML private TextField tf_nomProduit;
    @FXML private TextField tf_prix;
    @FXML private Spinner<Integer> tf_stock;
    @FXML private TextField tf_descProd;
    @FXML private ComboBox<categorie> comboBox_categ;
    @FXML private ImageView imageView;
    @FXML private Button selectImageButton;

    private ProduitService produitService = new ProduitService();
    private CategorieService categorieService = new CategorieService();
    private produit produitAModifier;
    private String imagePath;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialisation du Spinner
        ValidationUtilss.configureStockSpinner(tf_stock, 1, 999, 1);

        // Charger les catégories
        comboBox_categ.getItems().addAll(categorieService.readAll());

        // Configurer l'affichage des catégories dans le ComboBox
        comboBox_categ.setCellFactory(lv -> new ListCell<categorie>() {
            @Override
            protected void updateItem(categorie item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getNom());
            }
        });

        comboBox_categ.setButtonCell(new ListCell<categorie>() {
            @Override
            protected void updateItem(categorie item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getNom());
            }
        });
    }

    public void initData(produit p) {
        this.produitAModifier = p;

        // Remplir les champs avec les données du produit
        tf_nomProduit.setText(p.getNom());
        tf_prix.setText(String.valueOf(p.getPrix()));
        tf_stock.getValueFactory().setValue(p.getStock());
        tf_descProd.setText(p.getDescription());

        // Sélectionner la catégorie
        if (p.getCategorie() != null) {
            for (categorie cat : comboBox_categ.getItems()) {
                if (cat.getId() == p.getCategorie().getId()) {
                    comboBox_categ.getSelectionModel().select(cat);
                    break;
                }
            }
        }

        // Charger l'image
        if (p.getImage() != null && !p.getImage().isEmpty()) {
            try {
                // Essayer d'abord avec le chemin relatif
                try {
                    Image image = new Image(getClass().getResourceAsStream(p.getImage()));
                    imageView.setImage(image);
                    this.imagePath = p.getImage();
                } catch (Exception e) {
                    // Si cela échoue, essayer avec le chemin absolu
                    Image image = new Image(new File("src/main/resources" + p.getImage()).toURI().toString());
                    imageView.setImage(image);
                    this.imagePath = p.getImage();
                }
            } catch (Exception e) {
                System.err.println("Erreur de chargement de l'image: " + e.getMessage());
                // Ne pas échouer si l'image ne peut pas être chargée
            }
        }
    }

    @FXML
    private void selectImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(imageView.getScene().getWindow());
        if (selectedFile != null) {
            try {
                String destinationFolder = "src/main/resources/images/";
                File destinationDir = new File(destinationFolder);
                if (!destinationDir.exists()) destinationDir.mkdirs();

                String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                File destinationFile = new File(destinationFolder + fileName);

                Files.copy(
                        selectedFile.toPath(),
                        destinationFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING
                );

                // Stocker le chemin relatif pour la base de données
                imagePath = "/images/" + fileName;
                
                // Afficher l'image dans l'interface
                try {
                    // Essayer d'abord avec le chemin relatif
                    Image image = new Image(getClass().getResourceAsStream(imagePath));
                    imageView.setImage(image);
                } catch (Exception e) {
                    // Si cela échoue, essayer avec le chemin absolu
                    Image image = new Image(destinationFile.toURI().toString());
                    imageView.setImage(image);
                }
                
                System.out.println("Image sélectionnée et copiée: " + imagePath);
            } catch (Exception e) {
                showAlert("Erreur", "Erreur lors de la copie de l'image : " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleUpdateProduct() {
        // Validation du nom
        if (!ValidationUtilss.validateProduitNom(tf_nomProduit.getText())) {
            return;
        }

        // Validation du prix
        if (!ValidationUtilss.validatePrix(tf_prix.getText())) {
            return;
        }

        // Validation de la description
        if (!ValidationUtilss.validateProduitDescription(tf_descProd.getText())) {
            return;
        }

        // Validation de la catégorie
        if (comboBox_categ.getValue() == null) {
            showAlert("Erreur", "Veuillez sélectionner une catégorie");
            return;
        }

        produitAModifier.setNom(tf_nomProduit.getText());
        produitAModifier.setPrix(Double.parseDouble(tf_prix.getText()));
        produitAModifier.setStock(tf_stock.getValue());
        produitAModifier.setDescription(tf_descProd.getText());
        produitAModifier.setCategorie(comboBox_categ.getValue());
        if (imagePath != null) produitAModifier.setImage(imagePath);

        try {
            produitService.update(produitAModifier);
            showAlert("Succès", "Produit modifié avec succès");
            ((Stage) tf_nomProduit.getScene().getWindow()).close();
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la modification : " + e.getMessage());
        }
    }

    @FXML
    private void listeProduit() {
        closeWindow();
    }

    @FXML
    private void handleRetour() {
        closeWindow();
    }

    private void closeWindow() {
        ((Stage) tf_nomProduit.getScene().getWindow()).close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}