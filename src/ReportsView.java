import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.embed.swing.SwingFXUtils; // Requires javafx.swing module
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;

public class ReportsView {

    private VBox view;
    private int activeReportTab = 0; // 0=Prod Report, 1=History, 2=Efficiency, 3=Defects, 4=Machine, 5=Finished
    private DashboardView dashboard; // Reference to the main dashboard to refresh views

    // --- FIREBASE DATA & STATE ---
    private int activeTimeToggle = 0; // 0=Today, 1=Week, 2=Month
    private Map<String, String> prodStatsData; // e.g., {"totalOutput": "359", "vsTarget": "87%"}
    private Map<String, Map<String, String>> lineData; // e.g., {"Alpha": {"units": "148 / 240", "progress": "0.62", "color": "ACCENT_YELLOW"}}

    public ReportsView(DashboardView dashboard) {
        this.dashboard = dashboard;
        this.view = new VBox(15);
        loadDataFromFirebase(); // Minimal addition to load initial data
        buildReportsView();
    }

    public VBox getView() {
        return view;
    }

    // --- FIREBASE INTEGRATION (MINIMAL ADDITION) ---
    private void loadDataFromFirebase() {
        // TODO: Replace this with your actual Firebase query logic.
        // Example: FirebaseDatabase.getInstance().getReference("reports/today")...
        
        prodStatsData = new HashMap<>();
        lineData = new HashMap<>();
        
        // Mocking Firebase response for 'Today' (Default)
        prodStatsData.put("totalOutput", "359");
        prodStatsData.put("vsTarget", "87%");
        
        lineData.put("Alpha", Map.of("units", "148 / 240", "progress", "0.62", "color", DashboardView.ACCENT_YELLOW));
        lineData.put("Beta", Map.of("units", "211 / 400", "progress", "0.53", "color", DashboardView.ACCENT_RED));
    }

    private void fetchDataForTimeRange(int rangeIndex) {
        // TODO: Replace this with your actual Firebase query logic for Week/Month.
        // based on rangeIndex (0=Today, 1=Week, 2=Month)
        
        if (rangeIndex == 0) {
            // Today data
            prodStatsData.put("totalOutput", "359");
            prodStatsData.put("vsTarget", "87%");
            lineData.put("Alpha", Map.of("units", "148 / 240", "progress", "0.62", "color", DashboardView.ACCENT_YELLOW));
            lineData.put("Beta", Map.of("units", "211 / 400", "progress", "0.53", "color", DashboardView.ACCENT_RED));
        } else if (rangeIndex == 1) {
            // Mock Week data
            prodStatsData.put("totalOutput", "2,450");
            prodStatsData.put("vsTarget", "91%");
            lineData.put("Alpha", Map.of("units", "1020 / 1200", "progress", "0.85", "color", DashboardView.ACCENT_GREEN));
            lineData.put("Beta", Map.of("units", "1430 / 1600", "progress", "0.89", "color", DashboardView.ACCENT_GREEN));
        } else if (rangeIndex == 2) {
            // Mock Month data
            prodStatsData.put("totalOutput", "10,240");
            prodStatsData.put("vsTarget", "94%");
            lineData.put("Alpha", Map.of("units", "4800 / 5000", "progress", "0.96", "color", DashboardView.ACCENT_GREEN));
            lineData.put("Beta", Map.of("units", "5440 / 6000", "progress", "0.90", "color", DashboardView.ACCENT_GREEN));
        }
    }

