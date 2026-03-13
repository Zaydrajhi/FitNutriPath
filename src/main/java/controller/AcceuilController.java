package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import util.SessionManager;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import entite.User;

import java.io.IOException;

public class AcceuilController {

    @FXML
    private Label bmiLabel;
    @FXML
    private Label weightGoalLabel;
    @FXML
    private Label progressLabel;
    @FXML
    private TableView<?> upcomingAppointmentsTable;
    @FXML
    private TableView<?> activePlansTable;
    @FXML
    private ImageView userImageView;
    @FXML
    private Label userNameLabel;

    @FXML
    public void initialize() {
        // Vérifier le rôle de l'utilisateur
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser.getRole().equals("Admin") ||
            currentUser.getRole().equals("Nutritionniste") ||
            currentUser.getRole().equals("Coach")) {
            showErrorAndRedirect();
            return;
        }

        // Initialiser les données de l'utilisateur
        setupUserInfo(currentUser);

        // Initialiser les données
        loadUserData();
        loadUpcomingAppointments();
        loadActivePlans();
    }

    private void setupUserInfo(User user) {
        userNameLabel.setText(user.getNom() + " " + user.getPrenom());
        if (user.getImage() != null && !user.getImage().isEmpty()) {
            userImageView.setImage(new Image(user.getImage()));
        }
    }

    private void loadUserData() {
        // TODO: Implémenter le chargement des données utilisateur depuis la base de données
        bmiLabel.setText("0");
        weightGoalLabel.setText("0 kg");
        progressLabel.setText("0%");
    }

    private void loadUpcomingAppointments() {
        // TODO: Implémenter le chargement des rendez-vous à venir depuis la base de données
    }

    private void loadActivePlans() {
        // TODO: Implémenter le chargement des plans actifs depuis la base de données
    }

    @FXML
    private void handleLogout() {
        try {
            SessionManager.clearSession();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) bmiLabel.getScene().getWindow();
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
            
            Stage stage = (Stage) bmiLabel.getScene().getWindow();
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

    private void showErrorAndRedirect() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) bmiLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void prendreRendezVous(ActionEvent event) {
        naviguerVersVue("/AjouterRendezVous.fxml", "Prendre Rendez-vous", event);
    }

    public void afficherMesRendezVous(ActionEvent event) {
        naviguerVersVue("/RendezVouslistUser.fxml", "Mes Rendez-vous", event);
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
            stage.setScene(new Scene(root, 800, 800));
            stage.setTitle(title);
            stage.centerOnScreen();

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

    public void interfaceProduits(ActionEvent actionEvent) {
        try {
            // Charger le fichier FXML de l'interface produit
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/listeProduitFront.fxml"));
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


    public void interfaceAjouterAbonnement(ActionEvent actionEvent) {
        try {
            // Charger le fichier FXML de l'interface produit
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterAbonnement.fxml"));
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
