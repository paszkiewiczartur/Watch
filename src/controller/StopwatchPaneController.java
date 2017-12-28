package controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.commons.lang3.time.StopWatch;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import shared.Lap;

public class StopwatchPaneController implements Initializable{
	public static final String LAP_TIME_COLUMN = "Lap time";

	private StopWatch stopWatch = new StopWatch();
	private ObservableList<Lap> lapList = FXCollections.observableArrayList();
	private Runnable task;
	private Thread backgroundThread;
	private Object monitor = new Object();
	private boolean isStarted = false;
	private boolean isPaused = false;

    @FXML
    private TextField StopwatchTextField;

	@FXML
    private TableView<Lap> StopwatchTableView;

    @FXML
    private Button StopwatchResetButton;

    @FXML
    private Button StopwatchClearButton;

    @FXML
    private ToggleButton StopwatchStartButton;

    @FXML
    private Button StopwatchMiddleTimeButton;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        System.out.println("StopwatchPaneController initialized");
        configureProperties();
        configureTable();
        configureButtons();
    }

    private void configureProperties(){
    	isStarted = false;
    	isPaused = false;
    	StopwatchStartButton.setMinWidth(59);
    	StopwatchStartButton.setMaxWidth(59);
    }

    private void configureTable() {
        TableColumn<Lap, String> timeColumn = new TableColumn<Lap, String>(LAP_TIME_COLUMN);
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        StopwatchTableView.getColumns().add(timeColumn);
        StopwatchTableView.setItems(lapList);
        StopwatchTextField.setText("00:00:000");
    }

    private void configureButtons(){
        StopwatchResetButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	lapList.clear();
            }
        });
        StopwatchClearButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	if(stopWatch.isSuspended()){
            		isStarted = false;
            		isPaused = false;
            		synchronized(monitor){
            			monitor.notify();
            		}
            		stopWatch.resume();
            		stopWatch.stop();
            		stopWatch.reset();
            	} else {
            		isStarted = false;
            		stopWatch.stop();
            		stopWatch.reset();
                    StopwatchStartButton.setSelected(false);
            	}
            	StopwatchStartButton.setText("Start");
            	StopwatchTextField.setText("00:00:000");
            }
        });
        StopwatchStartButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	if(!stopWatch.isStarted()){
                 	stopWatch.start();
                 	isStarted = true;
                 	startTask();
                 	StopwatchStartButton.setText("Pause");
            	} else {
            		if (StopwatchStartButton.isSelected()) {
                    	stopWatch.resume();
                    	isPaused = false;
                    	synchronized(monitor){
                    		monitor.notify();
                    	}
                     	StopwatchStartButton.setText("Pause");
            		} else {
                		isPaused = true;
                		stopWatch.suspend();
                		StopwatchStartButton.setText("Resume");
            		}
            	}
            }
        });
        StopwatchMiddleTimeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	if(stopWatch.isStarted()){
            		lapList.add(new Lap(getStringFromTime(stopWatch.getTime())));
            	}
            }
        });
    }

    private String getStringFromTime(long time){
    	long minutes;
    	long seconds;
    	long miliseconds;

    	if(time >= 60000){
    		minutes = time/60000;
    		seconds = (time%60000)/1000;
    	} else {
    		minutes = 0;
    		seconds = time/1000;
    	}
    	miliseconds = time%1000;
    	StringBuilder sb = new StringBuilder();
    	if(minutes < 10) sb.append("0");
    	sb.append(minutes);
    	sb.append(":");
    	if(seconds < 10) sb.append("0");
    	sb.append(seconds);
    	sb.append(":");
    	if(miliseconds < 100) sb.append("0");
    	if(miliseconds < 10) sb.append("0");
    	sb.append(miliseconds);

    	return sb.toString();
    }

	private void startTask(){
		// Create a Runnable
		task = new Runnable(){
			public void run(){
				start();
			}
		};

		// Run the task in a background thread
		backgroundThread = new Thread(task);
		// Terminate the running thread if the application exits
		backgroundThread.setDaemon(true);
		// Start the thread
		backgroundThread.start();
	}

    private void start(){
    	while(isStarted){
    		if(isPaused){
    			synchronized(monitor){
    				try {
						monitor.wait();
					} catch (InterruptedException e) {
						System.out.println("interrupted in wait");
						e.printStackTrace();
					}
    			}
    		} else{
    			try {
    				Thread.sleep(251);
    			} catch (InterruptedException e) {
    				System.out.println("interrupted in sleep");
    				e.printStackTrace();
    			}
    			Platform.runLater(new Runnable(){
    				@Override
    				public void run(){
    					StopwatchTextField.setText(getStringFromTime(stopWatch.getTime()));
    				}
    			});
    		}
    	}
    }
}