    private void buildReportsView() {
        view.getChildren().clear();

        // 1. Report Tab Bar (Horizontally Scrollable)
        ScrollPane tabScroll = new ScrollPane();
        tabScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        tabScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        tabScroll.setFitToHeight(true);
        tabScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        HBox tabBar = new HBox(10);
        tabBar.setPadding(new Insets(0, 0, 10, 0));
        
        String[] tabs = {"Report", "History", "Efficiency", "Defects", "Machine", "Finished"};
        
        for (int i = 0; i < tabs.length; i++) {
            final int index = i;
            Label tabBtn = new Label(tabs[i]);
            tabBtn.setPadding(new Insets(8, 15, 8, 15));
            tabBtn.setStyle("-fx-cursor: hand; -fx-font-size: 12px; -fx-font-weight: bold;");
            
            if (i == activeReportTab) {
                tabBtn.setStyle(tabBtn.getStyle() + "-fx-background-color: " + DashboardView.ACCENT_YELLOW + "; -fx-text-fill: black; -fx-background-radius: 5;");
            } else {
                tabBtn.setStyle(tabBtn.getStyle() + "-fx-background-color: " + DashboardView.CARD_BG + "; -fx-text-fill: " + DashboardView.TEXT_MUTED + "; -fx-background-radius: 5;");
            }

            tabBtn.setOnMouseClicked(e -> {
                activeReportTab = index;
                buildReportsView(); // Rebuild the view to update tabs and content
            });
            tabBar.getChildren().add(tabBtn);
        }
        tabScroll.setContent(tabBar);

        // 2. Content Area
        VBox contentArea = new VBox(15);
        contentArea.setPadding(new Insets(5, 0, 0, 0));
        
        switch (activeReportTab) {
            case 0: contentArea.getChildren().add(createProdReportContent()); break;
            case 1: contentArea.getChildren().add(createHistoryContent()); break;
            case 2: contentArea.getChildren().add(createEfficiencyContent()); break;
            case 3: contentArea.getChildren().add(createDefectsContent()); break;
            case 4: contentArea.getChildren().add(createMachineStatusContent()); break;
            case 5: contentArea.getChildren().add(createFinishedGoodsContent()); break;
        }

        view.getChildren().addAll(tabScroll, contentArea);
    }

    // --- REPORT TAB CONTENTS ---

    // 4.2 Production Reports
    private VBox createProdReportContent() {
        VBox box = new VBox(15);
        
        // --- NEW: Export Button & Time Toggle Layout ---
        HBox topControlBar = new HBox();
        topControlBar.setAlignment(Pos.CENTER);
        
        // Time Toggle
        HBox toggle = new HBox(10);
        toggle.setAlignment(Pos.CENTER);
        
        Label tToday = createToggleBtn("Today", activeTimeToggle == 0);
        Label tWeek = createToggleBtn("Week", activeTimeToggle == 1);
        Label tMonth = createToggleBtn("Month", activeTimeToggle == 2);
        
        // Minimal addition: Handlers to switch data and re-render
        tToday.setOnMouseClicked(e -> {
            activeTimeToggle = 0;
            fetchDataForTimeRange(0);
            buildReportsView();
        });
        tWeek.setOnMouseClicked(e -> {
            activeTimeToggle = 1;
            fetchDataForTimeRange(1);
            buildReportsView();
        });
        tMonth.setOnMouseClicked(e -> {
            activeTimeToggle = 2;
            fetchDataForTimeRange(2);
            buildReportsView();
        });
        
        toggle.getChildren().addAll(tToday, tWeek, tMonth);
        
        // Export Button
        Label exportBtn = new Label("Export");
        exportBtn.setPadding(new Insets(8, 15, 8, 15));
        exportBtn.setStyle("-fx-cursor: hand; -fx-background-color: " + DashboardView.CARD_BG + "; -fx-text-fill: " + DashboardView.TEXT_MUTED + "; -fx-background-radius: 5; -fx-font-weight: bold; -fx-font-size: 12px;");
        
        // Spacer to push Export button to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        topControlBar.getChildren().addAll(toggle, spacer, exportBtn);
        
        // Stats (UPDATED TO USE FIREBASE DATA)
        HBox stats = new HBox(12);
        stats.getChildren().addAll(
            dashboard.createMetricCard("TOTAL OUTPUT", prodStatsData.get("totalOutput"), DashboardView.ACCENT_YELLOW),
            dashboard.createMetricCard("VS TARGET", prodStatsData.get("vsTarget"), "white")
        );

        // Lines (UPDATED TO USE FIREBASE DATA)
        VBox lines = new VBox(12);
        lines.getChildren().addAll(
            createProgressCard("Alpha Line", lineData.get("Alpha").get("units"), Double.parseDouble(lineData.get("Alpha").get("progress")), lineData.get("Alpha").get("color")),
            createProgressCard("Beta Line", lineData.get("Beta").get("units"), Double.parseDouble(lineData.get("Beta").get("progress")), lineData.get("Beta").get("color"))
        );

        // Create a container for the report content so we can easily snapshot it
        VBox reportContent = new VBox(15, stats, lines);
        
        // --- NEW: Export Button Click Action ---
        exportBtn.setOnMouseClicked(e -> {
            // Hide button temporarily
            exportBtn.setVisible(false);
            topControlBar.requestLayout(); 
            
            // Snapshot the report container
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(javafx.scene.paint.Color.TRANSPARENT); // Transparent background
            WritableImage image = reportContent.snapshot(params, null);
            
            // Restore button
            exportBtn.setVisible(true);
            topControlBar.requestLayout();
            
            // Save to file
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Report as PNG");
            
            String timeLabel = activeTimeToggle == 0 ? "Today" : (activeTimeToggle == 1 ? "Week" : "Month");
            fileChooser.setInitialFileName("Production_Report_" + timeLabel + "_" + LocalDate.now() + ".png");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png"));
            
            File file = fileChooser.showSaveDialog(view.getScene().getWindow());
            
            if (file != null) {
                try {
                    ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
                } catch (IOException ex) {
                    System.out.println("Error saving image: " + ex.getMessage());
                }
            }
        });

        box.getChildren().addAll(topControlBar, reportContent);
        return box;
    }

