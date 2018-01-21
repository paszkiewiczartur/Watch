package timekeeper;

public class TimestampsUpdater {
	private TimekeeperPaneController controller;

	public TimestampsUpdater(TimekeeperPaneController controller){
		this.controller = controller;
	}

	public void updateTimestamps(){
    	long last = controller.lastSetTime;
    	boolean found = false;
    	for(int i=0; i<5; i++){
    		if(controller.timestamps[i].getTime() == last){
    			controller.timestamps[i].setFrequency(controller.timestamps[i].getFrequency() + 1);
    			found = true;
    			break;
    		}
    	}
    	if(found == false){
    		int min = 0;
    		for(int i=0; i< 5; i++){
    			if(controller.timestamps[i].getFrequency() < controller.timestamps[min].getFrequency()){
    				min = i;
    			}
    		}
    		controller.timestamps[min].setTime(last);
    		controller.timestamps[min].setFrequency(2);
    	}
	}
}
