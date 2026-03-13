package controller;

import entite.Evenement;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import service.EvenementService;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

public class AjouterEvenement {
    @FXML
    private Button retour;
    @FXML
    private Label appName;
    @FXML
    private Button btnWorkouts;
    @FXML
    private Button btnProduits;
    @FXML
    private Button selectImageButton;
    @FXML
    private HBox headerHBox;
    @FXML
    private ImageView logo_menu;
    @FXML
    private ImageView imageView;
    @FXML
    private Button btnForum;
    @FXML
    private Button btnEvenements;
    @FXML
    private VBox menuVBox;
    @FXML
    private Rectangle cadre;
    @FXML
    private TextField tf_nbr_place;
    @FXML
    private TextField tf_prix;
    @FXML
    private TextField tf_titre;
    @FXML
    private DatePicker tf_date;
    @FXML
    private TextField tf_desc;
    @FXML
    private TextField tf_lieu;
    @FXML
    private ComboBox<String> tf_statut;
    @FXML
    private ComboBox<String> tf_type;

    private String imagePath;
    private EvenementService evenementService;

    public AjouterEvenement() {
        this.evenementService = new EvenementService();
    }

    @FXML
    public void initialize() {
        // Initialisation des valeurs du ComboBox Type
        tf_type.getItems().addAll("Conférence médicale", "Événement sportif");

        // Initialisation des valeurs du ComboBox Statut
        tf_statut.getItems().addAll("Actif", "Complet", "Terminé");

        // Optionnel : définir une valeur par défaut
        tf_type.setValue("Conférence médicale");
        tf_statut.setValue("Actif");
    }

    @FXML
    public void ajouterImage(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));

        // Ouvrir la boîte de dialogue pour sélectionner une image
        File selectedFile = fileChooser.showOpenDialog(selectImageButton.getScene().getWindow());
        if (selectedFile != null) {
            // Stocker le chemin de l'image
            imagePath = selectedFile.getAbsolutePath();
            // Charger l'image dans les deux ImageView
            Image image = new Image("file:" + selectedFile.getAbsolutePath());
            imageView.setImage(image);

            System.out.println("Image sélectionnée : " + selectedFile.getAbsolutePath());
        }
    }

    @FXML
    public void AjouterEvent(ActionEvent actionEvent) {
        System.out.println("Début de AjouterEvent");
        try {
            // Récupérer et valider les champs
            String titre = tf_titre.getText() != null ? tf_titre.getText().trim() : "";
            if (titre.isEmpty() || titre.length() > 50) {
                afficherAlerte("Erreur", "Le titre ne doit pas être vide et ne doit pas dépasser 50 caractères.");
                return;
            }

            String description = tf_desc.getText() != null ? tf_desc.getText().trim() : "";
            if (description.isEmpty() || description.length() > 200) {
                afficherAlerte("Erreur", "La description ne doit pas être vide et ne doit pas dépasser 200 caractères.");
                return;
            }

            LocalDate localDate = tf_date.getValue();
            if (localDate == null) {
                afficherAlerte("Erreur", "Veuillez sélectionner une date !");
                return;
            }
            // Vérifier que la date est supérieure à la date actuelle
            LocalDate dateActuelle = LocalDate.now();
            if (!localDate.isAfter(dateActuelle)) {
                afficherAlerte("Erreur", "La date de l'événement doit être postérieure à la date actuelle (" + dateActuelle + ").");
                return;
            }
            String date = localDate.toString();

            String lieu = tf_lieu.getText() != null ? tf_lieu.getText().trim() : "";
            if (lieu.isEmpty() || lieu.length() > 50) {
                afficherAlerte("Erreur", "Le lieu ne doit pas être vide et ne doit pas dépasser 50 caractères.");
                return;
            }

            String statut = tf_statut.getValue() != null ? tf_statut.getValue().trim() : "";
            if (statut.isEmpty()) {
                afficherAlerte("Erreur", "Veuillez sélectionner un statut !");
                return;
            }

            String type = tf_type.getValue() != null ? tf_type.getValue().trim() : "";
            if (type.isEmpty()) {
                afficherAlerte("Erreur", "Veuillez sélectionner un type !");
                return;
            }

            int nbrplace;
            try {
                String nbrPlaceText = tf_nbr_place.getText() != null ? tf_nbr_place.getText().trim() : "";
                if (nbrPlaceText.isEmpty()) {
                    afficherAlerte("Erreur", "Le nombre de places ne peut pas être vide.");
                    return;
                }
                nbrplace = Integer.parseInt(nbrPlaceText);
                if (nbrplace <= 0) {
                    afficherAlerte("Erreur", "Le nombre de places doit être supérieur à 0.");
                    return;
                }
            } catch (NumberFormatException e) {
                afficherAlerte("Erreur", "Le nombre de places doit être un entier valide.");
                return;
            }

            int prix;
            try {
                String prixText = tf_prix.getText() != null ? tf_prix.getText().trim() : "";
                if (prixText.isEmpty()) {
                    afficherAlerte("Erreur", "Le prix ne peut pas être vide.");
                    return;
                }
                prix = Integer.parseInt(prixText);
                if (prix <= 0) {
                    afficherAlerte("Erreur", "Le prix doit être strictement positif.");
                    return;
                }
            } catch (NumberFormatException e) {
                afficherAlerte("Erreur", "Le prix doit être un entier valide.");
                return;
            }

            if (imageView.getImage() == null) {
                afficherAlerte("Erreur", "Veuillez sélectionner une image !");
                return;
            } else if (imagePath == null) {
                afficherAlerte("Erreur", "Erreur lors de la sélection de l'image. Veuillez réessayer.");
                return;
            }

            // Créer un nouvel événement
            System.out.println("Création de l'événement...");
            Evenement evenement = new Evenement(titre, description, date, lieu, statut, type, nbrplace, imagePath, prix);

            // Ajouter l'événement via le service
            System.out.println("Ajout de l'événement dans la base de données...");
            evenementService.ajouter(evenement);
            System.out.println("Événement ajouté avec succès.");

            // Afficher un message de succès
            System.out.println("Affichage du message de succès...");
            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Succès");
            success.setContentText("Événement ajouté avec succès.");
            success.show(); // Utilisation de show() pour éviter les problèmes de thread

        } catch (Exception e) {
            System.out.println("Erreur lors de l'ajout de l'événement : " + e.getMessage());
            afficherAlerte("Erreur", "Une erreur est survenue lors de l'ajout de l'événement : " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("Fin de AjouterEvent");
    }

    @FXML
    public void afficherEvent(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/afficherEventBack.fxml"));
            if (loader.getLocation() == null) {
                System.out.println("Erreur : Impossible de trouver afficherEventBack.fxml");
                afficherAlerte("Erreur", "Erreur lors du retour à la liste des événements.");
                return;
            }
            Parent root = loader.load();

            Stage stage = (Stage) tf_desc.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Liste des Événements");
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            System.out.println("Erreur lors du retour à la liste des événements : " + e.getMessage());
            afficherAlerte("Erreur", "Erreur lors du retour à la liste des événements.");
            e.printStackTrace();
        }
    }

    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show(); // Utilisation de show() pour éviter les problèmes de thread
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
    }
