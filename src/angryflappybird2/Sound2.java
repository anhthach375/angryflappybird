package angryflappybird2;

import java.io.File;
import java.nio.file.Paths;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class Sound2 {    
    MediaPlayer mediaPlayer;
    public void play (String filename) {
        playSound(filename);
    }
    
    public void playSound (String filename) {
      Media h = new Media(Paths.get(filename).toUri().toString());
      mediaPlayer = new MediaPlayer(h);
      mediaPlayer.play();
    }
}