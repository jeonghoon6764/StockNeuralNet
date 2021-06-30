package com.jlee3688gatech;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.Node;

/**
 * Controller method for StockUpdateView
 * @version 1.0
 * @author Jeonghoon Lee
 */
public class StockUpdateController {
    @FXML
    private ListView<String> stocksListView;
    @FXML
    private TextField dateToUpdateTextField;
    @FXML
    private Button startUpdateButton;
    @FXML
    private ImageView yahooDownImageView;
    @FXML
    private Button selectAllButton;
    @FXML
    private Button clearSelectionButton;
    @FXML
    private Button backButton;


    private String slash;
    private Image[] yahooDownImages;
    private boolean threadVar;
    private int currTarget;
    
    /**
     * init FXML method
     */
    @FXML
    private void initialize() {
        this.currTarget = -1;
        this.slash = UtilMethods.slash;
        this.threadVar = false;
        showInitListView();
        startUpdateButton.setDisable(true);
        stocksListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        Calendar toPromptDateStr = Calendar.getInstance();
        toPromptDateStr.add(Calendar.DATE, -1);
        dateToUpdateTextField.setPromptText(UtilMethods.calendarToSimpleString(toPromptDateStr));
        yahooDownImages = new Image[4];
        for (int i = 0; i < yahooDownImages.length; i++) {
            String addr = slash + "Images" + slash + "YahooDownload" + slash + "YahooDown" + i + ".png";
            yahooDownImages[i] = new Image(getClass().getResource(addr).toExternalForm());
        }
        yahooDownImageView.setImage(yahooDownImages[0]);
    }

    /**
     * Method for initialize ListView
     */
    private void showInitListView() {
        ArrayList<String> nameAndTicker = MainController.getStockDatasSpecific();
        stocksListView.setItems(FXCollections.observableArrayList(nameAndTicker));
    }

    /**
     * getter or/and setter synchronized method for runThread variable.
     * @param val value. if null this will work as getter method.
     * @return value of threadVar variable.
     */
    public synchronized boolean getAndSetRunThreadVar(Boolean val) {
        if (val != null) {
            threadVar = val;
        }
        return threadVar;
    }

    /**
     * getter or/and setter synchronized method for currTarget variable.
     * @param val value. if null this will work as getter method.
     * @return value of currTarget variable.
     */
    public synchronized int getAndSetCurrTarget(Integer i) {
        if (i != null) {
            currTarget = i;
        }
        return currTarget;
    }

    /**
     * Thread for changing yahoo download image.
     */
    private class ChangeYahooDownImgclass extends Thread {
        @FXML
        public void run() {
            Random rd = new Random();
            try {
                while(getAndSetRunThreadVar(null)) {
                    Platform.runLater(() -> {
                        yahooDownImageView.setImage(yahooDownImages[rd.nextInt(4)]);
                    });
                    Thread.sleep(100);                    
                }
            } catch (Exception e) {
                getAndSetRunThreadVar(false);
            }
            Platform.runLater(() -> {
                yahooDownImageView.setImage(yahooDownImages[0]);
            });
        }
    }

    /**
     * ActionEvent method. when user click proceed button
     * @param actionEvent Action event
     * @throws IOException IO Exceprion
     */
    public void userClickStartUpdate(ActionEvent actionEvent) throws IOException {
        dateToUpdateTextField.setDisable(true);
        startUpdateButton.setDisable(true);
        backButton.setDisable(true);
        clearSelectionButton.setDisable(true);
        selectAllButton.setDisable(true);

        ObservableList<Integer> selectedList = stocksListView.getSelectionModel().getSelectedIndices();
        ArrayList<Integer> copy = new ArrayList<>();
        for (int i = 0; i < selectedList.size(); i++) {
            copy.add(selectedList.get(i));
        }
        UpdateStartClass updateStartClass = new UpdateStartClass(copy);
        updateStartClass.start();
    }

    /**
     * class for updatestart method.
     * for separating thread purpose.
     */
    public class UpdateStartClass extends Thread{
        private ArrayList<Integer> selectedList;

        public UpdateStartClass (ArrayList<Integer> selectedList) {
            this.selectedList = selectedList;
        }

