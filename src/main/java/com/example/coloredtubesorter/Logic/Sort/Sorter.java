package com.example.coloredtubesorter.Logic.Sort;

import com.example.coloredtubesorter.Elements.ColorEnum;
import com.example.coloredtubesorter.Elements.Tube;
import javafx.scene.shape.Rectangle;

import java.util.*;

public class Sorter {

    private static Sorter instance;

    Queue<State> states = new LinkedList<>();
    private final List<Tube> tubeList;
    private final Set<String> visitedStates = new HashSet<>();

    private final StringBuilder log = new StringBuilder();
    private List<Tube> finalConfig;


    private Sorter(List<Tube> tubeList) {
        this.tubeList = tubeList;
    }

    public static Sorter getInstance(List<Tube> tubeList) {
        if (instance == null) {
            instance = new Sorter(tubeList);
        }
        return instance;
    }


    public boolean pathFind() {

        // initial state
        states.add(new State(copyTubeList(tubeList), new ArrayList<>()));

        while (!states.isEmpty()) {
            // dequeue
            State s = states.poll();

            // check if state already visited, if not mark state as visited
            if (visitedStates.contains(serializeState(s))) continue;
            visitedStates.add(serializeState(s));

            // check if solved
            if (isSolved(s)) {
                transcribeHistory(s.getMoveHistory());
                transcribeState(s.getTubeConfig());
                return true;
            }

            // perform pouring
            for (Tube from : s.getTubeConfig()) {

                if (from.isEmpty()) continue;

                for (Tube to : s.getTubeConfig()) {

                    // optimize
                    if (from == to) continue;
                    if (to.isEmpty() && hasBetterMatchingTube(from, s)) continue;

                    if (canPour(from, to) && isOverflowSafe(from, to)) {

                        // copy state
                        List<Tube> tubeListCopyNewState = copyTubeList(s.getTubeConfig());
                        List<Move> moveHistoryCopy = copyMoveHistory(s.getMoveHistory());

                        // get tubes from copied state
                        Tube fromCopy = getTubeCopy(from.getName(), tubeListCopyNewState);
                        Tube toCopy = getTubeCopy(to.getName(), tubeListCopyNewState);

                        // perform move
                        Move m = new Move(fromCopy.getName(), toCopy.getName());
                        pour(fromCopy, toCopy);
                        moveHistoryCopy.add(m);

                        // enqueue new state
                        State newState = new State(tubeListCopyNewState, moveHistoryCopy);
                        states.add(newState);
                    }
                }
            }
        }

        return false;
    }

    private List<Tube> copyTubeList(List<Tube> orig) {
        List<Tube> copy = new ArrayList<>();
        for (Tube t : orig) {
            copy.add(t.cloneTube());
        }
        return copy;
    }
    private List<Move> copyMoveHistory(List<Move> orig) {
        List<Move> copy = new ArrayList<>();
        for (Move m : orig) {
            copy.add(m.cloneMove());
        }
        return copy;
    }

    private boolean isSolved(State s) {

        for (Tube t : s.getTubeConfig()) {

            if (t.isEmpty()) continue;

            ColorEnum top = getRectColor(t.getStackTube().top());
            for (Rectangle r : t.getStackTube().getStackLayers()) {
                if (top != getRectColor(r)) return false;
            }

            if (!t.isFull()) return false;
        }

        return true;
    }

    private void pour(Tube from, Tube to) {

        // quantifies 2 same-colored layers as 1 move
        while (canPour(from, to)) {
            Rectangle r = from.pourLiquid();
            to.fillLiquid(r);
        }
    }
    private Tube getTubeCopy(int name, List<Tube> tubes) {
        for (Tube t : tubes) {
            if (t.getName() == name) return t;
        }

        throw new RuntimeException("Tube copy reference is null.");
    }

    private boolean canPour(Tube from, Tube to) {

        // can pour = tops match color, from not empty, to not full, can pour if to empty
        if (from.isEmpty() || to.isFull()) return false;

        if (to.isEmpty()) return true;

        ColorEnum topFrom = getRectColor(from.getStackTube().top());
        ColorEnum topTo = getRectColor(to.getStackTube().top());

        return topFrom == topTo;
    }
    private boolean hasBetterMatchingTube(Tube from, State s) {

        // pruning that looks in the current tube list in advance to scout for same color, non-empty to tubes
        // prevents wasting of empty tubes
        ColorEnum fromTop = getRectColor(from.getStackTube().top());
        for (Tube t : s.getTubeConfig()) {
            // if from is not to, to is not full, to is not empty, top colors match
            if (t != from && !t.isFull() && !t.isEmpty()) {
                ColorEnum tTop = getRectColor((t.getStackTube().top()));
                if (tTop == fromTop) return true;
            }
        }

        return false;
    }
    private boolean isOverflowSafe(Tube from, Tube to) {

        // pruning that prevents moves that result in overflowing tube to
        if (from.isEmpty() || to.isFull()) return false;

        if (to.isEmpty()) return true;

        ColorEnum fromTop = getRectColor(from.getStackTube().top());
        ColorEnum toTop = getRectColor(to.getStackTube().top());

        if (fromTop != toTop) return false;

        int fromLayers = 0;
        for (Rectangle r : from.getStackTube().getStackLayers()) {
           if (r.getUserData() == fromTop) fromLayers++;
           else break;
        }

        return (fromLayers + to.getLiquidCount()) <= 4;
    }

    private String serializeState(State s) {
        StringBuilder sb = new StringBuilder();

        for (Tube t : s.getTubeConfig()) {

            if (t.isEmpty()) {
                sb.append("EMPTY|");
                continue;
            }

            for (Rectangle r : t.getStackTube().getStackLayers()) {
                sb.append(getRectColor(r).name()).append(",");
            }
            sb.append("|");
        }

        return sb.toString();

//        List<String> tubeStrings = new ArrayList<>();
//
//        for (Tube t : s.getTubeConfig()) {
//            if (t.isEmpty()) {
//                tubeStrings.add("EMPTY");
//                continue;
//            }
//
//            List<String> colors = new ArrayList<>();
//            for (Rectangle r : t.getStackTube().getStackLayers()) {
//                colors.add(getRectColor(r).name());
//            }
//
//            // optional: sort colors in the tube (if order doesn't matter within tube)
//            // Collections.sort(colors);
//
//            tubeStrings.add(String.join(",", colors));
//        }
//
//        Collections.sort(tubeStrings); // ✨ canonical tube ordering
//        return String.join("|", tubeStrings);
    }

    private ColorEnum getRectColor(Rectangle r) {
        return (ColorEnum) r.getUserData();
    }


    private void transcribeHistory(List<Move> moveList) {

        int i = 1;
        for (Move m : moveList) {
            log.append(i).append(": ").append(m.toString()).append("\n");
            i++;
        }

    }
    public String extractHistory() {

        return log.isEmpty() ? "No solution found." : log.toString();
    }

    private void transcribeState(List<Tube> tubes) {
        finalConfig = tubes;
    }
    public List<Tube> extractState() {
        return finalConfig;
    }
}
