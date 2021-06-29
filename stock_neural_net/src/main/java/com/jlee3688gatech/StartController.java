package com.jlee3688gatech;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * This class is main method for starting the Program.
 * @author Jeonghoon Lee
 * @version 1.0
 */
public class StartController extends Application {
    private String slash;

    /**
     * Start method for play this simulator
     * @param primaryStage stage of FX
     * @throws Exception which is will throw exeption to method that called this method.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        slash = UtilMethods.slash;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXML" + slash + "WelcomeScreen.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Inspiration of Blockhead");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.setResizable(false);
        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>(){
            @Override
            public void handle(WindowEvent e) {
                Platform.exit();
                System.exit(0);
            }
        });
    }
    /**
     * main method for MainController this will launch the start() method.
     * @param args arguments arrays.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
