package controller;

import entite.Regime;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import service.RegimeService;
import util.BadWordsDetector;

public class AjouterRegimeController {

    @FXML private TextField tfTitre;
    @FXML private TextArea taDescription;
    @FXML private TextField tfObjectif;
    @FXML private TextField tfDuree;
    @FXML private TextField tfCalories;
    @FXML private ComboBox<String> tfActivite;
    @FXML private Button btnAjouter;
    @FXML private Button btnRetour;
    private int rendezVousId;

    public void setRendezVousId(int id) {
        this.rendezVousId = id;
    }

    private final RegimeService regimeService = new RegimeService();

    @FXML
    private void initialize() {
        // Initialize ComboBox with activity levels
        ObservableList<String> activityLevels = FXCollections.observableArrayList(
            "Sédentaire",
            "Légèrement actif",
            "Modérément actif",
            "Très actif",
            "Extrêmement actif"
        );
        tfActivite.setItems(activityLevels);
        
        // Set up input validation
        setupValidation();
    }

    private void setupValidation() {
        // Add numeric validation for duree and calories fields
        tfDuree.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                tfDuree.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        tfCalories.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                tfCalories.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        // Character limits
        tfTitre.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 50) {
                tfTitre.setText(oldValue);
            }
        });

        tfObjectif.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 100) {
                tfObjectif.setText(oldValue);
            }
        });

        // Validation for ComboBox - no need for text length validation as values are predefined
        tfActivite.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.length() > 30) {
                tfActivite.setValue(oldValue);
            }
        });
    }

    @FXML
    public void ajouterRegime() {
        try {
            // Validate all inputs
            if (!validateInputs()) {
                return;
            }

            // Create Regime object
            Regime regime = new Regime(
                    tfTitre.getText().trim(),
                    taDescription.getText().trim(),
                    tfObjectif.getText().trim(),
                    Integer.parseInt(tfDuree.getText()),
                    Integer.parseInt(tfCalories.getText()),
                    tfActivite.getValue(),
                    rendezVousId
            );

            // Additional business validation
            if (regime.getDuree() <= 0 || regime.getDuree() > 365) {
                showAlert("Erreur", "La durée doit être entre 1 et 365 jours");
                return;
            }

            if (regime.getCaloriesCible() <= 0 || regime.getCaloriesCible() > 10000) {
                showAlert("Erreur", "Les calories doivent être entre 1 et 10000");
                return;
            }

            // Save to database
            regimeService.create(regime);

            // Show success message
            showAlert("Succès", "Régime ajouté avec succès");

            // Navigate back to RendezVousList
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/RendezVousList.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) btnAjouter.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Liste des Rendez-vous");
                stage.show();
            } catch (IOException e) {
                showAlert("Erreur", "Impossible de retourner à la liste des rendez-vous : " + e.getMessage());
                e.printStackTrace();
            }

        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'ajout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validateInputs() {
        StringBuilder errors = new StringBuilder();

        // Validation du titre
        if (tfTitre.getText().trim().isEmpty()) {
            errors.append("- Le titre est obligatoire\n");
        } else if (tfTitre.getText().trim().length() < 3) {
            errors.append("- Le titre doit contenir au moins 3 caractères\n");
        } else if (BadWordsDetector.containsBadWords(tfTitre.getText())) {
            String detectedWord = BadWordsDetector.getLastDetectedWord();
            int severity = BadWordsDetector.getLastSeverityScore();
            String suggestion = BadWordsDetector.getRandomSuggestion(detectedWord);
            errors.append("🚨 Le titre contient des mots inappropriés:\n");
            errors.append("- Mot détecté: '" + detectedWord + "'\n");
            errors.append("- Niveau de gravité: " + severity + "/5\n");
            errors.append("- Suggestion: " + suggestion + "\n");
        }

        // Validation de la description
        if (taDescription.getText().trim().isEmpty()) {
            errors.append("- La description est obligatoire\n");
        } else if (taDescription.getText().trim().length() < 10) {
            errors.append("- La description doit contenir au moins 10 caractères\n");
        } else if (BadWordsDetector.containsBadWords(taDescription.getText())) {
            String detectedWord = BadWordsDetector.getLastDetectedWord();
            int severity = BadWordsDetector.getLastSeverityScore();
            String suggestion = BadWordsDetector.getRandomSuggestion(detectedWord);
            errors.append("🚨 La description contient des mots inappropriés:\n");
            errors.append("- Mot détecté: '" + detectedWord + "'\n");
            errors.append("- Niveau de gravité: " + severity + "/5\n");
            errors.append("- Suggestion: " + suggestion + "\n");
        }

        // Validation de l'objectif
        if (tfObjectif.getText().trim().isEmpty()) {
            errors.append("- L'objectif est obligatoire\n");
        } else if (tfObjectif.getText().trim().length() < 10) {
            errors.append("- L'objectif doit contenir au moins 10 caractères\n");
        } else if (BadWordsDetector.containsBadWords(tfObjectif.getText())) {
            String detectedWord = BadWordsDetector.getLastDetectedWord();
            int severity = BadWordsDetector.getLastSeverityScore();
            String suggestion = BadWordsDetector.getRandomSuggestion(detectedWord);
            errors.append("🚨 L'objectif contient des mots inappropriés:\n");
            errors.append("- Mot détecté: '" + detectedWord + "'\n");
            errors.append("- Niveau de gravité: " + severity + "/5\n");
            errors.append("- Suggestion: " + suggestion + "\n");
        }

        // Validation de la durée
        if (tfDuree.getText().trim().isEmpty()) {
            errors.append("- La durée est obligatoire\n");
        }

        // Validation des calories
        if (tfCalories.getText().trim().isEmpty()) {
            errors.append("- Les calories sont obligatoires\n");
        }

        // Validation de l'activité
        if (tfActivite.getValue() == null) {
            errors.append("- Le niveau d'activité est obligatoire\n");
        }

        if (errors.length() > 0) {
            showAlert("Erreurs de validation", errors.toString());
            return false;
        }

        return true;
    }

    private void clearFields() {
        tfTitre.clear();
        taDescription.clear();
        tfObjectif.clear();
        tfDuree.clear();
        tfCalories.clear();
        tfActivite.setValue(null);
    }

    private void showAlert(String title, String message) {
        Alert.AlertType type = title.equals("Succès") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR;
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void retourRendezVousList(ActionEvent event) {
        try {
            // Charger la vue RendezVousList
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RendezVousList.fxml"));
            Parent root = loader.load();

            // Remplacer la scène actuelle
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des Rendez-vous");
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
            showAlert("Erreur", "Impossible de retourner à la liste des rendez-vous : " + ex.getMessage());
        }
    }
}