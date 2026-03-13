package controller;

import entite.SalleDeSport;
import jakarta.validation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import service.SalleDeSportService;

import java.io.IOException;
import java.util.Set;

public class AjouterSalleDeSport {

    @FXML
    private Label appName;

    @FXML
    private Button btnAjouterSalle;

    @FXML
    private Button btnAfficherSalle;

    @FXML
    private Button btnEvenements;

    @FXML
    private Button btnForum;

    @FXML
    private Button btnProduits;

    @FXML
    private Button btnWorkouts;

    @FXML
    private Rectangle cadre;

    @FXML
    private ChoiceBox<Integer> cb_prixAbonnementSalle;

    @FXML
    private HBox headerHBox;

    @FXML
    private ImageView logo_menu;

    @FXML
    private VBox menuVBox;

    @FXML
    private TextField tf_codePostalSalle;

    @FXML
    private TextField tf_emailSalle;

    @FXML
    private TextField tf_nomSalle;

    @FXML
    private TextField tf_rueSalle;

    @FXML
    private TextField tf_villeSalle;
    @FXML
    private Button retour;

    @FXML
    void ajouterSalle(ActionEvent event) {
        // Instancier le validateur
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        // Validez chaque champ dans l'ordre souhaité
        SalleDeSport salle = new SalleDeSport();

        // Valider le nom de la salle
        salle.setNom(tf_nomSalle.getText());
        Set<ConstraintViolation<SalleDeSport>> violations = validator.validateProperty(salle, "nom");
        if (!violations.isEmpty()) {
            afficherAlerte("Erreur de validation", null, violations.iterator().next().getMessage(), Alert.AlertType.ERROR);
            tf_nomSalle.requestFocus(); // Mettez le focus sur le champ correspondant
            return;
        }

        // Valider la ville
        salle.setVille(tf_villeSalle.getText());
        violations = validator.validateProperty(salle, "ville");
        if (!violations.isEmpty()) {
            afficherAlerte("Erreur de validation", null, violations.iterator().next().getMessage(), Alert.AlertType.ERROR);
            tf_villeSalle.requestFocus(); // Mettez le focus sur le champ correspondant
            return;
        }

        // Valider la rue
        salle.setRue(tf_rueSalle.getText());
        violations = validator.validateProperty(salle, "rue");
        if (!violations.isEmpty()) {
            afficherAlerte("Erreur de validation", null, violations.iterator().next().getMessage(), Alert.AlertType.ERROR);
            tf_rueSalle.requestFocus(); // Mettez le focus sur le champ correspondant
            return;
        }

        // Valider le code postal
        salle.setCodePostal(tf_codePostalSalle.getText());
        violations = validator.validateProperty(salle, "codePostal");
        if (!violations.isEmpty()) {
            afficherAlerte("Erreur de validation", null, violations.iterator().next().getMessage(), Alert.AlertType.ERROR);
            tf_codePostalSalle.requestFocus(); // Mettez le focus sur le champ correspondant
            return;
        }

        // Valider l'email
        salle.setEmail(tf_emailSalle.getText());
        violations = validator.validateProperty(salle, "email");
        if (!violations.isEmpty()) {
            afficherAlerte("Erreur de validation", null, violations.iterator().next().getMessage(), Alert.AlertType.ERROR);
            tf_emailSalle.requestFocus(); // Mettez le focus sur le champ correspondant
            return;
        }

        // Valider le prix de l'abonnement
        salle.setPrixAbonnement(cb_prixAbonnementSalle.getValue());
        violations = validator.validateProperty(salle, "prixAbonnement");
        if (!violations.isEmpty()) {
            afficherAlerte("Erreur de validation", null, violations.iterator().next().getMessage(), Alert.AlertType.ERROR);
            cb_prixAbonnementSalle.requestFocus(); // Mettez le focus sur le champ correspondant
            return;
        }

        // Si tout est valide, passez à l'enregistrement
        SalleDeSportService service = new SalleDeSportService();
        service.createPst(salle);

        // Affichez un message de succès
        afficherAlerte("Succès", null, "La salle de sport a été ajoutée avec succès.", Alert.AlertType.INFORMATION);

        // Réinitialisez les champs
        reinitialiserChamps();
    }


    @FXML
    public void initialize() {
        // Set the default value
        cb_prixAbonnementSalle.setValue(100);
    }

    private void afficherAlerte(String titre, String enTete, String contenu, Alert.AlertType type) {
        Alert alerte = new Alert(type);
        alerte.setTitle(titre);
        alerte.setHeaderText(enTete);
        alerte.setContentText(contenu);
        alerte.showAndWait();
    }


    private void reinitialiserChamps() {
        tf_nomSalle.clear();
        tf_villeSalle.clear();
        tf_rueSalle.clear();
        tf_codePostalSalle.clear();
        tf_emailSalle.clear();
        cb_prixAbonnementSalle.setValue(100); // Réinitialiser à la valeur par défaut
    }


    @FXML
    void AfficherSalle(ActionEvent event) {
        chargerScene("/ListeSalles.fxml", event);
    }

    // Méthode générique pour charger une scène (identique à celle d'Abonnement)
    private void chargerScene(String fxml, ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            ((Node) event.getSource()).getScene().setRoot(root);
        } catch (IOException e) {
            System.out.println("Erreur lors du chargement de l'interface: " + e.getMessage());
            // Vous pourriez aussi logger l'erreur ou afficher une alerte
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




