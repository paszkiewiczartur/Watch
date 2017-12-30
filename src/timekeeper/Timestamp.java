package timekeeper;

import java.io.Serializable;

public class Timestamp implements Serializable{
	private static final long serialVersionUID = -5419137604825002738L;
	private long time;
	private long frequency;

	public Timestamp(){}

	public Timestamp(long time, long frequency){
		this.time = time;
		this.frequency = frequency;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public long getFrequency() {
		return frequency;
	}

	public void setFrequency(long frequency) {
		this.frequency = frequency;
	}

    @Override
    public String toString() {
    	return "Timestamp [" + getTime() + "," + getFrequency() + "]";
    }
}
