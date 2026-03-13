package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import service.HealthMetricsService;
import service.PredictionService;
import org.json.JSONObject;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import service.AdviceService;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class HealthMetricsController implements Initializable {
    private final HealthMetricsService healthMetricsService = new HealthMetricsService();
    private final PredictionService predictionService = new PredictionService();
    private final AdviceService adviceService = new AdviceService();

    @FXML private TextField poidsField;
    @FXML private TextField tailleField;
    @FXML private TextField ageField;
    @FXML private ComboBox<String> sexeComboBox;
    @FXML private TextField tourDeTailleField;
    @FXML private TextField tourDeHancheField;
    @FXML private Label conseilLabel;

    @FXML private Label imcResultLabel;
    @FXML private Label imcInterpretationLabel;
    @FXML private Label graisseResultLabel;
    @FXML private Label graisseInterpretationLabel;
    @FXML private Label ratioResultLabel;
    @FXML private Label ratioInterpretationLabel;
    @FXML private Label poidsIdealLabel;
    @FXML private Label predictionLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialisation du ComboBox pour le sexe
        sexeComboBox.getItems().addAll("Homme", "Femme");
        sexeComboBox.setValue("Homme");

        // Affichage du conseil
        try {
            String conseil = adviceService.getAdvice();
            conseilLabel.setText("💡 " + conseil);
            conseilLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #2c3e50; -fx-font-weight: bold;");
        } catch (Exception e) {
            conseilLabel.setText("Unable to load daily advice");
            conseilLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #e74c3c;");
        }

        // Ajout des validateurs pour les champs numériques
        addNumericValidators();
    }

    private void addNumericValidators() {
        // Validation pour le poids
        poidsField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                poidsField.setText(oldValue);
            }
        });

        // Validation pour la taille
        tailleField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                tailleField.setText(oldValue);
            }
        });

        // Validation pour l'âge
        ageField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                ageField.setText(oldValue);
            }
        });

        // Validation pour le tour de taille
        tourDeTailleField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                tourDeTailleField.setText(oldValue);
            }
        });

        // Validation pour le tour de hanche
        tourDeHancheField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                tourDeHancheField.setText(oldValue);
            }
        });
    }

    @FXML
    private void calculerMetrics() {
        try {
            // Récupération des valeurs
            double poids = Double.parseDouble(poidsField.getText());
            double taille = Double.parseDouble(tailleField.getText()) / 100.0; // Conversion en mètres
            int age = Integer.parseInt(ageField.getText());
            String sexe = sexeComboBox.getValue();
            double tourDeTaille = Double.parseDouble(tourDeTailleField.getText());
            double tourDeHanche = Double.parseDouble(tourDeHancheField.getText());

            // Validation
            if (poids <= 0 || taille <= 0 || age <= 0 || tourDeTaille <= 0 || tourDeHanche <= 0) {
                showAlert("Erreur", "Tous les champs doivent être remplis avec des valeurs positives.");
                return;
            }

            // Calcul de l'IMC
            double imc = poids / (taille * taille);
            String interpretationIMC = healthMetricsService.interpreterIMC(imc);
            String emojiIMC;
            String colorIMC;
            if (imc < 18.5) { emojiIMC = "⚠️"; colorIMC = "#f39c12"; interpretationIMC = "Insuffisance pondérale"; }
            else if (imc < 25) { emojiIMC = "✅"; colorIMC = "#27ae60"; interpretationIMC = "Poids normal"; }
            else if (imc < 30) { emojiIMC = "⚠️"; colorIMC = "#f39c12"; interpretationIMC = "Surpoids"; }
            else { emojiIMC = "❌"; colorIMC = "#e74c3c"; interpretationIMC = "Obésité"; }
            imcResultLabel.setText(String.format("%s IMC: %.1f", emojiIMC, imc));
            imcResultLabel.setStyle("-fx-text-fill: " + colorIMC + "; -fx-font-size: 24px; -fx-font-weight: bold;");
            imcInterpretationLabel.setText(interpretationIMC);
            imcInterpretationLabel.setStyle("-fx-text-fill: " + colorIMC + "; -fx-font-size: 16px;");

            // Calcul du pourcentage de graisse
            double pourcentageGraisse;
            if (sexe.equalsIgnoreCase("Homme")) {
                pourcentageGraisse = (1.20 * imc) + (0.23 * age) - 16.2;
            } else {
                pourcentageGraisse = (1.20 * imc) + (0.23 * age) - 5.4;
            }
            String interpretationGraisse = healthMetricsService.interpreterPourcentageGraisse(pourcentageGraisse, sexe);
            String emojiGraisse;
            String colorGraisse;
            if ((sexe.equalsIgnoreCase("Homme") && pourcentageGraisse < 6) || (sexe.equalsIgnoreCase("Femme") && pourcentageGraisse < 14)) {
                emojiGraisse = "⚠️"; colorGraisse = "#f39c12"; interpretationGraisse = "Graisse essentielle";
            } else if ((sexe.equalsIgnoreCase("Homme") && pourcentageGraisse < 14) || (sexe.equalsIgnoreCase("Femme") && pourcentageGraisse < 21)) {
                emojiGraisse = "✅"; colorGraisse = "#27ae60"; interpretationGraisse = "Athlétique";
            } else if ((sexe.equalsIgnoreCase("Homme") && pourcentageGraisse < 24) || (sexe.equalsIgnoreCase("Femme") && pourcentageGraisse < 31)) {
                emojiGraisse = "✅"; colorGraisse = "#27ae60"; interpretationGraisse = "Normal";
            } else {
                emojiGraisse = "❌"; colorGraisse = "#e74c3c"; interpretationGraisse = "Élevé";
            }
            graisseResultLabel.setText(String.format("%s Pourcentage de graisse: %.1f%%", emojiGraisse, pourcentageGraisse));
            graisseResultLabel.setStyle("-fx-text-fill: " + colorGraisse + "; -fx-font-size: 24px; -fx-font-weight: bold;");
            graisseInterpretationLabel.setText(interpretationGraisse);
            graisseInterpretationLabel.setStyle("-fx-text-fill: " + colorGraisse + "; -fx-font-size: 16px;");

            // Calcul du ratio taille/hanche
            double ratio = healthMetricsService.calculerRatioTailleHanche(tourDeTaille, tourDeHanche);
            String interpretationRatio = healthMetricsService.interpreterRatioTailleHanche(ratio, sexe);
            String emojiRatio;
            String colorRatio;
            if ((sexe.equalsIgnoreCase("Homme") && ratio > 0.95) || (sexe.equalsIgnoreCase("Femme") && ratio > 0.85)) {
                emojiRatio = "❌"; colorRatio = "#e74c3c"; interpretationRatio = "Risque élevé";
            } else {
                emojiRatio = "✅"; colorRatio = "#27ae60"; interpretationRatio = "Risque faible";
            }
            ratioResultLabel.setText(String.format("%s Ratio taille/hanche: %.2f", emojiRatio, ratio));
            ratioResultLabel.setStyle("-fx-text-fill: " + colorRatio + "; -fx-font-size: 24px; -fx-font-weight: bold;");
            ratioInterpretationLabel.setText(interpretationRatio);
            ratioInterpretationLabel.setStyle("-fx-text-fill: " + colorRatio + "; -fx-font-size: 16px;");

            // Calcul du poids idéal (Lorentz)
            double poidsIdeal;
            if (sexe.equalsIgnoreCase("Homme")) {
                poidsIdeal = taille - 100 - ((taille - 150) / 4.0);
            } else {
                poidsIdeal = taille - 100 - ((taille - 150) / 2.5);
            }
            String emojiPoidsIdeal = (poids > poidsIdeal + 5) ? "⬆️" : (poids < poidsIdeal - 5) ? "⬇️" : "✅";
            String colorPoidsIdeal = (poids > poidsIdeal + 5) ? "#e74c3c" : (poids < poidsIdeal - 5) ? "#f39c12" : "#27ae60";
            poidsIdealLabel.setText(String.format("%s Poids idéal: %.1f kg", emojiPoidsIdeal, poidsIdeal));
            poidsIdealLabel.setStyle("-fx-text-fill: " + colorPoidsIdeal + "; -fx-font-size: 24px; -fx-font-weight: bold;");

            // Analyse IA personnalisée
            String analyseIA;
            if (imc < 18.5) {
                analyseIA = "⚠️ Vous êtes en insuffisance pondérale. Pensez à consulter un professionnel de santé.";
            } else if (imc < 25) {
                analyseIA = "🎉 Félicitations ! Votre poids est idéal. Continuez ainsi !";
            } else if (imc < 30) {
                analyseIA = "⚠️ Surpoids détecté. Un mode de vie actif et une alimentation équilibrée sont recommandés.";
            } else {
                analyseIA = "❌ Obésité détectée. Il est conseillé de consulter un professionnel de santé.";
            }
            predictionLabel.setText(analyseIA);
            predictionLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1976D2;");

            // Animation d'apparition sur tous les résultats
            FadeTransition ft1 = new FadeTransition(Duration.millis(600), imcResultLabel); ft1.setFromValue(0); ft1.setToValue(1); ft1.play();
            FadeTransition ft2 = new FadeTransition(Duration.millis(600), graisseResultLabel); ft2.setFromValue(0); ft2.setToValue(1); ft2.play();
            FadeTransition ft3 = new FadeTransition(Duration.millis(600), ratioResultLabel); ft3.setFromValue(0); ft3.setToValue(1); ft3.play();
            FadeTransition ft4 = new FadeTransition(Duration.millis(600), poidsIdealLabel); ft4.setFromValue(0); ft4.setToValue(1); ft4.play();
            FadeTransition ft5 = new FadeTransition(Duration.millis(600), predictionLabel); ft5.setFromValue(0); ft5.setToValue(1); ft5.play();

        } catch (NumberFormatException e) {
            showAlert("Erreur", "Veuillez entrer des valeurs numériques valides pour tous les champs.");
        } catch (IllegalArgumentException e) {
            showAlert("Erreur", e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void interfaceUser() {
        // Navigation vers l'interface utilisateur
    }

    @FXML
    private void prendreRendezVous() {
        // Navigation vers la prise de rendez-vous
    }

    @FXML
    private void afficherMesRendezVous() {
        // Navigation vers la liste des rendez-vous
    }

    @FXML
    private void predictForFree() {
        // Navigation vers la prédiction gratuite
    }

    @FXML
    private void naviguerVersVue(String vue) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + vue + ".fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) poidsField.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}