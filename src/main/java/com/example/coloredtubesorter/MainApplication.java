package com.example.coloredtubesorter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    private static Stage primaryStage;

    // TODO FIX ERROR solvable scenario fails after resetting from a failed attempt
    // TODO IMPLEMENT simulator

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("setup-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Tube Sorter Cheat Engine");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public static void switchScene(String fxml, int data) {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(fxml));
            Parent root = fxmlLoader.load();

            BaseController controller = fxmlLoader.getController();
            controller.setData(data);
            controller.postInit();

            Scene nextScene = new Scene(root);
            primaryStage.setScene(nextScene);
            primaryStage.show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void openShell(String data) {

        try {
            Stage shell = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("shell-view.fxml"));
            Parent root = fxmlLoader.load();

            BaseController controller = fxmlLoader.getController();
            controller.setData(data);
            controller.postInit();

            shell.setScene(new Scene(root));
            shell.show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}