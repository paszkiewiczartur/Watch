package shared;

import java.io.File;
import java.net.URI;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;


public class Ring {
	private final static Ring instance = new Ring();
	File file = new File("Iphone_SMS.mp3");
	private Media media;
    private MediaPlayer mediaPlayer;

	public static Ring getInstance() {
        return instance;
    }

    private Ring(){
		URI u = file.toURI();
        media = new Media(u.toString());
        setMediaPlayer(new MediaPlayer(media));
    }

	public MediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}

	public void setMediaPlayer(MediaPlayer mediaPlayer) {
		this.mediaPlayer = mediaPlayer;
	}

    public boolean isPlaying(){
    	return mediaPlayer.getStatus() == Status.PLAYING;
    }

    public void play(){
    	mediaPlayer.play();
		mediaPlayer.setCycleCount(3);
    }

    public void pause(){
    	mediaPlayer.pause();
    }

}
