package controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

public class MainPaneController implements Initializable {

    @FXML
    private MenuPaneController menuPaneController;
    @FXML
    private AlarmPaneController alarmPaneController;
    @FXML
    private TimekeeperPaneController timekeeperPaneController;
    @FXML
    private StopwatchPaneController stopwatchPaneController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}