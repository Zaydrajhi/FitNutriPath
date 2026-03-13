package controller;

import entite.categorie;
import entite.produit;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import service.CategorieService;
import service.ProduitService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import util.ValidationUtilss;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public class AjouterProduitController {
    // Champs FXML
    @FXML private TextField tf_nomProduit;
    @FXML private TextField tf_prix;
    @FXML private Spinner<Integer> tf_stock;
    @FXML private TextField tf_descProd;
    @FXML private ComboBox<categorie> comboBox_categ;
    @FXML private ImageView imageView;
    @FXML private Button selectImageButton;

    // Services
    private final CategorieService categorieService = new CategorieService();
    private final ProduitService produitService = new ProduitService();

    // Variables
    private String imagePath;

    @FXML
    public void initialize() {
        setupSpinner();
        setupComboBox();
        loadCategories();
    }

    private void setupSpinner() {
        ValidationUtilss.configureStockSpinner(tf_stock, 1, 999, 1);
    }

    private void setupComboBox() {
        comboBox_categ.setCellFactory(param -> new ListCell<categorie>() {
            @Override
            protected void updateItem(categorie item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNom());
            }
        });

        comboBox_categ.setConverter(new StringConverter<categorie>() {
            @Override
            public String toString(categorie categorie) {
                return categorie == null ? null : categorie.getNom();
            }

            @Override
            public categorie fromString(String string) {
                return comboBox_categ.getItems().stream()
                        .filter(c -> c.getNom().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });
    }

    private void loadCategories() {
        try {
            ObservableList<categorie> categories = FXCollections.observableArrayList(
                    categorieService.readAll()
            );
            comboBox_categ.setItems(categories);
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors du chargement des catégories: " + e.getMessage());
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
    private void ajouterProduit(ActionEvent event) {
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

        try {
            produit nouveauProduit = new produit(
                    tf_nomProduit.getText(),
                    Double.parseDouble(tf_prix.getText()),
                    tf_stock.getValue(),
                    tf_descProd.getText(),
                    imagePath != null ? imagePath : "",
                    comboBox_categ.getValue()
            );

            produitService.create(nouveauProduit);
            showAlert("Succès", "Produit ajouté avec succès !");
            clearFields();
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'ajout : " + e.getMessage());
        }
    }

    private void clearFields() {
        tf_nomProduit.clear();
        tf_prix.clear();
        tf_stock.getValueFactory().setValue(1);
        tf_descProd.clear();
        comboBox_categ.getSelectionModel().clearSelection();
        imageView.setImage(null);
        imagePath = null;
    }

    @FXML
    private void listeProduit(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/listeProduit.fxml"));
            Stage stage = (Stage) tf_nomProduit.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors du chargement de la liste : " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void interfaceEvennement(ActionEvent actionEvent) {
        try {
            // Charger le fichier FXML de l'interface produit
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterEvenement.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterSalleDeSport.fxml"));
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

    public void interfaceUser(ActionEvent actionEvent) {
        try {
            // Charger le fichier FXML de l'interface produit
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/listeUser.fxml"));
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
}