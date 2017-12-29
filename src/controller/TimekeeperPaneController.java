package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;

import shared.Timestamp;

import com.jfoenix.controls.JFXSlider;

import javafx.application.Platform;
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
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;

public class TimekeeperPaneController implements Initializable {

	private boolean isStarted = false;
	private boolean isTimePaused = false;
	private boolean isStopped = false;
	private boolean shouldSetTime = false;
	private Runnable task;
	private Thread backgroundThread;
	private Object monitor = new Object();
    private Media media;
    private MediaPlayer mediaPlayer;
    private Timestamp[] times;
    private long lastSetTime;
	String fileName = "timestamps.watch";

    @FXML
    private Button TimekeeperClearButton;

    @FXML
    private ToggleButton TimekeeperStartButton;

    @FXML
    private TextField TimekeeperMinuteTextField;

    @FXML
    private Label TimekeeperSemicolonLabel;

    @FXML
    private TextField TimekeeperSecondTextField;

    @FXML
    private Menu TimekeeperChooseTimeMenu;

    @FXML
    private MenuItem TimekeeperChooseTimeItem1;

    @FXML
    private MenuItem TimekeeperChooseTimeItem2;

    @FXML
    private MenuItem TimekeeperChooseTimeItem3;

    @FXML
    private MenuItem TimekeeperChooseTimeItem4;

    @FXML
    private MenuItem TimekeeperChooseTimeItem5;

    @FXML
    private Label TimekeeperLabelVolume;