        public void run() {
            getAndSetRunThreadVar(true);
            ChangeYahooDownImgclass changeYahooDownImgclass = new ChangeYahooDownImgclass();
            changeYahooDownImgclass.start();

            for (int i = 0; i < selectedList.size(); i++) {
                getAndSetCurrTarget(selectedList.get(i));
                BlinkBlinkTargetClass blinkBlinkTargetClass = new BlinkBlinkTargetClass(selectedList.get(i), 200);
                blinkBlinkTargetClass.start();
                MainController.stockDatasList.get(selectedList.get(i)).updateStockData(UtilMethods.CalendarMaker(dateToUpdateTextField.getText()));
                Platform.runLater(() -> {
                    showInitListView();
                    for (int j = 0; j < selectedList.size(); j++) {
                        stocksListView.getSelectionModel().select(selectedList.get(j));
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {}
            }
            getAndSetRunThreadVar(false);
            getAndSetCurrTarget(-1);
            dateToUpdateTextField.setDisable(false);
            startUpdateButton.setDisable(false);
            backButton.setDisable(false);
            clearSelectionButton.setDisable(false);
            selectAllButton.setDisable(false);
        }
    }

    /**
     * class for make some effect (select/clear)
     * for separating thread purpose.
     */
    public class BlinkBlinkTargetClass extends Thread {
        private int blinkTarget;
        private int delay;

        public BlinkBlinkTargetClass(int blinkTarget, int delay) {
            this.blinkTarget = blinkTarget;
            this.delay = delay;
        }

        public void run() {
            
            
            while(blinkTarget == getAndSetCurrTarget(null)) {
                if (stocksListView.getSelectionModel().isSelected(blinkTarget)) {
                    Platform.runLater(() -> {
                        stocksListView.getSelectionModel().clearSelection(blinkTarget);
                    });
                } else {
                    Platform.runLater(() -> {
                        stocksListView.getSelectionModel().select(blinkTarget);
                    });
                }
                try {
                    BlinkBlinkTargetClass.sleep(delay);
                } catch (Exception e) {}
            }
            Platform.runLater(() -> {
                stocksListView.getSelectionModel().select(blinkTarget);
            });
        }
    }
    


    /**
     * ActionEvent method. when user click proceed button
     * @param actionEvent Action event
     * @throws IOException IO Exceprion
     */
    public void userClickSelectAll(ActionEvent actionEvent) throws IOException {
        stocksListView.getSelectionModel().selectAll();
    }

    /**
     * ActionEvent method. when user click proceed button
     * @param actionEvent Action event
     * @throws IOException IO Exceprion
     */
    public void userClickClearSelection(ActionEvent actionEvent) throws IOException {
        stocksListView.getSelectionModel().clearSelection();
    }

    /**
     * ActionEvent method. when user click proceed button
     * @param actionEvent Action event
     * @throws IOException IO Exceprion
     */
    public void userClickBack(ActionEvent actionEvent) throws IOException {
        getAndSetRunThreadVar(false);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXML" + slash + "MainScreen.fxml"));
        Parent root = loader.load();
        MainScreenController controller = loader.<MainScreenController>getController();
        Scene scene = new Scene(root, 600, 400);
        Stage stage = (Stage) ((Node) (actionEvent.getSource())).getScene().getWindow();
        stage.setResizable(false);
        stage.setScene(scene);
    }

    /**
     * ActionEvent method. when user click proceed button
     * @param mouseEvent Action event
     * @throws IOException IO Exceprion
     */
    public void anyChangeInTextArea(MouseEvent mouseEvent) throws IOException {
        renewStartUpdateButton();
    }

    /**
     * Helper method for renew the start update button
     */
    private void renewStartUpdateButton() {
        if (!dateToUpdateTextField.getText().isEmpty() && !stocksListView.getSelectionModel().isEmpty()) {
            if (validDateChecker(dateToUpdateTextField.getText())) {
                startUpdateButton.setDisable(false);
            } else {
                startUpdateButton.setDisable(true);
            }
        } else {
            startUpdateButton.setDisable(true);
        }
    }


    /**
     * keyEvent method. when user type any button in the textField
     * @param keyEvent keyEvent
     * @throws IOException IO Exception
     */
    public void textFieldChanged(KeyEvent keyEvent) throws IOException {
        renewStartUpdateButton();
    }

    /**
     * method for valid String checker.
     * form should be a number of eight integers.
     * (e.g 20010101, 20210621)
     * @param str inputString
     * @return true if it is valid otherwise false
     */
    private boolean validDateChecker(String str) {
        if (str.length() != 8) {
            return false;
        }
        try {
            Integer.parseInt(str);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
