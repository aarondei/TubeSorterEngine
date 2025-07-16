package com.example.coloredtubesorter;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class SetupController {

    @FXML
    private Text tfFrontText;
    @FXML
    private TextField tfTubeNum;

    @FXML
    private void onClick() {

        String tubeNumRaw = tfTubeNum.getText();
        int tubeNum;

        if (!tubeNumRaw.isEmpty() && tubeNumRaw.chars().allMatch(Character::isDigit) && !tubeNumRaw.equals("0")) {
            tubeNum = Integer.parseInt(tubeNumRaw);

            int limit = 6;
            if (tubeNum > limit) {
                tfFrontText.setText("Tube Creation Limit: " + limit);
                return;
            }

            // pass to controller B
            MainApplication.switchScene("main-view.fxml", tubeNum);
        } else {
            // error handling
            tfFrontText.setText("Please enter valid number.");
        }
    }
}