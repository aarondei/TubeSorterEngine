package com.example.coloredtubesorter.Logic;

import com.example.coloredtubesorter.Elements.Tube;
import javafx.geometry.Pos;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.Map;

public class UtilityGUI {

    public static void configTubeLayout(Map<String, Double> layoutSettings, AnchorPane apMainPane) {
        layoutSettings.put("paneWidth", apMainPane.getPrefWidth());
        layoutSettings.put("spacing", 80.0);
        layoutSettings.put("rowSpacing", 150.0);
        layoutSettings.put("startX", 20.0);
        layoutSettings.put("startY", 50.0);
        layoutSettings.put("x", layoutSettings.get("startX"));
        layoutSettings.put("y", layoutSettings.get("startY"));
    }
    public static void setTubeDesign(Tube tube) {

        VBox container = tube.getContainer();

        container.setSpacing(2);
        container.setPrefSize(60, 120);
        container.setAlignment(Pos.BOTTOM_CENTER);

        borderTube(tube, false);
    }
    public static void setTubeLayout(Tube tube, Map<String, Double> layoutSetting) {

        // row warp
        if (layoutSetting.get("x") + 60 > layoutSetting.get("paneWidth")) {
            layoutSetting.put("x", layoutSetting.get("startX"));
            layoutSetting.put("y", layoutSetting.get("y") + layoutSetting.get("rowSpacing"));
        }
        tube.getContainer().setLayoutX(layoutSetting.get("x"));
        tube.getContainer().setLayoutY(layoutSetting.get("y"));

        layoutSetting.put(("x"), layoutSetting.get("x") + layoutSetting.get("spacing"));
    }
    public static void setTubeLabel(Tube tube, AnchorPane apMainPane) {

        Text label = new Text();
        label.setText(String.valueOf(tube.getName()));
        label.setLayoutX(tube.getContainer().getLayoutX());
        label.setLayoutY(tube.getContainer().getLayoutY());
        label.setFill(Color.GRAY);
        apMainPane.getChildren().add(label);
    }
    public static void borderTube(Tube tube, boolean highlight) {

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

    public static Rectangle createLiquid(Color color) {

        Rectangle rect = new Rectangle(45, 25, color);
        rect.setArcWidth(10);
        rect.setArcHeight(10);

        return rect;
    }
}
