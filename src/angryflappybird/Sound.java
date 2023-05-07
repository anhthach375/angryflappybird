package angryflappybird;

import java.io.File;
import java.nio.file.Paths;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
/**
 * Plays sounds for the game
 * @author Willow Kelleigh and Anh Thach
 */
public class Sound {    
    MediaPlayer mediaPlayer;
    /**
     * Method to play sound with no mediaPlayer necessary
     * @param filename filename of sound to be played
     */
    public void play (String filename) {
        playSound(filename);
    }
    
    /**
     * Method to play sound when mediaPlayer is necessary
     * @param filename filename of sound to be played
     */
    public void playSound (String filename) {
      Media h = new Media(Paths.get(filename).toUri().toString());
      mediaPlayer = new MediaPlayer(h);
      mediaPlayer.play();
    }
}