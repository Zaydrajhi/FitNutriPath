package controller;

import entite.Abonnement;
import entite.SalleDeSport;
import jakarta.validation.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import service.AbonnementService;
import service.SalleDeSportService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class AjouterAbonnement {

    @FXML private ComboBox<SalleDeSport> cb_salleDeSport;
    @FXML private TextField tf_nom;
    @FXML private TextField tf_prenom;
    @FXML private TextField tf_telephone;
    @FXML private TextField tf_email;
    @FXML private DatePicker tf_dateDebut;
    @FXML private DatePicker tf_dateFin;
    @FXML private Button btn_ajouter;
    @FXML private Button afficher;
    @FXML private Button btn_ajouterSalle;

    private SalleDeSportService salleService = new SalleDeSportService();
    private AbonnementService abonnementService = new AbonnementService();

    @FXML
    public void initialize() {
        chargerSallesDeSport();
        tf_dateDebut.setValue(LocalDate.now());
    }

    private void chargerSallesDeSport() {
        List<SalleDeSport> salles = salleService.readAll();
        ObservableList<SalleDeSport> observableList = FXCollections.observableArrayList(salles);
        cb_salleDeSport.setItems(observableList);

        cb_salleDeSport.setCellFactory(param -> new ListCell<SalleDeSport>() {
            @Override
            protected void updateItem(SalleDeSport salle, boolean empty) {
                super.updateItem(salle, empty);
                setText(empty || salle == null ? null : salle.getNom() + " - " + salle.getVille());
            }
        });

        cb_salleDeSport.setButtonCell(new ListCell<SalleDeSport>() {
            @Override
            protected void updateItem(SalleDeSport salle, boolean empty) {
                super.updateItem(salle, empty);
                setText(empty || salle == null ? null : salle.getNom() + " - " + salle.getVille());
            }
        });
    }

    @FXML
    void ajouterAbonnement(ActionEvent event) {
        // Création de l'objet Abonnement pour validation
        Abonnement abonnement = new Abonnement();
        abonnement.setSalleDeSport(cb_salleDeSport.getValue());
        abonnement.setNom(tf_nom.getText());
        abonnement.setPrenom(tf_prenom.getText());
        abonnement.setNumeroTlfn(tf_telephone.getText());
        abonnement.setEmail(tf_email.getText());
        abonnement.setDateDeb(tf_dateDebut.getValue());
        abonnement.setDateFin(tf_dateFin.getValue());

        // Validation avec Jakarta Validation
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        // Validation de la salle de sport
        Set<ConstraintViolation<Abonnement>> violations = validator.validateProperty(abonnement, "salleDeSport");
        if (!violations.isEmpty()) {
            afficherAlerte("Erreur", null, violations.iterator().next().getMessage(), Alert.AlertType.ERROR);
            cb_salleDeSport.requestFocus();
            return;
        }

        // Validation du nom
        violations = validator.validateProperty(abonnement, "nom");
        if (!violations.isEmpty()) {
            afficherAlerte("Erreur", null, violations.iterator().next().getMessage(), Alert.AlertType.ERROR);
            tf_nom.requestFocus();
            return;
        }

        // Validation du prénom
        violations = validator.validateProperty(abonnement, "prenom");
        if (!violations.isEmpty()) {
            afficherAlerte("Erreur", null, violations.iterator().next().getMessage(), Alert.AlertType.ERROR);
            tf_prenom.requestFocus();
            return;
        }

        // Validation du téléphone
        violations = validator.validateProperty(abonnement, "numeroTlfn");
        if (!violations.isEmpty()) {
            afficherAlerte("Erreur", null, violations.iterator().next().getMessage(), Alert.AlertType.ERROR);
            tf_telephone.requestFocus();
            return;
        }

        // Validation de l'email
        violations = validator.validateProperty(abonnement, "email");
        if (!violations.isEmpty()) {
            afficherAlerte("Erreur", null, violations.iterator().next().getMessage(), Alert.AlertType.ERROR);
            tf_email.requestFocus();
            return;
        }

        // Validation des dates
        if (abonnement.getDateDeb() == null) {
            afficherAlerte("Erreur", null, "La date de début est obligatoire", Alert.AlertType.ERROR);
            tf_dateDebut.requestFocus();
            return;
        }

        if (abonnement.getDateFin() == null) {
            afficherAlerte("Erreur", null, "La date de fin est obligatoire", Alert.AlertType.ERROR);
            tf_dateFin.requestFocus();
            return;
        }

        if (abonnement.getDateFin().isBefore(abonnement.getDateDeb())) {
            afficherAlerte("Erreur", null, "La date de fin doit être après la date de début", Alert.AlertType.ERROR);
            tf_dateFin.requestFocus();
            return;
        }

        // Si tout est valide, enregistrement
        abonnementService.create(abonnement);
        afficherAlerte("Succès", null, "Abonnement ajouté avec succès!", Alert.AlertType.INFORMATION);
        reinitialiserFormulaire();
    }

    private void reinitialiserFormulaire() {
        tf_nom.clear();
        tf_prenom.clear();
        tf_telephone.clear();
        tf_email.clear();
        tf_dateDebut.setValue(LocalDate.now());
        tf_dateFin.setValue(null);
        cb_salleDeSport.setValue(null);
    }

    @FXML
    void afficherAbonnements(ActionEvent event) {
        chargerScene("/ListeAbonnements.fxml", event);
    }

    private void chargerScene(String fxml, ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            ((Node) event.getSource()).getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println("Erreur lors du chargement: " + e.getMessage());
        }
    }

    private void afficherAlerte(String titre, String enTete, String contenu, Alert.AlertType type) {
        Alert alerte = new Alert(type);
        alerte.setTitle(titre);
        alerte.setHeaderText(enTete);
        alerte.setContentText(contenu);
        alerte.showAndWait();
    }

    public void prendreRendezVous(ActionEvent event) {
        naviguerVersVue("/AjouterRendezVous.fxml", "Prendre Rendez-vous", event);
    }

    public void afficherMesRendezVous(ActionEvent event) {
        naviguerVersVue("/RendezVouslist.fxml", "Mes Rendez-vous", event);
    }

    public void predictForFree(ActionEvent event) {
        naviguerVersVue("/predict.fxml", "Predict for Free", event);
    }
    private void naviguerVersVue(String fxmlPath, String title, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Get the stage from the event source
            Stage stage = (Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();

            // Set fixed size if needed
            stage.setScene(new Scene(root, 800, 800));
            stage.setTitle(title);
            stage.centerOnScreen();

        } catch (IOException e) {
            showAlert("Erreur de navigation", "Impossible de charger " + title + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void interfaceEvenement(ActionEvent actionEvent) {
        try {
            // Charger le fichier FXML de l'interface produit
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/afficherEvenementFront.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène
            Scene scene = new Scene(root);

            // Obtenir la fenêtre actuelle (stage)
            Stage stage = (Stage) ((javafx.scene.control.Button) actionEvent.getSource()).getScene().getWindow();

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/acceuil.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène
            Scene scene = new Scene(root);

            // Obtenir la fenêtre actuelle (stage)
            Stage stage = (Stage) ((javafx.scene.control.Button) actionEvent.getSource()).getScene().getWindow();

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterAbonnement.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène
            Scene scene = new Scene(root);

            // Obtenir la fenêtre actuelle (stage)
            Stage stage = (Stage) ((javafx.scene.control.Button) actionEvent.getSource()).getScene().getWindow();

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