package controller;

import controller.modifierAbonnementController;
import entite.Abonnement; // Assurez-vous que la classe Abonnement est correctement définie
import service.AbonnementService; // Service pour les abonnements
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;

public class ListeAbonnementsController {
    @FXML private TableView<Abonnement> tableViewAbonnements;
    @FXML private TableColumn<Abonnement, String> colNom;
    @FXML private TableColumn<Abonnement, String> colPrenom;
    @FXML private TableColumn<Abonnement, String> colTelephone;
    @FXML private TableColumn<Abonnement, String> colEmail;
    @FXML private TableColumn<Abonnement, String> colSalle;
    @FXML private TableColumn<Abonnement, String> colDateDebut;
    @FXML private TableColumn<Abonnement, String> colDateFin;
    @FXML private TableColumn<Abonnement, Void> colActions;

    private final AbonnementService abonnementService = new AbonnementService();

    @FXML
    public void initialize() {
        // Configuration des colonnes
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colTelephone.setCellValueFactory(new PropertyValueFactory<>("numeroTlfn"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colSalle.setCellValueFactory(new PropertyValueFactory<>("salleDeSport"));
        colDateDebut.setCellValueFactory(new PropertyValueFactory<>("dateDeb"));
        colDateFin.setCellValueFactory(new PropertyValueFactory<>("dateFin"));

        // Configuration de la colonne Actions
        colActions.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Abonnement, Void> call(final TableColumn<Abonnement, Void> param) {
                return new TableCell<>() {
                    private final HBox boutonsContainer = new HBox(5);
                    private final Button btnModifier = new Button("Modifier");
                    private final Button btnSupprimer = new Button("Supprimer");

                    {
                        btnModifier.setStyle("-fx-background-color: #00c6a9; -fx-text-fill: white; -fx-font-weight: bold;");
                        btnSupprimer.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white; -fx-font-weight: bold;");

                        btnModifier.setOnAction(event -> {
                            Abonnement abn = getTableView().getItems().get(getIndex());
                            ouvrirFenetreModification(abn);
                        });

                        btnSupprimer.setOnAction(event -> {
                            Abonnement abn = getTableView().getItems().get(getIndex());
                            supprimerAbonnement(abn);
                        });

                        boutonsContainer.getChildren().addAll(btnModifier, btnSupprimer);
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(boutonsContainer);
                        }
                    }
                };
            }
        });

        chargerAbonnements();
    }

    private void supprimerAbonnement(Abonnement abn) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer l'abonnement de \"" + abn.getNom() + " " + abn.getPrenom() + "\" ?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    abonnementService.delete(abn);
                    afficherAlerte("Succès", "Abonnement supprimé avec succès");
                    chargerAbonnements();
                } catch (Exception e) {
                    afficherAlerte("Erreur", "Erreur lors de la suppression : " + e.getMessage());
                }
            }
        });
    }

    private void ouvrirFenetreModification(Abonnement abn) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifierAbonnement.fxml"));
            Parent root = loader.load();

            modifierAbonnementController controller = loader.getController();
            controller.initData(abn);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.showAndWait();

            chargerAbonnements();
        } catch (IOException e) {
            afficherAlerte("Erreur", "Erreur lors de l'ouverture de la fenêtre de modification");
        }
    }

    private void chargerAbonnements() {
        ObservableList<Abonnement> abonnements = FXCollections.observableArrayList(abonnementService.readAll());
        tableViewAbonnements.setItems(abonnements);
    }

    @FXML
    private void handleRetour(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AjouterAbonnement.fxml"));
            Stage stage = (Stage) tableViewAbonnements.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            afficherAlerte("Erreur", "Erreur lors du retour à l'interface précédente");
        }
    }

    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
