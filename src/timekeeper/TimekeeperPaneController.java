package timekeeper;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;

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

public class TimekeeperPaneController implements Initializable {
	private Thread backgroundThread = new Thread();
    private TimestampsUpdater updater = new TimestampsUpdater(this);
    private TimekeeperFileManager fileManager = new TimekeeperFileManager();
    private MenuItem[] chooseTimeItems = new MenuItem[5];

    long lastSetTime;
	boolean isTimePaused = false;
    Timestamp[] timestamps;
    TimekeeperTimeConverter converter = new TimekeeperTimeConverter(this);
    CountDownLatch latch;

    @FXML
    private Button clearButton;
    @FXML
    ToggleButton startButton;
    @FXML
    TextField minuteTextField;
    @FXML
    private Label semicolonLabel;
    @FXML
    TextField secondTextField;
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
            	startButton.setText("Start");
            	converter.setTime(lastSetTime);
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
                    	latch.countDown();
                    	startButton.setText("Pause");
            		} else {
                    	isTimePaused = true;
            			latch = new CountDownLatch(1);
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