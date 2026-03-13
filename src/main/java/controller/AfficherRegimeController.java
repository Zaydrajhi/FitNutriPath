package controller;

import entite.Regime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import service.RegimeService;

import java.io.IOException;
import java.util.List;

public class AfficherRegimeController {

    @FXML
    private TableView<Regime> regimeTable;
    @FXML
    private TableColumn<Regime, String> titreCol;
    @FXML
    private TableColumn<Regime, String> descriptionCol;
    @FXML
    private TableColumn<Regime, String> objectifCol;
    @FXML
    private TableColumn<Regime, Integer> dureeCol;
    @FXML
    private TableColumn<Regime, Integer> caloriesCol;
    @FXML
    private TableColumn<Regime, String> activiteCol;
    @FXML
    private TableColumn<Regime, Void> actionsCol;

    private final RegimeService regimeService = new RegimeService();
    private final ObservableList<Regime> regimeList = FXCollections.observableArrayList();

    private int currentRendezVousId = -1;  // Valeur par défaut si pas encore de rdv.

    @FXML
    public void initialize() {
        // Initialisation colonnes
        titreCol.setCellValueFactory(new PropertyValueFactory<>("titre"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        objectifCol.setCellValueFactory(new PropertyValueFactory<>("objectif"));
        dureeCol.setCellValueFactory(new PropertyValueFactory<>("duree"));
        caloriesCol.setCellValueFactory(new PropertyValueFactory<>("caloriesCible"));
        activiteCol.setCellValueFactory(new PropertyValueFactory<>("niveauActivite"));

        addActionButtons();

        // Charger tous les régimes par défaut
        loadAllRegimes();
    }

    /**
     * Méthode appelée par le contrôleur parent pour charger les régimes liés à un rendez-vous spécifique.
     */
    public void loadRegimeForRendezVous(int rdvId) {
        this.currentRendezVousId = rdvId;
        try {
            regimeList.clear();
            List<Regime> regimes = regimeService.getRegimesByRendezVous(rdvId);
            regimeList.addAll(regimes);
            regimeTable.setItems(regimeList);
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de charger les régimes liés : " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    /**
     * Chargement complet si aucune restriction.
     */
    private void loadAllRegimes() {
        try {
            regimeList.clear();
            regimeList.addAll(regimeService.readAll());
            regimeTable.setItems(regimeList);
        } catch (Exception e) {
            showAlert("Erreur", "Échec du chargement : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void addActionButtons() {
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("Modifier");
            private final Button deleteBtn = new Button("Supprimer");

            {
                editBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

                editBtn.setOnAction(event -> {
                    Regime regime = getTableView().getItems().get(getIndex());
                    editRegime(regime);
                });

                deleteBtn.setOnAction(event -> {
                    Regime regime = getTableView().getItems().get(getIndex());
                    deleteRegime(regime);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(new HBox(5, editBtn, deleteBtn));
                }
            }
        });
    }

    private void editRegime(Regime regime) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditRegime.fxml"));
            Parent root = loader.load();

            EditRegimeController controller = loader.getController();
            controller.setRegime(regime);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier Régime");
            stage.showAndWait();

            // Refresh après édition
            if (currentRendezVousId != -1) {
                loadRegimeForRendezVous(currentRendezVousId);
            } else {
                loadAllRegimes();
            }
        } catch (Exception e) {
            showAlert("Erreur", "Impossible d'ouvrir le formulaire : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void deleteRegime(Regime regime) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation suppression");
        confirm.setHeaderText("Suppression d'un régime");
        confirm.setContentText("Voulez-vous vraiment supprimer : '" + regime.getTitre() + "' ?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    regimeService.delete(regime);
                    regimeList.remove(regime);
                    showAlert("Succès", "Régime supprimé avec succès.", Alert.AlertType.INFORMATION);
                } catch (Exception e) {
                    showAlert("Erreur", "Suppression échouée : " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleAddNew() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddRegime.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter un nouveau régime");
            stage.showAndWait();

            if (currentRendezVousId != -1) {
                loadRegimeForRendezVous(currentRendezVousId);
            } else {
                loadAllRegimes();
            }
        } catch (Exception e) {
            showAlert("Erreur", "Impossible d'ouvrir le formulaire d'ajout : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleClose() {
        regimeTable.getScene().getWindow().hide();
    }

    @FXML
    private void handleBackToRendezVous() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/RendezVousList.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) regimeTable.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            showAlert("Erreur", "Impossible de retourner à la liste des rendez-vous : " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
