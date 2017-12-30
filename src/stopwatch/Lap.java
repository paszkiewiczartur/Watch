package stopwatch;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Lap {
 	private StringProperty time;

 	public String getTime() {
		return time.get();
	}

	public void setTime(StringProperty time) {
		this.time = time;
	}

    public Lap(String time) {
       this.time = new SimpleStringProperty(time);
    }

    @Override
    public String toString() {
        return "Lap [time=" + time + "]";
    }
}