    @FXML
    private JFXSlider TimekeeperSliderVolume;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
       configureProperties();
       configureButtons();
       configureTimestamps();
       configureVolume();
       configureMediaPlayer();
    }

    private void configureMediaPlayer(){
		File file = new File("Iphone_SMS.mp3");
		URI u = file.toURI();
        media = new Media(u.toString());
        mediaPlayer = new MediaPlayer(media);
    }

    private void configureProperties(){
    	isStarted = false;
    	isStopped = false;
    	TimekeeperStartButton.setMinWidth(59);
    	TimekeeperStartButton.setMaxWidth(59);
    }

    private void configureButtons(){
        TimekeeperClearButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(mediaPlayer != null && mediaPlayer.getStatus() == Status.PLAYING) {
                    mediaPlayer.pause();
                }
            	if(!isStarted){
            		setTime(lastSetTime);
            	} else {
            		isStarted = false;
            		isStopped = true;
            		synchronized(monitor){
                		monitor.notify();
                	}
            		shouldSetTime = true;
            	}
            	TimekeeperStartButton.setText("Start");
            }
        });
        TimekeeperStartButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	if(!isStarted){
                    if(mediaPlayer != null && mediaPlayer.getStatus() == Status.PLAYING) {
                        mediaPlayer.pause();
                        TimekeeperStartButton.setSelected(false);
                        TimekeeperStartButton.setText("Start");
                    } else {
                    	startTask();
                    	TimekeeperStartButton.setText("Pause");
                    }
            	} else {
            		if (TimekeeperStartButton.isSelected()) {
                    	isTimePaused = false;
                    	synchronized(monitor){
                    		monitor.notify();
                    	}
                    	TimekeeperStartButton.setText("Pause");
            		} else {
                    	isTimePaused = true;
                    	TimekeeperStartButton.setText("Resume");
            		}
            	}
            }
        });
    }

    private void configureVolume() {
        final double minVolume = 0;
        final double maxVolume = 1;
        TimekeeperSliderVolume.setMin(minVolume);
        TimekeeperSliderVolume.setMax(maxVolume);
        TimekeeperSliderVolume.setValue(maxVolume);
        TimekeeperSliderVolume.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                mediaPlayer.setVolume(newValue.doubleValue());
            }
        });
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
    	isStarted = true;
    	isStopped = false;
    	shouldSetTime = false;
    	if(getTimeFromTextFields() != 0){
    		lastSetTime = getTimeFromTextFields();
    	}
    	updateTimestamps();
    	for(long i = getTimeFromTextFields(); i > 0 && isStarted; i--){
    		if(isTimePaused){
    			synchronized(monitor){
        			try {
						monitor.wait();
					} catch (InterruptedException e) {
						System.out.println("interrupted in wait");
						e.printStackTrace();
					}
    			}
    		}
    		if(!isStopped){
    			TimekeeperMinuteTextField.setText(String.valueOf(i/60));
    			TimekeeperSecondTextField.setText(String.valueOf(i%60));
    			try {
    				Thread.sleep(1000);
    			} catch (InterruptedException e) {
    				System.out.println("interrupted in sleep");
    				e.printStackTrace();
    			}
    			if(i == 1){
    	            mediaPlayer.play();
    	            mediaPlayer.setCycleCount(2);
        			Platform.runLater(new Runnable(){
        				@Override
        				public void run(){
            	        	TimekeeperStartButton.setText("Turn off");
        				}
        			});
    			}
    		}
    	}
    	TimekeeperMinuteTextField.setText("00");
    	TimekeeperSecondTextField.setText("00");
        TimekeeperStartButton.setSelected(false);
        isStarted = false;
        isTimePaused = false;
        if(shouldSetTime){
        	setTime(lastSetTime);
        }
    }

    private void configureTimestamps(){
    	times = new Timestamp[5];
        File file = new File(fileName);
        boolean fileExists = file.exists();
        if(fileExists){
    		try(
    				FileInputStream fis = new FileInputStream(fileName);
    				ObjectInputStream ois = new ObjectInputStream(fis);
    				){
    				for(int i=0; i<5; i++){
    					times[i] = (Timestamp) ois.readObject();
    				}
    			} catch (FileNotFoundException e){
    				e.printStackTrace();
    			} catch (IOException e){
    				e.printStackTrace();
    			} catch (ClassNotFoundException e){
    				e.printStackTrace();
    			}
        } else {
        	times[0] = new Timestamp(30,1);
    		times[1] = new Timestamp(60,1);
    		times[2] = new Timestamp(90,1);
    		times[3] = new Timestamp(120,1);
    		times[4] = new Timestamp(300,1);
        }

    	TimekeeperChooseTimeItem1.setText(getStringFromTime(times[0].getTime()));
    	TimekeeperChooseTimeItem2.setText(getStringFromTime(times[1].getTime()));
    	TimekeeperChooseTimeItem3.setText(getStringFromTime(times[2].getTime()));
    	TimekeeperChooseTimeItem4.setText(getStringFromTime(times[3].getTime()));
    	TimekeeperChooseTimeItem5.setText(getStringFromTime(times[4].getTime()));

    	long max = 0;
    	long time = 0;
    	for(int i=0; i<5; i++){
    		if(times[i].getAmount() > max){
    			max = times[i].getAmount();
    			time = times[i].getTime();
    		}
    	}
    	setTime(time);
    	lastSetTime = time;

        TimekeeperChooseTimeItem1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	setTime(times[0].getTime());
            }
        });
        TimekeeperChooseTimeItem2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	setTime(times[1].getTime());
            }
        });
        TimekeeperChooseTimeItem3.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	setTime(times[2].getTime());
            }
        });
        TimekeeperChooseTimeItem4.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	setTime(times[3].getTime());
            }
        });
        TimekeeperChooseTimeItem5.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	setTime(times[4].getTime());
            }
        });

    }

    private String getStringFromTime(long time){
    	StringBuilder sb = new StringBuilder();
    	if(time/60 < 10){
    		sb.append("0");
    	}
    	sb.append(String.valueOf(time/60));
    	sb.append(":");
    	if(time%60 < 10){
    		sb.append("0");
    	}
    	sb.append(String.valueOf(time%60));

    	return sb.toString();
    }

    private void updateTimestamps(){
    	long last = lastSetTime;
    	boolean found = false;
    	for(int i=0; i<5; i++){
    		if(times[i].getTime() == last){
    			times[i].setAmount(times[i].getAmount() + 1);
    			found = true;
    			break;
    		}
    	}
    	if(found == false){
    		int min = 0;
    		for(int i=0; i< 5; i++){
    			if(times[i].getAmount() < times[min].getAmount()){
    				min = i;
    			}
    		}
    		times[min].setTime(last);
    		times[min].setAmount(2);
    	}
        File file = new File(fileName);
        boolean fileExists = file.exists();
        if(!fileExists) {
            try {
                fileExists = file.createNewFile();
            } catch (IOException e) {
                System.out.println("Failed operation of creating new file.");
            }
        }
		try(
				FileOutputStream fs = new FileOutputStream(fileName);
				ObjectOutputStream os = new ObjectOutputStream(fs);
				){
				for(int i=0; i<5; i++){
					os.writeObject(times[i]);
					System.out.println("time "+times[i].getTime()+", amount"+times[i].getAmount());
				}
			} catch (FileNotFoundException e){
				e.printStackTrace();
			} catch (IOException e){
				e.printStackTrace();
			}

			System.out.println("Objects have been saved to the file.");
    }

    private void setTime(long time){
    	TimekeeperMinuteTextField.setText(String.valueOf(time/60));
    	TimekeeperSecondTextField.setText(String.valueOf(time%60));
    }

    private long getTimeFromTextFields(){
    	long result = 0;
    	result += Long.parseLong(TimekeeperSecondTextField.getText());
    	result += 60 * Long.parseLong(TimekeeperMinuteTextField.getText());
    	return result;
    }
}
