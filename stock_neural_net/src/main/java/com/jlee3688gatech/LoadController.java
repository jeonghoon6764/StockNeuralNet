package com.jlee3688gatech;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;

public class LoadController {
    @FXML
    private ListView listView;
    @FXML
    private ChoiceBox choiceBox;

    /**
     * Initializer method.
     */
    @FXML
    private void initialize() {
        SaveAndLoad saveAndLoad = new SaveAndLoad();
    }

}
