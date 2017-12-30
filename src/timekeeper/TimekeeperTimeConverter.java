package timekeeper;

import controller.TimekeeperPaneController;

public class TimekeeperTimeConverter {
    TimekeeperPaneController controller;

	public TimekeeperTimeConverter(TimekeeperPaneController controller){
		this.controller = controller;
	}

    public void setTime(long time){
    	controller.minuteTextField.setText(String.valueOf(time/60));
    	controller.secondTextField.setText(String.valueOf(time%60));
    }

    public long getTimeFromTextFields(){
    	long result = 0;
    	result += Long.parseLong(controller.secondTextField.getText());
    	result += 60 * Long.parseLong(controller.minuteTextField.getText());
    	return result;
    }

    public String getStringFromTime(long time){
    	StringBuilder sb = new StringBuilder();
    	if(time/60 < 10){
    		sb.append("0");
    	}
    	sb.append(String.valueOf(time/60));
    	sb.append(":");
    	if(time%60 < 10){
    		sb.append("0");
    	}
    	sb.append(String.valueOf(time%60));

    	return sb.toString();
    }

}
