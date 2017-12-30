package controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXSlider;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import shared.Ring;
import timekeeper.TimekeeperFileManager;
import timekeeper.TimekeeperRunnable;
import timekeeper.TimekeeperTimeConverter;
import timekeeper.Timestamp;
import timekeeper.TimestampsUpdater;

public class TimekeeperPaneController implements Initializable {
	private Thread backgroundThread = new Thread();
    private TimestampsUpdater updater = new TimestampsUpdater(this);
    private TimekeeperFileManager fileManager = new TimekeeperFileManager();
    private MenuItem[] chooseTimeItems = new MenuItem[5];

    public Object monitor = new Object();
    public long lastSetTime;
	public boolean isTimePaused = false;
    public Timestamp[] timestamps;
    public TimekeeperTimeConverter converter = new TimekeeperTimeConverter(this);

    @FXML
    private Button clearButton;
    @FXML
    public ToggleButton startButton;
    @FXML
    public TextField minuteTextField;
    @FXML
    private Label semicolonLabel;
    @FXML
    public TextField secondTextField;
    @FXML
    private Menu chooseTimeMenu;
    @FXML
    private MenuItem chooseTimeItem1;
    @FXML
    private MenuItem chooseTimeItem2;
    @FXML
    private MenuItem chooseTimeItem3;
    @FXML
    private MenuItem chooseTimeItem4;
    @FXML
    private MenuItem chooseTimeItem5;
    @FXML
    private Label labelVolume;
    @FXML
    private JFXSlider sliderVolume;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
<<<<<<< HEAD
    	System.out.println("test");
=======
>>>>>>> 9ed2872bcd37e3053be142fd04102e5c0586aa93
       createChooseTab();
       configureClearButton();
       configureStartButton();
       configureTimestamps();
       configureVolume();
    }

    private void createChooseTab(){
    	chooseTimeItems[0] = chooseTimeItem1;
    	chooseTimeItems[1] = chooseTimeItem2;
    	chooseTimeItems[2] = chooseTimeItem3;
    	chooseTimeItems[3] = chooseTimeItem4;
    	chooseTimeItems[4] = chooseTimeItem5;
    }

    private void configureClearButton(){
        clearButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(Ring.getInstance().isPlaying()) {
                    Ring.getInstance().stop();
                }
            	if(backgroundThread.isAlive()){
            		backgroundThread.interrupt();
            	}
        		converter.setTime(lastSetTime);
            	startButton.setText("Start");
            }
        });
    }

    private void configureStartButton(){
    	startButton.setMinWidth(59);
    	startButton.setMaxWidth(59);
        startButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	if(!backgroundThread.isAlive()){
                    if(Ring.getInstance().isPlaying()) {
                        Ring.getInstance().stop();
                        startButton.setSelected(false);
                        startButton.setText("Start");
                    } else {
                    	startTask();
                    	startButton.setText("Pause");
                    }
            	} else {
            		if (startButton.isSelected()) {
                    	isTimePaused = false;
                    	synchronized(monitor){
                    		monitor.notify();
                    	}
                    	startButton.setText("Pause");
            		} else {
                    	isTimePaused = true;
                    	startButton.setText("Resume");
            		}
            	}
            }
        });
    }

    private void configureTimestamps(){
    	timestamps = fileManager.readFromFile();
    	for(int i=0; i<5; i++){
    		final int j = i;
    		chooseTimeItems[i].setText(converter.getStringFromTime(timestamps[i].getTime()));
    		chooseTimeItems[i].setOnAction(new EventHandler<ActionEvent>() {
    			@Override
    			public void handle(ActionEvent event){
    				converter.setTime(timestamps[j].getTime());
    			}
    		});
    	}
    	converter.setTime(timestamps[5].getTime());
    	lastSetTime = timestamps[5].getTime();
    }

    private void configureVolume() {
        final double minVolume = 0;
        final double maxVolume = 1;
        sliderVolume.setMin(minVolume);
        sliderVolume.setMax(maxVolume);
        sliderVolume.setValue(maxVolume);
        sliderVolume.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                Ring.getInstance().setVolume(newValue.doubleValue());
            }
        });
    }

	private void startTask(){
		backgroundThread = new Thread(new TimekeeperRunnable(this));
		backgroundThread.setDaemon(true);
		backgroundThread.start();
	}

    public void updateTimestamps(){
    	updater.updateTimestamps();
    	fileManager.writeToFile(timestamps);
    }
}