package com.example.coloredtubesorter.Logic.Sort;

import com.example.coloredtubesorter.Elements.Tube;

import java.util.List;

public record State(List<Tube> tubeConfig, List<Move> moveHistory) {

    @Override
    public String toString() {
        return moveHistory.toString();
    }
}
