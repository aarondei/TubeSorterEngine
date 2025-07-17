package com.example.coloredtubesorter;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class ShellController extends BaseController {

    @FXML
    private TextArea taShellArea;

    private String data;
    private String[] raw;

    public void setData(Object data) {
        this.data = (String) data;
    }

    public void postInit() {
        raw = data.split("\n");
    }

    public void track(int i) throws ArrayIndexOutOfBoundsException {
        taShellArea.setText(taShellArea.getText() + raw[i] + "\n");
    }

    public TextArea getShellArea() {
        return taShellArea;
    }
}
