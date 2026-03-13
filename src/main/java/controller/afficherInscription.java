package controller;

import entite.Evenement;
import entite.Inscription;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import service.EvenementService;
import service.InscriptionService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class afficherInscription {
    @FXML
    private Button btnEvenements;
    @FXML
    private Button btnWorkouts;
    @FXML
    private Button btnProduits;
    @FXML
    private HBox headerHBox;
    @FXML
    private ImageView logo_menu;
    @FXML
    private MenuButton btnForum;
    @FXML
    private VBox detailsContainer;
    @FXML
    private ListView<Inscription> listeInscription;

    private InscriptionService inscriptionService;
    private EvenementService evenementService;
    private Map<Integer, String> evenementTitles;

    // Formateur pour afficher la date
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    @FXML
    private Button btnRetourner;

    public afficherInscription() {
        this.inscriptionService = new InscriptionService();
        this.evenementService = new EvenementService();
        this.evenementTitles = new HashMap<>();
        System.out.println("Contrôleur afficherInscription initialisé.");
    }

    @FXML
    public void initialize() {
        System.out.println("Méthode initialize de afficherInscription appelée.");
        if (listeInscription == null) {
            System.out.println("Erreur : listeInscription est null. Vérifiez fx:id dans afficherInscriptionFront.fxml.");
            return;
        }

        listeInscription.setCellFactory(listView -> new ListCell<Inscription>() {
            @Override
            protected void updateItem(Inscription inscription, boolean empty) {
                super.updateItem(inscription, empty);
                if (empty || inscription == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    HBox hbox = new HBox();
                    hbox.setSpacing(10);
                    hbox.setStyle("-fx-padding: 12; -fx-background-color: #ffffff; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");

                    String evenementTitre = evenementTitles.getOrDefault(inscription.getEvenementId(), "Événement inconnu");

                    Label evenementLabel = new Label(evenementTitre);
                    evenementLabel.setPrefWidth(250);
                    evenementLabel.setStyle("-fx-text-fill: #333333; -fx-font-size: 14px;");

                    // Formater la date pour n'afficher que yyyy-MM-dd
                    LocalDateTime dateTime = LocalDateTime.parse(inscription.getDate(), DATE_TIME_FORMATTER);
                    Label dateLabel = new Label(dateTime.format(DATE_FORMATTER));
                    dateLabel.setPrefWidth(150);
                    dateLabel.setStyle("-fx-text-fill: #333333; -fx-font-size: 14px;");

                    Label commentaireLabel = new Label(inscription.getCommentaire());
                    commentaireLabel.setPrefWidth(200);
                    commentaireLabel.setStyle("-fx-text-fill: #333333; -fx-font-size: 14px;");

                    Label placesLabel = new Label(String.valueOf(inscription.getNbrPlaceReserve()));
                    placesLabel.setPrefWidth(150);
                    placesLabel.setStyle("-fx-text-fill: #333333; -fx-font-size: 14px;");

                    Label typeLabel = new Label(inscription.getType());
                    typeLabel.setPrefWidth(100);
                    typeLabel.setStyle("-fx-text-fill: #333333; -fx-font-size: 14px;");

                    // Boutons d'action avec icônes
                    HBox actionsBox = new HBox(10);
                    actionsBox.setPrefWidth(150);

                    // Bouton Modifier avec icône
                    Button modifierButton = new Button("✏️");
                    modifierButton.setStyle(
                            "-fx-background-color: #4CAF50; " +
                                    "-fx-text-fill: white; " +
                                    "-fx-font-size: 16px; " +
                                    "-fx-background-radius: 5; " +
                                    "-fx-border-width: 0; " +
                                    "-fx-padding: 0 5 0 5; " +
                                    "-fx-content-display: text-only; " +
                                    "-fx-cursor: hand;"
                    );
                    modifierButton.setOnAction(event -> handleModifier(inscription));

                    // Bouton Supprimer avec icône
                    Button supprimerButton = new Button("🗑️");
                    supprimerButton.setStyle(
                            "-fx-background-color: #F44336; " +
                                    "-fx-text-fill: white; " +
                                    "-fx-font-size: 16px; " +
                                    "-fx-background-radius: 5; " +
                                    "-fx-border-width: 0; " +
                                    "-fx-padding: 0 5 0 5; " +
                                    "-fx-content-display: text-only; " +
                                    "-fx-cursor: hand;"
                    );
                    supprimerButton.setOnAction(event -> handleSupprimer(inscription));

                    actionsBox.getChildren().addAll(modifierButton, supprimerButton);

                    hbox.getChildren().addAll(evenementLabel, dateLabel, commentaireLabel, placesLabel, typeLabel, actionsBox);

                    if (getIndex() % 2 == 0) {
                        hbox.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 12; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");
                    }

                    hbox.setOnMouseEntered(e -> hbox.setStyle("-fx-background-color: #e0f7fa; -fx-padding: 12; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;"));
                    hbox.setOnMouseExited(e -> {
                        if (getIndex() % 2 == 0) {
                            hbox.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 12; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");
                        } else {
                            hbox.setStyle("-fx-background-color: #ffffff; -fx-padding: 12; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");
                        }
                    });

                    setGraphic(hbox);
                    setText(null);
                }
            }
        });

        try {
            loadEvenementTitles();
            afficherInscriptions();
        } catch (SQLException e) {
            System.out.println("Erreur lors du chargement des inscriptions : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadEvenementTitles() throws SQLException {
        System.out.println("Chargement des titres des événements...");
        List<Evenement> evenements = evenementService.afficher();
        for (Evenement evenement : evenements) {
            evenementTitles.put(evenement.getId(), evenement.getTitre());
        }
        System.out.println("Titres des événements chargés : " + evenementTitles.size() + " événements trouvés.");
    }

    public void afficherInscriptions() throws SQLException {
        System.out.println("Affichage des inscriptions...");
        List<Inscription> inscriptions = inscriptionService.afficher();
        System.out.println("Nombre d'inscriptions récupérées : " + inscriptions.size());

        listeInscription.getItems().clear();
        listeInscription.getItems().addAll(inscriptions);
        System.out.println("Inscriptions ajoutées au ListView.");
    }

    private void handleModifier(Inscription inscription) {
        try {
            System.out.println("Tentative de chargement de modifierInscription.fxml...");
            System.out.println("Chemin FXML : " + getClass().getResource("/modifierInscription.fxml"));
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifierInscription.fxml"));
            if (loader.getLocation() == null) {
                System.out.println("Erreur : Impossible de trouver modifierInscription.fxml");
                Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur : Fichier FXML de modification introuvable.");
                alert.showAndWait();
                return;
            }
            Parent root = loader.load();

            modifierInscription controller = loader.getController();
            if (controller == null) {
                System.out.println("Erreur : Contrôleur modifierInscription non chargé.");
                Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur : Contrôleur de modification non chargé.");
                alert.showAndWait();
                return;
            }

            // Passer l'inscription et une référence à ce contrôleur
            controller.setInscription(inscription);
            controller.setParentController(this);

            Stage stage = (Stage) listeInscription.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Modifier une Inscription");
            stage.setMaximized(true);
            stage.show();
            System.out.println("modifierInscription.fxml chargé avec succès.");
        } catch (IOException e) {
            System.out.println("Erreur lors du chargement de modifierInscription.fxml : " + e.getMessage());
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors du chargement de la fenêtre de modification : " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void handleSupprimer(Inscription inscription) {
        // Création de la boîte de dialogue de confirmation
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Voulez-vous vraiment supprimer cette inscription ?");

        // Personnalisation des boutons
        ButtonType ouiButton = new ButtonType("Oui", ButtonBar.ButtonData.YES);
        ButtonType nonButton = new ButtonType("Non", ButtonBar.ButtonData.NO);
        confirmation.getButtonTypes().setAll(ouiButton, nonButton);

        // Affichage de la boîte de dialogue et attente de la réponse
        Optional<ButtonType> result = confirmation.showAndWait();

        if (result.isPresent() && result.get() == ouiButton) {
            try {
                inscriptionService.supprimer(inscription.getId());
                System.out.println("Inscription ID " + inscription.getId() + " supprimée avec succès.");
                afficherInscriptions();

                // Afficher un message de succès
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Succès");
                success.setHeaderText(null);
                success.setContentText("Inscription supprimée avec succès !");
                success.showAndWait();
            } catch (SQLException e) {
                System.out.println("Erreur lors de la suppression de l'inscription : " + e.getMessage());
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Erreur");
                error.setHeaderText(null);
                error.setContentText("Une erreur est survenue lors de la suppression.");
                error.showAndWait();
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void retourner() {
        try {
            // Charger l'interface ajouterInscriptionFront.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterInscriptionFront.fxml"));
            if (loader.getLocation() == null) {
                System.out.println("Erreur : Impossible de trouver ajouterInscriptionFront.fxml");
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Erreur");
                error.setHeaderText(null);
                error.setContentText("Erreur lors du retour à l'interface d'ajout d'inscription.");
                error.showAndWait();
                return;
            }
            Parent root = loader.load();

            // Afficher la scène
            Stage stage = (Stage) btnRetourner.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Ajouter une Inscription");
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            System.out.println("Erreur lors du retour à l'interface d'ajout d'inscription : " + e.getMessage());
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Erreur");
            error.setHeaderText(null);
            error.setContentText("Erreur lors du retour à l'interface d'ajout d'inscription.");
            error.showAndWait();
            e.printStackTrace();
        }
    }
}