package alarm;

import java.time.LocalDateTime;

import javafx.application.Platform;
import shared.Ring;

public class AlarmRunnable implements Runnable {
	private AlarmPaneController controller;

	public AlarmRunnable(AlarmPaneController controller){
		this.controller = controller;
	}

	public void run(){
    	while(!Thread.currentThread().isInterrupted()){
			if(checkTime()){
    			Platform.runLater(new Runnable(){
    				@Override
    				public void run(){
    					controller.deleteButton.setText("Turn off");}
    			});
    			if(!Ring.getInstance().isPlaying()){
    				Ring.getInstance().play();
    			}
			}
			try {
    				Thread.sleep(10000);
    			} catch (InterruptedException e) {
    				Thread.currentThread().interrupt();
    			}
    	}
	}

    private boolean checkTime(){
    	boolean found = false;
    	for(int i=0; i<controller.alarmTableList.size(); i++){
    		if(shouldTheBellRing(i)){
    			found = true;
    			controller.currentRingingAlarm = controller.alarmTableList.get(i);
    			break;
    		}
    	}
    	return found;
    }

    private boolean shouldTheBellRing(int alarm){
    	LocalDateTime date = LocalDateTime.now();
    	boolean result = false;
    	if(date.getDayOfMonth() == controller.alarmTableList.get(alarm).getDay()
    			&& date.getMonthValue() == controller.alarmTableList.get(alarm).getMonth()
				&& date.getYear() == controller.alarmTableList.get(alarm).getYear()
				&& date.getHour() == controller.alarmTableList.get(alarm).getHour()
				&& date.getMinute() == controller.alarmTableList.get(alarm).getMinutes())
    		result = true;
    	return result;
    }
}
