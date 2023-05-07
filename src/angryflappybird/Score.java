package angryflappybird;

import javafx.scene.text.Text;

/**
 * Keeps track of the score of the game.
 * @author Willow Kelleigh and Anh Thach
 */
public class Score {   
    /**
     * Method to update the score text shown on screen
     * @param text Text shown telling the player the score
     * @param score the score before update
     */
    public void updateScoreText(Text text, int score) {
        text.setText(Integer.toString(score+1));
    }
    /**
     * Method to reset the score shown on screen
     * @param text Text shown telling the player the score
     */
    public void resetScoreText(Text text) {
        text.setText(Integer.toString(0));
    }
    /**
     * Method to update the lives shown on screen
     * @param text Text shown telling the player the lives left
     * @param lives the lives left before update
     */
    public void updateLivesText(Text text, int lives) {
        text.setText(Integer.toString(lives) + " lives left");
    }
}