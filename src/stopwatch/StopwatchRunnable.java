package stopwatch;

import controller.StopwatchPaneController;
import javafx.application.Platform;

public class StopwatchRunnable implements Runnable {
	private StopwatchPaneController controller;
	private StopwatchTimeConverter converter = new StopwatchTimeConverter();

	public StopwatchRunnable(StopwatchPaneController controller){
		this.controller = controller;
	}

    public void run(){
    	while(!Thread.currentThread().isInterrupted()){
    		if(controller.isPaused){
    			synchronized(controller.monitor){
    				try {
						controller.monitor.wait();
					} catch (InterruptedException e) {
						System.out.println("Interrupted in wait");
						Thread.currentThread().interrupt();
					}
    			}
    		} else{
    			try {
    				Thread.sleep(123);
    			} catch (InterruptedException e) {
    				System.out.println("Interrupted in sleep");
    				Thread.currentThread().interrupt();
    			}
    			Platform.runLater(new Runnable(){
    				@Override
    				public void run(){
    					long time = controller.stopWatch.getTime();
    					String convertedTime = converter.getStringFromTime(time);
    					controller.textField.setText(convertedTime);
    				}
    			});
    		}
    	}
    }
}