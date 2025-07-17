package com.example.coloredtubesorter.Elements;

import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Tube {
    private static int order = 1;
    private int name;

    private boolean isFull = false;
    private int stackLiquidCount = 0;

    // visual GUI node
    private VBox container = new VBox();
    // actual node
    private StackTube stackTube;

    public Tube() {
        name = order++;
        stackTube = new StackTube(container);
    }

    // MAIN METHODS
    public void fillLiquid(Rectangle rect) {

        if (isFull) {
            return;
        }

        stackTube.push(rect);
        stackLiquidCount++;

        if (stackLiquidCount == 4) isFull = true;
    }
    public Rectangle pourLiquid() {

        if (stackLiquidCount == 0) {
            return null;
        }

            Rectangle r = stackTube.top();
            stackTube.pop();
            stackLiquidCount--;

        if (stackLiquidCount != 4) isFull = false;

        return r;
    }

    public Tube cloneTube() {
        Tube copy = new Tube();

        // primitive fields
        copy.name = this.name;
        copy.isFull = this.isFull;
        copy.stackLiquidCount = this.stackLiquidCount;

        // deep copy
        copy.container = new VBox();
        copy.stackTube = new StackTube(copy.container);

        for (int i = this.getStackTube().getStackLayers().size() -1; i >= 0; i--)  {
            Rectangle orig = this.getStackTube().getStackLayers().get(i);
            copy.stackTube.push(cloneContent(orig));
        }

        return copy;
    }
    private Rectangle cloneContent(Rectangle orig) {
        Rectangle newRect = new Rectangle(orig.getWidth(), orig.getHeight(), orig.getFill());
        newRect.setArcWidth(10);
        newRect.setArcHeight(10);
        newRect.setUserData(ColorEnum.convertColorObj((Color) orig.getFill()));
        return newRect;
    }

    // ABSTRACTED METHODS
    public boolean isFull() { return isFull; }
    public boolean isEmpty() {
        return (stackLiquidCount == 0);
    }
    public int getLiquidCount() { return stackLiquidCount; }

    // GETTERS SETTERS
    public int getName() {
        return name;
    }
    public VBox getContainer() {
        return container;
    }
    public StackTube getStackTube() { return stackTube; }

    public void resetTube() {
        isFull = false;
        stackLiquidCount = 0;
        stackTube.resetStackTube();
    }
}
