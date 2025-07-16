package com.example.coloredtubesorter.Logic.Sort;

public class Move {

    private final int from;
    private final int to;

    public Move(int from, int to) {
        this.from = from;
        this.to = to;
    }

    public Move cloneMove() {
        return new Move(from, to);
    }

    @Override
    public String toString() {
        return "Tube " + from + " -> " + " Tube " + to;
    }
}
