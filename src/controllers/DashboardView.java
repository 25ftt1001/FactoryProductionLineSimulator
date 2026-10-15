package controllers;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DashboardView {

    public static final String BG_COLOR = "#0f1216";
    public static final String CARD_BG = "#14181d";
    public static final String TEXT_MUTED = "#6c7884";
    public static final String TEXT_LIGHT = "#e2e8f0";
    public static final String ACCENT_YELLOW = "#ffbd2e";
    public static final String ACCENT_RED = "#ff5f56";
    public static final String ACCENT_GREEN = "#27c93f";
    public static final String ACCENT_IDLE = "#8896a6";

    private BorderPane root;
    private VBox mainContentArea;
    private HBox[] navItems;

    public DashboardView() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG_COLOR + ";");

        VBox topContainer = new VBox(20);
        topContainer.setPadding(new Insets(25, 25, 20, 25));

        HBox header = createHeader();
        mainContentArea = new VBox(20);
        mainContentArea.getChildren().add(createHomeView());

        topContainer.getChildren().addAll(header, mainContentArea);

        HBox bottomNav = createBottomNavigation();

        root.setCenter(topContainer);
        root.setBottom(bottomNav);
    }

    public BorderPane getRoot() {
        return root;
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox greetingBox = new VBox(5);
        Label greeting = new Label("Good Morning, Aleksei");
        greeting.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE dd MMM yyyy", Locale.ENGLISH);
        Label dateLabel = new Label(today.format(formatter).toUpperCase());
        dateLabel.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 11px;");
        greetingBox.getChildren().addAll(greeting, dateLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox statusBox = new HBox(6);
        statusBox.setAlignment(Pos.CENTER);
        Circle statusDot = new Circle(4, Color.web("#00e5a0"));
        Label statusText = new Label("ONLINE");
        statusText.setStyle("-fx-text-fill: #00e5a0; -fx-font-size: 11px; -fx-font-weight: bold;");
        statusBox.getChildren().addAll(statusDot, statusText);

        header.getChildren().addAll(greetingBox, spacer, statusBox);
        return header;
    }

    private VBox createHomeView() {
        VBox view = new VBox(20);

        HBox metricsGrid = new HBox(12);
        metricsGrid.getChildren().addAll(
                createMetricCard("PRODUCED", "359", ACCENT_YELLOW),
                createMetricCard("EFFICIENCY", "87%", "white"),
                createMetricCard("DEFECTIVE", "11", ACCENT_RED)
        );

        Label lineStatusTitle = new Label("LINE STATUS");
        lineStatusTitle.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 11px; -fx-font-weight: bold;");

        VBox linesList = new VBox(12);
        linesList.getChildren().addAll(
                createLineCard("Alpha Line", "RUNNING", ACCENT_GREEN, 0.75),
                createLineCard("Beta Line", "PAUSED", ACCENT_YELLOW, 0.45),
                createLineCard("Gamma Line", "IDLE", ACCENT_IDLE, 0.0)
        );

        HBox alertBox = new HBox(10);
        alertBox.setAlignment(Pos.CENTER_LEFT);
        alertBox.setPadding(new Insets(12, 15, 12, 15));
        alertBox.setStyle("-fx-background-color: rgba(255, 95, 86, 0.05); -fx-border-color: rgba(255, 95, 86, 0.2); -fx-border-radius: 8; -fx-background-radius: 8;");
        Label alertIcon = new Label("!");
        alertIcon.setStyle("-fx-text-fill: " + ACCENT_RED + "; -fx-font-size: 14px; -fx-font-weight: bold;");
        Label alertText = new Label("LOW STOCK - COOLANT FLUID: 38L");
        alertText.setStyle("-fx-text-fill: " + ACCENT_RED + "; -fx-font-size: 10px; -fx-font-weight: bold;");
        alertBox.getChildren().addAll(alertIcon, alertText);

        view.getChildren().addAll(metricsGrid, lineStatusTitle, linesList, alertBox);
        return view;
    }

    private VBox createLinesView() {
        VBox view = new VBox(20);
        Label title = new Label("Production Lines Overview");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        Label desc = new Label("Detailed view of all factory lines goes here.");
        desc.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 14px;");
        view.getChildren().addAll(title, desc);
        return view;
    }

    private HBox createBottomNavigation() {
        HBox bottomNav = new HBox();
        bottomNav.setStyle("-fx-background-color: " + CARD_BG + "; -fx-border-color: #1f262e; -fx-border-width: 1 0 0 0;");
        bottomNav.setPadding(new Insets(15, 10, 20, 10));
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

        setActiveNav(0);
        return bottomNav;
    }

    // ✅ FIXED: pickOnBounds(true) + mouseTransparent(true) on children
    private HBox createNavItem(String label, String svgPathData, int index) {
        HBox navItem = new HBox(6);
        navItem.setAlignment(Pos.CENTER);
        navItem.setStyle("-fx-cursor: hand; -fx-background-color: transparent;");
        navItem.setPickOnBounds(true);
        navItem.setMinHeight(45);
        navItem.setPrefWidth(90);

        VBox content = new VBox(6);
        content.setAlignment(Pos.CENTER);
        content.setPickOnBounds(true);

        SVGPath icon = new SVGPath();
        icon.setContent(svgPathData);
        icon.setStroke(Color.web(TEXT_MUTED));
        icon.setFill(null);
        icon.setStrokeWidth(2);
        icon.setMouseTransparent(true);

        Label navLabel = new Label(label);
        navLabel.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 9px; -fx-font-weight: bold;");
        navLabel.setMouseTransparent(true);

        content.getChildren().addAll(icon, navLabel);
        navItem.getChildren().add(content);

        navItem.setOnMouseClicked(e -> {
            System.out.println("Nav clicked: " + label + " (index " + index + ")");
            setActiveNav(index);
            switchView(index);
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

    public void switchView(int index) {
        mainContentArea.getChildren().clear();
        switch (index) {
            case 0:
                mainContentArea.getChildren().add(createHomeView());
                break;
            case 1:
                mainContentArea.getChildren().add(createLinesView());
                break;
            case 2:
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/simulation.fxml"));
                    Parent simRoot = loader.load();
                    Stage stage = (Stage) root.getScene().getWindow();
                    stage.setScene(new Scene(simRoot, 520, 850));
                    stage.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                Label placeholder = new Label("Feature under development");
                placeholder.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
                mainContentArea.getChildren().add(placeholder);
                break;
        }
    }

    public VBox createMetricCard(String title, String value, String valueColor) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: " + CARD_BG + "; -fx-background-radius: 10;");
        HBox.setHgrow(card, Priority.ALWAYS);

        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 9px; -fx-font-weight: bold;");
        Label valueLbl = new Label(value);
        valueLbl.setStyle("-fx-text-fill: " + valueColor + "; -fx-font-size: 22px; -fx-font-weight: bold;");

        card.getChildren().addAll(titleLbl, valueLbl);
        return card;
    }

    public VBox createLineCard(String name, String status, String statusColor, double progress) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: " + CARD_BG + "; -fx-background-radius: 10;");

        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label nameLbl = new Label(name);
        nameLbl.setStyle("-fx-text-fill: " + TEXT_LIGHT + "; -fx-font-size: 14px;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label badge = new Label(status);
        badge.setPadding(new Insets(4, 8, 4, 8));
        badge.setStyle("-fx-text-fill: " + statusColor + "; -fx-background-color: " + statusColor + "20; -fx-background-radius: 4; -fx-font-size: 10px; -fx-font-weight: bold;");

        topRow.getChildren().addAll(nameLbl, spacer, badge);

        StackPane progressTrack = new StackPane();
        progressTrack.setAlignment(Pos.CENTER_LEFT);
        progressTrack.setStyle("-fx-background-color: #1f262e; -fx-background-radius: 2;");
        progressTrack.setPrefHeight(4);

        Region progressFill = new Region();
        progressFill.setStyle("-fx-background-color: " + statusColor + "; -fx-background-radius: 2;");
        progressFill.setPrefWidth(200 * progress);
        progressFill.setPrefHeight(4);

        progressTrack.getChildren().add(progressFill);
        card.getChildren().addAll(topRow, progressTrack);

        return card;
    }
}