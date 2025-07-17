package com.example.coloredtubesorter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    private static Stage primaryStage;
    private static Stage shell;

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

    public static BaseController openLog(Object data) {

        try {
            if (shell != null && shell.isShowing()) shell.close();

            shell = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("shell-view.fxml"));
            Parent root = fxmlLoader.load();

            BaseController controller = fxmlLoader.getController();
            controller.setData(data);
            controller.postInit();

            shell.setScene(new Scene(root));
            shell.setX(primaryStage.getWidth() + primaryStage.getX());
            shell.setY(primaryStage.getY());
            shell.show();

            return controller;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void closeShell() {
        if (shell != null && shell.isShowing()) shell.close();
    }
}