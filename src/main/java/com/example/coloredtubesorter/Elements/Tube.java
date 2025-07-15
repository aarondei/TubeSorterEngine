package com.example.coloredtubesorter.Elements;

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class Tube {
    private static int order = 1;
    private int name;
    private List<Double> coordinates = new ArrayList<>();

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
            System.out.println("Tube is full.");
            return;
        }

        stackTube.push(rect);
        stackLiquidCount++;

        if (stackLiquidCount == 4) isFull = true;
    }
    public Rectangle pourLiquid() {

        if (stackLiquidCount == 0) {
            System.out.println("Tube is empty.");
            return null;
        }

            Rectangle r = stackTube.top();
            stackTube.pop();
            stackLiquidCount--;

        if (stackLiquidCount != 4) isFull = false;

        return r;
    }
    public List<Tube> cloneState(List<Tube> orig) {
        List<Tube> copy = new ArrayList<>();
        for (Tube t : orig) {
            copy.add(cloneTube(t));
        }
        return copy;
    }
    private Tube cloneTube(Tube orig) {
        Tube copy = new Tube();

        // primitive fields
        copy.name = orig.name;
        copy.isFull = orig.isFull;
        copy.stackLiquidCount = orig.stackLiquidCount;

        // deep copy
        copy.container = new VBox();
        copy.stackTube = new StackTube(copy.container);

        for (Rectangle r : orig.stackTube.getStackLayers()) {
            Rectangle newRect = new Rectangle(r.getWidth(), r.getHeight(), r.getFill());
            copy.stackTube.push(newRect);
        }
        return copy;
    }

    // ABSTRACTED METHODS
    public boolean isFull() { return isFull; }
    public boolean isEmpty() {
        return (stackLiquidCount == 0);
    }
    public int getLiquidCount() { return stackLiquidCount; }
    public Rectangle peekTop() {
        return stackTube.top();
    }

    // GETTERS SETTERS
    public int getName() {
        return name;
    }
    public VBox getContainer() {
        return container;
    }
    public StackTube getStackTube() { return stackTube; }
    public List<Double> getCoordinates() {
        return coordinates;
    }
    public void setCoordinates(Double x, Double y) {
        coordinates.add(x);
        coordinates.add(y);
    }
}