    // 4.3 Production History
    private VBox createHistoryContent() {
        VBox box = new VBox(15);
        box.getChildren().add(createSectionTitle("RECENT RECORDS"));
        
        box.getChildren().addAll(
            createHistoryRow("Alpha Line", "19 Aug", "100%", "240/240", DashboardView.ACCENT_GREEN),
            createHistoryRow("Beta Line", "19 Aug", "95%", "381/400", DashboardView.ACCENT_YELLOW),
            createHistoryRow("Alpha Line", "18 Aug", "95%", "228/240", DashboardView.ACCENT_YELLOW),
            createHistoryRow("Beta Line", "18 Aug", "100%", "400/400", DashboardView.ACCENT_GREEN)
        );
        return box;
    }

    // 4.4 Efficiency Analysis
    private VBox createEfficiencyContent() {
        VBox box = new VBox(15);
        
        HBox stats = new HBox(12);
        stats.getChildren().addAll(
            dashboard.createMetricCard("OVERALL", "87%", DashboardView.ACCENT_YELLOW),
            dashboard.createMetricCard("BEST", "100%", DashboardView.ACCENT_GREEN)
        );

        VBox lines = new VBox(12);
        lines.getChildren().addAll(
            createEfficiencyCard("Alpha Line", "Engine Block", 0.52, "52%", "Below Target"),
            createEfficiencyCard("Beta Line", "Gear Assembly", 0.53, "53%", "Below Target")
        );
        
        box.getChildren().addAll(stats, lines);
        return box;
    }

    // 4.5 Defective Products
    private VBox createDefectsContent() {
        VBox box = new VBox(15);
        
        HBox stats = new HBox(12);
        stats.getChildren().addAll(
            dashboard.createMetricCard("TOTAL DEFECT", "11", DashboardView.ACCENT_RED),
            dashboard.createMetricCard("DEFECT RATE", "3.1%", DashboardView.ACCENT_YELLOW)
        );

        box.getChildren().add(createSectionTitle("BY PRODUCTION LINE"));

        VBox list = new VBox(12);
        list.getChildren().addAll(
            createDefectRow("Alpha Line", "148", "2.0%", "145", DashboardView.ACCENT_YELLOW, "3 defective"),
            createDefectRow("Beta Line", "211", "3.8%", "203", DashboardView.ACCENT_RED, "8 defective")
        );

        box.getChildren().addAll(stats, list);
        return box;
    }

