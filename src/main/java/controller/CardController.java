package controller;
import entite.RendezVous;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import java.time.format.DateTimeFormatter;

public class CardController {
    @FXML private VBox cardContainer;
    @FXML private Label lblNomPrenom;
    @FXML private Label lblType;
    @FXML private Label lblDateTime;
    @FXML private Label lblEtat;
    @FXML private Label lblDetails;
    @FXML private HBox priorityBox;
    @FXML private Rectangle colorIndicator;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    public void setRendezVous(RendezVous rdv) {
        updateCardData(rdv);
        styleCard(rdv);
        setupAnimations();
    }

    private void updateCardData(RendezVous rdv) {
        lblNomPrenom.setText(rdv.getPrenom() + " " + rdv.getNom());
        lblType.setText(rdv.getType().toUpperCase());
        lblDateTime.setText(rdv.getDateEtHeure().format(DATE_FORMAT));
        lblEtat.setText(rdv.getEtat().toUpperCase());

        String details = String.format("%d cm | %d kg | %s",
                rdv.getTaille(), rdv.getPoids(), rdv.getObjectif());
        lblDetails.setText(details);
    }

    private void styleCard(RendezVous rdv) {
        String priorityColor = "urgence".equals(rdv.getPriorite()) ?
                "linear-gradient(to right, #ff416c, #ff4b2b)" :
                "linear-gradient(to right, #4776E6, #8E54E9)";

        cardContainer.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2);");

        colorIndicator.setFill(Color.web(priorityColor.equals("urgence") ? "#ff4b2b" : "#4776E6"));
        priorityBox.setStyle("-fx-background-color: " + priorityColor + "; " +
                "-fx-background-radius: 5 5 0 0;");
    }


    private void setupAnimations() {
        // Animation d'entrée
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(300), cardContainer);
        scaleIn.setFromX(0.95); scaleIn.setFromY(0.95);
        scaleIn.setToX(1); scaleIn.setToY(1);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), cardContainer);
        fadeIn.setFromValue(0); fadeIn.setToValue(1);

        ParallelTransition enterTransition = new ParallelTransition(scaleIn, fadeIn);
        enterTransition.play();

        // Animation au survol
        cardContainer.setOnMouseEntered(e -> {
            ScaleTransition hoverScale = new ScaleTransition(Duration.millis(200), cardContainer);
            hoverScale.setToX(1.02); hoverScale.setToY(1.02);
            hoverScale.play();
        });

        cardContainer.setOnMouseExited(e -> {
            ScaleTransition hoverScale = new ScaleTransition(Duration.millis(200), cardContainer);
            hoverScale.setToX(1); hoverScale.setToY(1);
            hoverScale.play();
        });
    }
}