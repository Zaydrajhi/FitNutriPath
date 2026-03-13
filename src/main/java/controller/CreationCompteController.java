package controller;

import entite.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import service.UtilisateurService;
import util.ValidationUtils;
import static util.ValidationUtils.ValidationRule;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class CreationCompteController {

    @FXML
    private TextField nomField;

    @FXML
    private TextField prenomField;

    @FXML
    private DatePicker dateNaissanceField;

    @FXML
    private TextField numTelField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField motDePasseField;

    @FXML
    private TextField imageField;

    @FXML
    private ComboBox<String> roleField;

    private final UtilisateurService utilisateurService = new UtilisateurService();

    @FXML
    public void initialize() {
        // Initialisation des rôles disponibles
        if (roleField != null) {
            roleField.getItems().addAll("User", "Coach", "Nutritionniste");
        }
    }


    @FXML
    private void handleCreateAccount() {
        try {
            boolean isValid = true;

            isValid &= ValidationUtils.validateField(nomField, "Nom", ValidationRule.NAME);
            isValid &= ValidationUtils.validateField(prenomField, "Prénom", ValidationRule.NAME);
            isValid &= ValidationUtils.validateField(numTelField, "Téléphone", ValidationRule.PHONE);
            isValid &= ValidationUtils.validateField(emailField, "Email", ValidationRule.EMAIL);
            isValid &= ValidationUtils.validateField(motDePasseField, "Mot de passe", ValidationRule.PASSWORD);
            isValid &= ValidationUtils.validateDatePicker(dateNaissanceField, "Date de naissance");

            if (roleField.getValue() == null || roleField.getValue().trim().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation", "Veuillez sélectionner un rôle.");
                return;
            }

            if (!isValid) {
                showAlert(Alert.AlertType.WARNING, "Champs invalides", "Veuillez corriger les erreurs dans le formulaire.");
                return;
            }

            // Récupération des valeurs après validation
            String nom = nomField.getText().trim();
            String prenom = prenomField.getText().trim();
            Date dateNaissance = java.sql.Date.valueOf(dateNaissanceField.getValue());
            String numTel = numTelField.getText().trim();
            String email = emailField.getText().trim();
            String motDePasse = motDePasseField.getText().trim();
            String image = imageField != null ? imageField.getText().trim() : "";
            String role = roleField.getValue();

            String login = generateLogin(role);

            User newUser = new User();
            newUser.setNom(nom);
            newUser.setPrenom(prenom);
            newUser.setDatedenaissance(dateNaissance);
            newUser.setNumTel(numTel);
            newUser.setEmail(email);
            newUser.setMotDePasse(motDePasse);
            newUser.setImage(image);
            newUser.setRole(role);
            newUser.setLogin(login);

            if (role.equals("User")) {
                newUser.setPending(false);
                newUser.setStatut("validé");
            } else {
                newUser.setPending(true);
                newUser.setStatut("en attente");
            }

            utilisateurService.create(newUser);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Compte créé avec succès !\nVotre login est : " + login);
            clearForm();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la création du compte : " + e.getMessage());
        }
    }


    private String generateLogin(String role) {
        int randomNumber = (int)(Math.random() * 900 + 100); // Génère un nombre entre 100 et 999
        int randomSuffix = (int)(Math.random() * 9000 + 1000); // Génère un nombre entre 1000 et 9999
        
        String prefix = "";
        switch(role) {
            case "User":
                prefix = "USR";
                break;
            case "Coach":
                prefix = "COA";
                break;
            case "Nutritionniste":
                prefix = "NUT";
                break;
        }
        
        return randomNumber + prefix + randomSuffix;
    }

    @FXML
    private void handleRetourLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) nomField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la page de connexion.");
        }
    }

    @FXML
    private void handleChoisirImage() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choisir une image");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            File selectedFile = fileChooser.showOpenDialog(null);
            if (selectedFile != null && imageField != null) {
                imageField.setText(selectedFile.toURI().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la sélection de l'image.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearForm() {
        if (nomField != null) nomField.clear();
        if (prenomField != null) prenomField.clear();
        if (dateNaissanceField != null) dateNaissanceField.setValue(null);
        if (numTelField != null) numTelField.clear();
        if (emailField != null) emailField.clear();
        if (motDePasseField != null) motDePasseField.clear();
        if (imageField != null) imageField.clear();
        if (roleField != null) roleField.setValue(null);
    }
}
