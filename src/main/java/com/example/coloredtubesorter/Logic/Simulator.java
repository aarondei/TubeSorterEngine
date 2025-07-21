package com.example.coloredtubesorter.Logic;

public class Simulator implements Runnable {
    private Simulator instance;

    private Simulator() {

    }

    private Simulator getInstance() {
        if (instance == null) {
            instance = new Simulator();
        }
        return instance;
    }

    public void simulate() {
        // create a separate simulation shell then run
        run();
    }
    public void restart() {

    }

    @Override
    public void run() {

    }
}
