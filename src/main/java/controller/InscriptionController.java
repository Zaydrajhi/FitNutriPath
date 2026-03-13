package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import entite.User;
import service.UtilisateurService;
import util.ValidationUtils;
import util.ValidationUtils.ValidationRule;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class InscriptionController {

    @FXML
    private TextField loginField;
    @FXML
    private TextField nomField;
    @FXML
    private TextField prenomField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private TextField telField;
    @FXML
    private DatePicker dateNaissanceField;
    @FXML
    private ComboBox<String> roleComboBox;
    @FXML
    private ImageView profileImageView;
    @FXML
    private Button inscriptionButton;
    @FXML
    private Hyperlink loginLink;

    private String imagePath;
    private final UtilisateurService utilisateurService = new UtilisateurService();

    @FXML
    public void initialize() {
        // Initialiser la ComboBox des rôles
        roleComboBox.getItems().addAll("User", "Coach", "Nutritionniste");
        roleComboBox.setValue("User");

        // Ajouter la validation en temps réel
        loginField.textProperty().addListener((observable, oldValue, newValue) -> {
            ValidationUtils.validateField(loginField, "Login", ValidationRule.REQUIRED);
        });

        nomField.textProperty().addListener((observable, oldValue, newValue) -> {
            ValidationUtils.validateField(nomField, "Nom", ValidationRule.NAME);
        });

        prenomField.textProperty().addListener((observable, oldValue, newValue) -> {
            ValidationUtils.validateField(prenomField, "Prénom", ValidationRule.NAME);
        });

        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            ValidationUtils.validateField(emailField, "Email", ValidationRule.EMAIL);
        });

        telField.textProperty().addListener((observable, oldValue, newValue) -> {
            ValidationUtils.validateField(telField, "Téléphone", ValidationRule.PHONE);
        });

        // Validation du mot de passe en temps réel
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            validatePasswords();
        });

        confirmPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            validatePasswords();
        });
    }

    private void validatePasswords() {
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (password.isEmpty()) {
            passwordField.setStyle("-fx-border-color: red;");
            passwordField.setPromptText("Le mot de passe est requis");
        } else if (password.length() < 6) {
            passwordField.setStyle("-fx-border-color: red;");
            passwordField.setPromptText("Le mot de passe doit contenir au moins 6 caractères");
        } else {
            passwordField.setStyle("");
            passwordField.setPromptText("");
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordField.setStyle("-fx-border-color: red;");
            confirmPasswordField.setPromptText("Les mots de passe ne correspondent pas");
        } else {
            confirmPasswordField.setStyle("");
            confirmPasswordField.setPromptText("");
        }
    }

    @FXML
    private void handleChoisirImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image de profil");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(profileImageView.getScene().getWindow());
        if (selectedFile != null) {
            imagePath = selectedFile.toURI().toString();
            profileImageView.setImage(new Image(imagePath));
        }
    }

    @FXML
    private void handleInscription() {
        if (validateForm()) {
            try {
                User newUser = new User();
                newUser.setLogin(loginField.getText().trim());
                newUser.setNom(nomField.getText().trim());
                newUser.setPrenom(prenomField.getText().trim());
                newUser.setEmail(emailField.getText().trim());
                newUser.setMotDePasse(passwordField.getText());
                newUser.setNumTel(telField.getText().trim());
                newUser.setRole(roleComboBox.getValue());
                
                if (dateNaissanceField.getValue() != null) {
                    Date date = Date.from(dateNaissanceField.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
                    newUser.setDatedenaissance(date);
                }
                
                if (imagePath != null && !imagePath.isEmpty()) {
                    newUser.setImage(imagePath);
                }

                if (utilisateurService.updateUtilisateur(newUser)) {
                    showAlert("Succès", "Compte créé avec succès", Alert.AlertType.INFORMATION);
                    returnToLogin();
                } else {
                    showAlert("Erreur", "Impossible de créer le compte", Alert.AlertType.ERROR);
                }
            } catch (Exception e) {
                showAlert("Erreur", "Une erreur est survenue : " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private boolean validateForm() {
        boolean isValid = true;

        // Valider tous les champs requis
        isValid &= ValidationUtils.validateField(loginField, "Login", ValidationRule.REQUIRED);
        isValid &= ValidationUtils.validateField(nomField, "Nom", ValidationRule.NAME);
        isValid &= ValidationUtils.validateField(prenomField, "Prénom", ValidationRule.NAME);
        isValid &= ValidationUtils.validateField(emailField, "Email", ValidationRule.EMAIL);
        isValid &= ValidationUtils.validateField(telField, "Téléphone", ValidationRule.PHONE);
        isValid &= ValidationUtils.validateDatePicker(dateNaissanceField, "Date de naissance");

        // Valider les mots de passe
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (password.isEmpty() || password.length() < 6) {
            passwordField.setStyle("-fx-border-color: red;");
            isValid = false;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordField.setStyle("-fx-border-color: red;");
            isValid = false;
        }

        // Valider le rôle
        if (roleComboBox.getValue() == null) {
            roleComboBox.setStyle("-fx-border-color: red;");
            isValid = false;
        }

        if (!isValid) {
            showAlert("Erreur de validation", "Veuillez corriger les champs en rouge.", Alert.AlertType.ERROR);
        }

        return isValid;
    }

    @FXML
    private void returnToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) loginLink.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Connexion");
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de retourner à la page de connexion", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 