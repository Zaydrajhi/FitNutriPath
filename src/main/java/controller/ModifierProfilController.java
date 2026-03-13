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
import util.SessionManager;
import util.ValidationUtils;
import util.ValidationUtils.ValidationRule;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class ModifierProfilController {

    @FXML
    private ImageView profileImage;
    @FXML
    private TextField nomField;
    @FXML
    private TextField prenomField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField telField;
    @FXML
    private DatePicker dateNaissanceField;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private User currentUser;
    private String imagePath;
    private final UtilisateurService utilisateurService = new UtilisateurService();

    @FXML
    public void initialize() {
        // Ajouter des écouteurs pour la validation en temps réel
        nomField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateNom();
        });
        
        prenomField.textProperty().addListener((observable, oldValue, newValue) -> {
            validatePrenom();
        });
        
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateEmail();
        });
        
        telField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateTel();
        });
    }

    private boolean validateNom() {
        String nom = nomField.getText().trim();
        if (nom.isEmpty()) {
            nomField.setStyle("-fx-border-color: red;");
            nomField.setPromptText("Le nom est obligatoire");
            return false;
        }
        if (!nom.matches("^[A-Za-z\\s-]+$")) {
            nomField.setStyle("-fx-border-color: red;");
            nomField.setPromptText("Le nom ne doit contenir que des lettres");
            return false;
        }
        nomField.setStyle("");
        nomField.setPromptText("");
        return true;
    }

    private boolean validatePrenom() {
        String prenom = prenomField.getText().trim();
        if (prenom.isEmpty()) {
            prenomField.setStyle("-fx-border-color: red;");
            prenomField.setPromptText("Le prénom est obligatoire");
            return false;
        }
        if (!prenom.matches("^[A-Za-z\\s-]+$")) {
            prenomField.setStyle("-fx-border-color: red;");
            prenomField.setPromptText("Le prénom ne doit contenir que des lettres");
            return false;
        }
        prenomField.setStyle("");
        prenomField.setPromptText("");
        return true;
    }

    private boolean validateEmail() {
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            emailField.setStyle("-fx-border-color: red;");
            emailField.setPromptText("L'email est obligatoire");
            return false;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            emailField.setStyle("-fx-border-color: red;");
            emailField.setPromptText("Format d'email invalide (doit contenir @ et .)");
            return false;
        }
        emailField.setStyle("");
        emailField.setPromptText("");
        return true;
    }

    private boolean validateTel() {
        String tel = telField.getText().trim();
        if (tel.isEmpty()) {
            telField.setStyle("-fx-border-color: red;");
            telField.setPromptText("Le numéro de téléphone est obligatoire");
            return false;
        }
        if (!tel.matches("^[0-9]{8}$")) {
            telField.setStyle("-fx-border-color: red;");
            telField.setPromptText("Le numéro doit contenir exactement 8 chiffres");
            return false;
        }
        telField.setStyle("");
        telField.setPromptText("");
        return true;
    }

    private boolean validateForm() {
        boolean isValid = true;
        isValid &= validateNom();
        isValid &= validatePrenom();
        isValid &= validateEmail();
        isValid &= validateTel();
        
        if (dateNaissanceField.getValue() == null) {
            dateNaissanceField.setStyle("-fx-border-color: red;");
            dateNaissanceField.setPromptText("La date de naissance est obligatoire");
            isValid = false;
        } else {
            dateNaissanceField.setStyle("");
            dateNaissanceField.setPromptText("");
        }

        if (!isValid) {
            showAlert("Erreur de validation", "Veuillez corriger les champs en rouge.", Alert.AlertType.ERROR);
        }

        return isValid;
    }

    public void initData(User user) {
        this.currentUser = user;
        loadUserData();
    }

    private void loadUserData() {
        if (currentUser != null) {
            nomField.setText(currentUser.getNom());
            prenomField.setText(currentUser.getPrenom());
            emailField.setText(currentUser.getEmail());
            telField.setText(currentUser.getNumTel());

            if (currentUser.getDatedenaissance() != null) {
                java.sql.Date sqlDate = (java.sql.Date) currentUser.getDatedenaissance();
                LocalDate date = sqlDate.toLocalDate();
                dateNaissanceField.setValue(date);
            }

            if (currentUser.getImage() != null && !currentUser.getImage().isEmpty()) {
                try {
                    Image image = new Image(currentUser.getImage());
                    profileImage.setImage(image);
                } catch (Exception e) {
                    profileImage.setImage(new Image(getClass().getResourceAsStream("/images/default_user.png")));
                }
            } else {
                profileImage.setImage(new Image(getClass().getResourceAsStream("/images/default_user.png")));
            }
        }
    }

    @FXML
    private void handleChangeImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image de profil");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(profileImage.getScene().getWindow());
        if (selectedFile != null) {
            imagePath = selectedFile.toURI().toString();
            profileImage.setImage(new Image(imagePath));
        }
    }

    @FXML
    private void handleSave() {
        if (validateForm()) {
            try {
                currentUser.setNom(nomField.getText());
                currentUser.setPrenom(prenomField.getText());
                currentUser.setEmail(emailField.getText());
                currentUser.setNumTel(telField.getText());

                if (dateNaissanceField.getValue() != null) {
                    Date date = Date.from(dateNaissanceField.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
                    currentUser.setDatedenaissance(date);
                }

                if (imagePath != null && !imagePath.isEmpty()) {
                    currentUser.setImage(imagePath);
                }

                if (utilisateurService.updateUtilisateur(currentUser)) {
                    SessionManager.setCurrentUser(currentUser);
                    showAlert("Succès", "Profil mis à jour avec succès", Alert.AlertType.INFORMATION);
                    returnToProfile();
                } else {
                    showAlert("Erreur", "Échec de la mise à jour du profil", Alert.AlertType.ERROR);
                }
            } catch (Exception e) {
                showAlert("Erreur", "Une erreur est survenue : " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleCancel() {
        returnToProfile();
    }

    private void returnToProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/profil.fxml"));
            Parent root = loader.load();
            
            ProfilController controller = loader.getController();
            controller.initData(currentUser);
            
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Mon Profil");
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de retourner à la page de profil", Alert.AlertType.ERROR);
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