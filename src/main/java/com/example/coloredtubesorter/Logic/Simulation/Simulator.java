package com.example.coloredtubesorter.Logic.Simulation;

import com.example.coloredtubesorter.BaseController;
import com.example.coloredtubesorter.Elements.Tube;
import com.example.coloredtubesorter.Logic.Creator;
import com.example.coloredtubesorter.Logic.Pourable;
import com.example.coloredtubesorter.Logic.UtilityGUI;
import com.example.coloredtubesorter.MainApplication;
import com.example.coloredtubesorter.ShellController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;

public class Simulator implements Pourable, Creator {
    private static Simulator instance;

    private final List<Tube> origTubeList;
    private final String moveHistory;

    private List<Tube> copyTubeList;

    Timeline timeline;

    private Simulator(List<Tube> origTubeList, String moveHistory) {
        this.origTubeList = origTubeList;
        this.moveHistory = moveHistory;
    }
    public static Simulator getInstance(List<Tube> tubeList, String moveHistory) {
        if (instance == null) {
            instance = new Simulator(tubeList, moveHistory);
        }
        return instance;
    }


    public void simulate(AnchorPane apMainPane, ShellController controller) {

        linkSimulationToShellWindow(controller);

        apMainPane.getChildren().clear();
        copyTubeList = createTube(apMainPane, origTubeList.size());

        // SIMULATION BEGINS HERE
        String[] history = moveHistory.split("\n");
        int[] i = {0};

        timeline = new Timeline(
                new KeyFrame(Duration.seconds(1.5), e -> {

                    if (i[0] < history.length) {

                        List<Integer> move;

                        try {
                            controller.track(i[0]);
                            String str = history[i[0]];
                            move = decryptMove(str);

                        } catch (ArrayIndexOutOfBoundsException ex) {
                            return;
                        }

                        applyMove(move.getFirst(), move.getLast());
                        i[0]++;
                    }
                })
        );
        timeline.setCycleCount(history.length);
        timeline.play();
    }

    public void setOnSimulationDone(Runnable callback) {
        timeline.setOnFinished(e -> callback.run());
    }
    private void linkSimulationToShellWindow(ShellController controller) {

        Stage shell = (Stage) controller.getShellArea().getScene().getWindow();
        shell.setOnCloseRequest(e -> timeline.stop());
    }
    public void restart(AnchorPane apMainPane) {
        copyTubeList.clear();
        timeline = null;

        // open log window
        BaseController shell = MainApplication.openLog(moveHistory);
        simulate(apMainPane, (ShellController) shell);
    }

    private List<Integer> decryptMove(String str) throws ArrayIndexOutOfBoundsException {

        String[] raw = str.substring(str.indexOf(":") + 2).split("[^0-9]+");
        List<Integer> moves = new ArrayList<>();

        try {
            moves.add(Integer.parseInt(raw[1]));
            moves.add(Integer.parseInt(raw[2]));
        } catch (NumberFormatException e) {
            System.err.println("Attempting to decrypt: " + raw[1]);
            System.err.println("Attempting to decrypt: " + raw[2]);
        }
        return moves;
    }
    private void applyMove(int fromID, int toID) {

        Tube from = null, to = null;
        for (Tube t : copyTubeList) {
            if (t.getName() == fromID) from = t;
            if (t.getName() == toID) to = t;
            if (from != null && to != null) break;
        }

        if (from == null || to == null) throw new RuntimeException("Simulated tubes are null.");

        pour(from, to);
    }


    @Override
    public List<Tube> createTube(AnchorPane ap, int tubeNum) {

        // generate tube dimensions
        Map<String, Double> layoutSetting = new HashMap<>();
        UtilityGUI.configTubeLayout(layoutSetting, ap);

        // deep copy tubes
        List<Tube> copyTubeList = new ArrayList<>();
        for (int i = 0; i < tubeNum; i++) {
            Tube copy = origTubeList.get(i).cloneTube();

            // modify tubes for GUI
            UtilityGUI.setTubeDesign(copy);
            UtilityGUI.setTubeLayout(copy, layoutSetting);
            UtilityGUI.setTubeLabel(copy, ap);

            copyTubeList.add(copy);
            ap.getChildren().add(copy.getContainer());
        }

        return copyTubeList;

    }

}
