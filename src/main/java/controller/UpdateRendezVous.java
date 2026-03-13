package controller;
import entite.RendezVous;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import service.RendezVousService;
import util.BadWordsDetector;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

    public class UpdateRendezVous {
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

        private RendezVous currentRendezVous;
        private final RendezVousService rendezVousService = new RendezVousService();

        public void initialize() {
            // Initialize ComboBoxes
            typeField.getItems().addAll("Consultation", "Suivi", "Urgence");
            prioriteField.getItems().addAll("Faible", "Moyenne", "Haute");

            // Configure input validators
            configureValidators();
        }

        public void setRendezVousData(RendezVous rendezVous) {
            this.currentRendezVous = rendezVous;

            // Format date and time
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            // Populate fields with existing data
            nomField.setText(rendezVous.getNom());
            prenomField.setText(rendezVous.getPrenom());
            descriptionField.setText(rendezVous.getDescription());
            typeField.setValue(rendezVous.getType());
            dateField.setValue(rendezVous.getDateEtHeure().toLocalDate());
            heureField.setText(rendezVous.getDateEtHeure().toLocalTime().format(timeFormatter));
            prioriteField.setValue(rendezVous.getPriorite());
            numTelField.setText(rendezVous.getNumTel());
            tailleField.setText(String.valueOf(rendezVous.getTaille()));
            poidsField.setText(String.valueOf(rendezVous.getPoids()));
            objectifField.setText(rendezVous.getObjectif());
            dateDebutField.setValue(rendezVous.getDateDebut());

            if (rendezVous.getDateFin() != null) {
                dateFinField.setValue(rendezVous.getDateFin());
            }
        }

        @FXML
        private void handleUpdateButton() {
            try {
                if (!validateInputs()) {
                    return;
                }

                // Update the existing RendezVous object
                currentRendezVous.setNom(nomField.getText().trim());
                currentRendezVous.setPrenom(prenomField.getText().trim());
                currentRendezVous.setDescription(descriptionField.getText().trim());
                currentRendezVous.setType(typeField.getValue());
                currentRendezVous.setDateEtHeure(parseDateTime(dateField.getValue(), heureField.getText()));
                currentRendezVous.setPriorite(prioriteField.getValue());
                currentRendezVous.setNumTel(numTelField.getText().trim());
                currentRendezVous.setTaille(Integer.parseInt(tailleField.getText()));
                currentRendezVous.setPoids(Integer.parseInt(poidsField.getText()));
                currentRendezVous.setObjectif(objectifField.getText().trim());
                currentRendezVous.setDateDebut(dateDebutField.getValue());
                currentRendezVous.setDateFin(dateFinField.getValue());

                // Validate dates
                if (currentRendezVous.getDateDebut().isBefore(currentRendezVous.getDateEtHeure().toLocalDate())) {
                    showAlert("Erreur", "La date de début ne peut pas être avant la date du rendez-vous");
                    return;
                }

                if (currentRendezVous.getDateFin() != null &&
                        currentRendezVous.getDateFin().isBefore(currentRendezVous.getDateDebut())) {
                    showAlert("Erreur", "La date de fin ne peut pas être avant la date de début");
                    return;
                }

                // Update in database
                rendezVousService.update(currentRendezVous);
                showAlert("Succès", "Rendez-vous mis à jour avec succès");

            } catch (NumberFormatException e) {
                showAlert("Erreur", "Taille et poids doivent être des nombres valides");
            } catch (DateTimeParseException e) {
                showAlert("Erreur", "Format d'heure invalide. Utilisez HH:mm");
            } catch (Exception e) {
                showAlert("Erreur", "Erreur lors de la mise à jour: " + e.getMessage());
                e.printStackTrace();
            }
        }

        @FXML
        private void handleBackButton() {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/RendezVousList.fxml"));
                Stage stage = (Stage) nomField.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
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

        private void showAlert(String title, String message) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
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


        public void interfaceAcceuil(ActionEvent actionEvent) {
            try {
                // Charger le fichier FXML de l'interface produit
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/acceuil.fxml"));
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
