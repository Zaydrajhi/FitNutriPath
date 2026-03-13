package controller;

import entite.Regime;
import entite.RendezVous;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.scene.control.ScrollPane;
import javafx.util.Duration;
import service.RegimeService;
import service.RendezVousService;

import java.sql.SQLException;
import java.util.List;

public class Dashboardadmin {

    @FXML private VBox contentVBox;
    @FXML private Button btnRdv;
    @FXML private Button btnRegimes;

    private RendezVousService rendezVousService = new RendezVousService();
    private RegimeService regimeService = new RegimeService();

    @FXML
    private void initialize() {
        btnRdv.setOnAction(event -> displayRendezVous());
        btnRegimes.setOnAction(event -> displayRegimes());
    }

    private void displayRendezVous() {
        contentVBox.getChildren().clear();

        // Titre section avec couleur noire
        Label title = new Label("📅 Liste des Rendez-vous");
        title.setStyle("-fx-font-size: 22px; -fx-text-fill: black; -fx-font-weight: bold;");
        contentVBox.getChildren().add(title);

        try {
            List<RendezVous> rendezVousList = rendezVousService.readAll();

            for (RendezVous rdv : rendezVousList) {
                HBox card = createRdvCard(rdv);
                contentVBox.getChildren().add(card);
            }

            animateContent();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void displayRegimes() {
        contentVBox.getChildren().clear();

        // Titre section avec couleur noire
        Label title = new Label("🥗 Liste des Régimes");
        title.setStyle("-fx-font-size: 22px; -fx-text-fill: black; -fx-font-weight: bold;");
        contentVBox.getChildren().add(title);

        try {
            List<Regime> regimes = regimeService.readAll();

            for (Regime regime : regimes) {
                HBox card = createRegimeCard(regime);
                contentVBox.getChildren().add(card);
            }

            animateContent();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private HBox createRdvCard(RendezVous rdv) {
        Label titre = new Label("Rendez-vous de : " + rdv.getNom());
        titre.setStyle("-fx-text-fill: black;");  // Changer la couleur en noir
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

    private HBox createRegimeCard(Regime regime) {
        Label nom = new Label("Nom : " + regime.getTitre());
        nom.setStyle("-fx-text-fill: black;");  // Changer la couleur en noir
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

    private void animateContent() {
        FadeTransition ft = new FadeTransition(Duration.millis(500), contentVBox);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    @FXML
    public void addScrollPane() {
        // Wrap the contentVBox inside a ScrollPane
        ScrollPane scrollPane = new ScrollPane(contentVBox);
        scrollPane.setFitToWidth(true);  // Make the ScrollPane fill the width of the container
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Scrollbar appears if needed
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Disable horizontal scrolling
        scrollPane.setPrefHeight(Region.USE_COMPUTED_SIZE); // Allow it to adjust its height dynamically
        contentVBox.getChildren().clear();  // Clear existing content
        contentVBox.getChildren().add(scrollPane); // Add the scrollPane to your container
    }

}
