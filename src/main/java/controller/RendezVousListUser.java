package controller;

import entite.RendezVous;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import service.RendezVousService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class RendezVousListUser implements Initializable {

    @FXML private FlowPane cardLayout;
    @FXML private Button sortByDateButton;
    @FXML private Button sortByPriorityButton;
    @FXML private Button sortByStatusButton;
    @FXML private TextField searchField;
    @FXML private AnchorPane id_anchor;

    private final RendezVousService rendezVousService = new RendezVousService();
    private boolean isDateAscending = true;
    private boolean isPriorityAscending = true;
    private boolean isStatusAscending = true;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadRendezVous();
    }

    public void loadRendezVous() {
        try {
            List<RendezVous> list = rendezVousService.readAll();
            System.out.println("Loaded " + list.size() + " rendezvous");

            cardLayout.getChildren().clear();
            cardLayout.setHgap(20);
            cardLayout.setVgap(20);

            if (list.isEmpty()) {
                Label emptyLabel = new Label("Aucun rendez-vous trouvé");
                emptyLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
                cardLayout.getChildren().add(emptyLabel);
            } else {
                for (RendezVous rdv : list) {
                    try {
                        System.out.println("Loading card for: " + rdv.getNom());
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/RendezVousCardUser.fxml"));
                        Pane cardView = loader.load();
                        RendezVousCardUser controller = loader.getController();
                        controller.setRendezVousData(rdv);
                        cardLayout.getChildren().add(cardView);
                    } catch (IOException e) {
                        System.err.println("Error loading card for " + rdv.getNom());
                        e.printStackTrace();
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error loading rendezvous:");
            e.printStackTrace();
            Label errorLabel = new Label("Erreur de chargement des rendez-vous");
            errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
            cardLayout.getChildren().add(errorLabel);
        }
    }

    @FXML
    void sortRendezVousByDate(ActionEvent event) {
        try {
            List<RendezVous> list = rendezVousService.readAll();
            if (isDateAscending) {
                list.sort((r1, r2) -> r1.getDateEtHeure().compareTo(r2.getDateEtHeure()));
                sortByDateButton.setText("Sort by Date ↓");
            } else {
                list.sort((r1, r2) -> r2.getDateEtHeure().compareTo(r1.getDateEtHeure()));
                sortByDateButton.setText("Sort by Date ↑");
            }
            isDateAscending = !isDateAscending;
            updateRendezVousCards(list);
        } catch (SQLException e) {
            System.out.println("Error fetching rendezvous: " + e.getMessage());
        }
    }

    @FXML
    void sortRendezVousByPriority(ActionEvent event) {
        try {
            List<RendezVous> list = rendezVousService.readAll();
            if (isPriorityAscending) {
                list.sort((r1, r2) -> r1.getPriorite().compareTo(r2.getPriorite()));
                sortByPriorityButton.setText("Sort by Priority ↓");
            } else {
                list.sort((r1, r2) -> r2.getPriorite().compareTo(r1.getPriorite()));
                sortByPriorityButton.setText("Sort by Priority ↑");
            }
            isPriorityAscending = !isPriorityAscending;
            updateRendezVousCards(list);
        } catch (SQLException e) {
            System.out.println("Error fetching rendezvous: " + e.getMessage());
        }
    }
    @FXML
    private void handleShowRegimeButton(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AfficherRegime.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            System.out.println("Error loading regimes view: " + ex.getMessage());
        }
    }

    @FXML
    void sortRendezVousByStatus(ActionEvent event) {
        try {
            List<RendezVous> list = rendezVousService.readAll();
            if (isStatusAscending) {
                list.sort((r1, r2) -> r1.getEtat().compareTo(r2.getEtat()));
                sortByStatusButton.setText("Sort by Status ↓");
            } else {
                list.sort((r1, r2) -> r2.getEtat().compareTo(r1.getEtat()));
                sortByStatusButton.setText("Sort by Status ↑");
            }
            isStatusAscending = !isStatusAscending;
            updateRendezVousCards(list);
        } catch (SQLException e) {
            System.out.println("Error fetching rendezvous: " + e.getMessage());
        }
    }

    @FXML
    void searchRendezVous(ActionEvent event) {
        String searchText = searchField.getText().toLowerCase().trim();
        try {
            List<RendezVous> filtered = rendezVousService.readAll().stream()
                    .filter(r -> r.getNom().toLowerCase().contains(searchText) ||
                            r.getPrenom().toLowerCase().contains(searchText) ||
                            r.getDescription().toLowerCase().contains(searchText))
                    .collect(Collectors.toList());
            updateRendezVousCards(filtered);
        } catch (SQLException e) {
            System.out.println("Error fetching rendezvous: " + e.getMessage());
        }
    }

    private void updateRendezVousCards(List<RendezVous> list) {
        cardLayout.getChildren().clear();
        cardLayout.setHgap(20);
        cardLayout.setVgap(20);

        if (list.isEmpty()) {
            Label emptyLabel = new Label("Aucun résultat.");
            emptyLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
            cardLayout.getChildren().add(emptyLabel);
        } else {
            for (RendezVous rdv : list) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/RendezVousCardUser.fxml")); // Fix
                    Pane cardView = loader.load();
                    RendezVousCardUser controller = loader.getController(); // Fix
                    controller.setRendezVousData(rdv);
                    cardLayout.getChildren().add(cardView);
                } catch (IOException e) {
                    System.out.println("Error loading card for: " + rdv.getNom());
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    private void goBack(ActionEvent event) {
        loadNewScene(event, "/Acceuil.fxml");
    }
    @FXML
    private void handleRefreshButton(ActionEvent event) {
        // Recharge la liste des rendez-vous
        loadRendezVous();
    }

    @FXML
    void reload_page(ActionEvent event) {
        loadNewScene(event, "/MainMenu.fxml");
    }

    @FXML
    void viewCalendar(ActionEvent event) {
        loadNewScene(event, "/RendezVousCalendar.fxml");
    }

    @FXML
    void addRendezVous(ActionEvent event) {
        loadNewScene(event, "/AjouterRendezVous.fxml");
    }

    @FXML
    void showStatistics(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/RendezVousStatistics.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("RendezVous Statistics");
            stage.show();
        } catch (IOException e) {
            System.out.println("Error loading statistics view: " + e.getMessage());
        }
    }

    private void loadNewScene(ActionEvent event, String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.out.println("Error loading: " + fxmlPath);
            e.printStackTrace();
        }
    }

    public void interfaceAcceuil(ActionEvent actionEvent) {
        try {
            // Charger le fichier FXML de l'interface produit
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/acceuil.fxml"));
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

