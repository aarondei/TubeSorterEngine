package com.example.coloredtubesorter.Logic.Sort;

import com.example.coloredtubesorter.Elements.Tube;

import java.util.List;

public class State {

    private final List<Tube> tubeConfig;
    private final List<Move> moveHistory;

    public State(List<Tube> tubeConfig, List<Move> moveHistory) {
        this.tubeConfig = tubeConfig;
        this.moveHistory = moveHistory;
    }

    public List<Tube> getTubeConfig() {
        return tubeConfig;
    }

    public List<Move> getMoveHistory() {
        return moveHistory;
    }

    @Override
    public String toString() {
        return moveHistory.toString();
    }
}
