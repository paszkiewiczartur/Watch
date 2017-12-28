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
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTimePicker;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import shared.Alarm;

public class AlarmPaneController implements Initializable {
	public static final String DATE_COLUMN = "Date";
	public static final String TIME_COLUMN = "Time";

	private ObservableList<Alarm> alarmTableList = FXCollections.observableArrayList();
	private List<Alarm> alarmFileList;
	private Media media;
    private MediaPlayer mediaPlayer;
	private String fileName = "alarms.watch";
	private Runnable task;
	private Thread backgroundThread;
	private boolean isStarted = false;
	private int currentRingingAlarm;

	@FXML
    private Label AlarmVolumeLabel;

    @FXML
    private JFXSlider AlarmVolumeSlider;

    @FXML
    private Button AlarmResetButton;

    @FXML
    private TableView<Alarm> AlarmTableView;

    @FXML
    private Button AlarmAddButton;

    @FXML
    private Button AlarmDeleteButton;

    @FXML
    private JFXDatePicker AlarmDatePicker;

    @FXML
    private JFXTimePicker AlarmTimePicker;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        System.out.println("AlarmPaneController initialized");
        configureProperties();
        configureTable();
        configureButtons();
        configurePickers();
        configureMediaPlayer();
        configureVolume();
    }

    private void configureProperties(){
    	AlarmDeleteButton.setMinWidth(59);
    	AlarmDeleteButton.setMaxWidth(59);
    	isStarted = false;
    	alarmFileList = new LinkedList<>();
    }

    private void configureTable(){

        TableColumn<Alarm, String> dateColumn = new TableColumn<Alarm, String>(DATE_COLUMN);
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        AlarmTableView.getColumns().add(dateColumn);

        TableColumn<Alarm, String> timeColumn = new TableColumn<Alarm, String>(TIME_COLUMN);
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        AlarmTableView.getColumns().add(timeColumn);

        AlarmTableView.setItems(alarmTableList);

        File file = new File(fileName);
        boolean fileExists = file.exists();
        if(fileExists){
    		try(
    				FileInputStream fis = new FileInputStream(fileName);
    				ObjectInputStream ois = new ObjectInputStream(fis);
    			){
    				int size = (int) ois.read();
    				Alarm alarm;
    				while(size>0){
    					alarm = (Alarm) ois.readObject();
    					alarmFileList.add(alarm);
    					size--;
    				}
    			} catch (FileNotFoundException e){
    				e.printStackTrace();
    			} catch (IOException e){
    				e.printStackTrace();
    			} catch (ClassNotFoundException e){
    				e.printStackTrace();
    			}
				for(int i=0; i<alarmFileList.size(); i++){
					alarmFileList.get(i).setDate();
				}
    			alarmTableList.addAll(alarmFileList);

    			if(alarmFileList.size() > 0){
    				isStarted = true;
    				startTask();
    			}
        }
    }

    private void configureButtons(){
        AlarmResetButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	if(!shouldTheBellRing()){
            		alarmTableList.clear();
            		alarmFileList.clear();
            		updateAlarms();
            		isStarted = false;
            	}
            }
        });
        AlarmAddButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	LocalDate date = AlarmDatePicker.getValue();
            	int day = date.getDayOfMonth();
            	int month = date.getMonthValue();
            	int year = date.getYear();
            	LocalTime time = AlarmTimePicker.getValue();
            	int hour = time.getHour();
            	int minutes = time.getMinute();
            	Alarm alarm = new Alarm(day, month, year, hour, minutes);
            	alarmTableList.add(alarm);
            	alarmFileList.add(alarm);
            	updateAlarms();
            	if(!isStarted){
            		isStarted = true;
            		startTask();
            	}
            }
        });
        AlarmDeleteButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(mediaPlayer != null && mediaPlayer.getStatus() == Status.PLAYING) {
                    mediaPlayer.pause();
                    alarmTableList.remove(currentRingingAlarm);
                    alarmFileList.remove(currentRingingAlarm);
                    AlarmDeleteButton.setText("Delete");
                } else{
                	if(!AlarmTableView.getSelectionModel().isEmpty()){
            			int index = AlarmTableView.getSelectionModel().getSelectedIndex();
            			alarmTableList.remove(index);
            			alarmFileList.remove(index);
            		}
            		if(alarmFileList.size() == 0) {
            			isStarted = false;
            		}
                }
    			updateAlarms();
            }
        });
    }

    private void configurePickers(){
    	LocalDate date = LocalDate.now();
    	AlarmDatePicker.setValue(date);

    	LocalTime time = LocalTime.now();
    	AlarmTimePicker.setValue(time);
    }

    private void configureMediaPlayer(){
		File file = new File("Iphone_SMS.mp3");
		URI u = file.toURI();
        media = new Media(u.toString());
        mediaPlayer = new MediaPlayer(media);
    }

    private void updateAlarms(){

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
				os.write(alarmFileList.size());
				for(int i=0; i<alarmFileList.size(); i++){
					os.writeObject(alarmFileList.get(i));
				}
			} catch (FileNotFoundException e){
				e.printStackTrace();
			} catch (IOException e){
				e.printStackTrace();
			}

			System.out.println("Objects have been saved to the file.");
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
			if(shouldTheBellRing()){
    			Platform.runLater(new Runnable(){
    				@Override
    				public void run(){
    					AlarmDeleteButton.setText("Turn off");}
    			});
	            mediaPlayer.play();
	            mediaPlayer.setCycleCount(2);
			}
			try {
    				Thread.sleep(10000);
    			} catch (InterruptedException e) {
    				System.out.println("interrupted in sleep");
    				e.printStackTrace();
    			}
    	}
    }

    private boolean shouldTheBellRing(){
    	LocalDate date = LocalDate.now();
    	LocalTime time = LocalTime.now();
    	boolean found = false;

    	for(int i=0; i<alarmFileList.size(); i++){
    		if(date.getDayOfMonth() == alarmFileList.get(i).getDay() && date.getMonthValue() == alarmFileList.get(i).getMonth()
    				&& date.getYear() == alarmFileList.get(i).getYear()
    				&& time.getHour() == alarmFileList.get(i).getHour() && time.getMinute() == alarmFileList.get(i).getMinutes()){
    			found = true;
    			currentRingingAlarm = i;
    			break;
    		}
    	}
    	return found;
    }

    private void configureVolume() {
        final double minVolume = 0;
        final double maxVolume = 1;
        AlarmVolumeSlider.setMin(minVolume);
        AlarmVolumeSlider.setMax(maxVolume);
        AlarmVolumeSlider.setValue(maxVolume);
        AlarmVolumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                mediaPlayer.setVolume(newValue.doubleValue());
            }
        });
    }

}
