package test;
import controller.LoginController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MainFX extends Application {

    private Stage primaryStage;
    private static MainFX instance;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        instance = this;
        showLogin();
    }

    public static MainFX getInstance() {
        return instance;
    }

    private void showLogin() {
        try {
            // Charger le fichier FXML pour la scène de login
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = fxmlLoader.load();

            // Passer l'instance de MainFX au contrôleur de Login
            LoginController loginController = fxmlLoader.getController();
            loginController.setMainApp(this);

            // Créer et afficher la scène
            Scene scene = new Scene(root);
            primaryStage.setTitle("Login");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load login FXML file: " + e.getMessage());
        }
    }

    // Méthode pour afficher la scène de création de compte
    public void showCreationCompte() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/creationcompte.fxml"));
            Parent root = fxmlLoader.load();

            Scene scene = new Scene(root);
            primaryStage.setTitle("Création de Compte");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load creationcompte FXML file: " + e.getMessage());
        }
    }

    // Méthode pour rediriger vers la scène d'admin si la connexion réussit
    public void redirectToAdmin() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/listeuser.fxml"));
            Parent root = fxmlLoader.load();

            Scene scene = new Scene(root);
            primaryStage.setTitle("Admin Dashboard");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to load ListeUser FXML file: " + e.getMessage());
        }
    }
}
