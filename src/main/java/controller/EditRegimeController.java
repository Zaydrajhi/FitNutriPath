package controller;

import entite.Regime;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import service.RegimeService;
import util.BadWordsDetector;

public class EditRegimeController {
    @FXML private TextField titreField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField objectifField;
    @FXML private TextField dureeField;
    @FXML private TextField caloriesField;
    @FXML private TextField activiteField;

    private Regime regime;
    private final RegimeService regimeService = new RegimeService();

    @FXML
    private void initialize() {
        // Set up numeric validation for duree and calories fields
        dureeField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                dureeField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        caloriesField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                caloriesField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    public void setRegime(Regime regime) {
        this.regime = regime;

        // Initialize form with regime data
        titreField.setText(regime.getTitre());
        descriptionArea.setText(regime.getDescription());
        objectifField.setText(regime.getObjectif());
        dureeField.setText(String.valueOf(regime.getDuree()));
        caloriesField.setText(String.valueOf(regime.getCaloriesCible()));
        activiteField.setText(regime.getNiveauActivite());
    }

    @FXML
    private void handleSave() {
        try {
            // Validate inputs
            StringBuilder errors = new StringBuilder();

            // Validation du titre
            if (titreField.getText().trim().isEmpty()) {
                errors.append("- Le titre est obligatoire\n");
            } else if (titreField.getText().trim().length() < 3) {
                errors.append("- Le titre doit contenir au moins 3 caractères\n");
            } else if (BadWordsDetector.containsBadWords(titreField.getText())) {
                String detectedWord = BadWordsDetector.getLastDetectedWord();
                int severity = BadWordsDetector.getLastSeverityScore();
                String suggestion = BadWordsDetector.getRandomSuggestion(detectedWord);
                errors.append("🚨 Le titre contient des mots inappropriés:\n");
                errors.append("- Mot détecté: '" + detectedWord + "'\n");
                errors.append("- Niveau de gravité: " + severity + "/5\n");
                errors.append("- Suggestion: " + suggestion + "\n");
            }

            // Validation de la description
            if (descriptionArea.getText().trim().isEmpty()) {
                errors.append("- La description est obligatoire\n");
            } else if (descriptionArea.getText().trim().length() < 10) {
                errors.append("- La description doit contenir au moins 10 caractères\n");
            } else if (BadWordsDetector.containsBadWords(descriptionArea.getText())) {
                String detectedWord = BadWordsDetector.getLastDetectedWord();
                int severity = BadWordsDetector.getLastSeverityScore();
                String suggestion = BadWordsDetector.getRandomSuggestion(detectedWord);
                errors.append("🚨 La description contient des mots inappropriés:\n");
                errors.append("- Mot détecté: '" + detectedWord + "'\n");
                errors.append("- Niveau de gravité: " + severity + "/5\n");
                errors.append("- Suggestion: " + suggestion + "\n");
            }

            // Validation de l'objectif
            if (objectifField.getText().trim().isEmpty()) {
                errors.append("- L'objectif est obligatoire\n");
            } else if (objectifField.getText().trim().length() < 10) {
                errors.append("- L'objectif doit contenir au moins 10 caractères\n");
            } else if (BadWordsDetector.containsBadWords(objectifField.getText())) {
                String detectedWord = BadWordsDetector.getLastDetectedWord();
                int severity = BadWordsDetector.getLastSeverityScore();
                String suggestion = BadWordsDetector.getRandomSuggestion(detectedWord);
                errors.append("🚨 L'objectif contient des mots inappropriés:\n");
                errors.append("- Mot détecté: '" + detectedWord + "'\n");
                errors.append("- Niveau de gravité: " + severity + "/5\n");
                errors.append("- Suggestion: " + suggestion + "\n");
            }

            // Validation des autres champs
            if (dureeField.getText().trim().isEmpty() ||
                    caloriesField.getText().trim().isEmpty() ||
                    activiteField.getText().trim().isEmpty()) {
                errors.append("- Tous les champs sont obligatoires\n");
            }

            if (errors.length() > 0) {
                showAlert("Erreurs de validation", errors.toString(), Alert.AlertType.ERROR);
                return;
            }

            // Parse numeric values
            int duree, calories;
            try {
                duree = Integer.parseInt(dureeField.getText().trim());
                calories = Integer.parseInt(caloriesField.getText().trim());

                if (duree <= 0 || duree > 365) {
                    showAlert("Erreur", "La durée doit être entre 1 et 365 jours", Alert.AlertType.ERROR);
                    return;
                }

                if (calories <= 0 || calories > 10000) {
                    showAlert("Erreur", "Les calories doivent être entre 1 et 10000", Alert.AlertType.ERROR);
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert("Erreur", "Durée et calories doivent être des nombres valides", Alert.AlertType.ERROR);
                return;
            }

            // Update regime object
            regime.setTitre(titreField.getText().trim());
            regime.setDescription(descriptionArea.getText().trim());
            regime.setObjectif(objectifField.getText().trim());
            regime.setDuree(duree);
            regime.setCaloriesCible(calories);
            regime.setNiveauActivite(activiteField.getText().trim());

            // Save to database
            regimeService.update(regime);

            // Show success message and close window
            showAlert("Succès", "Régime mis à jour avec succès", Alert.AlertType.INFORMATION);
            titreField.getScene().getWindow().hide();
        } catch (Exception e) {
            showAlert("Erreur", "Mise à jour échouée : " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleCancel() {
        titreField.getScene().getWindow().hide();
    }
}