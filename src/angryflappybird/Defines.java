package angryflappybird;

import java.awt.Font;
import java.util.HashMap;
import java.util.Random;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class Defines {
    
	// dimension of the GUI application
    final int APP_HEIGHT = 600;
    final int APP_WIDTH = 600;
    final int SCENE_HEIGHT = 570;
    final int SCENE_WIDTH = 400;

    // coefficients related to the blob
    final int BLOB_WIDTH = 70;
    final int BLOB_HEIGHT = 70;
    final int BLOB_POS_X = 70;
    final int BLOB_POS_Y = 200;
    final int BLOB_DROP_TIME = 300000000;  	// the elapsed time threshold before the blob starts dropping
    final int BLOB_DROP_VEL = 300;    		// the blob drop velocity
    final int BLOB_FLY_VEL = -40;
    final int BLOB_IMG_LEN = 4;
    final int BLOB_IMG_PERIOD = 5;
    
    // coefficients related to the floors
    final int FLOOR_WIDTH = 400;
    final int FLOOR_HEIGHT = 100;
    final int FLOOR_COUNT = 3;
    
    // coefficients related to time
    final int SCENE_SHIFT_TIME = 5;
    final double SCENE_SHIFT_INCR = -0.4;
    final double NANOSEC_TO_SEC = 1.0 / 1000000000.0;
    final double TRANSITION_TIME = 0.1;
    final int TRANSITION_CYCLE = 2;
    
    // coefficients related to the pipes
    Random ran = new Random();
    final int PIPE_WIDTH = 80;
    int PIPE_HEIGHT = 230;   
    final int PIPE_COUNT = 2; 
    
    // coefficients related to the pig and egges
    final int PIG_WIDTH = 70;
    final int PIG_HEIGHT = 70;
    final double PIG_VELOCITY = 0.4;
    
    // coefficients related to the eggs
    final int EGG_WIDTH = 80;
    final int EGG_HEIGHT = 90;
    
    // coefficients  related to media display
    final String STAGE_TITLE = "Angry Flappy Bird";
	private final String IMAGE_DIR = "../resources/images/";

    final String[] IMAGE_FILES = 
        {"backgroundDay", "backgroundAfternoon", "backgroundNight", "blob0", "blob1", "blob2", "blob3", "floor", "pipeflap2",
         "pipeflap", "pig", "egg_white", "egg_golden"};

    final HashMap<String, ImageView> IMVIEW = new HashMap<String, ImageView>();
    final HashMap<String, Image> IMAGE = new HashMap<String, Image>();
    
    // coefficients related to media music
    private final String IMAGE_MUSIC = "../resources/music/";
    final String[] MUSIC_FILES = {"wing.mp3"};

    //nodes on the scene graph
    Button startButton;
    ListView<String> listView;
    HBox normalEggBox;
    HBox snoozeEggBox;
    HBox pigBox;
    final Text scoreText = new Text("0");
    final Text livesText = new Text("3 lives left");
    
    // constructor
	Defines() {
		
		// initialize images
		for(int i=0; i<IMAGE_FILES.length; i++) {
			Image img;
			if (i == 7) {
                img = new Image(pathImage(IMAGE_FILES[i]), FLOOR_WIDTH, FLOOR_HEIGHT, false, false);

			}
			else if (i == 3 || i == 4 || i == 5 | i == 6){
				img = new Image(pathImage(IMAGE_FILES[i]), BLOB_WIDTH, BLOB_HEIGHT, false, false);
			}
			else if (i == 8 | i == 9){
	             img = new Image(pathImage(IMAGE_FILES[i]), PIPE_WIDTH, PIPE_HEIGHT, false, false);
			}
			else if (i == 10) {
	              img = new Image(pathImage(IMAGE_FILES[i]), PIG_WIDTH, PIG_HEIGHT, false, false);
			}
			else if (i == 11 | i == 12) {
	               img = new Image(pathImage(IMAGE_FILES[i]), EGG_WIDTH, EGG_HEIGHT, false, false);
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
	    
	    // initialize the normal egg's description
	    normalEggBox= new HBox();
	    normalEggBox.getChildren().add(IMVIEW.get("egg_white"));
	    normalEggBox.getChildren().add(new Text ("Bonus points"));
	    
	    // initialize the snooze egg's description
	    snoozeEggBox= new HBox();
        snoozeEggBox.getChildren().add(IMVIEW.get("egg_golden"));
        snoozeEggBox.getChildren().add(new Text ("Lets you snooze"));
	    
        // initialize the snooze egg's description
        pigBox = new HBox();
        pigBox.getChildren().add(IMVIEW.get("pig"));
        pigBox.getChildren().add(new Text ("Avoid pigs"));
        
        // initialize the score
//        final scoreText = new Text("0");
        scoreText.setStyle("-fx-font-size: 50;");
//        Font font = Font.getFont("Comic Sans MS");
//        scoreText.setFont(Font.font(font, FontWeight.EXTRA_BOLD, 50));
        scoreText.setFill(javafx.scene.paint.Color.WHITE);
        scoreText.setLayoutX(20);
        scoreText.setLayoutY(50);
        
     // initialize the lives left
        livesText.setStyle("-fx-font-size: 35;");
        livesText.setFill(javafx.scene.paint.Color.WHITE);
        livesText.setLayoutX(230);
        livesText.setLayoutY(550);

	}
	
    public String pathImage(String filepath) {
    	String fullpath = getClass().getResource(IMAGE_DIR+filepath+".png").toExternalForm();
    	return fullpath;
    }
	
	public Image resizeImage(String filepath, int width, int height) {
    	IMAGE.put(filepath, new Image(pathImage(filepath), width, height, false, false));
    	return IMAGE.get(filepath);
    }	
}
