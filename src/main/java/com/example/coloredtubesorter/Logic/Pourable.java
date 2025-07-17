package com.example.coloredtubesorter.Logic;

import com.example.coloredtubesorter.Elements.ColorEnum;
import com.example.coloredtubesorter.Elements.Tube;
import javafx.scene.shape.Rectangle;

public interface Pourable {

    default void pour(Tube from, Tube to) {
        // quantifies 2 same-colored layers as 1 move
        while (canPour(from, to)) {
            Rectangle r = from.pourLiquid();
            to.fillLiquid(r);
        }
    }

    default boolean canPour(Tube from, Tube to) {

        // can pour = tops match color, from not empty, to not full, can pour if to empty
        if (from.isEmpty() || to.isFull()) return false;

        if (to.isEmpty()) return true;

        ColorEnum topFrom = getRectColor(from.getStackTube().top());
        ColorEnum topTo = getRectColor(to.getStackTube().top());

        return topFrom == topTo;
    }

    default ColorEnum getRectColor(Rectangle r) {
        return (ColorEnum) r.getUserData();
    }
}