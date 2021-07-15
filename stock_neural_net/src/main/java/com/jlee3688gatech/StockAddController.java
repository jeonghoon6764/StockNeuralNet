package com.jlee3688gatech;

import java.io.IOException;
import java.util.Calendar;
import java.util.Random;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Node;

/**
 * Controller class for StockAdd.fxml
 * @version 1.0
 * @author Jeonghoon Lee
 */
public class StockAddController {
    @FXML
    private ImageView yahooDownImageView;
    @FXML
    private TextField companyNameTextField;
    @FXML
    private TextField tickerSymbolTextField;
    @FXML
    private TextField dateStartTextField;
    @FXML
    private TextField dateEndTextField;
    @FXML
    private Button proceedButton;

    private Image[] yahooDownImages;
    private Image yahooDownFailImage;
    private Image yahooDownSuccessImage;
    private String slash;
    private Stage thisStage;
    private ListView<String> stockListView;
    private boolean runThread;
    private String companyName;
    private String tickerSymbol;
    private String startDate;
    private String endDate;
    private Learn1Controller learnController;
    private Learn1ServerVersionController learn1ServerVersionController;

    /**
     * init FXML method
     */
    @FXML
    private void initialize() {
        runThread = true;
        yahooDownImages = new Image[4];
        this.slash = UtilMethods.slash;
        for (int i = 0; i < yahooDownImages.length; i++) {
            String addr = UtilMethods.perOSStartAddress + "Images" + slash + "YahooDownload" + slash + "YahooDown" + i + ".png";
            yahooDownImages[i] = new Image(getClass().getResource(addr).toExternalForm());
        }
        String addr = UtilMethods.perOSStartAddress + "Images" + slash + "YahooDownload" + slash + "YahooDownFail.png";
        yahooDownFailImage = new Image(getClass().getResource(addr).toExternalForm());
        addr = UtilMethods.perOSStartAddress + "Images" + slash + "YahooDownload" + slash + "YahooDownSuccess.png";
        yahooDownSuccessImage = new Image(getClass().getResource(addr).toExternalForm());
        yahooDownImageView.setImage(yahooDownImages[0]);
        Calendar toPromptDateStr = Calendar.getInstance();
        toPromptDateStr.add(Calendar.DATE, -1);
        dateEndTextField.setPromptText(UtilMethods.calendarToSimpleString(toPromptDateStr));
        
        Platform.runLater(() -> {
            this.thisStage = (Stage) yahooDownImageView.getScene().getWindow();
            thisStage.setOnCloseRequest(new EventHandler<WindowEvent>(){
                @Override
                public void handle(WindowEvent e) {
                    getAndSetRunThreadVar(false);
                }
            });
        });
    }

    public void setLearn1Controller(Learn1Controller learnController) {
        this.learnController = learnController;
    }

    public void setLearn1ServerVersionController(Learn1ServerVersionController learn1ServerVersionController) {
        this.learn1ServerVersionController = learn1ServerVersionController;
    }

    /**
     * setter for listview.
     * Gets the list view from the previous screen. This allows you to control the list view on the main screen.
     * @param stockListView stockListView in main Screen
     */
    public void setStockListView(ListView<String> stockListView) {
        this.stockListView = stockListView;
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
     * @param actionEvent Action event
     * @throws IOException IO Exceprion
     */
    public void userClickProceed(ActionEvent actionEvent) throws IOException {
        ChangeYahooDownImgclass thread0 = new ChangeYahooDownImgclass();
        thread0.start();

        proceedButton.setDisable(true);
        companyNameTextField.setDisable(true);
        tickerSymbolTextField.setDisable(true);
        dateStartTextField.setDisable(true);
        dateEndTextField.setDisable(true);

        this.companyName = companyNameTextField.getText();
        this.tickerSymbol = tickerSymbolTextField.getText();
        this.startDate = dateStartTextField.getText();
        this.endDate = dateEndTextField.getText();

        makeStockDatasClass thread1 = new makeStockDatasClass();
        thread1.start();
    }

    /**
     * Thread for make stockdatas.
     */
    public class makeStockDatasClass extends Thread{

        public void run() {
            for (int i = 0; i < MainController.getAndSetStockDatasList(null).size(); i++) {
                if (tickerSymbol.equals(MainController.getAndSetStockDatasList(null).get(i).getTicker())) {
                    getAndSetRunThreadVar(false);
                    Platform.runLater(() -> {
                        yahooDownImageView.setImage(yahooDownFailImage);
                    });
                    try {
                        makeStockDatasClass.sleep(1000);
                    } catch (InterruptedException ie) {
                    }
                    Platform.runLater(() -> {
                        thisStage.close();
                    });
                    return;
                }
            }
            try {
                StockDatas stockDatas = new StockDatas(companyName, tickerSymbol, UtilMethods.CalendarMaker(startDate), UtilMethods.CalendarMaker(endDate));
                MainController.addStockToStockDatasList(stockDatas);
                getAndSetRunThreadVar(false);
                Platform.runLater(() -> {
                    yahooDownImageView.setImage(yahooDownSuccessImage);
                });
                try {
                    makeStockDatasClass.sleep(1000);
                } catch (InterruptedException ie) {
                }
                Platform.runLater(() -> {
                    thisStage.close();
                    if (stockListView != null) {
                        stockListView.setItems(FXCollections.observableArrayList(MainController.getStockDatasNameAndTicker()));
                    }
                    if (learnController != null) {
                        learnController.showRequiredStocks();
                    }
                    if (learn1ServerVersionController != null) {
                        learn1ServerVersionController.showRequiredStocks();
                    }
                });
            } catch (Exception e) {
                getAndSetRunThreadVar(false);
                Platform.runLater(() -> {
                    yahooDownImageView.setImage(yahooDownFailImage);
                });
                try {
                    makeStockDatasClass.sleep(1000);
                } catch (InterruptedException ie) {
                }
                Platform.runLater(() -> {
                    thisStage.close();
                });
            }
        }

    }

    /**
     * ActionEvent method. when user click cancel button
     * @param actionEvent Action event
     * @throws IOException IO Exceprion
     */
    public void userClickCancel(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) ((Node) (actionEvent.getSource())).getScene().getWindow();
        getAndSetRunThreadVar(false);
        stage.close();
    }

    /**
     * keyEvent method. when user type any button in the textField
     * @param keyEvent keyEvent
     * @throws IOException IO Exception
     */
    public void textFieldChanged(KeyEvent keyEvent) throws IOException {
        if (!companyNameTextField.getText().isEmpty() && !tickerSymbolTextField.getText().isEmpty()
          && !dateStartTextField.getText().isEmpty() && !dateEndTextField.getText().isEmpty()) {
            if (validDateChecker(dateStartTextField.getText()) && validDateChecker(dateEndTextField.getText())) {
                Platform.runLater(() -> {
                    proceedButton.setDisable(false);
                });
            } else {
                Platform.runLater(() -> {
                    proceedButton.setDisable(true);
                });
            }
        } else {
            Platform.runLater(() -> {
                proceedButton.setDisable(true);
            });
        }
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
