package controller;

import entite.RendezVous;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import service.RendezVousService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class RendezVousCardUser {
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button showRegimeButton;

    @FXML
    private Label rdvNom;
    @FXML
    private Label rdvPrenom;
    @FXML
    private Label rdvDateHeure;
    @FXML
    private Label rdvType;
    @FXML
    private Label rdvPriorite;
    @FXML
    private Label rdvTelephone;
    @FXML
    private Label rdvTaillePoids;
    @FXML
    private Label rdvObjectif;
    @FXML
    private Label rdvEtat;
    @FXML
    private Label rdvDescription;
    @FXML
    private Label rdvDateDebut;
    @FXML
    private Label rdvDateFin;

    private RendezVous rendezVous;
    private RendezVousService rendezVousService = new RendezVousService();

    public void setRendezVousData(RendezVous rendezVous) {
        this.rendezVous = rendezVous;

        // Format the date and time
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        rdvNom.setText(rendezVous.getNom());
        rdvPrenom.setText(rendezVous.getPrenom());
        rdvDateHeure.setText(rendezVous.getDateEtHeure().format(dateTimeFormatter));
        rdvType.setText(rendezVous.getType());
        rdvPriorite.setText(rendezVous.getPriorite());
        rdvTelephone.setText(rendezVous.getNumTel());
        rdvTaillePoids.setText(rendezVous.getTaille() + "cm / " + rendezVous.getPoids() + "kg");
        rdvObjectif.setText(rendezVous.getObjectif());
        rdvEtat.setText(rendezVous.getEtat());
        rdvDescription.setText(rendezVous.getDescription());
        rdvDateDebut.setText(rendezVous.getDateDebut() != null ?
                rendezVous.getDateDebut().format(dateFormatter) : "Non définie");
        rdvDateFin.setText(rendezVous.getDateFin() != null ?
                rendezVous.getDateFin().format(dateFormatter) : "Non définie");
    }

    @FXML
    private void handleUpdateButton(ActionEvent event) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateRendezVous.fxml"));
            Parent root = loader.load();

            UpdateRendezVous controller = loader.getController();
            controller.setRendezVousData(rendezVous);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteButton(ActionEvent event) throws SQLException {
        rendezVousService.delete(rendezVous);
        // Refresh the parent view or remove this card
    }
    @FXML
    private void handleShowRegimeButton(ActionEvent event) {
        try {
            // Log pour s'assurer que le RendezVous est bien récupéré
            System.out.println("Attempting to show regimes for RendezVous ID: " + rendezVous.getId());

            // Charger la vue AfficherRegime.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherRegime1.fxml"));
            Parent root = loader.load();

            // Récupérer le controller de la vue
            AfficherRegime1Controller controller = loader.getController();

            // Passer l'ID du rendez-vous pour charger uniquement les régimes liés
            controller.loadRegimeForRendezVous(rendezVous.getId());

            // Créer et afficher la scène
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Régimes associés au rendez-vous");
            stage.show();

        } catch (IOException ex) {
            System.err.println("Error loading regime view:");
            ex.printStackTrace();
            showAlert("Erreur", "Impossible d'afficher les régimes : " + ex.getMessage());
        }
    }

    /*
    @FXML
    private void handleShowRegimeButton(ActionEvent event) {
        try {
            System.out.println("Attempting to show regime for RendezVous ID: " + rendezVous.getId());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherRegime.fxml"));
            Parent root = loader.load();

            AfficherRegimeController controller = loader.getController();
            controller.loadRegimeForRendezVous(rendezVous.getId().intValue());

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Détails du Régime");
            stage.show();
        } catch (IOException ex) {
            System.err.println("Error loading regime view:");
            ex.printStackTrace();
            showAlert("Erreur", "Impossible d'afficher le régime: " + ex.getMessage());
        }
    }

 */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}