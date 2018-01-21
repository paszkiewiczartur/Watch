package timekeeper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

public class TimekeeperFileManager {
	String fileName = "timestamps.watch";

	public Timestamp[] readFromFile(){
		Timestamp[] timestamps = new Timestamp[6];
        File file = new File(fileName);
        boolean fileExists = file.exists();
        if(fileExists){
    		try(
    				FileInputStream fis = new FileInputStream(fileName);
    				ObjectInputStream ois = new ObjectInputStream(fis);
    				){
    				for(int i=0; i<5; i++){
    					timestamps[i] = (Timestamp) ois.readObject();
    				}
    			} catch (FileNotFoundException e){
    				e.printStackTrace();
    			} catch (IOException e){
    				e.printStackTrace();
    			} catch (ClassNotFoundException e){
    				e.printStackTrace();
    			}
    		timestamps[5] = new Timestamp(90, 1);
    		timestamps[5] = findTheMostFrequent(timestamps);
        } else {
        	timestamps = createPrimaryTimestamps();
        }
		return timestamps;
	}

	private Timestamp findTheMostFrequent(Timestamp[] timestamps){
		Timestamp max;
		Optional<Timestamp> timestamp = Arrays.stream(timestamps).max(new Comparator<Timestamp>(){
			@Override
			public int compare(Timestamp t1, Timestamp t2){
				return ((Long)t1.getFrequency()).compareTo((Long)t2.getFrequency());
			}
		});
		if(timestamp.isPresent()){
			max = timestamp.get();
		} else {
			max = timestamps[2];
		}
		return max;
	}

	private Timestamp[] createPrimaryTimestamps(){
		Timestamp[] timestamps = new Timestamp[6];
    	timestamps[0] = new Timestamp(30,1);
		timestamps[1] = new Timestamp(60,1);
		timestamps[2] = new Timestamp(90,1);
		timestamps[3] = new Timestamp(120,1);
		timestamps[4] = new Timestamp(300,1);
		timestamps[5] = new Timestamp(90,1);
		return timestamps;
	}

	public void writeToFile(Timestamp[] timestamps){
        File file = new File(fileName);
		boolean fileExists = file.exists();
        if(!fileExists) {
            try {
                fileExists = file.createNewFile();
            } catch (IOException e) {
                System.out.println("Creating new file failed.");
            }
        }
		try(
				FileOutputStream fs = new FileOutputStream(fileName);
				ObjectOutputStream os = new ObjectOutputStream(fs);
				){
				for(int i=0; i<5; i++){
					os.writeObject(timestamps[i]);
				}
			} catch (FileNotFoundException e){
				e.printStackTrace();
			} catch (IOException e){
				e.printStackTrace();
			}
	}
}