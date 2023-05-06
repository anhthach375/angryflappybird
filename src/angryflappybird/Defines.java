package angryflappybird;

import java.util.HashMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

/**
 * This class contains all of the coefficients related to the game app, scene, UI, and components. 
 * It can initialize all the components and their change images
 *
 */
public class Defines { 
    
    // dimensions of the GUI application
    final int APP_HEIGHT = 600;
    final int APP_WIDTH = 600;
    final int SCENE_HEIGHT = 570;
    final int SCENE_WIDTH = 400;
    final int START_WIDTH = 300;
    final int START_HEIGHT = 100;
    final int OVER_WIDTH = 300;
    final int OVER_HEIGHT = 300;

    // coefficients related to the blob
    final int KIKI_WIDTH = 110;
    final int KIKI_HEIGHT = 70;
    final int KIKI_POS_X = 30;
    final int KIKI_POS_Y = 10;
    final int KIKI_DROP_TIME = 300000000;  	// the elapsed time threshold before the blob starts dropping
    final int KIKI_FLY_BACK_VEL = -150;    // blob bounce back velocity
    final int KIKI_DROP_VEL = 200;    		// the blob drop velocity
    final int KIKI_FLY_VEL = -80;          // how far the blob goes up when clicked
    final int KIKI_IMG_LEN = 4;
    final int KIKI_IMG_PERIOD = 6;
    
    // coefficients related to the floors
    final int FLOOR_WIDTH = 400;
    final int FLOOR_HEIGHT = 100;
    final int FLOOR_COUNT = 3;
    
    // coefficients related to time
    final int SCENE_SHIFT_TIME = 5;
    final double SCENE_SHIFT_INCR_EASY = -0.3;
    final double SCENE_SHIFT_INCR_MED = -0.4;
    final double SCENE_SHIFT_INCR_HARD = -0.6;
    final double NANOSEC_TO_SEC = 1.0 / 1000000000.0;
    final double TRANSITION_TIME = 0.1;
    final int TRANSITION_CYCLE = 2;
    
    // coefficients related to the pipes
    final int PIPE_WIDTH = 70;
    int PIPE_HEIGHT = 200;   
    final int PIPE_COUNT = 2; 
    
    // coefficients related to the bread
    final int BREAD_WIDTH = 60;
    final int BREAD_HEIGHT = 60;
    final double BREAD_VELOCITY_EASY = 0.15;
    final double BREAD_VELOCITY_MED = 0.2;
    final double BREAD_VELOCITY_HARD = 0.3;
    
    // coefficients related to the eggs and peach
    final int CLOUD_WIDTH = 80;
    final int CLOUD_HEIGHT = 80;
    
    // coefficients related to the snoozedKiki
    final int SNOOZEDKIKI_WIDTH = 300;
    final int SNOOZEDKIKI_HEIGHT = 300;
    final double SNOOZEDKIKI_VELOCITY = 0.5;
    
    // coefficients  related to media display
    final String STAGE_TITLE = "Angry Flappy Bird";
	private final String IMAGE_DIR = "../resources/images/";

    final String[] IMAGE_FILES = 
        {"backgroundDay", "backgroundAfternoon", "backgroundNight", "kiki01", "kiki02", "kiki03", "kiki04", "floor", "pipeflap2",
         "pipeflap", "bread", "cactus", "cloud", "ready", "gameover"};

    final HashMap<String, ImageView> IMVIEW = new HashMap<String, ImageView>();
    final HashMap<String, Image> IMAGE = new HashMap<String, Image>();
    
    // coefficients related to media music
    private final String IMAGE_MUSIC = "../resources/music/";
    final String[] MUSIC_FILES = {"wing.mp3"};

    //nodes on the scene graph
    Button startButton;
    ListView<String> listView;
    HBox cactusBox;
    HBox snoozeCloudBox;
    HBox breadBox;
    Text scoreText = new Text("0");
    Text livesText = new Text("3 lives left");
    Text snoozeTime = new Text("");
    
