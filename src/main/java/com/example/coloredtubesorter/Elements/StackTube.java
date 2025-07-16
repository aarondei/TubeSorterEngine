package com.example.coloredtubesorter.Elements;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class StackTube {

    private final ObservableList<Node> stack;

    public StackTube(VBox container) {
        container.setAlignment(Pos.BOTTOM_CENTER);
        stack = container.getChildren();
    }

    public void push(Rectangle r) {
        stack.addFirst(r);
    }
    public void pop() {
        stack.removeFirst();
    }
    public Rectangle top() {
        Node n = stack.getFirst();

        if (n instanceof Rectangle r) return r;
        else {
            System.err.println("StackTube attempting to return a non-Rectangle Node");
            throw new RuntimeException();
        }
    }

    public List<Rectangle> getStackLayers() {
        List<Rectangle> list = new ArrayList<>();

        for (Node n : stack) {
            list.add((Rectangle) n);
        }
        return list;
    }

    public void resetStackTube() {
        stack.clear();
    }
}
