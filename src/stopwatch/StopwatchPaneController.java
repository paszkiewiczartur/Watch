package stopwatch;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang3.time.StopWatch;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;

public class StopwatchPaneController implements Initializable{
	private static final String LAP_TIME_COLUMN = "Lap time";

	private ObservableList<Lap> lapList = FXCollections.observableArrayList();
	private Thread backgroundThread;

	CountDownLatch latch;
	boolean isPaused = false;
	StopWatch stopWatch = new StopWatch();
	StopwatchTimeConverter converter = new StopwatchTimeConverter();

    @FXML
    TextField textField;
	@FXML
    private TableView<Lap> tableView;
    @FXML
    private Button resetButton;
    @FXML
    private Button clearButton;
    @FXML
    private ToggleButton startButton;
    @FXML
    private Button middleTimeButton;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
    	isPaused = false;
        configureTable();
        configureResetButton();
        configureClearButton();
        configureStartButton();
        configureMiddleTimeButton();
    }

    private void configureTable() {
        TableColumn<Lap, String> timeColumn = new TableColumn<Lap, String>(LAP_TIME_COLUMN);
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        tableView.getColumns().add(timeColumn);
        tableView.setItems(lapList);
        textField.setText("00:00:000");
    }

    private void configureResetButton(){
        resetButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	lapList.clear();
            }
        });
    }

    private void configureClearButton(){
        clearButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	backgroundThread.interrupt();
            	if(stopWatch.isSuspended()){
            		isPaused = false;
            		stopWatch.resume();
            	} else {
                    startButton.setSelected(false);
            	}
        		stopWatch.stop();
        		stopWatch.reset();
            	startButton.setText("Start");
            	textField.setText("00:00:000");
            }
        });
    }

    private void configureStartButton(){
    	startButton.setMinWidth(59);
    	startButton.setMaxWidth(59);
        startButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	if(!stopWatch.isStarted()){
                 	stopWatch.start();
                 	startTask();
                 	startButton.setText("Pause");
            	} else {
            		if (startButton.isSelected()) {
                    	isPaused = false;
            			latch.countDown();
                       	stopWatch.resume();
                     	startButton.setText("Pause");
            		} else {
                		isPaused = true;
                		stopWatch.suspend();
                		latch = new CountDownLatch(1);
                		startButton.setText("Resume");
            		}
            	}
            }
        });
    }

    private void configureMiddleTimeButton(){
        middleTimeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	if(stopWatch.isStarted()){
            		lapList.add(new Lap(converter.getStringFromTime(stopWatch.getTime())));
            	}
            }
        });
    }

	private void startTask(){
		backgroundThread = new Thread(new StopwatchRunnable(this));
		backgroundThread.setDaemon(true);
		backgroundThread.start();
	}
}