    /**
     * This is the constructor for the defines class. 
     * It initializes the components of the game
     * It takes in no arguments and returns nothing
     *   
     */
	Defines() {
		
		// initialize images
		for(int i=0; i<IMAGE_FILES.length; i++) {
			Image img;
			if (i == 7) {
                img = new Image(pathImage(IMAGE_FILES[i]), FLOOR_WIDTH, FLOOR_HEIGHT, false, false);
			}
			else if (i == 3 || i == 4 || i == 5 | i == 6){
				img = new Image(pathImage(IMAGE_FILES[i]), KIKI_WIDTH, KIKI_HEIGHT, false, false);
			}
			else if (i == 8 | i == 9){
	            img = new Image(pathImage(IMAGE_FILES[i]), PIPE_WIDTH, PIPE_HEIGHT, false, false);
			}
			else if (i == 10) {
	            img = new Image(pathImage(IMAGE_FILES[i]), BREAD_WIDTH, BREAD_HEIGHT, false, false);
			}
			else if (i == 11 | i == 12) {
	            img = new Image(pathImage(IMAGE_FILES[i]), CLOUD_WIDTH, CLOUD_HEIGHT, false, false);
			}
			else if (i == 13) {
                img = new Image(pathImage(IMAGE_FILES[i]), START_WIDTH, START_HEIGHT, false, false);
			}
			else if (i == 14) {
                img = new Image(pathImage(IMAGE_FILES[i]), OVER_WIDTH, OVER_HEIGHT, false, false);
			}
			else {
				img = new Image(pathImage(IMAGE_FILES[i]), SCENE_WIDTH, SCENE_HEIGHT, false, false);
			}
    		IMAGE.put(IMAGE_FILES[i],img);
    	}
		
		// initialize image views
		for(int i=0; i<IMAGE_FILES.length; i++) {
    		ImageView imgView = new ImageView(IMAGE.get(IMAGE_FILES[i]));
    		IMVIEW.put(IMAGE_FILES[i],imgView);
    	}
		
		// initialize scene nodes
		startButton = new Button("Start Game!");
		
		// initialize the ListView for level
		ObservableList<String> levels = FXCollections.observableArrayList("Easy", "Medium", "Hard");
		listView = new ListView<String>(levels);
	    listView.setMaxSize(200, 80);
	    
	    // initialize the cactus description
	    cactusBox= new HBox();
	    cactusBox.getChildren().add(IMVIEW.get("cactus"));
	    cactusBox.getChildren().add(new Text ("Bonus points"));
	    
	    // initialize the cloud description
	    snoozeCloudBox= new HBox();
	    snoozeCloudBox.getChildren().add(IMVIEW.get("cloud"));
	    snoozeCloudBox.getChildren().add(new Text ("Lets you snooze"));
	    
        // initialize the bread description
        breadBox = new HBox();
        breadBox.getChildren().add(IMVIEW.get("bread"));
        breadBox.getChildren().add(new Text ("Avoid bread"));
        
        // initialize the score
        scoreText.setStyle("-fx-font-size: 50;");
        scoreText.setFill(javafx.scene.paint.Color.WHITE);
        scoreText.setLayoutX(20);
        scoreText.setLayoutY(50);
        
        // initialize the lives left
        livesText.setStyle("-fx-font-size: 35;");
        livesText.setFill(javafx.scene.paint.Color.RED);
        livesText.setLayoutX(210);
        livesText.setLayoutY(550);
        
        // initialized the second count for snooze
        snoozeTime.setStyle("-fx-font-size: 35;");
        snoozeTime.setFill(javafx.scene.paint.Color.YELLOW);
        snoozeTime.setLayoutX(20);
        snoozeTime.setLayoutY(130);   

	}
	
	/**
     * This is a method to get the filepath of something in the resources/images folder.
     * It is used within the Defines class to initialize the images of the components
     * 
     * @param filepath The name of the image as it is stored in the resources/images folder
     * @return the full filepath path of the image  
     */
    public String pathImage(String filepath) {
    	String fullpath = getClass().getResource(IMAGE_DIR+filepath+".png").toExternalForm();
    	return fullpath;
    }	
}
