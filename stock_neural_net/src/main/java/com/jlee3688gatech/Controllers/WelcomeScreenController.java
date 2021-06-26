package com.jlee3688gatech.Controllers;

import com.jlee3688gatech.UtilMethods;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class WelcomeScreenController {

    

    private String slash;
    private static boolean runThread;

    @FXML
    private Image[] neuralNetImages;

    @FXML
    private ImageView neuralNetAnimation;
    
    @FXML
    private void initialize() {
        this.slash = UtilMethods.getOSSlash();
        this.runThread = true;

        neuralNetImages = new Image[8];
        for (int i = 1; i <= 8; i++) {
            neuralNetImages[i - 1] = new Image(getClass().getResourceAsStream(".." + slash + "Images" + slash + "NN0" + i + ".png"));
        }
        neuralNetAnimation = new ImageView();

        ChangeNeuralNetImgClass thread0 = new ChangeNeuralNetImgClass();
        thread0.start();

        
    }

    public synchronized static void getRunThread() {
        return this.runThread;
    }

    private class ChangeNeuralNetImgClass extends Thread {
        public void run() {
            try {
                int i = 0;
                while(runThread) {
                    if (i >= 8) {
                        i = 0;
                    }
                    neuralNetAnimation.setImage(neuralNetImages[i]);
                    i++;
                    Thread.sleep(500);
                }
            }catch(Exception e) {
            }
        }
    }

    
}
