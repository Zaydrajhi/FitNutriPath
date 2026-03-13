package controller;

import entite.RendezVous;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import service.RendezVousService;

import java.sql.SQLException;
import java.util.List;

public class RendezVousController {

    @FXML private VBox contentVBox;
    private RendezVousService rendezVousService = new RendezVousService();

    @FXML
    public void initialize() {
        displayRendezVous();
    }

    private void displayRendezVous() {
        contentVBox.getChildren().clear();

        try {
            List<RendezVous> rendezVousList = rendezVousService.readAll();

            for (RendezVous rdv : rendezVousList) {
                HBox card = createRdvCard(rdv);
                contentVBox.getChildren().add(card);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private HBox createRdvCard(RendezVous rdv) {
        Label titre = new Label("Rendez-vous de : " + rdv.getNom());
        titre.setStyle("-fx-text-fill: black;");
        Label date = new Label("Date : " + rdv.getDateEtHeure());
        date.setStyle("-fx-text-fill: black;");
        Label lieu = new Label("État : " + rdv.getEtat());
        lieu.setStyle("-fx-text-fill: black;");

        VBox info = new VBox(titre, date, lieu);
        info.setSpacing(5);
        info.setPadding(new Insets(10));

        HBox card = new HBox(info);
        card.setSpacing(10);
        card.setPadding(new Insets(15));
        card.setStyle(
                "-fx-background-color: rgba(255,255,255,0.7); " +
                        "-fx-border-color: #00c6a9; " +
                        "-fx-border-radius: 15; " +
                        "-fx-background-radius: 15;"
        );
        card.setEffect(new DropShadow());

        return card;
    }
}