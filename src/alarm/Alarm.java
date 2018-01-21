package alarm;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Alarm implements Serializable{
	private static final long serialVersionUID = 7465049257657539187L;
	private StringProperty date;
	private StringProperty time;
 	private int day;
 	private int month;
 	private int year;
 	private int hour;
 	private int minutes;

 	public String getDate() {
 		return date.get();
 	}
 	public void setDate(StringProperty date) {
 		this.date = date;
 	}
 	public String getTime() {
		return time.get();
	}
	public void setTime(StringProperty time) {
		this.time = time;
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

	public Alarm(int day, int month, int year, int hour, int minutes){
		this.day = day;
		this.month = month;
		this.year = year;
		this.hour = hour;
		this.minutes = minutes;
		this.date = makeDate(day, month, year);
		this.time = makeTime(hour, minutes);
	}

	private Object writeReplace() {
		return new SerializationProxy(this);
	}

	private void readObject(ObjectInputStream stream) throws InvalidObjectException {
		throw new InvalidObjectException("Proxy is required");
	}

    @Override
    public String toString() {
    	return "Alarm ["+getDay()+"."+getMonth()+"."+getYear()+", "+getHour()+":"+getMinutes()+"]";
    }

    private static class SerializationProxy implements Serializable {
		private static final long serialVersionUID = -1956012241705660032L;
     	private int day;
     	private int month;
     	private int year;
     	private int hour;
     	private int minutes;

     	SerializationProxy(Alarm alarm) {
     		this.day = alarm.day;
     		this.month = alarm.month;
     		this.year = alarm.year;
     		this.hour = alarm.hour;
     		this.minutes = alarm.minutes;
     	}

     	private Object readResolve() {
     		return new Alarm(day, month, year, hour, minutes);
     	}
	}

    private StringProperty makeDate(int day, int month, int year){
		StringBuilder sbDate = new StringBuilder();
		sbDate.append(day);
		sbDate.append(".");
		if(month <10 ) sbDate.append("0");
		sbDate.append(month);
		sbDate.append(".");
		sbDate.append(year);
		return new SimpleStringProperty(sbDate.toString());
    }

    private StringProperty makeTime(int hour, int minutes){
		StringBuilder sbTime = new StringBuilder();
		if(hour < 10) sbTime.append("0");
		sbTime.append(hour);
		sbTime.append(":");
		if(minutes < 10) sbTime.append("0");
		sbTime.append(minutes);
		return new SimpleStringProperty(sbTime.toString());
    }
}
