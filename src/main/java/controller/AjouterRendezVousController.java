package controller;

import entite.RendezVous;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import service.RendezVousService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import util.BadWordsDetector;
import java.sql.SQLException;

public class AjouterRendezVousController {

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextArea descriptionField;
    @FXML private ComboBox<String> typeField;
    @FXML private DatePicker dateField;
    @FXML private TextField heureField;
    @FXML private ComboBox<String> prioriteField;
    @FXML private TextField numTelField;
    @FXML private TextField tailleField;
    @FXML private TextField poidsField;
    @FXML private TextArea objectifField;
    @FXML private DatePicker dateDebutField;
    @FXML private DatePicker dateFinField;

    private final RendezVousService rendezVousService = new RendezVousService();

    @FXML
    public void initialize() {
        // Initialisation des ComboBox
        typeField.getItems().addAll("Consultation", "Suivi", "Urgence");
        prioriteField.getItems().addAll("Faible", "Moyenne", "Haute");

        // Configuration des validateurs
        configureValidators();

        // Valeurs par défaut
        dateField.setValue(LocalDate.now());
        dateDebutField.setValue(LocalDate.now());
        heureField.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
    }

    private void configureValidators() {
        // Validation numérique pour la taille et le poids
        tailleField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                tailleField.setText(oldVal);
            }
        });

        poidsField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                poidsField.setText(oldVal);
            }
        });

        // Format de l'heure (HH:mm)
        heureField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("^\\d{0,2}:?\\d{0,2}$")) {
                heureField.setText(oldVal);
            } else if (newVal.length() == 2 && !newVal.contains(":")) {
                heureField.setText(newVal + ":");
            }
        });

        // Validation du numéro de téléphone
        numTelField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*") || newVal.length() > 15) {
                numTelField.setText(oldVal);
            }
        });

        // Empêcher les dates passées
        dateField.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });

        dateDebutField.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });

        dateFinField.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate dateDebut = dateDebutField.getValue();
                setDisable(empty ||
                        date.isBefore(LocalDate.now()) ||
                        (dateDebut != null && date.isBefore(dateDebut)));
            }
        });
    }


    @FXML
    private void ajouterRendezVous() {
        try {
            // Validation des entrées
            if (!validateInputs()) {
                return;
            }

            // Création de l'objet RendezVous
            RendezVous rendezVous = new RendezVous();
            rendezVous.setNom(nomField.getText().trim());
            rendezVous.setPrenom(prenomField.getText().trim());
            rendezVous.setDescription(descriptionField.getText().trim());
            rendezVous.setType(typeField.getValue());
            rendezVous.setDateEtHeure(parseDateTime(dateField.getValue(), heureField.getText()));
            rendezVous.setPriorite(prioriteField.getValue());
            rendezVous.setNumTel(numTelField.getText().trim());
            rendezVous.setTaille(Integer.parseInt(tailleField.getText()));
            rendezVous.setPoids(Integer.parseInt(poidsField.getText()));
            rendezVous.setObjectif(objectifField.getText().trim());
            rendezVous.setDateDebut(dateDebutField.getValue());

            if (dateFinField.getValue() != null) {
                rendezVous.setDateFin(dateFinField.getValue());
            }

            // Enregistrement en base de données
            rendezVousService.create(rendezVous);

            // Affichage du succès et réinitialisation
            showAlert("Succès", "Rendez-vous ajouté avec succès");
            clearFields();

            // Retourner à l'accueil
            retourAccueil();

        } catch (SQLException e) {
            // Gestion des erreurs spécifiques
            if (e.getMessage().contains("créneau horaire")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Créneau non disponible");
                alert.setHeaderText("Le créneau sélectionné n'est pas disponible");
                
                // Créer un TextArea pour afficher les créneaux disponibles
                TextArea textArea = new TextArea(e.getMessage());
                textArea.setEditable(false);
                textArea.setWrapText(true);
                textArea.setMaxWidth(Double.MAX_VALUE);
                textArea.setMaxHeight(Double.MAX_VALUE);
                
                // Créer un GridPane pour organiser le contenu
                GridPane gridPane = new GridPane();
                gridPane.setMaxWidth(Double.MAX_VALUE);
                gridPane.add(textArea, 0, 0);
                
                // Ajouter un bouton pour sélectionner un créneau
                Button selectButton = new Button("Sélectionner un créneau");
                selectButton.setOnAction(event -> {
                    // Mettre à jour l'heure avec le premier créneau disponible
                    String[] lines = e.getMessage().split("\n");
                    for (String line : lines) {
                        if (line.startsWith("- ")) {
                            String heure = line.substring(2);
                            heureField.setText(heure);
                            break;
                        }
                    }
                    alert.close();
                });
                
                gridPane.add(selectButton, 0, 1);
                
                alert.getDialogPane().setContent(gridPane);
                alert.showAndWait();
            } else if (e.getMessage().contains("date de début") || e.getMessage().contains("date de fin")) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur de date");
                alert.setHeaderText("Problème avec les dates");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            } else {
                showAlert("Erreur", "Erreur lors de l'ajout: " + e.getMessage());
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Taille et poids doivent être des nombres valides");
        } catch (DateTimeParseException e) {
            showAlert("Erreur", "Format d'heure invalide. Utilisez HH:mm");
        } catch (Exception e) {
            showAlert("Erreur", "Erreur inattendue: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private boolean validateInputs() {
        StringBuilder errors = new StringBuilder();

        // Validation du nom
        if (nomField.getText().trim().isEmpty()) {
            errors.append("- Le nom est obligatoire\n");
        } else if (nomField.getText().trim().length() < 2) {
            errors.append("- Le nom doit contenir au moins 2 caractères\n");
        }

        // Validation du prénom
        if (prenomField.getText().trim().isEmpty()) {
            errors.append("- Le prénom est obligatoire\n");
        } else if (prenomField.getText().trim().length() < 2) {
            errors.append("- Le prénom doit contenir au moins 2 caractères\n");
        }

        // Validation de la description
        if (descriptionField.getText().trim().isEmpty()) {
            errors.append("- La description est obligatoire\n");
        } else if (descriptionField.getText().trim().length() < 10) {
            errors.append("- La description doit contenir au moins 10 caractères\n");
        } else if (BadWordsDetector.containsBadWords(descriptionField.getText())) {
            String detectedWord = BadWordsDetector.getLastDetectedWord();
            int severity = BadWordsDetector.getLastSeverityScore();
            String suggestion = BadWordsDetector.getRandomSuggestion(detectedWord);
            errors.append("🚨 La description contient des mots inappropriés:\n");
            errors.append("- Mot détecté: '" + detectedWord + "'\n");
            errors.append("- Niveau de gravité: " + severity + "/5\n");
            errors.append("- Suggestion: " + suggestion + "\n");
        }

        // Validation du type
        if (typeField.getValue() == null) {
            errors.append("- Le type de rendez-vous est obligatoire\n");
        }

        // Validation de la date et heure
        if (dateField.getValue() == null) {
            errors.append("- La date est obligatoire\n");
        }

        if (heureField.getText().trim().isEmpty()) {
            errors.append("- L'heure est obligatoire\n");
        } else if (!heureField.getText().matches("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            errors.append("- Le format de l'heure doit être HH:mm\n");
        }

        // Validation de la priorité
        if (prioriteField.getValue() == null) {
            errors.append("- La priorité est obligatoire\n");
        }

        // Validation du téléphone
        if (numTelField.getText().trim().isEmpty()) {
            errors.append("- Le numéro de téléphone est obligatoire\n");
        } else if (numTelField.getText().trim().length() < 8) {
            errors.append("- Le numéro de téléphone doit contenir au moins 8 chiffres\n");
        }

        // Validation de la taille
        if (tailleField.getText().trim().isEmpty()) {
            errors.append("- La taille est obligatoire\n");
        }

        // Validation du poids
        if (poidsField.getText().trim().isEmpty()) {
            errors.append("- Le poids est obligatoire\n");
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

        // Validation de la date de début
        if (dateDebutField.getValue() == null) {
            errors.append("- La date de début est obligatoire\n");
        }

        if (errors.length() > 0) {
            showAlert("Erreurs de validation", errors.toString());
            return false;
        }

        return true;
    }

    private LocalDateTime parseDateTime(LocalDate date, String time) throws DateTimeParseException {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime localTime = LocalTime.parse(time, timeFormatter);
        return LocalDateTime.of(date, localTime);
    }

    private void clearFields() {
        nomField.clear();
        prenomField.clear();
        descriptionField.clear();
        typeField.getSelectionModel().clearSelection();
        dateField.setValue(LocalDate.now());
        heureField.clear();
        prioriteField.getSelectionModel().clearSelection();
        numTelField.clear();
        tailleField.clear();
        poidsField.clear();
        objectifField.clear();
        dateDebutField.setValue(LocalDate.now());
        dateFinField.setValue(null);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(message.startsWith("Erreur") ?
                Alert.AlertType.ERROR : Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void retourAccueil() {
        try {
            // Charger la vue d'accueil
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Accueil.fxml"));
            Parent root = loader.load();

            // Obtenir la fenêtre actuelle
            Stage stage = (Stage) nomField.getScene().getWindow();

            // Créer et afficher la nouvelle scène
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors du retour à l'accueil: " + e.getMessage());
            alert.showAndWait();
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
}