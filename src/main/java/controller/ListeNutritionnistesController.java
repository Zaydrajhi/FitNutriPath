package controller;

import entite.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import service.UtilisateurService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ListeNutritionnistesController {

    @FXML
    private TableView<User> tableViewNutritionnistes;

    @FXML
    private TableColumn<User, Integer> colId;

    @FXML
    private TableColumn<User, String> colLogin;

    @FXML
    private TableColumn<User, String> colNom;

    @FXML
    private TableColumn<User, String> colPrenom;

    @FXML
    private TableColumn<User, String> colTel;

    @FXML
    private TableColumn<User, String> colEmail;

    @FXML
    private TableColumn<User, String> colImage;

    @FXML
    private TableColumn<User, Void> colActions;

    private UtilisateurService utilisateurService = new UtilisateurService();

    @FXML
    public void initialize() {
        // Initialisation des colonnes
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colLogin.setCellValueFactory(new PropertyValueFactory<>("login"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colTel.setCellValueFactory(new PropertyValueFactory<>("numTel"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Image
        colImage.setCellFactory(column -> new TableCell<>() {
            private final ImageView imageView = new ImageView();

            {
                imageView.setFitWidth(50);
                imageView.setFitHeight(50);
            }

            @Override
            protected void updateItem(String imageUrl, boolean empty) {
                super.updateItem(imageUrl, empty);
                if (empty || imageUrl == null || imageUrl.isEmpty()) {
                    setGraphic(null);
                } else {
                    try {
                        imageView.setImage(new Image(imageUrl, true));
                        setGraphic(imageView);
                    } catch (Exception e) {
                        setGraphic(null);
                    }
                }
            }
        });

        // Actions
        colActions.setCellFactory(column -> new TableCell<>() {
            private final Button btnModifier = new Button("✏️");
            private final Button btnSupprimer = new Button("🗑️");

            {
                btnModifier.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    openModifierNutritionnisteWindow(user);
                });

                btnSupprimer.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    if (showConfirmationDialog("Confirmation", "Êtes-vous sûr de vouloir supprimer ce nutritionniste ?")) {
                        utilisateurService.supprimerUtilisateur(user.getId());
                        tableViewNutritionnistes.getItems().remove(user);
                    }
                });

                btnModifier.setStyle("-fx-background-color: #ffb700; -fx-text-fill: white;");
                btnSupprimer.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, btnModifier, btnSupprimer);
                    setGraphic(buttons);
                }
            }
        });

        loadNutritionnistes();
    }

    private void openModifierNutritionnisteWindow(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierNutritionniste.fxml"));
            Parent root = loader.load();

            ModifierNutritionnisteController controller = loader.getController();
            controller.setNutritionniste(user);

            Stage stage = new Stage();
            stage.setTitle("Modifier Nutritionniste");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            loadNutritionnistes(); // Recharger la liste après modification
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean showConfirmationDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        return alert.showAndWait().get() == ButtonType.OK;
    }

    private void loadNutritionnistes() {
        ObservableList<User> nutritionnistes = FXCollections.observableArrayList(
                utilisateurService.getAllNutritionnistes()
        );
        tableViewNutritionnistes.setItems(nutritionnistes);
    }
}
