package timekeeper;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class Timestamp implements Serializable{
	private static final long serialVersionUID = -7865169026313046201L;
	private long time;
	private long frequency;

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

	private Object writeReplace() {
		return new SerializationProxy(this);
	}

	private void readObject(ObjectInputStream stream) throws InvalidObjectException {
		throw new InvalidObjectException("Proxy is required");
	}

    private static class SerializationProxy implements Serializable {
		private static final long serialVersionUID = -2639724364549659375L;
		private long time;
    	private long frequency;

     	SerializationProxy(Timestamp timestamp) {
     		this.time = timestamp.time;
     		this.frequency = timestamp.frequency;
     	}

     	private Object readResolve() {
     		return new Timestamp(time, frequency);
     	}
	}

}
