package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import util.SessionManager;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import entite.User;

import java.io.IOException;

public class AcceuilNutritionnisteController {

    @FXML
    private Label activePatientsLabel;
    @FXML
    private Label todayAppointmentsLabel;
    @FXML
    private Label createdPlansLabel;
    @FXML
    private TableView<?> recentAppointmentsTable;
    @FXML
    private ImageView userImageView;
    @FXML
    private Label userNameLabel;

    @FXML
    public void initialize() {
        // Vérifier le rôle de l'utilisateur
        User currentUser = SessionManager.getCurrentUser();
        if (!currentUser.getRole().equals("Nutritionniste")) {
            showErrorAndRedirect();
            return;
        }

        // Initialiser les données de l'utilisateur
        setupUserInfo(currentUser);

        // Initialiser les données
        loadStatistics();
        loadRecentAppointments();
    }

    private void setupUserInfo(User user) {
        // Afficher le nom de l'utilisateur
        userNameLabel.setText("Bienvenue, " + user.getNom() + " " + user.getPrenom());
        
        // Charger l'image de l'utilisateur si elle existe
        if (user.getImage() != null && !user.getImage().isEmpty()) {
            try {
                Image image = new Image(user.getImage());
                userImageView.setImage(image);
                userImageView.setFitHeight(50);
                userImageView.setFitWidth(50);
            } catch (Exception e) {
                // Si l'image ne peut pas être chargée, utiliser une image par défaut
                userImageView.setImage(new Image(getClass().getResourceAsStream("/images/default_user.png")));
            }
        } else {
            // Utiliser une image par défaut si aucune image n'est définie
            userImageView.setImage(new Image(getClass().getResourceAsStream("/images/default_user.png")));
        }
    }

    private void loadStatistics() {
        // TODO: Implémenter le chargement des statistiques depuis la base de données
        activePatientsLabel.setText("0");
        todayAppointmentsLabel.setText("0");
        createdPlansLabel.setText("0");
    }

    private void loadRecentAppointments() {
        // TODO: Implémenter le chargement des rendez-vous récents depuis la base de données
    }

    @FXML
    private void handleLogout() {
        try {
            SessionManager.clearSession();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) activePatientsLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showErrorAndRedirect() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) activePatientsLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleProfil() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/profil.fxml"));
            Parent root = loader.load();
            
            // Récupérer le contrôleur et initialiser les données
            ProfilController profilController = loader.getController();
            profilController.initData(SessionManager.getCurrentUser());
            
            Stage stage = (Stage) activePatientsLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Mon Profil");
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de charger la page de profil", Alert.AlertType.ERROR);
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

    public void prendreRendezVous(ActionEvent event) {
        naviguerVersVue("/RendezVousList.fxml", "Mes Rendez-vous", event);
    }

    public void afficherMesRendezVous(ActionEvent event) {
        naviguerVersVue("/AfficherRegime.fxml", "Mes Régimes", event);
    }

    public void predictForFree(ActionEvent event) {
        naviguerVersVue("/predict.fxml", "Predict for Free", event);
    }

    private void naviguerVersVue(String fxmlPath, String title, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Get the stage from the event source
            Stage stage = (Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();

            // Set fixed size if needed
            stage.setScene(new Scene(root, 1200, 800));
            stage.setTitle(title);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            showAlert("Erreur de navigation", "Impossible de charger " + title + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void interfaceEvenement(ActionEvent actionEvent) {
        try {
            // Charger le fichier FXML de l'interface produit
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/afficherEvenementFront.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène
            Scene scene = new Scene(root);

            // Obtenir la fenêtre actuelle (stage)
            Stage stage = (Stage) ((javafx.scene.control.Button) actionEvent.getSource()).getScene().getWindow();

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeUser.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène
            Scene scene = new Scene(root);

            // Obtenir la fenêtre actuelle (stage)
            Stage stage = (Stage) ((javafx.scene.control.Button) actionEvent.getSource()).getScene().getWindow();

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