package controller;

import controller.modifierSalleController;
import entite.Abonnement;
import entite.SalleDeSport;
import service.SalleDeSportService;
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
import java.net.URL;
import java.util.List;

public class ListeSallesController {
    @FXML private TableView<SalleDeSport> tableViewSalles;
    @FXML private TableColumn<SalleDeSport, String> colNom;
    @FXML private TableColumn<SalleDeSport, String> colVille;
    @FXML private TableColumn<SalleDeSport, String> colRue;
    @FXML private TableColumn<SalleDeSport, String> colCodePostal;
    @FXML private TableColumn<SalleDeSport, String> colEmail;
    @FXML private TableColumn<SalleDeSport, Integer> colPrix;
    @FXML private TableColumn<SalleDeSport, Void> colActions;

    private final SalleDeSportService salleDeSportService = new SalleDeSportService();

    @FXML
    public void initialize() {
        // Configuration des colonnes
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colVille.setCellValueFactory(new PropertyValueFactory<>("ville"));
        colRue.setCellValueFactory(new PropertyValueFactory<>("rue"));
        colCodePostal.setCellValueFactory(new PropertyValueFactory<>("codePostal"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prixAbonnement"));

        // Configuration de la colonne Actions
        colActions.setCellFactory(new Callback<>() {
            @Override
            public TableCell<SalleDeSport, Void> call(final TableColumn<SalleDeSport, Void> param) {
                return new TableCell<>() {
                    private final HBox boutonsContainer = new HBox(5);
                    private final Button btnModifier = new Button("Modifier");
                    private final Button btnSupprimer = new Button("Supprimer");

                    {
                        btnModifier.setStyle("-fx-background-color: #00c6a9; -fx-text-fill: white; -fx-font-weight: bold;");
                        btnSupprimer.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white; -fx-font-weight: bold;");

                        btnModifier.setOnAction(event -> {
                            SalleDeSport salle = getTableView().getItems().get(getIndex());
                            ouvrirFenetreModification(salle);
                        });

                        btnSupprimer.setOnAction(event -> {
                            SalleDeSport salle = getTableView().getItems().get(getIndex());
                            supprimerSalle(salle);
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

        chargerSalles();
    }

    private void supprimerSalle(SalleDeSport salle) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer la salle \"" + salle.getNom() + "\" ?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Vérification null-safe des abonnements
                    List<Abonnement> abonnements = salle.getAbonnements();
                    if (abonnements != null && !abonnements.isEmpty()) {
                        Alert info = new Alert(Alert.AlertType.INFORMATION);
                        info.setTitle("Information");
                        info.setHeaderText(null);
                        info.setContentText(abonnements.size() + " abonnement(s) associé(s) seront également supprimés.");
                        info.showAndWait();
                    }

                    salleDeSportService.delete(salle);
                    afficherAlerte("Succès", "Salle et ses abonnements associés supprimés avec succès");
                    chargerSalles();
                } catch (Exception e) {
                    afficherAlerte("Erreur", "Erreur lors de la suppression : " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    private void ouvrirFenetreModification(SalleDeSport salle) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifierSalle.fxml"));
            Parent root = loader.load();

            modifierSalleController controller = loader.getController();
            controller.initData(salle);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.showAndWait();

            chargerSalles();
        } catch (IOException e) {
            afficherAlerte("Erreur", "Erreur lors de l'ouverture de la fenêtre de modification");
        }
    }

    private void chargerSalles() {
        ObservableList<SalleDeSport> salles = FXCollections.observableArrayList(salleDeSportService.readAll());
        tableViewSalles.setItems(salles);
    }

    @FXML
    private void handleRetour(ActionEvent event) {
        try {
            // Chargez la ressource en vérifiant d'abord qu'elle existe
            URL fxmlUrl = getClass().getResource("/ajouterSalleDeSport.fxml");
            if (fxmlUrl == null) {
                throw new IOException("Fichier FXML introuvable: /ajouterSalle.fxml");
            }

            Parent root = FXMLLoader.load(fxmlUrl);
            Stage stage = (Stage) tableViewSalles.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            afficherAlerte("Erreur", "Erreur lors du retour: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void interfaceEvennement(ActionEvent actionEvent) {

    }
}
