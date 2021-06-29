package com.jlee3688gatech;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.slf4j.helpers.Util;

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
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.Node;

public class StockUpdateController {
    @FXML
    private ListView<String> stocksListView;
    @FXML
    private TextField dateToUpdateTextField;
    @FXML
    private Button startUpdateButton;
    @FXML
    private ImageView yahooDownImageView;

    private boolean runThread;
    private String slash;
    private Image[] yahooDownImages;
    private boolean[] flagVars;
    
    



    /**
     * init FXML method
     */
    @FXML
    private void initialize() {
        this.slash = UtilMethods.slash;
        flagVars = new boolean[MainController.stockDatasList.size()];
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

    private void showInitListView() {
        ArrayList<String> nameAndTicker = MainController.getStockDatasSpecific();
        stocksListView.setItems(FXCollections.observableArrayList(nameAndTicker));
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
        }
    }

    public synchronized boolean getAndSetFlagVar(Boolean val, int idx) {
        if (val != null) {
            this.flagVars[idx] = val;
        }
        return this.flagVars[idx];
    }

    private class UpdateStockDatasClass extends Thread {
        private int idx;
        private Calendar date;

        public UpdateStockDatasClass(int idx, Calendar date) {
            this.idx = idx;
            this.date = date;
        }

        @FXML
        public void run() {
            getAndSetFlagVar(true, idx);
            BlinkListViewClass tempThread = new BlinkListViewClass(idx);
            tempThread.start();
            MainController.stockDatasList.get(idx).updateStockData(date);
            getAndSetFlagVar(false, idx);
        }
    }

    private class BlinkListViewClass extends Thread {
        private int idx;

        public BlinkListViewClass(int idx) {
            this.idx = idx;
        }

        @FXML
        public void run() {
            while(getAndSetFlagVar(null, idx)) {
                try {
                    if (stocksListView.getSelectionModel().isSelected(idx)) {
                        Platform.runLater(() -> {
                            stocksListView.getSelectionModel().clearSelection(idx);
                        });
                    } else {
                        Platform.runLater(() -> {
                            stocksListView.getSelectionModel().select(idx);
                        });
                    }
                    BlinkListViewClass.sleep(200);
                } catch (Exception e) {
                    break;
                }
            }
            stocksListView.getSelectionModel().select(idx);
        }
    }

    private class UpdateStockDatasMasterClass extends Thread{
        private int delay = 0;
        private ObservableList<Integer> selectionList;

        public UpdateStockDatasMasterClass(int delay, ObservableList<Integer> selectionList) {
            this.delay = delay;
            this.selectionList = selectionList;
        }

        @FXML
        public void run() {
            for (int i = 0; i < selectionList.size(); i++) {
                UpdateStockDatasClass temp = new UpdateStockDatasClass(selectionList.get(i), UtilMethods.CalendarMaker(dateToUpdateTextField.getText()));
                temp.start();
                try {
                    UpdateStockDatasMasterClass.sleep(delay);
                } catch (Exception e) {}
            }
        }
    }

    public class CheckAllFlagVarAreFalseClass extends Thread {
        public void run() {
            while (true) {
                boolean total = false;
                for (int i = 0; i < flagVars.length; i++) {
                    total |= getAndSetFlagVar(null, i);
                }
                if (!total) {
                    break;
                }
                try {
                    CheckAllFlagVarAreFalseClass.sleep(1000);
                } catch (Exception e) {}
            }
            getAndSetRunThreadVar(false);
        }
    }

    /**
     * ActionEvent method. when user click proceed button
     * @param actionEvent Action event
     * @throws IOException IO Exceprion
     */
    public void userClickStartUpdate(ActionEvent actionEvent) throws IOException {
        getAndSetRunThreadVar(true);
        ChangeYahooDownImgclass thread0 = new ChangeYahooDownImgclass();
        thread0.start();
        ObservableList<Integer> selectionList = stocksListView.getSelectionModel().getSelectedIndices();
        UpdateStockDatasMasterClass updateStockDatasMasterClass = new UpdateStockDatasMasterClass(3000, selectionList);   
        updateStockDatasMasterClass.start();
        CheckAllFlagVarAreFalseClass checkAllFlagVarAreFalseClass = new CheckAllFlagVarAreFalseClass();
        checkAllFlagVarAreFalseClass.start();
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
     * getter/setter of runThread variable.
     * @param var if just want to get variable, put null.
     * @return runThread variable.
     */
    private synchronized boolean getAndSetRunThreadVar(Boolean var) {
        if (var != null) {
            runThread = var;
        }
        return runThread;
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
