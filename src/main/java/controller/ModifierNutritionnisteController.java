package controller;

import entite.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import service.UtilisateurService;
import util.ValidationUtils;
import util.ValidationUtils.ValidationRule;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;

public class ModifierNutritionnisteController {

    private User nutritionniste;
    private UtilisateurService utilisateurService = new UtilisateurService();

    @FXML
    private TextField tfLogin;
    @FXML
    private TextField tfNom;
    @FXML
    private TextField tfPrenom;
    @FXML
    private TextField tfTel;
    @FXML
    private TextField tfEmail;
    @FXML
    private TextField tfImage;
    @FXML
    private ComboBox<String> cbRole;
    @FXML
    private ImageView userImageView;

    @FXML
    public void initialize() {
        // Initialiser les rôles disponibles
        cbRole.getItems().addAll("User", "Coach", "Nutritionniste");
        
        // Ajouter des écouteurs pour la validation en temps réel
        tfNom.textProperty().addListener((observable, oldValue, newValue) -> {
            ValidationUtils.validateField(tfNom, "Nom", ValidationRule.NAME);
        });
        
        tfPrenom.textProperty().addListener((observable, oldValue, newValue) -> {
            ValidationUtils.validateField(tfPrenom, "Prénom", ValidationRule.NAME);
        });
        
        tfEmail.textProperty().addListener((observable, oldValue, newValue) -> {
            ValidationUtils.validateField(tfEmail, "Email", ValidationRule.EMAIL);
        });
        
        tfTel.textProperty().addListener((observable, oldValue, newValue) -> {
            ValidationUtils.validateField(tfTel, "Téléphone", ValidationRule.PHONE);
        });
    }

    public void setNutritionniste(User nutritionniste) {
        this.nutritionniste = nutritionniste;
        if (nutritionniste != null) {
            tfLogin.setText(nutritionniste.getLogin());
            tfNom.setText(nutritionniste.getNom());
            tfPrenom.setText(nutritionniste.getPrenom());
            tfTel.setText(nutritionniste.getNumTel());
            tfEmail.setText(nutritionniste.getEmail());
            tfImage.setText(nutritionniste.getImage());
            cbRole.setValue(nutritionniste.getRole());
            
            // Charger l'image si elle existe
            if (nutritionniste.getImage() != null && !nutritionniste.getImage().isEmpty()) {
                try {
                    userImageView.setImage(new Image(nutritionniste.getImage()));
                } catch (Exception e) {
                    userImageView.setImage(new Image(getClass().getResourceAsStream("/images/default_user.png")));
                }
            }
        }
    }

    @FXML
    private void handleChoisirImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File selectedFile = fileChooser.showOpenDialog(tfImage.getScene().getWindow());
        if (selectedFile != null) {
            tfImage.setText(selectedFile.toURI().toString());
            userImageView.setImage(new Image(selectedFile.toURI().toString()));
        }
    }

    @FXML
    private void handleAnnuler() {
        Stage stage = (Stage) tfLogin.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void enregistrerModifications(ActionEvent event) {
        if (validateForm()) {
            try {
                nutritionniste.setLogin(tfLogin.getText());
                nutritionniste.setNom(tfNom.getText());
                nutritionniste.setPrenom(tfPrenom.getText());
                nutritionniste.setNumTel(tfTel.getText());
                nutritionniste.setEmail(tfEmail.getText());
                nutritionniste.setImage(tfImage.getText());
                nutritionniste.setRole(cbRole.getValue());

                if (utilisateurService.updateUser(
                    nutritionniste.getId(),
                    nutritionniste.getLogin(),
                    nutritionniste.getNom(),
                    nutritionniste.getPrenom(),
                    nutritionniste.getNumTel(),
                    nutritionniste.getEmail(),
                    nutritionniste.getRole(),
                    nutritionniste.getImage()
                )) {
                    showAlert("Succès", "Nutritionniste mis à jour avec succès", Alert.AlertType.INFORMATION);
                    handleAnnuler();
                } else {
                    showAlert("Erreur", "Échec de la mise à jour du nutritionniste", Alert.AlertType.ERROR);
                }
            } catch (Exception e) {
                showAlert("Erreur", "Une erreur est survenue : " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private boolean validateForm() {
        boolean isValid = true;

        // Valider chaque champ
        isValid &= ValidationUtils.validateField(tfLogin, "Login", ValidationRule.REQUIRED);
        isValid &= ValidationUtils.validateField(tfNom, "Nom", ValidationRule.NAME);
        isValid &= ValidationUtils.validateField(tfPrenom, "Prénom", ValidationRule.NAME);
        isValid &= ValidationUtils.validateField(tfEmail, "Email", ValidationRule.EMAIL);
        isValid &= ValidationUtils.validateField(tfTel, "Téléphone", ValidationRule.PHONE);

        // Valider le rôle
        if (cbRole.getValue() == null || cbRole.getValue().isEmpty()) {
            cbRole.setStyle("-fx-border-color: red;");
            isValid = false;
        } else {
            cbRole.setStyle("");
        }

        if (!isValid) {
            showAlert("Erreur de validation", "Veuillez corriger les champs en rouge.", Alert.AlertType.ERROR);
        }

        return isValid;
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
