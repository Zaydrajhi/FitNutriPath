package controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.Image;
import entite.Regime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import service.RegimeService;
import service.IAService;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.chart.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.Priority;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.BubbleChart;
import javafx.scene.layout.GridPane;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;
import java.util.ArrayList;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AfficherRegime1Controller {

    @FXML
    private TableView<Regime> regimeTable;
    @FXML
    private TableColumn<Regime, String> titreCol;
    @FXML
    private TableColumn<Regime, String> descriptionCol;
    @FXML
    private TableColumn<Regime, String> objectifCol;
    @FXML
    private TableColumn<Regime, Integer> dureeCol;
    @FXML
    private TableColumn<Regime, Integer> caloriesCol;
    @FXML
    private TableColumn<Regime, String> activiteCol;
    @FXML
    private TextField pdfPathField;
    @FXML
    private Button choosePdfLocation;
    @FXML
    private Button exportPdfButton;
    @FXML
    private Button analyseIAButton;
    @FXML
    private Button retourButton;
    
    private int currentRendezVousId;
    private final RegimeService regimeService = new RegimeService();
    private final ObservableList<Regime> regimeList = FXCollections.observableArrayList();
    private final IAService iaService = new IAService();

    @FXML
    public void initialize() {
        titreCol.setCellValueFactory(new PropertyValueFactory<>("titre"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        objectifCol.setCellValueFactory(new PropertyValueFactory<>("objectif"));
        dureeCol.setCellValueFactory(new PropertyValueFactory<>("duree"));
        caloriesCol.setCellValueFactory(new PropertyValueFactory<>("caloriesCible"));
        activiteCol.setCellValueFactory(new PropertyValueFactory<>("niveauActivite"));

        loadAllRegimes();
        setupPdfExport();
    }

    private void setupPdfExport() {
        choosePdfLocation.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer le PDF");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf")
            );
            
            File file = fileChooser.showSaveDialog(choosePdfLocation.getScene().getWindow());
            
            if (file != null) {
                pdfPathField.setText(file.getAbsolutePath());
            }
        });

        exportPdfButton.setOnAction(event -> {
            String pdfPath = pdfPathField.getText();
            if (pdfPath != null && !pdfPath.isEmpty()) {
                exportToPdf(pdfPath);
            } else {
                showAlert("Attention", "Veuillez sélectionner un emplacement pour sauvegarder le PDF.", Alert.AlertType.WARNING);
            }
        });
    }

    private void exportToPdf(String filePath) {
        try {
            // Création du document avec des marges personnalisées
            Document document = new Document(PageSize.A4, 36, 36, 90, 36);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // Ajout de l'en-tête avec logo
            try {
                Image logo = Image.getInstance(getClass().getResource("ressources/images/logo.jpeg"));
                logo.scaleToFit(100, 100);
                logo.setAlignment(Element.ALIGN_CENTER);
                document.add(logo);
            } catch (Exception e) {
                // Si le logo n'est pas trouvé, on continue sans logo
                System.out.println("Logo non trouvé");
            }

            // Titre principal avec style amélioré
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, new BaseColor(41, 128, 185));
            Paragraph title = new Paragraph("Programme de Régimes", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingBefore(20);
            title.setSpacingAfter(10);
            document.add(title);

            // Sous-titre avec la date
            Font subtitleFont = new Font(Font.FontFamily.HELVETICA, 12, Font.ITALIC, new BaseColor(127, 140, 141));
            String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm"));
            Paragraph subtitle = new Paragraph("Généré le " + dateStr, subtitleFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(20);
            document.add(subtitle);

            // Ligne de séparation décorative
            LineSeparator line = new LineSeparator();
            line.setLineColor(new BaseColor(52, 152, 219));
            line.setLineWidth(2);
            document.add(line);
            document.add(new Paragraph("\n"));

            // Création du tableau avec style amélioré
            PdfPTable pdfTable = new PdfPTable(6);
            pdfTable.setWidthPercentage(100);
            pdfTable.setSpacingBefore(20);
            pdfTable.setSpacingAfter(20);

            // Définition des largeurs relatives des colonnes
            float[] columnWidths = {2f, 3f, 2f, 1.5f, 1.5f, 2f};
            pdfTable.setWidths(columnWidths);

            // Style pour les en-têtes
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
            BaseColor headerBackground = new BaseColor(52, 152, 219); // Bleu

            // En-têtes du tableau
            String[] headers = {"Titre", "Description", "Objectif", "Durée", "Calories", "Activité"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBackgroundColor(headerBackground);
                cell.setPadding(8);
                cell.setMinimumHeight(30);
                pdfTable.addCell(cell);
            }

            // Style pour le contenu
            Font contentFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, new BaseColor(44, 62, 80));
            BaseColor altBackground = new BaseColor(236, 240, 241); // Gris très clair

            // Ajout des données avec alternance de couleurs
            boolean alternate = false;
            for (Regime regime : regimeTable.getItems()) {
                BaseColor backgroundColor = alternate ? altBackground : BaseColor.WHITE;

                addStyledCell(pdfTable, regime.getTitre(), contentFont, backgroundColor);
                addStyledCell(pdfTable, regime.getDescription(), contentFont, backgroundColor);
                addStyledCell(pdfTable, regime.getObjectif(), contentFont, backgroundColor);
                addStyledCell(pdfTable, String.valueOf(regime.getDuree()), contentFont, backgroundColor);
                addStyledCell(pdfTable, String.valueOf(regime.getCaloriesCible()), contentFont, backgroundColor);
                addStyledCell(pdfTable, regime.getNiveauActivite(), contentFont, backgroundColor);

                alternate = !alternate;
            }

            document.add(pdfTable);

            // Pied de page
            Font footerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, new BaseColor(127, 140, 141));
            Paragraph footer = new Paragraph("FitNutripath - Votre partenaire santé et bien-être", footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(20);
            document.add(footer);

            document.close();

            showAlert("Succès", "Le PDF a été généré avec succès !\nEmplacement: " + filePath, Alert.AlertType.INFORMATION);

        } catch (Exception e) {
            showAlert("Erreur", "Une erreur est survenue lors de la génération du PDF : " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void addStyledCell(PdfPTable table, String content, Font font, BaseColor backgroundColor) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBackgroundColor(backgroundColor);
        cell.setPadding(6);
        cell.setMinimumHeight(25);
        table.addCell(cell);
    }

    public void loadRegimeForRendezVous(int rdvId) {
        this.currentRendezVousId = rdvId;
        try {
            regimeList.clear();
            List<Regime> regimes = regimeService.getRegimesByRendezVous(rdvId);
            regimeList.addAll(regimes);
            regimeTable.setItems(regimeList);
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de charger les régimes liés : " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    /**
     * Chargement complet si aucune restriction.
     */
    private void loadAllRegimes() {
        try {
            regimeList.clear();
            regimeList.addAll(regimeService.readAll());
            regimeTable.setItems(regimeList);
        } catch (Exception e) {
            showAlert("Erreur", "Échec du chargement : " + e.getMessage(), Alert.AlertType.ERROR);
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
    private void analyserRegimeIA() {
        Regime regime = regimeTable.getSelectionModel().getSelectedItem();
        if (regime == null) {
            showAlert("Information", "Veuillez sélectionner un régime à analyser", Alert.AlertType.INFORMATION);
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Analyse IA Avancée du Régime");

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        content.setStyle("-fx-background-color: white;");

        // Création d'une grille pour organiser les graphiques
        GridPane chartsGrid = new GridPane();
        chartsGrid.setHgap(10);
        chartsGrid.setVgap(10);
        chartsGrid.setAlignment(Pos.CENTER);

        // 1. Score et Prédiction
        VBox scoreSection = createScoreSection(regime);
        scoreSection.setPrefWidth(300);
        chartsGrid.add(scoreSection, 0, 0);
        
        // 2. Graphique Circulaire des Nutriments
        VBox nutrientsChart = createNutrientsChart(regime);
        nutrientsChart.setPrefWidth(300);
        chartsGrid.add(nutrientsChart, 1, 0);
        
        // 3. Graphique de Progression
        VBox progressionChart = createProgressionChart(regime);
        progressionChart.setPrefWidth(300);
        chartsGrid.add(progressionChart, 0, 1);
        
        // 4. Graphique Bulle 3D
        VBox bubbleChart = createBubbleChart(regime);
        bubbleChart.setPrefWidth(300);
        chartsGrid.add(bubbleChart, 1, 1);
        
        // 5. Recommandations
        VBox recommendationsSection = createRecommendationsSection(regime);
        recommendationsSection.setPrefWidth(600);
        
        // 6. Distribution des Calories
        VBox caloriesSection = createCaloriesDistributionChart(regime);
        caloriesSection.setPrefWidth(600);

        content.getChildren().addAll(
            chartsGrid,
            recommendationsSection,
            caloriesSection
        );

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(600);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        // Ajout du bouton de retour
        ButtonType retourButtonType = new ButtonType("Retour", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(retourButtonType);
        
        // Configuration du bouton de retour
        Button retourButton = (Button) dialog.getDialogPane().lookupButton(retourButtonType);
        retourButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");
        retourButton.setOnAction(event -> dialog.close());

        dialog.getDialogPane().setContent(scrollPane);
        dialog.getDialogPane().setPrefWidth(800);
        dialog.getDialogPane().setPrefHeight(700);

        // Centrer la fenêtre sur l'écran
        dialog.setResizable(true);
        dialog.showAndWait();
    }

    private VBox createScoreSection(Regime regime) {
        VBox section = new VBox(5);
        section.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 10; -fx-background-radius: 10;");
        section.setPrefHeight(200);

        double score = iaService.predireReussiteRegime(regime, "moyen", false, false);
        
        Label scoreTitle = new Label("Score de Réussite Prévu");
        scoreTitle.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

        ProgressBar progressBar = new ProgressBar(score / 100);
        progressBar.setPrefWidth(150);
        progressBar.setStyle(score > 70 ? "-fx-accent: #2ecc71;" : 
                           score > 50 ? "-fx-accent: #f1c40f;" : 
                                      "-fx-accent: #e74c3c;");

        Label scoreLabel = new Label(String.format("%.1f%%", score));
        scoreLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");

        section.getChildren().addAll(scoreTitle, progressBar, scoreLabel);
        return section;
    }

    private VBox createNutrientsChart(Regime regime) {
        VBox section = new VBox(5);
        section.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 10; -fx-background-radius: 10;");
        section.setPrefHeight(200);

        Label title = new Label("Répartition des Nutriments");
        title.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

        PieChart pieChart = new PieChart();
        pieChart.setTitle("Distribution Recommandée");
        pieChart.setPrefSize(250, 150);
        
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
            new PieChart.Data("Protéines", 30),
            new PieChart.Data("Glucides", 40),
            new PieChart.Data("Lipides", 30)
        );
        
        pieChart.setData(pieChartData);
        pieChart.setLabelsVisible(true);
        pieChart.setStartAngle(90);

        // Ajout de couleurs personnalisées
        pieChartData.forEach(data -> {
            switch (data.getName()) {
                case "Protéines" -> data.getNode().setStyle("-fx-pie-color: #2ecc71;");
                case "Glucides" -> data.getNode().setStyle("-fx-pie-color: #3498db;");
                case "Lipides" -> data.getNode().setStyle("-fx-pie-color: #e74c3c;");
            }
        });

        section.getChildren().addAll(title, pieChart);
        return section;
    }

    private VBox createProgressionChart(Regime regime) {
        VBox section = new VBox(5);
        section.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 10; -fx-background-radius: 10;");
        section.setPrefHeight(200);

        Label title = new Label("Progression Prévue");
        title.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

        // Création du graphique linéaire
        NumberAxis xAxis = new NumberAxis(0, regime.getDuree(), 1);
        NumberAxis yAxis = new NumberAxis(0, 100, 10);
        xAxis.setLabel("Jours");
        yAxis.setLabel("Progression (%)");

        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Évolution Attendue");
        lineChart.setPrefSize(250, 150);

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Progression");

        // Génération de la courbe de progression
        for (int i = 0; i <= regime.getDuree(); i++) {
            double progress = 100 * (1 - Math.exp(-0.1 * i));
            series.getData().add(new XYChart.Data<>(i, progress));
        }

        lineChart.getData().add(series);
        lineChart.setCreateSymbols(false);

        section.getChildren().addAll(title, lineChart);
        return section;
    }

    private VBox createBubbleChart(Regime regime) {
        VBox section = new VBox(5);
        section.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 10; -fx-background-radius: 10;");
        section.setPrefHeight(200);

        Label title = new Label("Analyse 3D des Paramètres");
        title.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

        // Création des axes avec des plages plus appropriées
        NumberAxis xAxis = new NumberAxis(0, 100, 10);
        NumberAxis yAxis = new NumberAxis(0, 100, 10);
        xAxis.setLabel("Efficacité");
        yAxis.setLabel("Adaptabilité");

        BubbleChart<Number, Number> bubbleChart = new BubbleChart<>(xAxis, yAxis);
        bubbleChart.setTitle("Analyse des Paramètres");
        bubbleChart.setPrefSize(250, 150);
        bubbleChart.setLegendVisible(true);

        // Calcul des scores normalisés
        double efficacite = iaService.calculerScoreEfficacite(regime);
        double adaptabilite = iaService.calculerScoreAdaptabilite(regime);
        double intensite = regime.getCaloriesCible() / 2000.0 * 100; // Normalisation des calories

        // Série 1: Efficacité vs Adaptabilité
        XYChart.Series<Number, Number> series1 = new XYChart.Series<>();
        series1.setName("Paramètres Principaux");
        XYChart.Data<Number, Number> data1 = new XYChart.Data<>(efficacite, adaptabilite, intensite);
        series1.getData().add(data1);

        // Série 2: Points de référence
        XYChart.Series<Number, Number> series2 = new XYChart.Series<>();
        series2.setName("Points de Référence");
        series2.getData().add(new XYChart.Data<>(50, 50, 50)); // Point de référence moyen
        series2.getData().add(new XYChart.Data<>(80, 80, 80)); // Point de référence optimal

        bubbleChart.getData().addAll(series1, series2);

        // Personnalisation des couleurs
        for (XYChart.Series<Number, Number> series : bubbleChart.getData()) {
            for (XYChart.Data<Number, Number> data : series.getData()) {
                Node node = data.getNode();
                if (node != null) {
                    if (series.getName().equals("Paramètres Principaux")) {
                        node.setStyle("-fx-background-color: #3498db;");
                    } else {
                        node.setStyle("-fx-background-color: #95a5a6;");
                    }
                }
            }
        }

        section.getChildren().addAll(title, bubbleChart);
        return section;
    }

    private VBox createCaloriesDistributionChart(Regime regime) {
        VBox section = new VBox(5);
        section.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 10; -fx-background-radius: 10;");
        section.setPrefHeight(200);

        Label title = new Label("Distribution des Calories sur la Journée");
        title.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

        PieChart pieChart = new PieChart();
        pieChart.setTitle("Répartition Journalière");
        pieChart.setPrefSize(500, 150);
        
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
            new PieChart.Data("Petit Déjeuner (25%)", 25),
            new PieChart.Data("Déjeuner (35%)", 35),
            new PieChart.Data("Collation (15%)", 15),
            new PieChart.Data("Dîner (25%)", 25)
        );
        
        pieChart.setData(pieChartData);
        pieChart.setLabelsVisible(true);
        pieChart.setStartAngle(90);

        // Ajout de couleurs personnalisées
        String[] colors = {"#f1c40f", "#e67e22", "#3498db", "#2ecc71"};
        int i = 0;
        for (PieChart.Data data : pieChartData) {
            data.getNode().setStyle("-fx-pie-color: " + colors[i++] + ";");
        }

        section.getChildren().addAll(title, pieChart);
        return section;
    }

    private VBox createRecommendationsSection(Regime regime) {
        VBox section = new VBox(5);
        section.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 10; -fx-background-radius: 10;");
        section.setPrefHeight(150);

        Label title = new Label("Recommandations Personnalisées");
        title.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

        VBox recommendationsBox = new VBox(5);
        List<String> recommendations = iaService.genererRecommandations(regime);

        for (String rec : recommendations) {
            Label recLabel = new Label(rec);
            recLabel.setStyle("-fx-padding: 5; -fx-background-color: #e8f5e9; -fx-background-radius: 5;");
            recLabel.setWrapText(true);
            recommendationsBox.getChildren().add(recLabel);
        }

        section.getChildren().addAll(title, recommendationsBox);
        return section;
    }

    @FXML
    private void handleRetour() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RendezVousListUser.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) retourButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des Rendez-vous");
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de retourner à la liste des rendez-vous : " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
}
