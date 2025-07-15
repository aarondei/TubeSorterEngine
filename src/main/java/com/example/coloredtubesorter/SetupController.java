package com.example.coloredtubesorter;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class SetupController {
    @FXML
    private Button btnStart;
    @FXML
    private TextField tfTubeNum;

    @FXML
    private void onClick() {

        String tubeNumRaw = tfTubeNum.getText();
        int tubeNum;

        if (!tubeNumRaw.isEmpty() && tubeNumRaw.chars().allMatch(Character::isDigit)) {
            tubeNum = Integer.parseInt(tubeNumRaw);

            // pass to controller B
            MainApplication.switchScene("main-view.fxml", tubeNum);
        } else {
            // do shaky red thing
        }
    }
}