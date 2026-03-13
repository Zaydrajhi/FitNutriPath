package controller;

import entite.RendezVous;
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
import service.RendezVousService;

import java.sql.SQLException;
import java.util.List;

public class AfficherRendezVousBack {

    @FXML
    private GridPane gridPaneRendezVous;

    private final RendezVousService rendezVousService = new RendezVousService();

    @FXML
    public void initialize() {
        afficher();  // Appelé automatiquement après le chargement du FXML
    }

    public void afficher() {
        try {
            List<RendezVous> rendezVousList = rendezVousService.readAll();

            gridPaneRendezVous.getChildren().clear();  // Vide le grid s'il y a des anciens nœuds.

            int column = 0;
            int row = 0;

            for (RendezVous rendezVous : rendezVousList) {
                VBox card = createRendezVousCard(rendezVous);

                gridPaneRendezVous.add(card, column, row);

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

    private VBox createRendezVousCard(RendezVous rendezVous) {
        VBox card = new VBox();
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 15;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 4);");
        card.setSpacing(8);
        card.setPrefWidth(250);

        Label nomLabel = new Label(rendezVous.getNom() + " " + rendezVous.getPrenom());
        nomLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");

        Label descriptionLabel = new Label(rendezVous.getDescription());
        descriptionLabel.setWrapText(true);

        Label dateLabel = new Label("Date et Heure : " + rendezVous.getDateEtHeure());
        Label objectifLabel = new Label("Objectif : " + rendezVous.getObjectif());
        Label prioriteLabel = new Label("Priorité : " + rendezVous.getPriorite());

        card.getChildren().addAll(nomLabel, descriptionLabel, dateLabel, objectifLabel, prioriteLabel);

        return card;
    }

    public void interfacePlanSportif(ActionEvent actionEvent) {
        try {
            // Charger le fichier FXML de l'interface produit
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterSalleDeSport.fxml"));
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

    public void interfaceProduits(ActionEvent actionEvent) {
        try {
            // Charger le fichier FXML de l'interface produit
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterProduit.fxml"));
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

    public void interfaceRegime(ActionEvent actionEvent) {
        try {
            // Charger le fichier FXML de l'interface produit
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RegimeBack.fxml"));
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

    public void interfaceEvenement(ActionEvent actionEvent) {
        try {
            // Charger le fichier FXML de l'interface produit
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterEvenement.fxml"));
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
