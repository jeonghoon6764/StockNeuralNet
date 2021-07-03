package com.jlee3688gatech;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class LoadingScreenController {

    @FXML
    private ImageView imageView;
    @FXML
    private Pane newMainPane;
    
    private Image[] loadingImages;
    private String slash;
    private boolean threadRun;

    @FXML
    private void initialize() {

        this.threadRun = true;
        this.slash = UtilMethods.slash;

        loadingImages = new Image[15];
        for (int i = 0; i < loadingImages.length; i++) {
            String addr = slash + "Images" + slash + "Loading" + slash + "Loading" + i + ".png";
            loadingImages[i] = new Image(getClass().getResource(addr).toExternalForm());
        }

        ChangeLoadingImgClass changeLoadingImgClass = new ChangeLoadingImgClass();
        changeLoadingImgClass.start();
    }

    public synchronized boolean getAndSetThreadRunVar(Boolean val) {
        if (val != null) {
            this.threadRun = val;
        }

        return this.threadRun;
    }

    public class ChangeLoadingImgClass extends Thread {

        public void run() {
            while (getAndSetThreadRunVar(null)) {
                for (int i = 0; i < loadingImages.length; i++) {
                    int tempI = i;
                    Platform.runLater(() -> {
                        imageView.setImage(loadingImages[tempI]);
                    });

                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {}
                }
            }
        }
    }

    public Pane getNewMainPane() {
        return this.newMainPane;
    }
}
