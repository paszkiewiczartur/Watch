package stopwatch;

public class TimeConverter {
	public TimeConverter(){}

    public String getStringFromTime(long time){
    	long minutes;
    	long seconds;
    	long miliseconds;

    	if(time >= 60000){
    		minutes = time/60000;
    		seconds = (time%60000)/1000;
    	} else {
    		minutes = 0;
    		seconds = time/1000;
    	}
    	miliseconds = time%1000;
    	return buildString(minutes, seconds, miliseconds);
    }

    private String buildString(long minutes, long seconds, long miliseconds){
    	StringBuilder sb = new StringBuilder();

    	if(minutes < 10) sb.append("0");
    	sb.append(minutes);
    	sb.append(":");
    	if(seconds < 10) sb.append("0");
    	sb.append(seconds);
    	sb.append(":");
    	if(miliseconds < 100) sb.append("0");
    	if(miliseconds < 10) sb.append("0");
    	sb.append(miliseconds);

    	return sb.toString();
    }
}
