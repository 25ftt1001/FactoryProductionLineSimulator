package controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class SimulationController implements Initializable {

    @FXML private VBox idleView;
    @FXML private ComboBox<String> lineSelector;
    @FXML private Label statusLabel;
    @FXML private Label progressPercent;
    @FXML private Label progressSubLabel;
    @FXML private Label targetLabel;
    @FXML private Label speedLabel;
    @FXML private Label productLabel;

    @FXML private VBox runningView;
    @FXML private Label runningProgressPercent;
    @FXML private Label runningProgressSub;
    @FXML private ProgressBar runningProgressBar;
    @FXML private Label elapsedLabel;
    @FXML private Label producedLabel;
    @FXML private Label defectiveLabel;
    @FXML private HBox runningStageBar;
    @FXML private Label runningCurrentStageLabel;

    @FXML private VBox pausedView;
    @FXML private Label pausedProgressPercent;
    @FXML private Label pausedProgressSub;
    @FXML private ProgressBar pausedProgressBar;
    @FXML private Label pausedTimeLabel;
    @FXML private Label pausedMessageLabel;

    @FXML private VBox progressView;
    @FXML private Label progressUnitsLabel;
    @FXML private Label progressTargetLabel;
    @FXML private Label progressDefectLabel;
    @FXML private VBox progressStagesBox;
    @FXML private VBox currentStageBox;

    @FXML private Label headerTitle;
    @FXML private Label stateIndicator;
    @FXML private Circle stateDot;

    private Timeline timeline;
    private int elapsed = 0;
    private int produced = 0;
    private int defective = 0;
    private final int target = 240;
    private double progress = 0.0;
    private boolean paused = false;
    private boolean running = false;
    private boolean finished = false;

    private static final String[] STAGES = {
        "Raw Material Feed", "Machining / Cutting", "Assembly",
        "Quality Inspection", "Packaging", "Dispatch"
    };

    private static final String[] STAGE_DESC = {
        "Feeding raw materials into the production line",
        "Cutting and shaping materials to specification",
        "Assembling components into finished product",
        "Checking unit tolerances and surface quality",
        "Packaging finished units for shipment",
        "Loading units onto transport vehicles"
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (lineSelector != null) {
            lineSelector.getItems().addAll("L01 — Line 01", "L02 — Line 02", "L03 — Line 03");
            lineSelector.getSelectionModel().selectFirst();
        }
        buildStageBar();
        buildStages(progressStagesBox);
        showIdle();
    }

    private void buildStageBar() {
        if (runningStageBar == null) return;
        runningStageBar.getChildren().clear();
        for (int i = 0; i < STAGES.length; i++) {
            VBox seg = new VBox(4);
            seg.setAlignment(Pos.CENTER);
            HBox.setHgrow(seg, Priority.ALWAYS);

            Region bar = new Region();
            bar.setPrefHeight(3);
            bar.setMaxWidth(Double.MAX_VALUE);
            bar.setStyle("-fx-background-color: #1f262e; -fx-background-radius: 2;");

            Label lbl = new Label(STAGES[i].split(" ")[0].toUpperCase());
            lbl.setStyle("-fx-text-fill: #475569; -fx-font-size: 8px; -fx-font-weight: bold;");

            seg.getChildren().addAll(bar, lbl);
            runningStageBar.getChildren().add(seg);
        }
    }

    private void updateStageBar(int currentStage) {
        if (runningStageBar == null) return;
        for (int i = 0; i < runningStageBar.getChildren().size(); i++) {
            VBox seg = (VBox) runningStageBar.getChildren().get(i);
            Region bar = (Region) seg.getChildren().get(0);
            Label lbl = (Label) seg.getChildren().get(1);

            if (i < currentStage) {
                bar.setStyle("-fx-background-color: #27c93f; -fx-background-radius: 2;");
                lbl.setStyle("-fx-text-fill: #27c93f; -fx-font-size: 8px; -fx-font-weight: bold;");
            } else if (i == currentStage) {
                bar.setStyle("-fx-background-color: #ffbd2e; -fx-background-radius: 2;");
                lbl.setStyle("-fx-text-fill: #ffbd2e; -fx-font-size: 8px; -fx-font-weight: bold;");
            } else {
                bar.setStyle("-fx-background-color: #1f262e; -fx-background-radius: 2;");
                lbl.setStyle("-fx-text-fill: #475569; -fx-font-size: 8px; -fx-font-weight: bold;");
            }
        }
    }

    private void buildStages(VBox container) {
        if (container == null) return;
        container.getChildren().clear();
        for (int i = 0; i < STAGES.length; i++) {
            HBox row = new HBox(8);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(4, 0, 4, 0));

            Circle dot = new Circle(5);
            dot.setFill(Color.web("#475569"));

            Label name = new Label(STAGES[i]);
            name.setStyle("-fx-text-fill: #6c7884; -fx-font-size: 11px;");

            Label check = new Label("");
            check.setStyle("-fx-text-fill: #27c93f; -fx-font-size: 11px; -fx-font-weight: bold;");

            row.getChildren().addAll(dot, name, check);
            container.getChildren().add(row);
        }
    }

    private void updateStageHighlight(int currentStage) {
        if (progressStagesBox == null) return;
        for (int i = 0; i < progressStagesBox.getChildren().size(); i++) {
            HBox row = (HBox) progressStagesBox.getChildren().get(i);
            Circle dot = (Circle) row.getChildren().get(0);
            Label name = (Label) row.getChildren().get(1);
            Label check = (Label) row.getChildren().get(2);

            if (i < currentStage) {
                dot.setFill(Color.web("#27c93f"));
                name.setStyle("-fx-text-fill: #27c93f; -fx-font-size: 11px;");
                check.setText("\u2713");
            } else if (i == currentStage) {
                dot.setFill(Color.web("#ffbd2e"));
                name.setStyle("-fx-text-fill: #ffbd2e; -fx-font-size: 11px; -fx-font-weight: bold;");
                check.setText("");
            } else {
                dot.setFill(Color.web("#475569"));
                name.setStyle("-fx-text-fill: #6c7884; -fx-font-size: 11px;");
                check.setText("");
            }
        }
    }

    private void hideAll() {
        if (idleView != null) { idleView.setVisible(false); idleView.setManaged(false); }
        if (runningView != null) { runningView.setVisible(false); runningView.setManaged(false); }
        if (pausedView != null) { pausedView.setVisible(false); pausedView.setManaged(false); }
        if (progressView != null) { progressView.setVisible(false); progressView.setManaged(false); }
    }

    private void showIdle() {
        hideAll();
        idleView.setVisible(true); idleView.setManaged(true);
        updateHeader("Simulation", "IDLE", "#8896a6");
    }

    private void showRunning() {
        hideAll();
        runningView.setVisible(true); runningView.setManaged(true);
        updateHeader("Simulation — Running", "RUNNING", "#27c93f");
    }

    private void showPaused() {
        hideAll();
        pausedView.setVisible(true); pausedView.setManaged(true);
        updateHeader("Simulation — Paused", "PAUSED", "#ffbd2e");
    }

    private void showProgress() {
        hideAll();
        progressView.setVisible(true); progressView.setManaged(true);
        updateHeader("Production Progress", "COMPLETED", "#27c93f");
    }

    private void updateHeader(String title, String state, String color) {
        if (headerTitle != null) headerTitle.setText(title);
        if (stateIndicator != null) {
            stateIndicator.setText(state);
            stateIndicator.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 10px; -fx-font-weight: bold;");
        }
        if (stateDot != null) stateDot.setFill(Color.web(color));
    }

    @FXML
    private void handleStart() {
        elapsed = 0;
        produced = 0;
        defective = 0;
        progress = 0.0;
        paused = false;
        running = true;
        finished = false;

        updateRunningUI();
        showRunning();

        if (timeline != null) timeline.stop();
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> tick()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    @FXML
    private void handlePause() {
        if (!running || paused) return;
        paused = true;
        updatePausedUI();
        showPaused();
    }

    @FXML
    private void handleResume() {
        if (!running || !paused) return;
        paused = false;
        showRunning();
        updateRunningUI();
    }

    @FXML
    private void handleStop() {
        running = false;
        paused = false;
        finished = true;
        if (timeline != null) timeline.stop();
        updateProgressUI();
        showProgress();
    }

    @FXML
    private void handleBack() {
        if (running && paused) {
            showPaused();
        } else if (running) {
            showRunning();
        } else {
            navigateTo("/views/dashboard.fxml");
        }
    }

    @FXML
    private void handleViewDetails() {
        updateProgressUI();
        showProgress();
    }

    private void tick() {
        if (paused || !running) return;
        elapsed++;

        double totalDuration = 120.0;
        progress = Math.min(elapsed / totalDuration, 1.0);
        produced = (int) (target * progress);

        if (elapsed % 15 == 0 && elapsed > 0 && defective < 5) defective++;

        updateRunningUI();
        updatePausedUI();

        if (progress >= 1.0) {
            running = false;
            finished = true;
            timeline.stop();
            updateProgressUI();
            showProgress();
        }
    }

    private void updateRunningUI() {
        if (runningProgressPercent != null)
            runningProgressPercent.setText(String.format("%.0f%%", progress * 100));
        if (runningProgressBar != null)
            runningProgressBar.setProgress(progress);
        if (runningProgressSub != null)
            runningProgressSub.setText(produced + " / " + target + " UNITS PRODUCED");
        if (elapsedLabel != null)
            elapsedLabel.setText(String.format("%02d:%02d", elapsed / 60, elapsed % 60));
        if (producedLabel != null) producedLabel.setText(String.valueOf(produced));
        if (defectiveLabel != null) defectiveLabel.setText(String.valueOf(defective));

        int stage = Math.min((int) (progress * STAGES.length), STAGES.length - 1);
        updateStageBar(stage);

        if (runningCurrentStageLabel != null)
            runningCurrentStageLabel.setText("CURRENT STAGE — " + STAGES[stage].toUpperCase());
    }

    private void updatePausedUI() {
        if (pausedProgressPercent != null)
            pausedProgressPercent.setText(String.format("%.0f%%", progress * 100));
        if (pausedProgressBar != null)
            pausedProgressBar.setProgress(progress);
        if (pausedProgressSub != null)
            pausedProgressSub.setText("Production temporarily halted — " + produced + " / " + target);
        if (pausedTimeLabel != null)
            pausedTimeLabel.setText("Production paused at " + String.format("%02d:%02d", elapsed / 60, elapsed % 60));
        if (pausedMessageLabel != null)
            pausedMessageLabel.setText("Resume to continue from current progress");
    }

    private void updateProgressUI() {
        if (progressUnitsLabel != null) progressUnitsLabel.setText(String.valueOf(produced));
        if (progressTargetLabel != null) progressTargetLabel.setText(String.valueOf(target));
        if (progressDefectLabel != null) progressDefectLabel.setText(String.valueOf(defective));

        int stage = Math.min((int) (progress * STAGES.length), STAGES.length - 1);
        updateStageHighlight(stage);

        if (currentStageBox != null) {
            currentStageBox.getChildren().clear();
            Label title = new Label("CURRENT STAGE");
            title.setStyle("-fx-text-fill: #8896a6; -fx-font-size: 10px; -fx-font-weight: bold;");
            Label name = new Label(STAGES[stage]);
            name.setStyle("-fx-text-fill: #ffbd2e; -fx-font-size: 14px; -fx-font-weight: bold;");
            Label desc = new Label(STAGE_DESC[stage]);
            desc.setStyle("-fx-text-fill: #6c7884; -fx-font-size: 11px;");
            desc.setWrapText(true);
            currentStageBox.getChildren().addAll(title, name, desc);
        }
    }

    // ==========================================
    // BOTTOM NAV — ActionEvent handlers (matches onAction)
    // ==========================================
    @FXML private void handleNavigateHome(ActionEvent e) { navigateTo("/views/dashboard.fxml"); }
    @FXML private void handleNavigateLines(ActionEvent e) { navigateTo("/views/line_list.fxml"); }
    @FXML private void handleNavigateSimulate(ActionEvent e) { showIdle(); }
    @FXML private void handleNavigateReports(ActionEvent e) { /* placeholder */ }
    @FXML private void handleNavigateProfile(ActionEvent e) { /* placeholder */ }

    // ==========================================
    // NAV — Handles both FXML and Java views
    // ==========================================
    private void navigateTo(String path) {
        try {
            // FXML file exists?
            URL loc = getClass().getResource(path);
            if (loc != null) {
                Parent newRoot = FXMLLoader.load(loc);
                Stage stage = (Stage) headerTitle.getScene().getWindow();
                stage.setScene(new Scene(newRoot, 520, 850));
                stage.show();
                return;
            }

            // Dashboard path? Use Java-based DashboardView
            if (path.contains("dashboard")) {
                Stage stage = (Stage) headerTitle.getScene().getWindow();
                stage.setScene(new Scene(new DashboardView().getRoot(), 520, 850));
                stage.setTitle("Factory Control App");
                stage.show();
                return;
            }

            System.out.println("Could not find: " + path);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}