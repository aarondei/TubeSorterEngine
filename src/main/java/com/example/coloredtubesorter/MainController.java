package com.example.coloredtubesorter;

import com.example.coloredtubesorter.Elements.ColorEnum;
import com.example.coloredtubesorter.Elements.Tube;
import com.example.coloredtubesorter.Logic.Creator;
import com.example.coloredtubesorter.Logic.Simulation.Simulator;
import com.example.coloredtubesorter.Logic.Sort.Sorter;
import com.example.coloredtubesorter.Logic.UtilityGUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


public class MainController extends BaseController implements Creator {

    @FXML
    private GridPane btnsGrid;

    @FXML
    private Button btnSolve;

    @FXML
    private Button btnRedo;
    @FXML
    private Button btnSkip;
    @FXML
    private Button btnClear;
    @FXML
    private Button btnSimulate;
    @FXML
    private TextField tfStatus;
    @FXML
    private AnchorPane apMainPane;

    private int tubeQty;
    private List<Tube> tubeList;

    private int currentTubeIndex = 0;
    private boolean isLocked = false;
    private String moveHistory = "";

    @Override
    public void setData(Object data) {
        tubeQty = (Integer) data;
    }

    public void postInit() {
        tfStatus.setFocusTraversable(false);
        tfStatus.setMouseTransparent(true);
        tubeList = createTube(apMainPane, tubeQty);
        UtilityGUI.borderTube(tubeList.getFirst(), true);
        lockButton(btnSimulate);
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
        UtilityGUI.borderTube(tube, true);

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
            Rectangle liquid = UtilityGUI.createLiquid(fxColor);
            liquid.setUserData(logicColor);

            // fill tube
            tube.fillLiquid(liquid);

            if (tube.isFull()) moveToNextTube(tube);

        } catch (IllegalArgumentException e) {
            System.err.println("Unknown color: " + colorName);
        }
    }

    @FXML
    public void onSolveClicked(ActionEvent actionEvent) {

        if (isLocked) return;

        // lock mechanism
        isLocked = true;
        lockButton(btnSolve);
        lockButton(btnRedo);
        lockButton(btnSkip);
        lockButton(btnsGrid);
        lockButton(btnSimulate);

        UtilityGUI.borderTube(tubeList.get(currentTubeIndex), false);

        // PATHFINDING BEGINS HERE
        Sorter sorter = Sorter.getInstance(tubeList);
        long start = System.currentTimeMillis();
        boolean solved = sorter.pathFind();
        long end = System.currentTimeMillis();

        // measure BFS' elapsed time
        System.out.println("Elapsed Time: " + (end - start) / 1000 + " second/s.");

        // measure BFS' used memory
        Runtime runtime = Runtime.getRuntime();
        System.out.println("Used Memory: " +
                (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024) + " MB");

        // GUI results
        if (solved) {
            tfStatus.setStyle("""
                    -fx-background-color: #80ef80;
                    """);

            // auto simulate
            tfStatus.setText("Simulating Path");
            lockButton(btnSimulate);
            moveHistory = sorter.extractHistory();
            simulate(moveHistory);

        } else {
            tfStatus.setStyle("""
                    -fx-background-color: #e54c38;
                    """);
            tfStatus.setText("No Valid Move");
        }

        // free memory
        sorter.reset();
    }

    public void onResetClick(ActionEvent actionEvent) {

        if (isLocked) unlockButton(btnSimulate);

        MainApplication.closeShell();
        apMainPane.getChildren().clear();
        tfStatus.clear();
        tfStatus.setFocusTraversable(false);
        tfStatus.setMouseTransparent(true);
        tfStatus.setStyle("");
        currentTubeIndex = 0;
        isLocked = false;

        unlockButton(btnSolve);
        unlockButton(btnRedo);
        unlockButton(btnSkip);
        unlockButton(btnsGrid);

        for (Tube t : tubeList) {
            t.resetTube();
            UtilityGUI.borderTube(t, false);
            apMainPane.getChildren().add(t.getContainer());
        }

        UtilityGUI.borderTube(tubeList.getFirst(), true);
    }

    public void onSimulateClick(ActionEvent actionEvent) {

        if (!isLocked) return;

        if (moveHistory.isEmpty()) throw new RuntimeException("Move history is missing.");

        Simulator simulator = Simulator.getInstance(tubeList, moveHistory);

        tfStatus.setText("Simulation Running");
        lockButton(btnSimulate);
        simulator.restart(apMainPane);

        simulator.setOnSimulationDone(() -> {
            tfStatus.setText("Simulation Done");
            unlockButton(btnSimulate);
        });
    }

    private void simulate(String moves) {

        // open log window
        BaseController shell = MainApplication.openLog(moves);

        // run simulator
        Simulator simulator = Simulator.getInstance(tubeList, moves);
        simulator.simulate(apMainPane, (ShellController) shell);
        simulator.setOnSimulationDone(() -> {
            tfStatus.setText("Simulation Done");
            unlockButton(btnSimulate);
        });
    }


    @Override
    public List<Tube> createTube(AnchorPane ap, int tubeNum) {

        // generate tube dimensions
        Map<String, Double> layoutSetting = new HashMap<>();
        UtilityGUI.configTubeLayout(layoutSetting, ap);

        // create tubes
        List<Tube> tubeList = new ArrayList<>();
        for (int i = 0; i < tubeNum; i++) {
            Tube tube = new Tube();

            // modify tubes for GUI
            UtilityGUI.setTubeDesign(tube);
            UtilityGUI.setTubeLayout(tube, layoutSetting);
            UtilityGUI.setTubeLabel(tube, ap);

            tubeList.add(tube);
            ap.getChildren().add(tube.getContainer());
        }

        return tubeList;
    }
    private void moveToNextTube(Tube currentTube) {

        UtilityGUI.borderTube(currentTube, false);

        currentTubeIndex++;
        if (currentTubeIndex < tubeList.size())
            UtilityGUI.borderTube(tubeList.get(currentTubeIndex), true);
        else
            currentTubeIndex--;
    }
    private void moveToPrevTube(Tube currentTube) {

        UtilityGUI.borderTube(currentTube, false);

        currentTubeIndex--;
        if (currentTubeIndex > -1)
            UtilityGUI.borderTube(tubeList.get(currentTubeIndex), true);
        else
            currentTubeIndex++;
    }


    private void lockButton(Node b) {
        b.setDisable(true);
    }
    private void unlockButton(Node b) {
        b.setDisable(false);
    }

}