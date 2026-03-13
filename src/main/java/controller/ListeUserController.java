package controller;

import entite.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import util.SessionManager;

import java.io.IOException;
import java.util.List;

public class ListeUserController {

    @FXML
    private TableView<User> tableViewUtilisateurs;

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
    private TableColumn<User, String> colRole;

    @FXML
    private TableColumn<User, String> colImage;

    @FXML
    private TableColumn<User, String> colStatut;

    @FXML
    private TableColumn<User, Void> colActions;

    @FXML
    private Button btnTousUtilisateurs;

    @FXML
    private Button btnNutritionnistes;

    @FXML
    private Button btnCoachs;

    @FXML
    private Button btnEnAttente;
    @FXML
    private Label bmiLabel;

    private UtilisateurService utilisateurService = new UtilisateurService();
    private ObservableList<User> usersList;

    @FXML
    public void initialize() {
        // Initialisation des colonnes
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colLogin.setCellValueFactory(new PropertyValueFactory<>("login"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colTel.setCellValueFactory(new PropertyValueFactory<>("numTel"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colImage.setCellValueFactory(new PropertyValueFactory<>("image"));
        colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));

        // Image
        colImage.setCellFactory(column -> new TableCell<User, String>() {
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

        // Statut
        colStatut.setCellFactory(column -> new TableCell<User, String>() {
            @Override
            protected void updateItem(String statut, boolean empty) {
                super.updateItem(statut, empty);
                if (empty || statut == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(statut);
                    if (statut.equals("validé")) {
                        setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
                    } else if (statut.equals("en attente")) {
                        setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
                    } else if (statut.equals("rejeté")) {
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    }
                }
            }
        });

        // Actions
        colActions.setCellFactory(column -> new TableCell<User, Void>() {
            private final Button btnModifier = new Button("✏️");
            private final Button btnSupprimer = new Button("🗑️");
            private final Button btnValider = new Button("✓");
            private final Button btnRejeter = new Button("✗");

            {
                btnModifier.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    openUpdateUserWindow(user);
                });

                btnSupprimer.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    if (showConfirmationDialog("Confirmation", "Êtes-vous sûr de vouloir supprimer cet utilisateur ?")) {
                        utilisateurService.delete(user);
                        usersList.remove(user);
                    }
                });

                btnValider.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    if (showConfirmationDialog("Confirmation", "Êtes-vous sûr de vouloir valider cet utilisateur ?")) {
                        user.setPending(false);
                        user.setStatut("validé");
                        utilisateurService.updateUserStatus(user.getId(), false, "validé");
                        refreshTable();
                    }
                });

                btnRejeter.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    if (showConfirmationDialog("Confirmation", "Êtes-vous sûr de vouloir rejeter cet utilisateur ?")) {
                        user.setPending(false);
                        user.setStatut("rejeté");
                        utilisateurService.updateUserStatus(user.getId(), false, "rejeté");
                        refreshTable();
                    }
                });

                btnModifier.setStyle("-fx-background-color: #ffb700; -fx-text-fill: white;");
                btnSupprimer.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white;");
                btnValider.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
                btnRejeter.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
            }
            @FXML
            private void handleLogout() {
                try {
                    SessionManager.clearSession();
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
                    Parent root = loader.load();

                    Stage stage = (Stage) bmiLabel.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Login");
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    User user = getTableView().getItems().get(getIndex());
                    HBox buttons = new HBox(5);
                    buttons.getChildren().addAll(btnModifier, btnSupprimer);
                    
                    // Afficher les boutons de validation/rejet uniquement pour les utilisateurs en attente
                    if (user.isPending()) {
                        buttons.getChildren().addAll(btnValider, btnRejeter);
                    }
                    
                    setGraphic(buttons);
                }
            }
        });

        // Initialisation des boutons
        btnTousUtilisateurs.setOnAction(e -> loadAllUsers());
        btnNutritionnistes.setOnAction(e -> loadNutritionnistes());
        btnCoachs.setOnAction(e -> loadCoachs());
        btnEnAttente.setOnAction(e -> loadUsersEnAttente());

        // Charger tous les utilisateurs par défaut
        loadAllUsers();
    }

    private void refreshTable() {
        if (btnTousUtilisateurs.isFocused()) loadAllUsers();
        else if (btnNutritionnistes.isFocused()) loadNutritionnistes();
        else if (btnCoachs.isFocused()) loadCoachs();
        else if (btnEnAttente.isFocused()) loadUsersEnAttente();
    }

    private void loadAllUsers() {
        List<User> users = utilisateurService.getAllUsers();
        usersList = FXCollections.observableArrayList(users);
        tableViewUtilisateurs.setItems(usersList);
    }

    private void loadNutritionnistes() {
        List<User> nutritionnistes = utilisateurService.getUsersByRole("Nutritionniste");
        usersList = FXCollections.observableArrayList(nutritionnistes);
        tableViewUtilisateurs.setItems(usersList);
    }

    private void loadCoachs() {
        List<User> coachs = utilisateurService.getUsersByRole("Coach");
        usersList = FXCollections.observableArrayList(coachs);
        tableViewUtilisateurs.setItems(usersList);
    }

    private void loadUsersEnAttente() {
        List<User> users = utilisateurService.getAllUsers();
        usersList = FXCollections.observableArrayList(
            users.stream()
                .filter(user -> user.isPending())
                .toList()
        );
        tableViewUtilisateurs.setItems(usersList);
    }

    private void openUpdateUserWindow(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierNutritionniste.fxml"));
            Parent root = loader.load();

            ModifierNutritionnisteController controller = loader.getController();
            controller.setNutritionniste(user);

            Stage stage = new Stage();
            stage.setTitle("Modifier Utilisateur");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            refreshTable();
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

    public void interfaceRendezVous(ActionEvent actionEvent) {

        try {
            // Charger le fichier FXML de l'interface produit
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dashboardadmin.fxml"));
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



    public void interfaceProduit(ActionEvent actionEvent) {
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

    public void interfaceEvennement(ActionEvent actionEvent) {
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



    public void loadRegimes(ActionEvent actionEvent) {
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

    public void interfaceRend(ActionEvent actionEvent) {
        try {
            // Charger le fichier FXML de l'interface produit
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RendezVousBack.fxml"));
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

    public void handleLogout(ActionEvent actionEvent) {

        try {
            // Charger le fichier FXML de l'interface produit
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
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

    public void interfaceCategorie(ActionEvent actionEvent) {
        try {
            // Charger le fichier FXML de l'interface produit
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterCategorie.fxml"));
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
