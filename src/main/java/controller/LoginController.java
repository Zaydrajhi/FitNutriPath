package controller;
import org.mindrot.jbcrypt.BCrypt;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import test.MainFX;
import util.DataSource;
import entite.User;
import service.UtilisateurService;
import util.SessionManager;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import util.ValidationUtils;
import util.ValidationUtils.ValidationRule;

import java.sql.*;
import java.io.IOException;

public class LoginController {

    // Liens vers les éléments dans le FXML
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Button createAccountButton;
    @FXML
    private Button adminButton;  // Nouveau bouton
    @FXML
    private Label errorMessageLabel;

    private MainFX mainApp;  // Référence vers l'application principale
    private final UtilisateurService utilisateurService = new UtilisateurService();

    // Injecter l'application principale (MainFX)
    public void setMainApp(MainFX mainApp) {
        this.mainApp = mainApp;  // Enregistrer la référence de MainFX
    }

    @FXML
    public void initialize() {
        // Initialisation des composants si nécessaire
        // Ajouter la validation en temps réel
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            ValidationUtils.validateField(emailField, "Email", ValidationRule.EMAIL);
        });

        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            ValidationUtils.validateField(passwordField, "Mot de passe", ValidationRule.REQUIRED);
        });
    }

    // Gérer l'action du bouton "Se connecter"
    @FXML
    private void handleLogin() {
        if (validateLoginForm()) {
            try {
                String email = emailField.getText().trim();
                String password = passwordField.getText();

                User user = utilisateurService.authenticate(email, password);

                if (user != null) {
                    // Stocker l'utilisateur dans la session
                    SessionManager.setCurrentUser(user);
                    
                    // Rediriger en fonction du rôle
                    redirectToDashboard(user.getRole());
                } else {
                    showAlert("Erreur", "Email ou mot de passe incorrect", Alert.AlertType.ERROR);
                }
            } catch (Exception e) {
                showAlert("Erreur", "Une erreur est survenue : " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private boolean validateLoginForm() {
        boolean isValid = true;

        isValid &= ValidationUtils.validateField(emailField, "Email", ValidationRule.EMAIL);
        isValid &= ValidationUtils.validateField(passwordField, "Mot de passe", ValidationRule.REQUIRED);

        if (!isValid) {
            showAlert("Erreur de validation", "Veuillez corriger les champs en rouge.", Alert.AlertType.ERROR);
        }

        return isValid;
    }

    // Gérer l'action du bouton "Pas de compte ? Créer un compte"
    @FXML
    private void handleCreateAccount() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CreationCompte.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) createAccountButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Création de Compte");
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de charger la page de création de compte", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // Fonction de validation de la connexion de l'admin
    private boolean isValidAdminLogin(String login, String password) {
        String sql = "SELECT * FROM user WHERE email = ? AND role = 'Admin'";

        try (Connection conn = DataSource.getInstance().getConnection(); // Connexion ouverte ici
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, login);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String hashedPassword = rs.getString("mot_de_passe");
                    // Vérifier le mot de passe avec BCrypt
                    if (BCrypt.checkpw(password, hashedPassword)) {
                        return true; // Le mot de passe est correct
                    }
                }
                return false; // Si l'utilisateur n'existe pas ou le mot de passe est incorrect
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Afficher un message d'erreur dans l'interface
    private void showErrorMessage(String message) {
        errorMessageLabel.setText(message); // Afficher l'erreur dans le Label
        errorMessageLabel.setStyle("-fx-text-fill: red;"); // Changer la couleur du texte en rouge
    }

    // Gérer l'action du bouton "Accéder à Liste des Utilisateurs"
    @FXML
    private void handleAdminRedirect() {
        try {
            if (mainApp == null) {
                System.err.println("Erreur: MainFX n'est pas initialisé");
                return;
            }
            mainApp.redirectToAdmin();
        } catch (Exception e) {
            System.err.println("Erreur lors de la redirection vers l'admin: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void redirectToDashboard(String role) {
        try {
            String fxmlFile = switch (role) {
                case "Admin" -> "/listeuser.fxml";
                case "Nutritionniste" -> "/acceuilnutritionniste.fxml";
                case "Coach" -> "/acceuilcoach.fxml";
                default -> "/acceuil.fxml";
            };

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de charger le tableau de bord", Alert.AlertType.ERROR);
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
