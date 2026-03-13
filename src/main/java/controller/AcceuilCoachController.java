package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import util.SessionManager;
import javafx.scene.control.Alert;

import java.io.IOException;

public class AcceuilCoachController {

    @FXML
    private Label activeClientsLabel;
    @FXML
    private Label todaySessionsLabel;
    @FXML
    private Label createdProgramsLabel;
    @FXML
    private TableView<?> recentSessionsTable;

    @FXML
    public void initialize() {
        // Vérifier le rôle de l'utilisateur
        if (!SessionManager.getCurrentUser().getRole().equals("Coach")) {
            showErrorAndRedirect();
            return;
        }

        // Initialiser les données
        loadStatistics();
        loadRecentSessions();
    }

    private void loadStatistics() {
        // TODO: Implémenter le chargement des statistiques depuis la base de données
        activeClientsLabel.setText("0");
        todaySessionsLabel.setText("0");
        createdProgramsLabel.setText("0");
    }

    private void loadRecentSessions() {
        // TODO: Implémenter le chargement des séances récentes depuis la base de données
    }

    @FXML
    private void handleLogout() {
        try {
            SessionManager.clearSession();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) activeClientsLabel.getScene().getWindow();
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
            
            Stage stage = (Stage) activeClientsLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Mon Profil");
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de charger la page de profil", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void showErrorAndRedirect() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) activeClientsLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
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