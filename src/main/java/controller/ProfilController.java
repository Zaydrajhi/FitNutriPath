package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import entite.User;
import util.SessionManager;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class ProfilController {

    @FXML
    private ImageView profileImageView;
    @FXML
    private Label loginLabel;
    @FXML
    private Label nomLabel;
    @FXML
    private Label prenomLabel;
    @FXML
    private Label dateNaissanceLabel;
    @FXML
    private Label phoneLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label roleLabel;
    @FXML
    private Label statutLabel;
    @FXML
    private Button editButton;
    @FXML
    private Button backButton;
    @FXML
    private Button changePasswordButton;

    private User currentUser;

    public void initData(User user) {
        this.currentUser = user;
        displayUserInfo();
    }

    private void displayUserInfo() {
        if (currentUser != null) {
            // Afficher les informations de base
            loginLabel.setText(currentUser.getLogin());
            nomLabel.setText(currentUser.getNom());
            prenomLabel.setText(currentUser.getPrenom());
            
            // Formater la date de naissance
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            dateNaissanceLabel.setText(dateFormat.format(currentUser.getDatedenaissance()));
            
            phoneLabel.setText(currentUser.getNumTel());
            emailLabel.setText(currentUser.getEmail());
            roleLabel.setText(currentUser.getRole());
            statutLabel.setText(currentUser.getStatut());

            // Charger l'image de profil
            if (currentUser.getImage() != null && !currentUser.getImage().isEmpty()) {
                try {
                    Image image = new Image(currentUser.getImage());
                    profileImageView.setImage(image);
                } catch (Exception e) {
                    profileImageView.setImage(new Image(getClass().getResourceAsStream("/images/default_user.png")));
                }
            } else {
                profileImageView.setImage(new Image(getClass().getResourceAsStream("/images/default_user.png")));
            }
        }
    }

    @FXML
    private void handleBack() {
        try {
            String fxmlFile = switch (currentUser.getRole()) {
                case "Nutritionniste" -> "/acceuilnutritionniste.fxml";
                case "Coach" -> "/acceuilcoach.fxml";
                case "Admin" -> "/listeuser.fxml";
                default -> "/acceuil.fxml";
            };

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de retourner à la page précédente", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifierProfil.fxml"));
            Parent root = loader.load();
            
            ModifierProfilController controller = loader.getController();
            controller.initData(currentUser);
            
            Stage stage = new Stage();
            stage.setTitle("Modifier le profil");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            // Rafraîchir les informations après la modification
            displayUserInfo();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de charger la page de modification", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleChangePassword() {
        // TODO: Implémenter la logique de changement de mot de passe
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Changement de mot de passe");
        alert.setHeaderText(null);
        alert.setContentText("Fonctionnalité à venir");
        alert.showAndWait();
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 