    // 4.6 Machine Status
    private VBox createMachineStatusContent() {
        VBox box = new VBox(15);
        
        box.getChildren().addAll(
            createMachineRow("L01", "Alpha Line", "RUNNING", DashboardView.ACCENT_GREEN, "75%", "72°C", "14 Aug", 0.88),
            createMachineRow("L02", "Beta Line", "PAUSED", DashboardView.ACCENT_YELLOW, "90%", "68°C", "10 Aug", 0.72)
        );
        return box;
    }

    // 4.7 Finished Products
    private VBox createFinishedGoodsContent() {
        VBox box = new VBox(15);
        
        HBox stats = new HBox(12);
        stats.getChildren().addAll(
            dashboard.createMetricCard("TOTAL FINISHED", "348", DashboardView.ACCENT_GREEN),
            dashboard.createMetricCard("DEFECTIVE", "11", DashboardView.ACCENT_RED)
        );

        VBox list = new VBox(12);
        list.getChildren().addAll(
            createFinishedCard("Engine Block", "Alpha Line", "B-2026-081", "145", "GOOD UNITS"),
            createFinishedCard("Gear Assembly", "Beta Line", "B-2026-082", "203", "GOOD UNITS")
        );

        box.getChildren().addAll(stats, list);
        return box;
    }

    // ==========================================
    // REPORT HELPER COMPONENTS
    // ==========================================

    private Label createSectionTitle(String title) {
        Label label = new Label(title);
        label.setStyle("-fx-text-fill: " + DashboardView.TEXT_MUTED + "; -fx-font-size: 10px; -fx-font-weight: bold;");
        return label;
    }

    private Label createToggleBtn(String text, boolean active) {
        Label l = new Label(text);
        l.setPadding(new Insets(8, 25, 8, 25));
        l.setStyle("-fx-cursor: hand;"); // Minimal addition to show it is clickable
        if(active) {
            l.setStyle(l.getStyle() + "-fx-background-color: #d4c4a8; -fx-text-fill: #0f1216; -fx-background-radius: 5; -fx-font-weight: bold;");
        } else {
            l.setStyle(l.getStyle() + "-fx-background-color: " + DashboardView.CARD_BG + "; -fx-text-fill: " + DashboardView.TEXT_MUTED + "; -fx-background-radius: 5;");
        }
        return l;
    }

    private VBox createProgressCard(String title, String sub, double progress, String color) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: " + DashboardView.CARD_BG + "; -fx-background-radius: 10;");

        HBox top = new HBox();
        Label name = new Label(title);
        name.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label pct = new Label((int)(progress * 100) + "%");
        pct.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 14px; -fx-font-weight: bold;");
        
        top.getChildren().addAll(name, spacer, pct);

        StackPane track = new StackPane();
        track.setAlignment(Pos.CENTER_LEFT);
        track.setStyle("-fx-background-color: #1f262e; -fx-background-radius: 2;");
        track.setPrefHeight(6);

        Region fill = new Region();
        fill.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 2;");
        fill.setPrefWidth(400 * progress); 
        fill.setPrefHeight(6);
        track.getChildren().add(fill);

        Label subLbl = new Label(sub);
        subLbl.setStyle("-fx-text-fill: " + DashboardView.TEXT_MUTED + "; -fx-font-size: 10px;");

