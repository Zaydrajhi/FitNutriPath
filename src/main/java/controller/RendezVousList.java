package controller;

import entite.RendezVous;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import service.RendezVousService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class RendezVousList implements Initializable {

    @FXML private FlowPane cardLayout;
    @FXML private Button sortByDateButton;
    @FXML private Button sortByPriorityButton;
    @FXML private Button goBack;
    @FXML private Button sortByStatusButton;
    @FXML private TextField searchField;
    @FXML private AnchorPane id_anchor;
    @FXML private Button Calendar;
    @FXML private DatePicker dateFilter;
    @FXML private ComboBox<String> statusFilter;
    @FXML private ComboBox<String> priorityFilter;
    @FXML private Label totalRendezVous;
    @FXML private Label completedRendezVous;
    @FXML private Label pendingRendezVous;
    @FXML private PieChart statusChart;
    @FXML private BarChart<String, Number> monthlyChart;

    private RendezVousService rendezVousService = new RendezVousService();
    private boolean isDateAscending = true;
    private boolean isPriorityAscending = true;
    private boolean isStatusAscending = true;
    private List<RendezVous> allRendezVous;
    private int num ;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            allRendezVous = rendezVousService.readAll();
            loadRendezVous();
            initializeFilters();
            updateStatistics();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initializeFilters() {
        // Initialize status filter
        statusFilter.getItems().addAll("Tous", "En attente", "accepté");
        statusFilter.setValue("Tous");

        // Initialize priority filter
        priorityFilter.getItems().addAll("Tous", "Basse", "Moyenne", "Haute");
        priorityFilter.setValue("Tous");
    }

    @FXML
    void sortRendezVousByDate(ActionEvent event) {
        List<RendezVous> list;
        try {
            list = rendezVousService.readAll();
        } catch (SQLException e) {
            System.out.println("Error fetching rendezvous: " + e.getMessage());
            return;
        }

        if (isDateAscending) {
            list.sort((r1, r2) -> r1.getDateEtHeure().compareTo(r2.getDateEtHeure()));
            sortByDateButton.setText("Sort by Date ↓");
        } else {
            list.sort((r1, r2) -> r2.getDateEtHeure().compareTo(r1.getDateEtHeure()));
            sortByDateButton.setText("Sort by Date ↑");
        }

        isDateAscending = !isDateAscending;
        updateRendezVousCards(list);
    }

    @FXML
    void sortRendezVousByPriority(ActionEvent event) {
        List<RendezVous> list;
        try {
            list = rendezVousService.readAll();
        } catch (SQLException e) {
            System.out.println("Error fetching rendezvous: " + e.getMessage());
            return;
        }

        if (isPriorityAscending) {
            list.sort((r1, r2) -> r1.getPriorite().compareTo(r2.getPriorite()));
            sortByPriorityButton.setText("Sort by Priority ↓");
        } else {
            list.sort((r1, r2) -> r2.getPriorite().compareTo(r1.getPriorite()));
            sortByPriorityButton.setText("Sort by Priority ↑");
        }

        isPriorityAscending = !isPriorityAscending;
        updateRendezVousCards(list);
    }

    @FXML
    void sortRendezVousByStatus(ActionEvent event) {
        List<RendezVous> list;
        try {
            list = rendezVousService.readAll();
        } catch (SQLException e) {
            System.out.println("Error fetching rendezvous: " + e.getMessage());
            return;
        }

        if (isStatusAscending) {
            list.sort((r1, r2) -> r1.getEtat().compareTo(r2.getEtat()));
            sortByStatusButton.setText("Sort by Status ↓");
        } else {
            list.sort((r1, r2) -> r2.getEtat().compareTo(r1.getEtat()));
            sortByStatusButton.setText("Sort by Status ↑");
        }

        isStatusAscending = !isStatusAscending;
        updateRendezVousCards(list);
    }

    @FXML
    void applyFilters(ActionEvent event) {
        try {
            List<RendezVous> filteredList = new ArrayList<>(allRendezVous);

            // Apply date filter
            if (dateFilter.getValue() != null) {
                LocalDate selectedDate = dateFilter.getValue();
                filteredList = filteredList.stream()
                    .filter(rdv -> rdv.getDateEtHeure().toLocalDate().equals(selectedDate))
                    .collect(Collectors.toList());
            }

            // Apply status filter
            String selectedStatus = statusFilter.getValue();
            if (!selectedStatus.equals("Tous")) {
                filteredList = filteredList.stream()
                    .filter(rdv -> rdv.getEtat().equals(selectedStatus))
                    .collect(Collectors.toList());
            }

            // Apply priority filter
            String selectedPriority = priorityFilter.getValue();
            if (!selectedPriority.equals("Tous")) {
                filteredList = filteredList.stream()
                    .filter(rdv -> rdv.getPriorite().equals(selectedPriority))
                    .collect(Collectors.toList());
            }

            updateRendezVousCards(filteredList);
            updateStatistics();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateStatistics() {
        try {
            // Update counters
            totalRendezVous.setText(String.valueOf(allRendezVous.size()));
            completedRendezVous.setText(String.valueOf(
                allRendezVous.stream().filter(rdv -> rdv.getEtat().equals("Terminé")).count()
            ));
            pendingRendezVous.setText(String.valueOf(
                allRendezVous.stream().filter(rdv -> rdv.getEtat().equals("En attente")).count()
            ));

            // Update status chart
            statusChart.getData().clear();
            Map<String, Long> statusCount = allRendezVous.stream()
                .collect(Collectors.groupingBy(RendezVous::getEtat, Collectors.counting()));
            
            statusCount.forEach((status, count) -> {
                PieChart.Data slice = new PieChart.Data(status, count);
                statusChart.getData().add(slice);
            });

            // Update monthly chart
            monthlyChart.getData().clear();
            Map<Month, Long> monthlyCount = allRendezVous.stream()
                .collect(Collectors.groupingBy(
                    rdv -> rdv.getDateEtHeure().getMonth(),
                    Collectors.counting()
                ));

            // Create a new series for the bar chart
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Rendez-vous par mois");

            monthlyCount.forEach((month, count) -> {
                series.getData().add(new XYChart.Data<>(month.toString(), count));
            });

            monthlyChart.getData().add(series);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadRendezVous() {
        cardLayout.getChildren().clear();
        cardLayout.setHgap(20);
        cardLayout.setVgap(20);

        if (allRendezVous.isEmpty()) {
            Label emptyLabel = new Label("Aucun rendez-vous trouvé");
            emptyLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 16px;");
            cardLayout.getChildren().add(emptyLabel);
        } else {
            for (RendezVous rdv : allRendezVous) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/RendezVousCard.fxml"));
                    Pane cardView = loader.load();
                    RendezVousCard controller = loader.getController();
                    controller.setRendezVousData(rdv);
                    cardLayout.getChildren().add(cardView);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    void searchRendezVous(ActionEvent event) {
        String searchText = searchField.getText().toLowerCase().trim();
        
        List<RendezVous> filteredList = allRendezVous.stream()
            .filter(r -> r.getNom().toLowerCase().contains(searchText) ||
                    r.getPrenom().toLowerCase().contains(searchText) ||
                    r.getDescription().toLowerCase().contains(searchText))
            .collect(Collectors.toList());

        updateRendezVousCards(filteredList);
    }

    private void updateRendezVousCards(List<RendezVous> list) {
        cardLayout.getChildren().clear();
        cardLayout.setHgap(20);
        cardLayout.setVgap(20);

        if (list.isEmpty()) {
            Label emptyLabel = new Label("Aucun résultat trouvé");
            emptyLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 16px;");
            cardLayout.getChildren().add(emptyLabel);
        } else {
            for (RendezVous rdv : list) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/RendezVousCard.fxml"));
                    Pane cardView = loader.load();
                    RendezVousCard controller = loader.getController();
                    controller.setRendezVousData(rdv);
                    cardLayout.getChildren().add(cardView);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    private void goBack(ActionEvent event) {
        try {
            // Load the SessionList.fxml file
            Parent root = FXMLLoader.load(getClass().getResource("/Acceuil.fxml"));

            // Get the current stage from the event source
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the scene to the SessionList.fxml
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.out.println("Error loading SessionList: " + e.getMessage());
        }
    }

    @FXML
    void reload_page(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/MainMenu.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @FXML
    void viewRegimes(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AfficherRegime.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Mes Régimes");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'afficher les régimes : " + e.getMessage());
        }
    }

    @FXML
    void addRendezVous(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AjouterRendezVous.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void showStatistics(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/RendezVousStatistics.fxml"));
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("RendezVous Statistics");
            stage.show();
        } catch (IOException ex) {
            System.out.println("Error loading statistics view: " + ex.getMessage());
        }
    }

    @FXML
    void viewCalendar(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/RendezVousCalendar.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleRefreshButtonn(ActionEvent event) {
        try {
            allRendezVous = rendezVousService.readAll();
            loadRendezVous();
            updateStatistics();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void interfaceNutritionniste(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Acceuil.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
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
}
