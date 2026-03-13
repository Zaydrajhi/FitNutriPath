<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>
<?import javafx.scene.image.ImageView?>
<?import javafx.scene.image.Image?>

<AnchorPane xmlns:fx="http://javafx.com/fxml/1" fx:controller="controller.LoginController">

    <!-- Arrière-plan -->
    <AnchorPane fx:id="background" style="-fx-background-image: url('img/imgbck.jpg'); -fx-background-size: cover;" layoutX="0" layoutY="0" prefWidth="800.0" prefHeight="600.0"/>

    <!-- Conteneur principal -->
    <HBox layoutX="0" layoutY="0" prefWidth="800.0" prefHeight="600.0">
        <!-- Panneau de gauche -->
        <VBox fx:id="leftPanel" alignment="CENTER" spacing="20" prefWidth="300.0" prefHeight="600.0">
            <Label text="Connexion" style="-fx-font-size: 24px; -fx-font-weight: bold;"/>

            <!-- Formulaire de connexion -->
            <TextField fx:id="loginField" promptText="Login" style="-fx-font-size: 14px;"/>
            <PasswordField fx:id="passwordField" promptText="Mot de passe" style="-fx-font-size: 14px;"/>

            <Button text="Se connecter" fx:id="loginButton" style="-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 12px 24px;"/>


            <!-- Lien pour créer un compte -->
            <Button text="Pas de compte ? Créer un compte" fx:id="createAccountButton"/>

        </VBox>

        <!-- Panneau de droite -->
        <VBox fx:id="rightPanel" alignment="CENTER" spacing="10" prefWidth="500.0" prefHeight="600.0">
            <ImageView fx:id="logo" fitWidth="200.0" fitHeight="200.0" image="@img/logo1.jpeg"/>

            <Label text="FitNutriPath" style="-fx-font-size: 36px; -fx-font-weight: bold;"/>
            <Label text="Votre chemin vers une vie saine et équilibrée." style="-fx-font-size: 18px; -fx-font-style: italic;"/>
            <Label text="FitNutriPath est votre partenaire pour adopter une alimentation équilibrée et un mode de vie sain." style="-fx-font-size: 14px;"/>
        </VBox>
    </HBox>

</AnchorPane>
