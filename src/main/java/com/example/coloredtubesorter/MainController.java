package com.example.coloredtubesorter;

import com.example.coloredtubesorter.Elements.ColorEnum;
import com.example.coloredtubesorter.Elements.Tube;
import com.example.coloredtubesorter.Logic.Sorter;
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
import javafx.stage.Stage;


public class MainController extends BaseController {

    @FXML
    private TextField tfStatus;
    @FXML
    private AnchorPane apMainPane;

    private int tubeNum;
    private List<Tube> tubeList = new ArrayList<>();
    private int currentTubeIndex = 0;

    private boolean isLocked = false;

    @Override
    public void setData(Object data) {
        tubeNum = (Integer) data;
    }

    public void postInit() {
        createTube();
        highlightTube(tubeList.getFirst(), true);
    }

    // TUBE SETUP METHODS
    private void createTube() {

        // generate tube dimensions
        Map<String, Double> stng = new HashMap<>();
        configTubeLayout(stng);

        // create tubes
        for (int i = 0; i < tubeNum; i++) {
            Tube tube = new Tube();
            setTubeVisuals(tube);
            setTubeLayout(tube, stng);

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
    private void setTubeVisuals(Tube tube) {

        VBox container = tube.getContainer();

        container.setSpacing(2);
        container.setPrefSize(60, 120);
        container.setAlignment(Pos.BOTTOM_CENTER);

        highlightTube(tube, false);
    }
    private void setTubeLayout(Tube tube, Map<String, Double> stng) {
        // row warp
        if (stng.get("x") + 60 > stng.get("paneWidth")) {
            stng.put("x", stng.get("startX"));
            stng.put("y", stng.get("y") + stng.get("rowSpacing"));
        }
        tube.getContainer().setLayoutX(stng.get("x"));
        tube.getContainer().setLayoutY(stng.get("y"));

        // set coordinates
        tube.setCoordinates(stng.get("x"), stng.get("y"));

        stng.put(("x"), stng.get("x") + stng.get("spacing"));
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
    private Rectangle createLiquid(Color color, Tube tube) {

        Rectangle rect = new Rectangle(45, 25);
        rect.setFill(color);
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
        };

        // color handling
        try {
            ColorEnum logicColor = ColorEnum.fromName(colorName);
            Color fxColor = logicColor.getColor();

            // create and tag rect
            Rectangle liquid = createLiquid(fxColor, tube);
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

        // locks pane
        isLocked = true;
        for (Tube t : tubeList) highlightTube(t, false);

        // BACKTRACKING LOGIC BEGINS HERE
        Sorter sorter = Sorter.getInstance(tubeList);
        boolean solved = sorter.solve();

        String record = sorter.extractRecord();
        MainApplication.openShell(record);

        tfStatus.setText(solved ? "Complete" : "Failed");
    }
}
