package controller;

import entite.RendezVous;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import service.RendezVousService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class RendezVousCard {
    @FXML
    private Button deleteButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button addRegimeButton;

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
        try {
            // Supprimer les régimes associés au rendez-vous
            rendezVousService.deleteRendezVousWithRegimes(rendezVous.getId());

            // Afficher une alerte pour confirmer la suppression
            showAlert("Succès", "Rendez-vous et ses régimes associés supprimés avec succès.");

            // Optionnel : Fermer la fenêtre ou rafraîchir la vue parent
            // Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // stage.close(); // Si tu veux fermer la fenêtre actuelle

        } catch (SQLException e) {
            // Gérer l'erreur de suppression
            showAlert("Erreur", "Une erreur est survenue lors de la suppression du rendez-vous : " + e.getMessage());
        }
    }

    @FXML
    private void handleAddRegimeButton(ActionEvent event) throws IOException {
        try {
            // Changer l'état du rendez-vous à "accepté"
            rendezVous.setEtat("accepté");

            // Mettre à jour le rendez-vous dans la base de données
            rendezVousService.update(rendezVous);

            // Ouvrir directement la fenêtre d'ajout de régime dans la même fenêtre
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterregime.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur de la vue AjouterRegime
            AjouterRegimeController controller = loader.getController();

            // Passer l'ID du RendezVous sélectionné
            controller.setRendezVousId(rendezVous.getId());

            // Remplacer la scène actuelle par la nouvelle scène
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter un Régime pour " + rendezVous.getNom() + " " + rendezVous.getPrenom());
            stage.show();

        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("Erreur", "Impossible de traiter le rendez-vous : " + ex.getMessage());
        }
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}