package timekeeper;

import javafx.application.Platform;
import shared.Ring;

public class TimekeeperRunnable implements Runnable {
	private TimekeeperPaneController controller;
    private TimekeeperTimeConverter converter;

	public TimekeeperRunnable(TimekeeperPaneController controller){
		this.controller = controller;
		this.converter = new TimekeeperTimeConverter(controller);
	}

	public void run(){
    	if(converter.getTimeFromTextFields() != 0){
    		controller.lastSetTime = converter.getTimeFromTextFields();
    		controller.updateTimestamps();
    		for(long i = converter.getTimeFromTextFields(); i > 0 && !Thread.currentThread().isInterrupted(); i--){
    			if(controller.isTimePaused){
    				synchronized(controller.monitor){
    					try {
    						controller.monitor.wait();
    					} catch (InterruptedException e) {
    						System.out.println("Interrupted in wait");
    						Thread.currentThread().interrupt();
    					}
    				}
    			}
    			final long j = i;
    			Platform.runLater(new Runnable(){
    				@Override
   					public void run(){
   						controller.minuteTextField.setText(String.valueOf(j/60));
   						controller.secondTextField.setText(String.valueOf(j%60));
   					}
   				});
  				try {
   					Thread.sleep(1000);
   				} catch (InterruptedException e) {
   					System.out.println("Interrupted in sleep");
					Thread.currentThread().interrupt();
   				}
   				if(i == 1){
   					if(!Ring.getInstance().isPlaying()){
   						Ring.getInstance().play();
   						Ring.getInstance().getMediaPlayer().setCycleCount(1);
   					}
   					Platform.runLater(new Runnable(){
   						@Override
   						public void run(){
   							controller.startButton.setText("Turn off");
   						}
   					});
   				}
    		}
    	}
		Platform.runLater(new Runnable(){
			@Override
			public void run(){
	        	controller.minuteTextField.setText("00");
	        	controller.secondTextField.setText("00");
	        	controller.startButton.setSelected(false);
	        	controller.startButton.setText("Start");
			}
		});
        controller.isTimePaused = false;
	}
}
