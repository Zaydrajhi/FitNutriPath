package controller;

import entite.SalleDeSport;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import service.SalleDeSportService;

import java.util.Set;

public class modifierSalleController {

    @FXML private TextField tf_nomSalle;
    @FXML private TextField tf_villeSalle;
    @FXML private TextField tf_rueSalle;
    @FXML private TextField tf_codePostalSalle;
    @FXML private TextField tf_emailSalle;
    @FXML private ComboBox<Integer> cb_prixAbonnementSalle;

    private SalleDeSport salleAModifier;
    private final SalleDeSportService salleService = new SalleDeSportService();

    public void initData(SalleDeSport salle) {
        this.salleAModifier = salle;
        tf_nomSalle.setText(salle.getNom());
        tf_villeSalle.setText(salle.getVille());
        tf_rueSalle.setText(salle.getRue());
        tf_codePostalSalle.setText(salle.getCodePostal());
        tf_emailSalle.setText(salle.getEmail());
        cb_prixAbonnementSalle.setValue(salle.getPrixAbonnement());

        // Initialiser les choix de prix
        cb_prixAbonnementSalle.getItems().setAll(100, 200, 300);

        // Sélectionner la valeur actuelle ou une valeur par défaut
        if (salle.getPrixAbonnement() != 0) {
            cb_prixAbonnementSalle.setValue(salle.getPrixAbonnement());
        } else {
            cb_prixAbonnementSalle.setValue(100); // Valeur par défaut
        }
    }

    @FXML
    private void modifierSalle(ActionEvent event) {
        // Mise à jour de l'objet salle avec les nouvelles valeurs
        salleAModifier.setNom(tf_nomSalle.getText());
        salleAModifier.setVille(tf_villeSalle.getText());
        salleAModifier.setRue(tf_rueSalle.getText());
        salleAModifier.setCodePostal(tf_codePostalSalle.getText());
        salleAModifier.setEmail(tf_emailSalle.getText());
        salleAModifier.setPrixAbonnement(cb_prixAbonnementSalle.getValue());

        // Validation avec Jakarta Validation
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        // Validation du nom
        Set<ConstraintViolation<SalleDeSport>> violations = validator.validateProperty(salleAModifier, "nom");
        if (!violations.isEmpty()) {
            afficherAlerte("Erreur", null, violations.iterator().next().getMessage(), Alert.AlertType.ERROR);
            tf_nomSalle.requestFocus();
            return;
        }

        // Validation de la ville
        violations = validator.validateProperty(salleAModifier, "ville");
        if (!violations.isEmpty()) {
            afficherAlerte("Erreur", null, violations.iterator().next().getMessage(), Alert.AlertType.ERROR);
            tf_villeSalle.requestFocus();
            return;
        }

        // Validation de la rue
        violations = validator.validateProperty(salleAModifier, "rue");
        if (!violations.isEmpty()) {
            afficherAlerte("Erreur", null, violations.iterator().next().getMessage(), Alert.AlertType.ERROR);
            tf_rueSalle.requestFocus();
            return;
        }

        // Validation du code postal
        violations = validator.validateProperty(salleAModifier, "codePostal");
        if (!violations.isEmpty()) {
            afficherAlerte("Erreur", null, violations.iterator().next().getMessage(), Alert.AlertType.ERROR);
            tf_codePostalSalle.requestFocus();
            return;
        }

        // Validation de l'email
        violations = validator.validateProperty(salleAModifier, "email");
        if (!violations.isEmpty()) {
            afficherAlerte("Erreur", null, violations.iterator().next().getMessage(), Alert.AlertType.ERROR);
            tf_emailSalle.requestFocus();
            return;
        }

        // Validation du prix d'abonnement
        violations = validator.validateProperty(salleAModifier, "prixAbonnement");
        if (!violations.isEmpty()) {
            afficherAlerte("Erreur", null, violations.iterator().next().getMessage(), Alert.AlertType.ERROR);
            cb_prixAbonnementSalle.requestFocus();
            return;
        }

        // Si tout est valide, procéder à la modification
        try {
            salleService.update(salleAModifier);
            afficherAlerte("Succès", null, "Salle modifiée avec succès!", Alert.AlertType.INFORMATION);
            closeWindow();
        } catch (Exception e) {
            afficherAlerte("Erreur", "Erreur lors de la modification", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void annulerModification(ActionEvent event) {
        closeWindow();
    }

    private void afficherAlerte(String titre, String enTete, String contenu, Alert.AlertType type) {
        Alert alerte = new Alert(type);
        alerte.setTitle(titre);
        alerte.setHeaderText(enTete);
        alerte.setContentText(contenu);
        alerte.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) tf_nomSalle.getScene().getWindow();
        stage.close();
    }
}