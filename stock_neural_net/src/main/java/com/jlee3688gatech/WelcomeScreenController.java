package com.jlee3688gatech;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * Welcome Screen Controller.
 * @version 1.0
 * @author Jeonghoon Lee
 */
public class WelcomeScreenController {

    private String slash;
    private static boolean runThread;

    @FXML
    private Image[] neuralNetImages;

    @FXML
    private ImageView neuralNetAnimation;

    @FXML
    private CheckBox checkReadAndUnderstood;

    @FXML
    private Button continueButton;

    @FXML
    private TextArea warningTextArea;
    
    /**
     * Initializer method.
     */
    @FXML
    private void initialize() {
        this.slash = UtilMethods.getOSSlash();
        runThread = true;
        setWarningText();
        neuralNetImages = new Image[8];
        for (int i = 1; i <= 8; i++) {
            String addr = slash + "Images" + slash + "NeuralNetAnimation" + slash + "NN0" + i + ".png";
            neuralNetImages[i - 1] = new Image(getClass().getResource(addr).toExternalForm());
        }
        ChangeNeuralNetImgClass thread0 = new ChangeNeuralNetImgClass();
        thread0.start();

    }

    /**
     * getter/setter of runThread variable.
     * @param var if just want to get variable, put null.
     * @return runThread variable.
     */
    private synchronized static boolean getAndSetRunThreadVar(Boolean var) {
        if (var != null) {
            runThread = var;
        }
        return runThread;
    }

    /**
     * Method for make animation of Neural Net picture.
     */
    private class ChangeNeuralNetImgClass extends Thread {
        @FXML
        public void run() {
            try {
                boolean isInc = true;
                int i = 0;
                while(getAndSetRunThreadVar(null)) {
                    neuralNetAnimation.setImage(neuralNetImages[i]);
                    if (isInc) {
                        i++;
                    } else {
                        i--;
                    }
                    if (i <= 0 || i >= 7) {
                        isInc = !isInc;
                    }
                    Thread.sleep(100);
                }
            }catch(Exception e) {
                getAndSetRunThreadVar(false);
            }
        }
    }

    /**
     * EventAction method. when user click radioBox (read/understand warning button)
     * @param ActionEvent Action Event
     * @throws IOException IO Exception
     */
    public void userCheckRadioBox(ActionEvent actionEvent) throws IOException {
        if (checkReadAndUnderstood.isSelected()) {
            continueButton.setDisable(false);
        } else {
            continueButton.setDisable(true);
        }
    }

    /**
     * ActionEvent method. when user click continue button
     * @param actionEvent Action event
     * @throws IOException IO Exceprion
     */
    public void userClickContinue(ActionEvent actionEvent) throws IOException {
        getAndSetRunThreadVar(false);
        //FXMLLoader loader = new FXMLLoader(getClass().getResource("WelcomeScreen.fxml"));
        //Parent root = loader.load();
        //WelcomeScreenController controller = loader.<WelcomeScreenController>getController();
        //Scene scene = new Scene(root, 600, 400);
        //Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        //stage.setScene(scene);
    }

    /**
     * Setter for Warning Text Area.
     */
    private void setWarningText() {
        String warning = new String();
        warning += "WARNING. Read carefully.\n";
        warning += "This program was made and produced by a student studying computer science" +
        " who is just spending time without doing anything (such as internships) during the summer vacation.";
        warning += "\n This program provides its own artificial neural network and data, but it's as stupid as the program maker.";
        warning += "\n This program's creator has little knowledge and experience in stocks, and it is designed to review the student's one of last semester's classes.";
        warning += "\n Any financial or/and mental damage caused by this program is the user's responsibility, and this program creator will not take any responsibility.";

        warningTextArea.setText(warning);
    }
}
