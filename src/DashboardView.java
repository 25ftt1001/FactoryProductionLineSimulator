package controllers;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DashboardView {

    // --- COLORS ---
    public static final String BG_COLOR = "#0f1216";
    public static final String CARD_BG = "#14181d";
    public static final String TEXT_MUTED = "#6c7884";
    public static final String TEXT_LIGHT = "#e2e8f0";
    public static final String ACCENT_YELLOW = "#ffbd2e";
    public static final String ACCENT_RED = "#ff5f56";
    public static final String ACCENT_GREEN = "#27c93f";
    public static final String BORDER_COLOR = "#1f262e";

    private BorderPane root;
    private VBox mainContentArea;
    private HBox[] navItems;
    private int simState = 0;
    private boolean profileEditMode = false;

    // Profile data comes from UserSession
    private String profileName;
    private String profileEmail;

    public DashboardView() {
        // Pull user info from the session
        profileName = UserSession.displayName;
        profileEmail = UserSession.email;

        root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG_COLOR + ";");

        VBox topContainer = new VBox(15);
        topContainer.setPadding(new Insets(25, 25, 20, 25));

        mainContentArea = new VBox(15);
        topContainer.getChildren().add(mainContentArea);

        HBox bottomNav = createBottomNavigation();

        root.setCenter(topContainer);
        root.setBottom(bottomNav);

        // ⭐ Start on HOME view
        setActiveNav(0);
        showHomeView();
    }

    public BorderPane getRoot() {
        return root;
    }

    // ==========================================
    // HOME VIEW
    // ==========================================
    private void showHomeView() {
        mainContentArea.getChildren().clear();
        VBox view = new VBox(20);

        // Header with greeting + date + ONLINE status
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox greetingBox = new VBox(5);
        Label greeting = new Label("Good Morning, " + profileName);
        greeting.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-weight: bold;");

        String formattedDate = LocalDate.now().format(
                DateTimeFormatter.ofPattern("EEE dd MMM yyyy", Locale.ENGLISH)
        ).toUpperCase();
        Label dateLabel = new Label(formattedDate);
        dateLabel.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 11px; -fx-letter-spacing: 1px;");
        greetingBox.getChildren().addAll(greeting, dateLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox statusBox = new HBox(6);
        statusBox.setAlignment(Pos.CENTER);
        statusBox.getChildren().addAll(
            new Circle(4, Color.web("#00e5a0")),
            createStyledLabel("ONLINE", "#00e5a0", 11, true)
        );

        header.getChildren().addAll(greetingBox, spacer, statusBox);
        view.getChildren().add(header);

        // Metrics
        HBox metricsGrid = new HBox(12);
        metricsGrid.getChildren().addAll(
            createMetricCard("PRODUCED", "359", ACCENT_YELLOW),
            createMetricCard("EFFICIENCY", "87%", "white"),
            createMetricCard("DEFECTIVE", "11", ACCENT_RED)
        );
        view.getChildren().add(metricsGrid);

        // Line status
        Label lineTitle = createStyledLabel("LINE STATUS", TEXT_MUTED, 11, true);
        view.getChildren().add(lineTitle);

        VBox linesList = new VBox(12);
        linesList.getChildren().addAll(
            createLineCard("Alpha Line", "RUNNING", ACCENT_GREEN, 0.75),
            createLineCard("Beta Line", "PAUSED", ACCENT_YELLOW, 0.45),
            createLineCard("Gamma Line", "IDLE", "#8896a6", 0.0)
        );
        view.getChildren().add(linesList);

        // Alert
        HBox alertBox = new HBox(10);
        alertBox.setAlignment(Pos.CENTER_LEFT);
        alertBox.setPadding(new Insets(12, 15, 12, 15));
        alertBox.setStyle("-fx-background-color: rgba(255, 95, 86, 0.05); -fx-border-color: rgba(255, 95, 86, 0.2); -fx-border-radius: 8; -fx-background-radius: 8;");
        alertBox.getChildren().addAll(
            createStyledLabel("⚠", ACCENT_RED, 14, true),
            createStyledLabel("LOW STOCK — COOLANT FLUID: 38L", ACCENT_RED, 11, true)
        );
        view.getChildren().add(alertBox);

        mainContentArea.getChildren().add(view);
    }

    // ==========================================
    // BOTTOM NAVIGATION
    // ==========================================
    private HBox createBottomNavigation() {
        HBox bottomNav = new HBox();
        bottomNav.setStyle("-fx-background-color: " + CARD_BG + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-width: 1 0 0 0;");
        bottomNav.setPadding(new Insets(12, 10, 15, 10));
        bottomNav.setAlignment(Pos.CENTER);

        navItems = new HBox[5];
        navItems[0] = createNavItem("HOME", "M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z M9 22V12h6v10", 0);
        navItems[1] = createNavItem("LINES", "M8 6h13 M8 12h13 M8 18h13 M3 6h.01 M3 12h.01 M3 18h.01", 1);
        navItems[2] = createNavItem("SIMULATE", "M5 3l14 9-14 9V3z", 2);
        navItems[3] = createNavItem("REPORTS", "M3 3h18v18H3z M3 9h18 M9 21V9", 3);
        navItems[4] = createNavItem("PROFILE", "M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2 M12 7a4 4 0 1 0 0-8 4 4 0 0 0 0 8z", 4);

        bottomNav.getChildren().addAll(navItems);
        for (HBox item : navItems) {
            HBox.setHgrow(item, Priority.ALWAYS);
        }
        return bottomNav;
    }

    private HBox createNavItem(String label, String svgPathData, int index) {
        HBox navItem = new HBox(6);
        navItem.setAlignment(Pos.CENTER);
        navItem.setStyle("-fx-cursor: hand;");
        VBox content = new VBox(6);
        content.setAlignment(Pos.CENTER);

        SVGPath icon = new SVGPath();
        icon.setContent(svgPathData);
        icon.setStroke(Color.web(TEXT_MUTED));
        icon.setFill(null);
        icon.setStrokeWidth(2);

        Label navLabel = new Label(label);
        navLabel.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 9px; -fx-font-weight: bold;");

        content.getChildren().addAll(icon, navLabel);
        navItem.getChildren().add(content);

        navItem.setOnMouseClicked(e -> {
            setActiveNav(index);
            switch (index) {
                case 0: showHomeView(); break;
                case 1: mainContentArea.getChildren().setAll(createPlaceholder("Production Lines")); break;
                case 2: simState = 0; showSimulatorView(); break;
                case 3: mainContentArea.getChildren().setAll(createProductionProgressView()); break;
                case 4: profileEditMode = false; mainContentArea.getChildren().setAll(createProfileView()); break;
            }
        });

        return navItem;
    }

    private void setActiveNav(int activeIndex) {
        for (int i = 0; i < navItems.length; i++) {
            VBox content = (VBox) navItems[i].getChildren().get(0);
            SVGPath icon = (SVGPath) content.getChildren().get(0);
            Label label = (Label) content.getChildren().get(1);

            if (i == activeIndex) {
                icon.setStroke(Color.web(ACCENT_YELLOW));
                label.setStyle("-fx-text-fill: " + ACCENT_YELLOW + "; -fx-font-size: 9px; -fx-font-weight: bold;");
            } else {
                icon.setStroke(Color.web(TEXT_MUTED));
                label.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 9px; -fx-font-weight: bold;");
            }
        }
    }

    // ==========================================
    // SIMULATOR VIEWS (unchanged)
    // ==========================================
    private void showSimulatorView() {
        mainContentArea.getChildren().clear();
        switch (simState) {
            case 0: mainContentArea.getChildren().add(createSimulatorIdleView()); break;
            case 1: mainContentArea.getChildren().add(createSimulatorRunningView()); break;
            case 2: mainContentArea.getChildren().add(createSimulatorPausedView()); break;
        }
    }

    private VBox createSimulatorIdleView() {
        VBox view = new VBox(12);
        view.getChildren().add(createStyledLabel("Simulator", "white", 16, true));

        VBox selectBox = new VBox(5);
        selectBox.getChildren().add(createStyledLabel("SELECT PRODUCTION LINE", TEXT_MUTED, 9, true));
        HBox dropdown = new HBox();
        dropdown.setPadding(new Insets(10, 12, 10, 12));
        dropdown.setStyle("-fx-background-color: " + CARD_BG + "; -fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 5; -fx-background-radius: 5;");
        dropdown.getChildren().add(createStyledLabel("L01 — Line 01", TEXT_LIGHT, 12, false));
        selectBox.getChildren().add(dropdown);
        view.getChildren().add(selectBox);

        VBox statsCard = createCard();
        statsCard.setAlignment(Pos.CENTER);
        statsCard.setPadding(new Insets(25));
        statsCard.getChildren().addAll(
            createStyledLabel("IDLE", ACCENT_YELLOW, 10, true),
            createStyledLabel("0%", "white", 50, true),
            createStyledLabel("359 UNITS / 500 UNITS TARGET", TEXT_MUTED, 10, false)
        );
        ProgressBar progress = new ProgressBar(0);
        progress.setPrefWidth(250);
        progress.setStyle("-fx-accent: " + ACCENT_YELLOW + ";");
        statsCard.getChildren().add(progress);
        view.getChildren().add(statsCard);

        HBox metricsRow = new HBox(10);
        metricsRow.getChildren().addAll(
            createMetricIconCard("240", "Man", "M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"),
            createMetricIconCard("96%", "Machine", "M20 8h-3V4H3c-1.1 0-2 .9-2 2v11h2c0 1.66 1.34 3 3 3s3-1.34 3-3h6c0 1.66 1.34 3 3 3s3-1.34 3-3h2v-5l-3-4z"),
            createMetricIconCard("7 m", "Engine", "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2z")
        );
        for (var node : metricsRow.getChildren()) HBox.setHgrow(node, Priority.ALWAYS);
        view.getChildren().add(metricsRow);

        Button startBtn = createPrimaryButton("▶  START SIMULATION");
        startBtn.setOnAction(e -> { simState = 1; showSimulatorView(); });
        view.getChildren().add(startBtn);

        return view;
    }

    private VBox createSimulatorRunningView() {
        VBox view = new VBox(12);
        view.getChildren().add(createStyledLabel("Simulator — Running", "white", 16, true));

        VBox progressCard = createCard();
        progressCard.setAlignment(Pos.CENTER);
        progressCard.setPadding(new Insets(25));
        progressCard.getChildren().addAll(
            createStyledLabel("• SIMULATION RUNNING", ACCENT_GREEN, 10, true),
            createStyledLabel("62%", ACCENT_YELLOW, 50, true)
        );
        ProgressBar progress = new ProgressBar(0.62);
        progress.setPrefWidth(300);
        progress.setStyle("-fx-accent: " + ACCENT_YELLOW + ";");
        progressCard.getChildren().add(progress);
        view.getChildren().add(progressCard);

        HBox metricsRow = new HBox(10);
        metricsRow.getChildren().addAll(
            createSmallMetricCard("03:41", "Time"),
            createSmallMetricCard("186", "Units"),
            createSmallMetricCard("3", "Lines")
        );
        for (var node : metricsRow.getChildren()) HBox.setHgrow(node, Priority.ALWAYS);
        view.getChildren().add(metricsRow);

        HBox actionsRow = new HBox(10);
        Button pauseBtn = new Button("⏸  PAUSE");
        styleButton(pauseBtn, ACCENT_YELLOW);
        pauseBtn.setPrefWidth(160);
        pauseBtn.setOnAction(e -> { simState = 2; showSimulatorView(); });

        Button stopBtn = new Button("⏹  STOP");
        styleButton(stopBtn, ACCENT_RED);
        stopBtn.setPrefWidth(160);
        stopBtn.setOnAction(e -> { simState = 0; showSimulatorView(); });

        actionsRow.getChildren().addAll(pauseBtn, stopBtn);
        actionsRow.setAlignment(Pos.CENTER);
        view.getChildren().add(actionsRow);

        return view;
    }

    private VBox createSimulatorPausedView() {
        VBox view = new VBox(12);
        view.getChildren().add(createStyledLabel("Simulator — Paused", "white", 16, true));

        VBox progressCard = createCard();
        progressCard.setAlignment(Pos.CENTER);
        progressCard.setPadding(new Insets(25));

        Label pct = createStyledLabel("62%", ACCENT_YELLOW, 50, true);
        ProgressBar progress = new ProgressBar(0.62);
        progress.setPrefWidth(300);
        progress.setStyle("-fx-accent: " + ACCENT_YELLOW + ";");

        HBox pauseInfo = new HBox(8);
        pauseInfo.setAlignment(Pos.CENTER);
        pauseInfo.setPadding(new Insets(8));
        pauseInfo.setStyle("-fx-background-color: rgba(255, 189, 46, 0.1); -fx-border-color: rgba(255, 189, 46, 0.3); -fx-border-radius: 4; -fx-background-radius: 4;");
        pauseInfo.getChildren().add(createStyledLabel("⏸  Paused Line: Alpha Line", ACCENT_YELLOW, 10, true));

        Label cycleInfo = createStyledLabel("Cycle Time: 7.4s | Cooldown: 82.4s", TEXT_MUTED, 9, false);

        progressCard.getChildren().addAll(pauseInfo, pct, cycleInfo, progress);
        view.getChildren().add(progressCard);

        Button resumeBtn = createPrimaryButton("▶  RESUME PRODUCTION");
        resumeBtn.setOnAction(e -> { simState = 1; showSimulatorView(); });
        view.getChildren().add(resumeBtn);

        Button stopBtn = new Button("⏹  STOP SIMULATION");
        styleButton(stopBtn, TEXT_MUTED);
        stopBtn.setMaxWidth(Double.MAX_VALUE);
        stopBtn.setPrefHeight(45);
        stopBtn.setOnAction(e -> { simState = 0; showSimulatorView(); });
        view.getChildren().add(stopBtn);

        return view;
    }

    // ==========================================
    // PRODUCTION PROGRESS VIEW (unchanged)
    // ==========================================
    private VBox createProductionProgressView() {
        VBox view = new VBox(15);

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Button backBtn = new Button("← Back");
        styleButton(backBtn, TEXT_MUTED);
        backBtn.setOnAction(e -> {
            setActiveNav(0);
            showHomeView();
        });
        header.getChildren().addAll(backBtn, createStyledLabel("Production Progress", "white", 16, true));
        view.getChildren().add(header);

        HBox statsRow = new HBox(10);
        statsRow.getChildren().addAll(
            createSmallMetricCard("48%", "Progress"),
            createSmallMetricCard("240", "Completed"),
            createSmallMetricCard("11", "Defective")
        );
        for (var node : statsRow.getChildren()) HBox.setHgrow(node, Priority.ALWAYS);
        view.getChildren().add(statsRow);

        HBox chartsRow = new HBox(15);

        VBox leftChart = createCard();
        leftChart.setPrefWidth(180);
        leftChart.getChildren().add(createStyledLabel("STAGES", TEXT_MUTED, 9, true));
        String[] stages = {"Material Prep", "Assembly", "Quality Control", "Packaging"};
        String[] stageColors = {ACCENT_GREEN, ACCENT_GREEN, ACCENT_YELLOW, TEXT_MUTED};
        for (int i = 0; i < stages.length; i++) {
            HBox stageRow = new HBox(8);
            stageRow.setAlignment(Pos.CENTER_LEFT);
            Circle dot = new Circle(4, Color.web(stageColors[i]));
            stageRow.getChildren().addAll(dot, createStyledLabel(stages[i], TEXT_LIGHT, 11, false));
            leftChart.getChildren().add(stageRow);
        }

        VBox rightChart = createCard();
        HBox.setHgrow(rightChart, Priority.ALWAYS);
        rightChart.getChildren().add(createStyledLabel("LIVE ACTIVITY", TEXT_MUTED, 9, true));
        rightChart.getChildren().add(createStyledLabel("• Alpha Line: 98 units passed", ACCENT_GREEN, 11, false));
        rightChart.getChildren().add(createStyledLabel("• Beta Line: 4 units flagged", ACCENT_YELLOW, 11, false));
        rightChart.getChildren().add(createStyledLabel("• Gamma Line: 1 unit defective", ACCENT_RED, 11, false));

        chartsRow.getChildren().addAll(leftChart, rightChart);
        view.getChildren().add(chartsRow);

        return view;
    }

    // ==========================================
    // PROFILE VIEW
    // ==========================================
    private VBox createProfileView() {
        VBox view = new VBox(15);

        // Top bar with title + LOG OUT
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(10, 15, 10, 15));
        topBar.setStyle("-fx-background-color: #1a202c; -fx-background-radius: 0;");
        topBar.setMaxWidth(Double.MAX_VALUE);

        Label topTitle = new Label("User Profile");
        topTitle.setStyle("-fx-text-fill: " + ACCENT_YELLOW + "; -fx-font-size: 13px; -fx-font-weight: bold;");
        topTitle.setMaxWidth(Double.MAX_VALUE);
        topTitle.setAlignment(Pos.CENTER);
        HBox.setHgrow(topTitle, Priority.ALWAYS);

        Button logOutBtn = new Button("LOG OUT");
        logOutBtn.setStyle("-fx-background-color: transparent; -fx-border-color: " + ACCENT_RED + "; -fx-border-radius: 4; -fx-text-fill: " + ACCENT_RED + "; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 4 12 4 12; -fx-cursor: hand;");
        logOutBtn.setOnAction(e -> {
            UserSession.clear();
            javafx.stage.Stage stage = (javafx.stage.Stage) root.getScene().getWindow();
            try {
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/views/login_page.fxml"));
                javafx.scene.Parent loginRoot = loader.load();
                javafx.scene.Scene loginScene = new javafx.scene.Scene(loginRoot, 850, 650);
                loginScene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                stage.setScene(loginScene);
                stage.show();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        topBar.getChildren().addAll(topTitle, logOutBtn);
        view.getChildren().add(topBar);

        // Dark profile card
        HBox profileCard = new HBox(15);
        profileCard.setAlignment(Pos.CENTER_LEFT);
        profileCard.setPadding(new Insets(20));
        profileCard.setStyle("-fx-background-color: #0f172a; -fx-background-radius: 10;");

        // Avatar with first letter of name
        StackPane avatar = new StackPane();
        avatar.setPrefSize(70, 70);
        avatar.setMinSize(70, 70);
        avatar.setMaxSize(70, 70);
        avatar.setStyle("-fx-background-color: #1a202c; -fx-background-radius: 12; -fx-border-color: " + ACCENT_YELLOW + "; -fx-border-radius: 12; -fx-border-width: 1;");
        String firstLetter = (profileName != null && !profileName.isEmpty())
                ? profileName.substring(0, 1).toUpperCase() : "?";
        Label avatarLetter = new Label(firstLetter);
        avatarLetter.setStyle("-fx-text-fill: " + ACCENT_YELLOW + "; -fx-font-size: 28px; -fx-font-weight: bold;");
        avatar.getChildren().add(avatarLetter);

        // User info
        VBox info = new VBox(4);
        String displayShortName = (profileName != null && !profileName.isEmpty())
                ? profileName.split(" ")[0] : "User";
        Label nameLbl = new Label(displayShortName);
        nameLbl.setStyle("-fx-text-fill: " + ACCENT_YELLOW + "; -fx-font-size: 18px; -fx-font-weight: bold;");
        Label roleLbl = new Label("Staff");
        roleLbl.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 12px;");
        Label statusLbl = new Label("ACTIVE");
        statusLbl.setPadding(new Insets(2, 8, 2, 8));
        statusLbl.setStyle("-fx-text-fill: " + ACCENT_GREEN + "; -fx-background-color: " + ACCENT_GREEN + "20; -fx-background-radius: 3; -fx-font-size: 9px; -fx-font-weight: bold;");
        info.getChildren().addAll(nameLbl, roleLbl, statusLbl);

        profileCard.getChildren().addAll(avatar, info);
        view.getChildren().add(profileCard);

        // Full Name
        Label nameLabel = createStyledLabel("FULL NAME", TEXT_MUTED, 10, true);
        TextField nameField = new TextField(profileName);
        nameField.setStyle("-fx-background-color: #d1d5db; -fx-text-fill: #1a202c; -fx-padding: 12; -fx-background-radius: 4; -fx-font-size: 13px;");
        nameField.setEditable(profileEditMode);

        // Email
        Label emailLabel = createStyledLabel("EMAIL", TEXT_MUTED, 10, true);
        TextField emailField = new TextField(profileEmail);
        emailField.setStyle("-fx-background-color: #d1d5db; -fx-text-fill: #1a202c; -fx-padding: 12; -fx-background-radius: 4; -fx-font-size: 13px;");
        emailField.setEditable(false); // Email can't change (tied to Firebase Auth)

        view.getChildren().addAll(nameLabel, nameField, emailLabel, emailField);

        if (profileEditMode) {
            HBox actions = new HBox(15);
            actions.setAlignment(Pos.CENTER);
            actions.setMaxWidth(Double.MAX_VALUE);

            Button cancelBtn = new Button("CANCEL");
            cancelBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #6c7884; -fx-border-radius: 6; -fx-text-fill: #6c7884; -fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand;");
            cancelBtn.setPrefHeight(45);
            cancelBtn.setPrefWidth(300);
            cancelBtn.setOnAction(e -> {
                profileEditMode = false;
                mainContentArea.getChildren().setAll(createProfileView());
            });

            Button saveBtn = new Button("SAVE CHANGES");
            saveBtn.setStyle("-fx-background-color: " + ACCENT_YELLOW + "; -fx-text-fill: #0f1216; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 6; -fx-cursor: hand;");
            saveBtn.setPrefHeight(45);
            saveBtn.setPrefWidth(400);
            saveBtn.setOnAction(e -> {
                profileName = nameField.getText();
                UserSession.displayName = profileName;
                profileEditMode = false;
                mainContentArea.getChildren().setAll(createProfileView());
            });

            actions.getChildren().addAll(cancelBtn, saveBtn);
            view.getChildren().add(actions);
        } else {
            HBox actions = new HBox();
            actions.setAlignment(Pos.CENTER_RIGHT);
            actions.setMaxWidth(Double.MAX_VALUE);
            Hyperlink editLink = new Hyperlink("EDIT PROFILE");
            editLink.setStyle("-fx-text-fill: " + ACCENT_YELLOW + "; -fx-font-size: 12px; -fx-font-weight: bold; -fx-border-color: transparent; -fx-cursor: hand;");
            editLink.setOnAction(e -> {
                profileEditMode = true;
                mainContentArea.getChildren().setAll(createProfileView());
            });
            actions.getChildren().add(editLink);
            view.getChildren().add(actions);
        }

        return view;
    }

    // ==========================================
    // HELPERS
    // ==========================================
    private VBox createCard() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: " + CARD_BG + "; -fx-background-radius: 10;");
        return card;
    }

    private VBox createPlaceholder(String text) {
        VBox view = new VBox(15);
        view.getChildren().add(createStyledLabel(text, "white", 20, true));
        return view;
    }

    private Label createStyledLabel(String text, String color, int size, boolean bold) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: " + color + "; -fx-font-size: " + size + "px;" + (bold ? " -fx-font-weight: bold;" : ""));
        return label;
    }

    private Button createPrimaryButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + ACCENT_YELLOW + "; -fx-text-fill: #0f1216; -fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 6; -fx-cursor: hand;");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPrefHeight(45);
        return btn;
    }

    private void styleButton(Button btn, String color) {
        btn.setStyle("-fx-background-color: transparent; -fx-border-color: " + color + "; -fx-border-radius: 6; -fx-text-fill: " + color + "; -fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand;");
        btn.setPrefHeight(45);
    }

    private VBox createSmallMetricCard(String value, String label) {
        VBox card = createCard();
        card.setAlignment(Pos.CENTER);
        card.getChildren().addAll(createStyledLabel(value, "white", 18, true), createStyledLabel(label, TEXT_MUTED, 9, false));
        return card;
    }

    private VBox createMetricIconCard(String value, String label, String svgPath) {
        VBox card = createCard();
        card.setAlignment(Pos.CENTER);
        SVGPath icon = new SVGPath();
        icon.setContent(svgPath);
        icon.setStroke(Color.web(TEXT_MUTED));
        icon.setFill(null);
        icon.setStrokeWidth(1.5);
        card.getChildren().addAll(createStyledLabel(value, "white", 18, true), icon, createStyledLabel(label, TEXT_MUTED, 9, false));
        return card;
    }

    private VBox createMetricCard(String title, String value, String valueColor) {
        VBox card = createCard();
        HBox.setHgrow(card, Priority.ALWAYS);
        card.getChildren().addAll(createStyledLabel(title, TEXT_MUTED, 9, true), createStyledLabel(value, valueColor, 22, true));
        return card;
    }

    private VBox createLineCard(String name, String status, String statusColor, double progress) {
        VBox card = createCard();
        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);
        Label nameLbl = createStyledLabel(name, TEXT_LIGHT, 14, false);
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        Label badge = new Label(status);
        badge.setPadding(new Insets(4, 8, 4, 8));
        badge.setStyle("-fx-text-fill: " + statusColor + "; -fx-background-color: " + statusColor + "20; -fx-background-radius: 4; -fx-font-size: 10px; -fx-font-weight: bold;");
        topRow.getChildren().addAll(nameLbl, spacer, badge);

        StackPane track = new StackPane();
        track.setAlignment(Pos.CENTER_LEFT);
        track.setStyle("-fx-background-color: #1f262e; -fx-background-radius: 2;");
        track.setPrefHeight(4);
        Region fill = new Region();
        fill.setStyle("-fx-background-color: " + statusColor + "; -fx-background-radius: 2;");
        fill.setPrefWidth(200 * progress);
        fill.setPrefHeight(4);
        track.getChildren().add(fill);

        card.getChildren().addAll(topRow, track);
        return card;
    }
}