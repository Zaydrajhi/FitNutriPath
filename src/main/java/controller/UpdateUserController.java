package controller;

import entite.User;
import service.UtilisateurService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class UpdateUserController {

    private User user; // Add this field to store the selected user

    @FXML
    private TextField tfLogin;

    @FXML
    private TextField tfNom;

    @FXML
    private TextField tfPrenom;

    @FXML
    private TextField tfTel;

    @FXML
    private TextField tfEmail;

    @FXML
    private ComboBox<String> cbRole;

    @FXML
    private TextField tfImage;

    @FXML
    private Button btnUpdate;

    private UtilisateurService utilisateurService = new UtilisateurService();

    // Method to set the user for editing
    public void setUser(User user) {
        this.user = user;
        // Initialize the fields with the user's current data
        tfLogin.setText(user.getLogin());
        tfNom.setText(user.getNom());
        tfPrenom.setText(user.getPrenom());
        tfTel.setText(user.getNumTel());
        tfEmail.setText(user.getEmail());
        tfImage.setText(user.getImage());
        cbRole.setValue(user.getRole()); // Assuming role values are pre-defined
    }

    @FXML
    private void updateUser() {
        if (user != null) {
            // Récupérer les valeurs des champs de texte et de la comboBox
            String login = tfLogin.getText();
            String nom = tfNom.getText();
            String prenom = tfPrenom.getText();
            String numTel = tfTel.getText();
            String email = tfEmail.getText();
            String image = tfImage.getText();
            String role = cbRole.getValue();

            // Mettre à jour l'utilisateur
            user.setLogin(login);
            user.setNom(nom);
            user.setPrenom(prenom);
            user.setNumTel(numTel);
            user.setEmail(email);
            user.setImage(image);
            user.setRole(role);

            // Appeler la méthode updateUser dans UtilisateurService pour mettre à jour la base de données
            boolean isUpdated = utilisateurService.updateUser(user.getId(), login, nom, prenom, numTel, email, role, image);

            if (isUpdated) {
                // Afficher un message de succès
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Mise à jour réussie");
                alert.setHeaderText(null);
                alert.setContentText("L'utilisateur a été mis à jour avec succès.");
                alert.showAndWait();
                closeWindow(); // Fermer la fenêtre après la mise à jour
            } else {
                // Afficher un message d'erreur si la mise à jour échoue
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur de mise à jour");
                alert.setHeaderText(null);
                alert.setContentText("Une erreur est survenue lors de la mise à jour.");
                alert.showAndWait();
            }
        }
    }


    // Close the window after update
    private void closeWindow() {
        Stage stage = (Stage) btnUpdate.getScene().getWindow();
        stage.close();
    }
}