        card.getChildren().addAll(top, track, subLbl);
        return card;
    }

    private VBox createHistoryRow(String title, String date, String pct, String units, String color) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(12));
        card.setStyle("-fx-background-color: " + DashboardView.CARD_BG + "; -fx-background-radius: 8;");

        HBox top = new HBox();
        VBox left = new VBox(3);
        Label n = new Label(title); n.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");
        Label d = new Label(date); d.setStyle("-fx-text-fill: " + DashboardView.TEXT_MUTED + "; -fx-font-size: 10px;");
        left.getChildren().addAll(n, d);

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox right = new VBox(3);
        right.setAlignment(Pos.CENTER_RIGHT);
        Label p = new Label(pct); p.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 14px; -fx-font-weight: bold;");
        Label u = new Label(units); u.setStyle("-fx-text-fill: " + DashboardView.TEXT_MUTED + "; -fx-font-size: 10px;");
        right.getChildren().addAll(p, u);

        top.getChildren().addAll(left, spacer, right);

        StackPane track = new StackPane();
        track.setAlignment(Pos.CENTER_LEFT);
        track.setStyle("-fx-background-color: #1f262e; -fx-background-radius: 2;");
        track.setPrefHeight(4);
        Region fill = new Region();
        fill.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 2;");
        fill.setPrefWidth(400 * (Double.parseDouble(pct.replace("%",""))/100.0));
        fill.setPrefHeight(4);
        track.getChildren().add(fill);

        card.getChildren().addAll(top, track);
        return card;
    }

    private VBox createEfficiencyCard(String line, String product, double prog, String pct, String status) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: " + DashboardView.CARD_BG + "; -fx-background-radius: 10;");

        HBox top = new HBox();
        VBox l = new VBox(3);
        Label n = new Label(line); n.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        Label p = new Label(product); p.setStyle("-fx-text-fill: " + DashboardView.TEXT_MUTED + "; -fx-font-size: 10px;");
        l.getChildren().addAll(n, p);
        
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        
        Label percent = new Label(pct); percent.setStyle("-fx-text-fill: " + DashboardView.ACCENT_YELLOW + "; -fx-font-size: 18px; -fx-font-weight: bold;");
        
        top.getChildren().addAll(l, sp, percent);

        StackPane track = new StackPane();
        track.setAlignment(Pos.CENTER_LEFT);
        track.setStyle("-fx-background-color: #1f262e; -fx-background-radius: 2;");
        track.setPrefHeight(6);
        Region fill = new Region();
        fill.setStyle("-fx-background-color: " + DashboardView.ACCENT_YELLOW + "; -fx-background-radius: 2;");
        fill.setPrefWidth(400 * prog);
        fill.setPrefHeight(6);
        track.getChildren().add(fill);

        HBox footer = new HBox();
        Label speed = new Label("Speed: 75%"); speed.setStyle("-fx-text-fill: " + DashboardView.TEXT_MUTED + "; -fx-font-size: 10px;");
        Region sp2 = new Region(); HBox.setHgrow(sp2, Priority.ALWAYS);
        Label stat = new Label("△ " + status); stat.setStyle("-fx-text-fill: " + DashboardView.ACCENT_YELLOW + "; -fx-font-size: 10px;");
        footer.getChildren().addAll(speed, sp2, stat);

        card.getChildren().addAll(top, track, footer);
        return card;
    }

    private VBox createDefectRow(String line, String prod, String rate, String good, String rateColor, String defectTxt) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: " + DashboardView.CARD_BG + "; -fx-background-radius: 10;");

        HBox top = new HBox();
        Label n = new Label(line); n.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label d = new Label(defectTxt); d.setStyle("-fx-text-fill: " + rateColor + "; -fx-font-size: 12px;");
        top.getChildren().addAll(n, sp, d);

        HBox stats = new HBox(30);
        stats.getChildren().addAll(
            createMiniStat("PRODUCED", prod, "white"),
            createMiniStat("DEFECT RATE", rate, rateColor),
            createMiniStat("GOOD UNITS", good, DashboardView.ACCENT_GREEN)
        );

        card.getChildren().addAll(top, stats);
        return card;
    }

    private VBox createMiniStat(String label, String val, String color) {
        VBox v = new VBox(2);
        Label l = new Label(label); l.setStyle("-fx-text-fill: " + DashboardView.TEXT_MUTED + "; -fx-font-size: 8px;");
        Label v1 = new Label(val); v1.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 12px; -fx-font-weight: bold;");
        v.getChildren().addAll(l, v1);
        return v;
    }

    private VBox createMachineRow(String id, String name, String status, String statusColor, String speed, String temp, String date, double health) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: " + DashboardView.CARD_BG + "; -fx-background-radius: 10;");

        HBox top = new HBox();
        VBox l = new VBox(2);
        Label i = new Label(id); i.setStyle("-fx-text-fill: " + DashboardView.TEXT_MUTED + "; -fx-font-size: 10px; -fx-font-weight: bold;");
        Label n = new Label(name); n.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        l.getChildren().addAll(i, n);

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        Label badge = new Label(status);
        badge.setPadding(new Insets(4, 8, 4, 8));
        badge.setStyle("-fx-text-fill: " + statusColor + "; -fx-background-color: " + statusColor + "20; -fx-background-radius: 4; -fx-font-size: 9px; -fx-font-weight: bold;");

        top.getChildren().addAll(l, sp, badge);

        HBox stats = new HBox(30);
        stats.getChildren().addAll(
            createMiniStat("SPEED", speed, DashboardView.ACCENT_YELLOW),
            createMiniStat("TEMP", temp, DashboardView.ACCENT_YELLOW),
            createMiniStat("LAST MAINT", date, "white")
        );

        StackPane track = new StackPane();
        track.setAlignment(Pos.CENTER_LEFT);
        track.setStyle("-fx-background-color: #1f262e; -fx-background-radius: 2;");
        track.setPrefHeight(6);
        Region fill = new Region();
        fill.setStyle("-fx-background-color: " + DashboardView.ACCENT_YELLOW + "; -fx-background-radius: 2;");
        fill.setPrefWidth(400 * health);
        fill.setPrefHeight(6);
        track.getChildren().add(fill);

        HBox healthBox = new HBox(10);
        Label hLbl = new Label("MACHINE HEALTH - " + (int)(health*100) + "%");
        hLbl.setStyle("-fx-text-fill: " + DashboardView.TEXT_MUTED + "; -fx-font-size: 9px; -fx-font-weight: bold;");
        
        card.getChildren().addAll(top, stats, hLbl, track);
        return card;
    }

    private VBox createFinishedCard(String name, String line, String batch, String units, String unitLabel) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: " + DashboardView.CARD_BG + "; -fx-background-radius: 10;");

        HBox top = new HBox();
        VBox l = new VBox(3);
        Label n = new Label(name); n.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        Label li = new Label(line); li.setStyle("-fx-text-fill: " + DashboardView.TEXT_MUTED + "; -fx-font-size: 10px;");
        Label bt = new Label(batch); bt.setStyle("-fx-text-fill: " + DashboardView.TEXT_MUTED + "; -fx-font-size: 10px;");
        l.getChildren().addAll(n, li, bt);

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        VBox right = new VBox(3);
        right.setAlignment(Pos.CENTER_RIGHT);
        Label u = new Label(units); u.setStyle("-fx-text-fill: " + DashboardView.ACCENT_GREEN + "; -fx-font-size: 22px; -fx-font-weight: bold;");
        Label ul = new Label(unitLabel); ul.setStyle("-fx-text-fill: " + DashboardView.TEXT_MUTED + "; -fx-font-size: 10px;");
        right.getChildren().addAll(u, ul);

        top.getChildren().addAll(l, sp, right);

        HBox footer = new HBox();
        Label badge = new Label("READY FOR SHIPMENT");
        badge.setPadding(new Insets(4, 8, 4, 8));
        badge.setStyle("-fx-text-fill: " + DashboardView.ACCENT_GREEN + "; -fx-background-color: " + DashboardView.ACCENT_GREEN + "20; -fx-background-radius: 4; -fx-font-size: 9px; -fx-font-weight: bold;");
        footer.getChildren().add(badge);

        card.getChildren().addAll(top, footer);
        return card;
    }
}