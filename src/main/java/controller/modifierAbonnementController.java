package controller;

import entite.Abonnement;
import entite.SalleDeSport;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import service.AbonnementService;
import service.SalleDeSportService;

import java.time.LocalDate;
import java.util.Set;

public class modifierAbonnementController {

    @FXML private TextField tf_nom;
    @FXML private TextField tf_prenom;
    @FXML private TextField tf_telephone;
    @FXML private TextField tf_email;
    @FXML private ComboBox<SalleDeSport> cb_salleDeSport;
    @FXML private DatePicker tf_dateDebut;
    @FXML private DatePicker tf_dateFin;

    private Abonnement abonnementAModifier;
    private final AbonnementService abonnementService = new AbonnementService();
    private final SalleDeSportService salleDeSportService = new SalleDeSportService();

    public void initData(Abonnement abonnement) {
        this.abonnementAModifier = abonnement;
        tf_nom.setText(abonnement.getNom());
        tf_prenom.setText(abonnement.getPrenom());
        tf_telephone.setText(abonnement.getNumeroTlfn());
        tf_email.setText(abonnement.getEmail());
        tf_dateDebut.setValue(abonnement.getDateDeb());
        tf_dateFin.setValue(abonnement.getDateFin());

        cb_salleDeSport.setItems(FXCollections.observableArrayList(salleDeSportService.readAll()));
        cb_salleDeSport.setValue(abonnement.getSalleDeSport());

        // Configuration de l'affichage des salles dans le ComboBox
        cb_salleDeSport.setCellFactory(param -> new javafx.scene.control.ListCell<SalleDeSport>() {
            @Override
            protected void updateItem(SalleDeSport salle, boolean empty) {
                super.updateItem(salle, empty);
                setText(empty || salle == null ? null : salle.getNom() + " - " + salle.getVille());
            }
        });

        cb_salleDeSport.setButtonCell(new javafx.scene.control.ListCell<SalleDeSport>() {
            @Override
            protected void updateItem(SalleDeSport salle, boolean empty) {
                super.updateItem(salle, empty);
                setText(empty || salle == null ? null : salle.getNom() + " - " + salle.getVille());
            }
        });
    }

    @FXML
    private void modifierAbonnement(ActionEvent event) {
        // Mise à jour de l'objet abonnement avec les nouvelles valeurs
        abonnementAModifier.setNom(tf_nom.getText());
        abonnementAModifier.setPrenom(tf_prenom.getText());
        abonnementAModifier.setNumeroTlfn(tf_telephone.getText());
        abonnementAModifier.setEmail(tf_email.getText());
        abonnementAModifier.setSalleDeSport(cb_salleDeSport.getValue());
        abonnementAModifier.setDateDeb(tf_dateDebut.getValue());
        abonnementAModifier.setDateFin(tf_dateFin.getValue());

        // Validation avec Jakarta Validation
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        // Validation de la salle de sport
        Set<ConstraintViolation<Abonnement>> violations = validator.validateProperty(abonnementAModifier, "salleDeSport");
        if (!violations.isEmpty()) {
            afficherAlerte("Erreur", null, violations.iterator().next().getMessage(), Alert.AlertType.ERROR);
            cb_salleDeSport.requestFocus();
            return;
        }

        // Validation du nom
        violations = validator.validateProperty(abonnementAModifier, "nom");
        if (!violations.isEmpty()) {
            afficherAlerte("Erreur", null, violations.iterator().next().getMessage(), Alert.AlertType.ERROR);
            tf_nom.requestFocus();
            return;
        }

        // Validation du prénom
        violations = validator.validateProperty(abonnementAModifier, "prenom");
        if (!violations.isEmpty()) {
            afficherAlerte("Erreur", null, violations.iterator().next().getMessage(), Alert.AlertType.ERROR);
            tf_prenom.requestFocus();
            return;
        }

        // Validation du téléphone
        violations = validator.validateProperty(abonnementAModifier, "numeroTlfn");
        if (!violations.isEmpty()) {
            afficherAlerte("Erreur", null, violations.iterator().next().getMessage(), Alert.AlertType.ERROR);
            tf_telephone.requestFocus();
            return;
        }

        // Validation de l'email
        violations = validator.validateProperty(abonnementAModifier, "email");
        if (!violations.isEmpty()) {
            afficherAlerte("Erreur", null, violations.iterator().next().getMessage(), Alert.AlertType.ERROR);
            tf_email.requestFocus();
            return;
        }

        // Validation des dates
        if (abonnementAModifier.getDateDeb() == null) {
            afficherAlerte("Erreur", null, "La date de début est obligatoire", Alert.AlertType.ERROR);
            tf_dateDebut.requestFocus();
            return;
        }

        if (abonnementAModifier.getDateFin() == null) {
            afficherAlerte("Erreur", null, "La date de fin est obligatoire", Alert.AlertType.ERROR);
            tf_dateFin.requestFocus();
            return;
        }

        if (abonnementAModifier.getDateFin().isBefore(abonnementAModifier.getDateDeb())) {
            afficherAlerte("Erreur", null, "La date de fin doit être après la date de début", Alert.AlertType.ERROR);
            tf_dateFin.requestFocus();
            return;
        }

        // Si tout est valide, procéder à la modification
        try {
            abonnementService.update(abonnementAModifier);
            afficherAlerte("Succès", null, "Abonnement modifié avec succès!", Alert.AlertType.INFORMATION);
            closeWindow();
        } catch (Exception e) {
            afficherAlerte("Erreur", "Erreur lors de la modification", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void listeAbonnements(ActionEvent event) {
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
        Stage stage = (Stage) tf_nom.getScene().getWindow();
        stage.close();
    }
}