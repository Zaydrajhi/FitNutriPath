package controller;

import entite.Regime;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import service.RegimeService;

import java.sql.SQLException;
import java.util.List;

public class RegimeController {

    @FXML private VBox contentVBox;
    private RegimeService regimeService = new RegimeService();

    @FXML
    public void initialize() {
        displayRegimes();
    }

    private void displayRegimes() {
        contentVBox.getChildren().clear();

        try {
            List<Regime> regimes = regimeService.readAll();

            for (Regime regime : regimes) {
                HBox card = createRegimeCard(regime);
                contentVBox.getChildren().add(card);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private HBox createRegimeCard(Regime regime) {
        Label nom = new Label("Nom : " + regime.getTitre());
        nom.setStyle("-fx-text-fill: black;");
        Label desc = new Label("Description : " + regime.getDescription());
        desc.setStyle("-fx-text-fill: black;");
        Label type = new Label("Type : " + regime.getObjectif());
        type.setStyle("-fx-text-fill: black;");

        VBox info = new VBox(nom, desc, type);
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
