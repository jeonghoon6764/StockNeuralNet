package com.jlee3688gatech;

import java.io.IOError;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.Node;

public class EditNeuralNetSetController {

    @FXML
    private ListView<String> neuralNetListView;
    @FXML
    private ListView<Label> noteListView;
    @FXML
    private TextArea textArea;
    @FXML
    private Button addButton;

    private ArrayList<NeuralNetSet> neuralNetSets;
    private String slash;

    /**
     * Initializer method.
     */
    @FXML
    private void initialize() {
        this.slash = UtilMethods.slash;
        addButton.setDisable(true);
        this.neuralNetSets = MainController.getAndSetNeuralNetSetsList(null);
        showNeuralNetListView();
    }


    private void showNeuralNetListView() {

        ArrayList<String> names = new ArrayList<>();

        for (int i = 0; i < this.neuralNetSets.size(); i++) {
            names.add(this.neuralNetSets.get(i).getName());
        }
        Platform.runLater(() -> {
            neuralNetListView.setItems(FXCollections.observableArrayList(names));
        });
    }

    private void showNoteListView(int idx) {

        ArrayList<Label> note = new ArrayList<>();

        for (int i = 0; i < this.neuralNetSets.get(idx).getNotes().size(); i++) {
            String str = this.neuralNetSets.get(idx).getNotes().get(i)[0] + " : " + this.neuralNetSets.get(idx).getNotes().get(i)[1];
            Label tempLabel = new Label(str);
            tempLabel.setWrapText(true);
            tempLabel.setMaxWidth(noteListView.getWidth() - 20);
            note.add(tempLabel);
        }

        Platform.runLater(() -> {
            noteListView.setItems(FXCollections.observableArrayList(note));
            noteListView.getSelectionModel().selectLast();
        });
    }

    public void userTypeTextArea(KeyEvent keyEvent) throws IOException{
        if (textArea.getText().isEmpty()) {
            Platform.runLater(() -> {
                addButton.setDisable(true);
            });
        } else {
            Platform.runLater(() -> {
                addButton.setDisable(false);
            });
        }
    }

    public void userClickNeuralNetListView(MouseEvent mouseEvent) throws IOException {
        
        if (neuralNetListView.getSelectionModel().isEmpty()) {
            return;
        }
        int idx = neuralNetListView.getSelectionModel().getSelectedIndex();
        showNoteListView(idx);
    }

    public void userClickAddButton(ActionEvent actionEvent) throws IOException {
        int neuralNetIdx = neuralNetListView.getSelectionModel().getSelectedIndex();

        this.neuralNetSets.get(neuralNetIdx).addNote(textArea.getText());
        showNoteListView(neuralNetIdx);
        Platform.runLater(() -> {
            textArea.setText("");
            addButton.setDisable(true);
        });
    }

    /**
     * ActionEvent method. when user click proceed button
     * @param actionEvent Action event
     * @throws IOException IO Exceprion
     */
    public void userClickBack(ActionEvent actionEvent) throws IOException {
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXML" + slash + "MainScreen.fxml"));
        Parent root = loader.load();
        MainScreenController controller = loader.<MainScreenController>getController();
        Scene scene = new Scene(root, 600, 400);
        Stage stage = (Stage) ((Node) (actionEvent.getSource())).getScene().getWindow();
        stage.setResizable(false);
        stage.setScene(scene);
    }
}
