package controller;

import entite.categorie;
import javafx.scene.control.Button;
import service.CategorieService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import util.ValidationUtilss;
import util.ValidationUtilss;

import java.io.File;
import java.io.IOException;

public class AjouterCategorieController {

    @FXML
    private TextField tf_nomCategorie;

    @FXML
    private TextField tf_descCat;

    @FXML
    private ImageView imageView;

    private String imagePath;

    private final CategorieService categorieService = new CategorieService();

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

                java.nio.file.Files.copy(
                        selectedFile.toPath(),
                        destinationFile.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING
                );

                imagePath = "/images/" + fileName;
                imageView.setImage(new javafx.scene.image.Image(destinationFile.toURI().toString()));
            } catch (Exception e) {
                showAlert("Erreur", "Erreur lors de la copie de l'image : " + e.getMessage());
            }
        }
    }

    @FXML
    private void ajouterCat(ActionEvent event) {
        // Validation du nom
        if (!ValidationUtilss.validateCategorieNom(tf_nomCategorie.getText())) {
            return;
        }

        // Validation de la description
        if (!ValidationUtilss.validateCategorieDescription(tf_descCat.getText())) {
            return;
        }

        categorie nouvelleCategorie = new categorie(
                tf_nomCategorie.getText(),
                tf_descCat.getText(),
                imagePath != null ? imagePath : ""
        );

        try {
            categorieService.create(nouvelleCategorie);
            showAlert("Succès", "Catégorie ajoutée avec succès !");
            tf_nomCategorie.clear();
            tf_descCat.clear();
            imageView.setImage(null);
            imagePath = null;
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'ajout : " + e.getMessage());
        }
    }

    @FXML
    private void listeCat(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ListeCategories.fxml"));
            Stage stage = (Stage) tf_nomCategorie.getScene().getWindow();
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

    public void interfaceUser(ActionEvent actionEvent) {
        try {
            // Charger le fichier FXML de l'interface produit
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeUser.fxml"));
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
