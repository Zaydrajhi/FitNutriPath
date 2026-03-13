package controller;

import entite.RendezVous;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import service.RendezVousService;

import java.sql.SQLException;
import java.util.List;

public class RdvController {

    @FXML
    private VBox rdvContainer;

    private RendezVousService rendezVousService = new RendezVousService();

    @FXML
    public void initialize() {
        try {
            List<RendezVous> rendezVousList = rendezVousService.readAll();
            for (RendezVous rdv : rendezVousList) {
                Label label = new Label(rdv.toString());
                rdvContainer.getChildren().add(label);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
