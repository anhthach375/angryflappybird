package angryflappybird;

import javafx.scene.text.Text;

public class Score {    
    public void updateScoreText(Text text, int score) {
        text.setText(Integer.toString(score+1));
    }
    
    public void resetScoreText(Text text) {
        text.setText(Integer.toString(0));
    }
    
    public void updateLivesText(Text text, int score) {
        text.setText(Integer.toString(score-1) + " lives left");
    }
}