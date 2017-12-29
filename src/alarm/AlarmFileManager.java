package alarm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;

import javafx.collections.ObservableList;

public class AlarmFileManager {
	private String fileName = "alarms.watch";

	public List<Alarm> readFromFile(){
        File file = new File(fileName);
        boolean fileExists = file.exists();
        List<Alarm> alarmFileList = new LinkedList<>();
        if(fileExists){
    		try(FileInputStream fis = new FileInputStream(fileName);
    				ObjectInputStream ois = new ObjectInputStream(fis);
    		){
    			int size = (int) ois.read();
    			Alarm alarm;
    			while(size>0){
    				alarm = (Alarm) ois.readObject();
    				alarmFileList.add(alarm);
    				size--;
    			}
    		} catch (FileNotFoundException e){
    			e.printStackTrace();
    		} catch (IOException e){
    			e.printStackTrace();
    		} catch (ClassNotFoundException e){
    			e.printStackTrace();
    		}
			for(int i=0; i<alarmFileList.size(); i++){
				alarmFileList.get(i).setDate();
			}
        }
		return alarmFileList;
	}

	public void writeToFile(ObservableList<Alarm> alarmTableList){
        File file = new File(fileName);
        boolean fileExists = file.exists();
        if(!fileExists) {
            try {
                fileExists = file.createNewFile();
            } catch (IOException e) {
                System.out.println("Failed operation of creating new file.");
            }
        }
		try(FileOutputStream fs = new FileOutputStream(fileName);
				ObjectOutputStream os = new ObjectOutputStream(fs);){
				os.write(alarmTableList.size());
				for(int i=0; i<alarmTableList.size(); i++){
					os.writeObject(alarmTableList.get(i));
				}
			} catch (FileNotFoundException e){
				e.printStackTrace();
			} catch (IOException e){
				e.printStackTrace();
			}
	}
}