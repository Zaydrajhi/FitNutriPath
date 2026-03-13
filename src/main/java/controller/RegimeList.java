package Controller;
import entite.Regime;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import service.RegimeService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
public class RegimeList {
    @FXML private FlowPane cardlayout;
    @FXML private Button sortByDureeButton;
    @FXML private Button sortByCaloriesButton;
    @FXML private TextField searchField;
    @FXML private AnchorPane id_anchor;

    private RegimeService regimeService = new RegimeService();
    private boolean isDureeAscending = true;
    private boolean isCaloriesAscending = true;



    public void loadRegimes() {
        List<Regime> list;
        try {
            list = regimeService.readAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        cardlayout.getChildren().clear();
        cardlayout.setHgap(20);
        cardlayout.setVgap(20);

        if (list.isEmpty()) {
            System.out.println("Regime list is empty");
        } else {
            for (Regime regime : list) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/RegimeCard.fxml"));
                    Pane cardView = loader.load();
                    CardController controller = loader.getController();
                    controller.setRegimeData(regime);
                    cardlayout.getChildren().add(cardView);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    @FXML
    private void sortRegimesByDuree(ActionEvent event) {
        List<Regime> list;
        try {
            list = regimeService.readAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        if (isDureeAscending) {
            list.sort((r1, r2) -> Integer.compare(r1.getDuree(), r2.getDuree()));
            sortByDureeButton.setText("Sort by Duration ↓");
        } else {
            list.sort((r1, r2) -> Integer.compare(r2.getDuree(), r1.getDuree()));
            sortByDureeButton.setText("Sort by Duration ↑");
        }

        isDureeAscending = !isDureeAscending;
        updateRegimeCards(list);
    }

    @FXML
    private void sortRegimesByCalories(ActionEvent event) {
        List<Regime> list;
        try {
            list = regimeService.readAll();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        if (isCaloriesAscending) {
            list.sort((r1, r2) -> Integer.compare(r1.getCaloriesCible(), r2.getCaloriesCible()));
            sortByCaloriesButton.setText("Sort by Calories ↓");
        } else {
            list.sort((r1, r2) -> Integer.compare(r2.getCaloriesCible(), r1.getCaloriesCible()));
            sortByCaloriesButton.setText("Sort by Calories ↑");
        }

        isCaloriesAscending = !isCaloriesAscending;
        updateRegimeCards(list);
    }

    @FXML
    private void searchRegime(ActionEvent event) {
        String searchText = searchField.getText().toLowerCase().trim();
        List<Regime> filteredRegimes;

        try {
            filteredRegimes = regimeService.readAll().stream()
                    .filter(r -> r.getTitre().toLowerCase().contains(searchText) ||
                            r.getObjectif().toLowerCase().contains(searchText))
                    .collect(Collectors.toList());
            updateRegimeCards(filteredRegimes);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void reload_page(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/MainView.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) id_anchor.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @FXML
    private void addRegime(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AjouterRegime.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) id_anchor.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @FXML
    private void showStatistics(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/RegimeStatistics.fxml"));
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Regime Statistics");
            stage.show();
        } catch (IOException ex) {
            System.out.println("Error loading statistics view: " + ex.getMessage());
        }
    }

    private void updateRegimeCards(List<Regime> list) {
        cardlayout.getChildren().clear();
        cardlayout.setHgap(20);
        cardlayout.setVgap(20);

        for (Regime regime : list) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/RegimeCard.fxml"));
                Pane cardView = loader.load();
                CardController controller = loader.getController();
                controller.setRendezVous(RendezVousCard);
                cardlayout.getChildren().add(cardView);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }}