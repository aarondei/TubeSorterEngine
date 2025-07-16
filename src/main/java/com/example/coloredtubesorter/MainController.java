package com.example.coloredtubesorter;

import com.example.coloredtubesorter.Elements.ColorEnum;
import com.example.coloredtubesorter.Elements.Tube;
import com.example.coloredtubesorter.Logic.Sort.Sorter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;


public class MainController extends BaseController {

    @FXML
    private TextField tfStatus;
    @FXML
    private AnchorPane apMainPane;

    private int tubeNum;
    private final List<Tube> tubeList = new ArrayList<>();
    private int currentTubeIndex = 0;

    private boolean isLocked = false;

    @Override
    public void setData(Object data) {
        tubeNum = (Integer) data;
    }

    public void postInit() {
        tfStatus.setFocusTraversable(false);
        tfStatus.setMouseTransparent(true);
        createTube();
        highlightTube(tubeList.getFirst(), true);
    }

    // TUBE SETUP METHODS
    private void createTube() {

        // generate tube dimensions
        Map<String, Double> layoutSetting = new HashMap<>();
        configTubeLayout(layoutSetting);

        // create tubes
        for (int i = 0; i < tubeNum; i++) {
            Tube tube = new Tube();
            setTubeDesign(tube);
            setTubeLayout(tube, layoutSetting);
            setTubeLabel(tube);

            tubeList.add(tube);
            apMainPane.getChildren().add(tube.getContainer());
        }

        // tubes now ready to be filled
    }
    private void configTubeLayout(Map<String, Double> layoutSettings) {
        layoutSettings.put("paneWidth", apMainPane.getPrefWidth());
        layoutSettings.put("spacing", 80.0);
        layoutSettings.put("rowSpacing", 150.0);
        layoutSettings.put("startX", 20.0);
        layoutSettings.put("startY", 50.0);
        layoutSettings.put("x", layoutSettings.get("startX"));
        layoutSettings.put("y", layoutSettings.get("startY"));
    }
    private void setTubeDesign(Tube tube) {

        // GUI
        VBox container = tube.getContainer();

        container.setSpacing(2);
        container.setPrefSize(60, 120);
        container.setAlignment(Pos.BOTTOM_CENTER);

        highlightTube(tube, false);
    }
    private void setTubeLayout(Tube tube, Map<String, Double> layoutSetting) {
        // GUI
        // row warp
        if (layoutSetting.get("x") + 60 > layoutSetting.get("paneWidth")) {
            layoutSetting.put("x", layoutSetting.get("startX"));
            layoutSetting.put("y", layoutSetting.get("y") + layoutSetting.get("rowSpacing"));
        }
        tube.getContainer().setLayoutX(layoutSetting.get("x"));
        tube.getContainer().setLayoutY(layoutSetting.get("y"));

        layoutSetting.put(("x"), layoutSetting.get("x") + layoutSetting.get("spacing"));
    }
    private void setTubeLabel(Tube tube) {
        // GUI
        Text label = new Text();
        label.setText(String.valueOf(tube.getName()));
        label.setLayoutX(tube.getContainer().getLayoutX());
        label.setLayoutY(tube.getContainer().getLayoutY());
        label.setFill(Color.GRAY);
        apMainPane.getChildren().add(label);
    }
    public void highlightTube(Tube tube, boolean highlight) {

        if (highlight) {
            tube.getContainer().setStyle("""
                -fx-background-color: linear-gradient(to bottom, #e0e0e0cc, #ffffff33);
                -fx-border-color: #0ADD08;
                -fx-border-width: 4;
                -fx-border-radius: 15;
                -fx-background-radius: 15;
            """);
        } else {
            tube.getContainer().setStyle("""
                -fx-background-color: linear-gradient(to bottom, #e0e0e0cc, #ffffff33);
                -fx-border-color: #999;
                -fx-border-width: 4;
                -fx-border-radius: 15;
                -fx-background-radius: 15;
            """);
        }
    }
    private Rectangle createLiquid(Color color) {

        Rectangle rect = new Rectangle(45, 25, color);
        rect.setArcWidth(10);
        rect.setArcHeight(10);

        return rect;
    }

    @FXML
    public void colorOnClick(ActionEvent actionEvent) {

        if (isLocked) return;

        if (currentTubeIndex >= tubeList.size()) {
            System.out.println("All tubes are filled.");
            return;
        }

        Button clickedBtn = (Button) actionEvent.getSource();
        String colorName = clickedBtn.getText();

        Tube tube = tubeList.get(currentTubeIndex);
        highlightTube(tube, true);

        // non color handling
        switch (colorName) {
            case "SKIP" -> {
                moveToNextTube(tube);
                return;
            }
            case "REDO" -> {
                tube = tubeList.get(currentTubeIndex);
                tube.pourLiquid();

                if (tube.isEmpty()) moveToPrevTube(tube);
                return;
            }
        }

        // color handling
        try {
            ColorEnum logicColor = ColorEnum.fromName(colorName);
            Color fxColor = logicColor.getColor();

            // create and tag rect
            Rectangle liquid = createLiquid(fxColor);
            liquid.setUserData(logicColor);

            // fill tube
            tube.fillLiquid(liquid);

            if (tube.isFull()) moveToNextTube(tube);

        } catch (IllegalArgumentException e) {
            System.err.println("Unknown color: " + colorName);
        }
    }
    private void moveToNextTube(Tube currentTube) {
        highlightTube(currentTube, false);

        currentTubeIndex++;
        if (currentTubeIndex < tubeList.size())
            highlightTube(tubeList.get(currentTubeIndex), true);
        else
            currentTubeIndex--;
    }
    private void moveToPrevTube(Tube currentTube) {
        highlightTube(currentTube, false);

        currentTubeIndex--;
        if (currentTubeIndex > -1)
            highlightTube(tubeList.get(currentTubeIndex), true);
        else
            currentTubeIndex++;
    }

    @FXML
    public void onSolveClicked(ActionEvent actionEvent) {

        if (isLocked) return;

        // locks pane
        isLocked = true;
        highlightTube(tubeList.get(currentTubeIndex), false);

        // PATHFINDING BEGINS HERE
        Sorter sorter = Sorter.getInstance(tubeList);

        long start = System.currentTimeMillis();
        boolean solved = sorter.pathFind();
        long end = System.currentTimeMillis();

        // measure BFS' elapsed time
        System.out.println("Elapsed Time: " + (end - start) / 1000 + " second/s.");

        // measure BFS' used memory
        Runtime runtime = Runtime.getRuntime();
        System.out.println("Used memory: " +
                (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024) + " MB");

        // GUI results
        tfStatus.setText(solved ? "Run: Success" : "Run: Failed");
        if (solved) {
            tfStatus.setStyle("""
                    -fx-background-color: #80ef80;
                    """);

            // reconstruct winning state
            apMainPane.getChildren().clear();
            Map<String, Double> layoutSetting = new HashMap<>();
            configTubeLayout(layoutSetting);

            for (Tube t : sorter.extractState()) {
                setTubeDesign(t);
                setTubeLayout(t, layoutSetting);
                setTubeLabel(t);
                apMainPane.getChildren().add(t.getContainer());
            }

            // reconstruct winning move history
            MainApplication.openShell(sorter.extractHistory());

        } else {
            tfStatus.setStyle("""
                    -fx-background-color: #e54c38;
                    """);
        }
    }

    public void onResetClick(ActionEvent actionEvent) {

        tfStatus.clear();
        tfStatus.setFocusTraversable(false);
        tfStatus.setMouseTransparent(true);
        tfStatus.setStyle("");
        currentTubeIndex = 0;
        isLocked = false;

        for (Tube t : tubeList) {
            t.resetTube();
            highlightTube(t, false);
        }

        highlightTube(tubeList.getFirst(), true);
    }

    public void onSaveClick(ActionEvent actionEvent) {




    }


}
