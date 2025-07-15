package com.example.coloredtubesorter;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class ShellController extends BaseController {

    @FXML
    private TextArea taShellArea;

    private String data;

    public void setData(Object data) {
        this.data = (String) data;
    }

    public void postInit() {
        taShellArea.setText(data);
    }
}
