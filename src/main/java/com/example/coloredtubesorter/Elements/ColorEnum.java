package com.example.coloredtubesorter.Elements;

import javafx.scene.paint.Color;

public enum ColorEnum {
    RED("#FF0000"),
    ORANGE("#FFA500"),
    YELLOW("#FFEF00"),
    LIME("#00E600"),
    FOREST("#2E6F40"),
    BLUE("#0000FF"),
    CYAN("#00FFFF"),
    VIOLET("#9400D3"),
    LAVENDER("#CCCCFF"),
    SALMON("#FA8072"),
    ROUGE("#FF007F"),
    BROWN("#896129"),
    GRAY("#80808D"),
    NOIR("#0C0B0A"),
    BEIGE("#E1C699");

    private final String hex;

    ColorEnum(String hex) {
        this.hex = hex;
    }

    public Color getColor() {
        return Color.web(hex);
    }
    public static ColorEnum fromName(String name) {
        return ColorEnum.valueOf(name.toUpperCase());
    }
    public static ColorEnum convertColorObj(Color c) {

        for (ColorEnum ce : values()) {
            if (Math.abs(c.getRed() - ce.getColor().getRed()) < 0.001 &&
                    Math.abs(c.getGreen() - ce.getColor().getGreen()) < 0.001 &&
                    Math.abs(c.getBlue() - ce.getColor().getBlue()) < 0.001) {
                return ce;
            }
        }
        throw new IllegalArgumentException("No matching ColorEnum for: " + c);
    }
}
