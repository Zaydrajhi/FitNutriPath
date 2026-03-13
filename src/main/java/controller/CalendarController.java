package controller;

import entite.RendezVous;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import service.RendezVousService;

import java.util.List;

public class CalendarController {

    @FXML private GridPane calendarGrid;
    @FXML private VBox appointmentPanel;
    @FXML private Label monthLabel;
    @FXML private Button previousMonthBtn;
    @FXML private Button nextMonthBtn;
    @FXML private VBox appointmentListContainer;
    private LocalDate currentDate;
    private RendezVousService rendezVousService = new RendezVousService();

    public void initialize() {
        currentDate = LocalDate.now();
        updateCalendar();

        previousMonthBtn.setOnAction(e -> {
            currentDate = currentDate.minusMonths(1);
            updateCalendar();
        });
        nextMonthBtn.setOnAction(e -> {
            currentDate = currentDate.plusMonths(1);
            updateCalendar();
        });
    }

    private void updateCalendar() {
        monthLabel.setText(currentDate.getMonth().name() + " " + currentDate.getYear());
        calendarGrid.getChildren().clear();

        LocalDate firstDay = currentDate.withDayOfMonth(1);
        int firstDayWeekIndex = firstDay.getDayOfWeek().getValue() % 7;
        int totalDays = currentDate.lengthOfMonth();

        int col = firstDayWeekIndex;
        int row = 0;

        for (int day = 1; day <= totalDays; day++) {
            LocalDate date = currentDate.withDayOfMonth(day);

            VBox dayCard = new VBox();
            dayCard.setStyle("-fx-background-color: white; -fx-border-radius: 12; -fx-background-radius: 12; -fx-padding: 8;");
            dayCard.setPrefSize(100, 100);

            Label dayLabel = new Label(String.valueOf(day));
            dayLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
            dayCard.getChildren().add(dayLabel);

            dayCard.setOnMouseClicked(e -> showAppointments(date));

            calendarGrid.add(dayCard, col, row);

            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }
    }

    private void showAppointments(LocalDate date) {
        appointmentListContainer.getChildren().clear();

        Label title = new Label("📅 Rendez-vous — " + date.format(DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy")));
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #16A085; -fx-padding: 10 0 20 0;");
        appointmentListContainer.getChildren().add(title);

        try {
            List<RendezVous> all = rendezVousService.readAll();
            boolean hasAppointments = false;

            for (RendezVous rdv : all) {
                if (rdv.getDateEtHeure().toLocalDate().equals(date)) {
                    hasAppointments = true;

                    VBox card = new VBox();
                    card.setStyle(
                            "-fx-background-color: white;" +
                                    "-fx-background-radius: 14;" +
                                    "-fx-border-radius: 14;" +
                                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0.3, 0, 4);" +
                                    "-fx-padding: 12;" +
                                    "-fx-spacing: 5;" +
                                    "-fx-cursor: hand;"
                    );

                    Label timeLabel = new Label("🕒 " + rdv.getDateEtHeure().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
                    timeLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #34495E;");

                    Label nomLabel = new Label(rdv.getNom() + " — " + rdv.getType());
                    nomLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #2C3E50;");

                    card.getChildren().addAll(timeLabel, nomLabel);

                    // Animation légère au survol
                    card.setOnMouseEntered(e -> card.setStyle(
                            "-fx-background-color: #ECF9F6;" +
                                    "-fx-background-radius: 14;" +
                                    "-fx-border-radius: 14;" +
                                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 12, 0.3, 0, 6);" +
                                    "-fx-padding: 12;" +
                                    "-fx-spacing: 5;" +
                                    "-fx-cursor: hand;"
                    ));
                    card.setOnMouseExited(e -> card.setStyle(
                            "-fx-background-color: white;" +
                                    "-fx-background-radius: 14;" +
                                    "-fx-border-radius: 14;" +
                                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0.3, 0, 4);" +
                                    "-fx-padding: 12;" +
                                    "-fx-spacing: 5;" +
                                    "-fx-cursor: hand;"
                    ));

                    appointmentListContainer.getChildren().add(card);
                }
            }

            if (!hasAppointments) {
                Label emptyLabel = new Label("Aucun rendez-vous pour cette date.");
                emptyLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7F8C8D; -fx-padding: 20 0 0 0;");
                appointmentListContainer.getChildren().add(emptyLabel);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            Label error = new Label("Erreur lors du chargement des rendez-vous.");
            error.setStyle("-fx-text-fill: red;");
            appointmentListContainer.getChildren().add(error);
        }
    }
}
