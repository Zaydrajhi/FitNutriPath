package controller;

import entite.categorie;
import service.CategorieService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import util.ValidationUtilss;
import util.ValidationUtilss;

import java.io.File;

public class modifierCategorieController {
    @FXML
    private TextField tf_nomCategorie;
    @FXML
    private TextField tf_descCat;
    @FXML
    private ImageView imageView;

    private String imagePath;
    private categorie categorieAModifier;
    private final CategorieService categorieService = new CategorieService();

    public void initData(categorie categorie) {
        this.categorieAModifier = categorie;
        tf_nomCategorie.setText(categorie.getNom());
        tf_descCat.setText(categorie.getDescription());

        if (categorie.getImage() != null && !categorie.getImage().isEmpty()) {
            try {
                Image image = new Image(getClass().getResource(categorie.getImage()).toString());
                imageView.setImage(image);
                imagePath = categorie.getImage();
            } catch (Exception e) {
                System.out.println("Erreur de chargement de l'image: " + e.getMessage());
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

                java.nio.file.Files.copy(
                        selectedFile.toPath(),
                        destinationFile.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING
                );

                imagePath = "/images/" + fileName;
                imageView.setImage(new Image(destinationFile.toURI().toString()));
            } catch (Exception e) {
                showAlert("Erreur", "Erreur lors de la copie de l'image : " + e.getMessage());
            }
        }
    }

    @FXML
    private void modifierCat(ActionEvent event) {
        // Validation du nom
        if (!ValidationUtilss.validateCategorieNom(tf_nomCategorie.getText())) {
            return;
        }

        // Validation de la description
        if (!ValidationUtilss.validateCategorieDescription(tf_descCat.getText())) {
            return;
        }

        categorieAModifier.setNom(tf_nomCategorie.getText());
        categorieAModifier.setDescription(tf_descCat.getText());
        if (imagePath != null) categorieAModifier.setImage(imagePath);

        try {
            categorieService.update(categorieAModifier);
            showAlert("Succès", "Catégorie modifiée avec succès");
            ((Stage) tf_nomCategorie.getScene().getWindow()).close();
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la modification : " + e.getMessage());
        }
    }

    @FXML
    private void listeCat(ActionEvent event) {
        ((Stage) tf_nomCategorie.getScene().getWindow()).close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}