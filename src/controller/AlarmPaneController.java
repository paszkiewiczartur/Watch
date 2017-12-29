package controller;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTimePicker;

import alarm.Alarm;
import alarm.AlarmFileManager;
import alarm.AlarmRunnable;
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
import shared.Ring;

public class AlarmPaneController implements Initializable {
	public static final String DATE_COLUMN = "Date";
	public static final String TIME_COLUMN = "Time";

	private Thread backgroundThread = new Thread();
	private AlarmFileManager fileManager= new AlarmFileManager();

	public ObservableList<Alarm> alarmTableList = FXCollections.observableArrayList();
	public Alarm currentRingingAlarm = new Alarm();

	@FXML
    private Label volumeLabel;
    @FXML
    private JFXSlider volumeSlider;
    @FXML
    private Button resetButton;
    @FXML
    private TableView<Alarm> tableView;
    @FXML
    private Button addButton;
    @FXML
    public Button deleteButton;
    @FXML
    private JFXDatePicker datePicker;
    @FXML
    private JFXTimePicker timePicker;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        configureTable();
        readFromFile();
        configureResetButton();
        configureAddButton();
        configureDeleteButton();
        configurePickers();
        configureVolume();
    }

    private void configureTable(){
        TableColumn<Alarm, String> dateColumn = new TableColumn<Alarm, String>(DATE_COLUMN);
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        tableView.getColumns().add(dateColumn);

        TableColumn<Alarm, String> timeColumn = new TableColumn<Alarm, String>(TIME_COLUMN);
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        tableView.getColumns().add(timeColumn);

        tableView.setItems(alarmTableList);
    }

    private void readFromFile(){
        List<Alarm> alarmFileList;
        alarmFileList = fileManager.readFromFile();
    	alarmTableList.addAll(alarmFileList);
    	if(alarmFileList.size() > 0){
    		startTask();
    	}
    }

    private void configureResetButton(){
        resetButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	alarmTableList.clear();
            	backgroundThread.interrupt();
                if(Ring.getInstance().isPlaying()) {
                    Ring.getInstance().pause();
                }
                deleteButton.setText("Delete");
        		fileManager.writeToFile(alarmTableList);
            }
        });
    }

    private void configureAddButton(){
        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	Alarm alarm = createAlarm();
            	alarmTableList.add(alarm);
            	fileManager.writeToFile(alarmTableList);
            	if(!backgroundThread.isAlive()){
            		startTask();
            	}
            }
        });
    }

    private void configureDeleteButton(){
    	deleteButton.setMinWidth(59);
    	deleteButton.setMaxWidth(59);
    	deleteButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(Ring.getInstance().isPlaying()) {
                    Ring.getInstance().pause();
                    alarmTableList.remove(currentRingingAlarm);
                    deleteButton.setText("Delete");
                } else{
                	if(!tableView.getSelectionModel().isEmpty()){
            			int index = tableView.getSelectionModel().getSelectedIndex();
            			alarmTableList.remove(index);
            		}
                }
        		if(alarmTableList.size() == 0) {
        			System.out.println("interrupt");
        			backgroundThread.interrupt();
        		}
                fileManager.writeToFile(alarmTableList);
            }
        });
    }

    private Alarm createAlarm(){
       	LocalDate date = datePicker.getValue();
    	int day = date.getDayOfMonth();
    	int month = date.getMonthValue();
    	int year = date.getYear();
    	LocalTime time = timePicker.getValue();
    	int hour = time.getHour();
    	int minutes = time.getMinute();
    	return new Alarm(day, month, year, hour, minutes);
    }

    private void configurePickers(){
    	LocalDate date = LocalDate.now();
    	datePicker.setValue(date);
    	LocalTime time = LocalTime.now();
    	timePicker.setValue(time);
    }

    private void configureVolume() {
        final double minVolume = 0;
        final double maxVolume = 1;
        volumeSlider.setMin(minVolume);
        volumeSlider.setMax(maxVolume);
        volumeSlider.setValue(maxVolume);
        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                Ring.getInstance().getMediaPlayer().setVolume(newValue.doubleValue());
            }
        });
    }

	private void startTask(){
		backgroundThread = new Thread(new AlarmRunnable(this));
		backgroundThread.setDaemon(true);
		backgroundThread.start();
	}
}