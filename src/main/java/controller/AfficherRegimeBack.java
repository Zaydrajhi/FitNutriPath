package controller;

import entite.Regime;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import service.RegimeService;

import java.sql.SQLException;
import java.util.List;

public class AfficherRegimeBack {

    @FXML
    private GridPane gridPaneEvents;

    private final RegimeService regimeService = new RegimeService();

    @FXML
    public void initialize() {
        afficher();  // Appelé automatiquement après le chargement du FXML
    }

    public void afficher() {
        try {
            List<Regime> regimes = regimeService.readAll();



            int column = 0;
            int row = 0;

            for (Regime regime : regimes) {
                VBox card = createRegimeCard(regime);

                gridPaneEvents.add(card, column, row);

                column++;
                if (column == 3) { // 3 cards par ligne
                    column = 0;
                    row++;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private VBox createRegimeCard(Regime regime) {
        VBox card = new VBox();
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 15;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 4);");
        card.setSpacing(8);
        card.setPrefWidth(250);

        Label titreLabel = new Label(regime.getTitre());
        titreLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");

        Label descriptionLabel = new Label(regime.getDescription());
        descriptionLabel.setWrapText(true);

        Label objectifLabel = new Label("Objectif : " + regime.getObjectif());
        Label caloriesLabel = new Label("Calories : " + regime.getCaloriesCible() + " kcal");
        Label niveauLabel = new Label("Niveau : " + regime.getNiveauActivite());

        card.getChildren().addAll(titreLabel, descriptionLabel, objectifLabel, caloriesLabel, niveauLabel);

        return card;
    }

    public void interfaceUser(ActionEvent actionEvent) {
        try {
            // Charger le fichier FXML de l'interface produit
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/listeUser.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène
            Scene scene = new Scene(root);

            // Obtenir la fenêtre actuelle (stage)
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();

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
