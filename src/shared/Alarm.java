package shared;

import java.io.Serializable;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Alarm implements Serializable{

	private static final long serialVersionUID = 4308792603865529707L;
	private transient StringProperty date;
	private transient StringProperty time;
 	private int day;
 	private int month;
 	private int year;
 	private int hour;
 	private int minutes;
 	public String getDate() {
 		setDate();
 		return date.get();
 	}
 	public void setDate(StringProperty date) {
 		this.date = date;
 	}
 	public void setDate(){
		StringBuilder sbDate = new StringBuilder();
		sbDate.append(this.day);
		sbDate.append(".");
		if(this.month <10 ) sbDate.append("0");
		sbDate.append(this.month);
		sbDate.append(".");
		sbDate.append(this.year);
		this.date = new SimpleStringProperty(sbDate.toString());
 	}
 	public String getTime() {
 		setTime();
		return time.get();
	}
	public void setTime(StringProperty time) {
		this.time = time;
	}
	public void setTime(){
		StringBuilder sbTime = new StringBuilder();
		if(this.hour < 10) sbTime.append("0");
		sbTime.append(this.hour);
		sbTime.append(":");
		if(this.minutes < 10) sbTime.append("0");
		sbTime.append(this.minutes);
		this.time = new SimpleStringProperty(sbTime.toString());
	}
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getHour() {
		return hour;
	}
	public void setHour(int hour) {
		this.hour = hour;
	}
	public int getMinutes() {
		return minutes;
	}
	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}

	public Alarm(){}

	public Alarm(int day, int month, int year, int hour, int minutes){
		this.day = day;
		this.month = month;
		this.year = year;
		this.hour = hour;
		this.minutes = minutes;

		StringBuilder sbDate = new StringBuilder();
		sbDate.append(day);
		sbDate.append(".");
		if(month <10 ) sbDate.append("0");
		sbDate.append(month);
		sbDate.append(".");
		sbDate.append(year);
		this.date = new SimpleStringProperty(sbDate.toString());

		StringBuilder sbTime = new StringBuilder();
		if(hour < 10) sbTime.append("0");
		sbTime.append(hour);
		sbTime.append(":");
		if(minutes < 10) sbTime.append("0");
		sbTime.append(minutes);
		this.time = new SimpleStringProperty(sbTime.toString());
	}

    @Override
    public String toString() {
    	return "Alarm ["+getDay()+"."+getMonth()+"."+getYear()+", "+getHour()+":"+getMinutes()+"]";
    }
}
