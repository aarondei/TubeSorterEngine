package com.example.coloredtubesorter.Logic;

import com.example.coloredtubesorter.Elements.Tube;
import javafx.scene.layout.AnchorPane;

import java.util.List;

public interface Creator {

    List<Tube> createTube(AnchorPane ap, int tubeNum);
}
