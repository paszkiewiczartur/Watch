package shared;

import java.io.Serializable;

public class Timestamp implements Serializable{

	private static final long serialVersionUID = -5419137604825002738L;
	private long time;
	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	private long amount;

	public Timestamp(){}

	public Timestamp(long time, long amount){
		this.time = time;
		this.amount = amount;
	}

}
