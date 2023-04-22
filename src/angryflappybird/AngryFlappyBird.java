package angryflappybird;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;


//The Application layer
public class AngryFlappyBird extends Application {
	
	private Defines DEF = new Defines();
    private Sound sound = new Sound();
    private Score SCORE = new Score();


    // time related attributes
    private long clickTime, startTime, elapsedTime;   
    private AnimationTimer timer;
    
    // game components
    private Sprite blob;
    private ArrayList<Sprite> floors;
    private ArrayList<Sprite> pipeUps;
    private ArrayList<Sprite> pipeDowns;
    private Text scoreText;
    private int totalScore;
    private int livesLeft;
    private int hitTime;

    // game flags
    private boolean CLICKED, GAME_START, GAME_OVER;
    
    // scene graphs
    private Group gameScene;	 // the left half of the scene
    private VBox gameControl;	 // the right half of the GUI (control)
    private GraphicsContext gc;		
    
	// the mandatory main method 
    public static void main(String[] args) {
        launch(args);
    }
       
    // the start method sets the Stage layer
    @Override
    public void start(Stage primaryStage) throws Exception {
    	
    	// initialize scene graphs and UIs
        resetGameControl();    // resets the gameControl
    	resetGameScene(true);  // resets the gameScene
    	
        HBox root = new HBox();
		HBox.setMargin(gameScene, new Insets(0,0,0,15));
		root.getChildren().add(gameScene);
		root.getChildren().add(gameControl);
		
		// add scene graphs to scene
        Scene scene = new Scene(root, DEF.APP_WIDTH, DEF.APP_HEIGHT);
        
        // finalize and show the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle(DEF.STAGE_TITLE);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    
    // the getContent method sets the Scene layer
    private void resetGameControl() {      
        DEF.startButton.setOnMouseClicked(this::mouseClickHandler);
        gameControl = new VBox(25);
        gameControl.getChildren().addAll(DEF.startButton,DEF.listView,DEF.normalEggBox,DEF.snoozeEggBox,DEF.pigBox); 
    }
    
    private void mouseClickHandler(MouseEvent e) {
    	if (GAME_OVER) {
            resetGameScene(false);
        }
    	else if (GAME_START){
            clickTime = System.nanoTime();   
        }
    	GAME_START = true;
    	CLICKED = true;
    	sound.play("wing.wav");
    }
    
    public void gameLoop() {
        if (livesLeft < 0) {
            resetGameScene(false);
            totalScore = 0;
            livesLeft = 3;           
        }
        
    }
    
    private void resetGameScene(boolean firstEntry) {	
    	// reset variables        
        CLICKED = false;
        GAME_OVER = false;
        GAME_START = false;
        floors = new ArrayList<>();
        pipeUps = new ArrayList<>();
        pipeDowns = new ArrayList<>();
        
        final ImageView[] backgrounds = new ImageView[]{
                DEF.IMVIEW.get("backgroundDay"),
                DEF.IMVIEW.get("backgroundAfternoon"),
                DEF.IMVIEW.get("backgroundNight"),
         };
       
    	if(firstEntry) {           
    		// create two canvases
            Canvas canvas = new Canvas(DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);
            gc = canvas.getGraphicsContext2D();

            // create a background
            ImageView background = DEF.IMVIEW.get("backgroundDay");
            
            // create the game scene
            gameScene = new Group();
            gameScene.getChildren().addAll(backgrounds[0], canvas, DEF.scoreText, DEF.livesText);
            int[] currentBackgroundIndex = {0};
            
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.seconds(15), event -> {
                        gameScene.getChildren().clear();
                        currentBackgroundIndex[0] = (currentBackgroundIndex[0] + 1) % backgrounds.length;
                        gameScene.getChildren().addAll(backgrounds[currentBackgroundIndex[0]], canvas, DEF.scoreText, DEF.livesText);
                    })            
            );
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();          
    	}
    	
    	// initialize floor
    	for(int i=0; i<DEF.FLOOR_COUNT; i++) {
    		
    		int posX = i * DEF.FLOOR_WIDTH;
    		int posY = DEF.SCENE_HEIGHT - DEF.FLOOR_HEIGHT;
    		
    		Sprite floor = new Sprite(posX, posY, DEF.IMAGE.get("floor"));
    		floor.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
    		floor.render(gc);
    		
    		floors.add(floor);
    	}
        
        // initialize blob
        blob = new Sprite(DEF.BLOB_POS_X, DEF.BLOB_POS_Y,DEF.IMAGE.get("blob0"));
        blob.render(gc);
        
        // initialize pipeUp  
        Random ran = new Random();
        int prePosUpX = 0;
        for (int i = 0; i < DEF.PIPE_COUNT; i++) {
            int PIPEUP_POS_X = ran.nextInt((i+1)*150, (i+2)*150);
            if (PIPEUP_POS_X - prePosUpX <= 100) {
                PIPEUP_POS_X += ran.nextInt(80, 150);
            }
            prePosUpX = PIPEUP_POS_X;
            int PIPEUP_POS_Y = ran.nextInt(-20, 0);

            Sprite pipeUp = new Sprite(PIPEUP_POS_X, PIPEUP_POS_Y, DEF.IMAGE.get("pipeflap2"));
            pipeUp.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
            pipeUp.render(gc);

            pipeUps.add(pipeUp);
        }        
        // initialize pipeDown
        int prePosDownX = 0;
        for (int i = 0; i < DEF.PIPE_COUNT; i++) {
            int PIPEDOWN_POS_X = ran.nextInt((i+1)*150, (i+2)*150);
            if (PIPEDOWN_POS_X - prePosDownX <= 100) {
                PIPEDOWN_POS_X += ran.nextInt(80, 150);
            }
                prePosDownX = PIPEDOWN_POS_X;         
            int PIPEDOWN_POS_Y = ran.nextInt(380, 400);               
            Sprite pipeDown = new Sprite(PIPEDOWN_POS_X, PIPEDOWN_POS_Y, DEF.IMAGE.get("pipeflap"));
            pipeDown.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
            pipeDown.render(gc);
          
            pipeDowns.add(pipeDown);
        }      
        // initialize timer
        startTime = System.nanoTime();
        timer = new MyTimer();
        timer.start();      
    }

    //timer stuff
    class MyTimer extends AnimationTimer { 	
    	int counter = 0;  	
    	 @Override
    	 public void handle(long now) {   		 
    		 // time keeping
    	     elapsedTime = now - startTime;
    	     startTime = now;
    	     
    	     // clear current scene
    	     gc.clearRect(0, 0, DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);

    	     if (GAME_START) {
    	    	 // step1: update floor and pipe
    	    	 moveFloor();
    	    	 movePipeUp();
                 movePipeDown();   	    	 
    	    	 // step2: update blob
    	    	 moveBlob();
    	    	 checkCollision();
    	    	 passPipeEffect();	
    	    	 gameLoop();
    	     }
    	 }
    	 
    	 // step1: update floor
    	 private void moveFloor() {            
             for(int i=0; i<DEF.FLOOR_COUNT; i++) {
                 if (floors.get(i).getPositionX() <= -DEF.FLOOR_WIDTH) {
                     double nextX = floors.get((i+1)%DEF.FLOOR_COUNT).getPositionX() + DEF.FLOOR_WIDTH;
                     double nextY = DEF.SCENE_HEIGHT - DEF.FLOOR_HEIGHT;
                     floors.get(i).setPositionXY(nextX, nextY);
                 }
                 floors.get(i).render(gc);
                 floors.get(i).update(DEF.SCENE_SHIFT_TIME);
             }
          }
    	 
    	 // step2: update blob
    	 private void moveBlob() {
    		 
			long diffTime = System.nanoTime() - clickTime;
			
			// blob flies upward with animation
			if (CLICKED && diffTime <= DEF.BLOB_DROP_TIME) {
				
				int imageIndex = Math.floorDiv(counter++, DEF.BLOB_IMG_PERIOD);
				imageIndex = Math.floorMod(imageIndex, DEF.BLOB_IMG_LEN);
				blob.setImage(DEF.IMAGE.get("blob"+String.valueOf(imageIndex)));
				blob.setVelocity(0, DEF.BLOB_FLY_VEL);
			}
			// blob drops after a period of time without button click
			else {
			    blob.setVelocity(0, DEF.BLOB_DROP_VEL); 
			    CLICKED = false;
			}

			// render blob on GUI
			blob.update(elapsedTime * DEF.NANOSEC_TO_SEC);
			blob.render(gc);
    	 }
    	 
    	 // step 3: update pipe
    	 private void movePipeUp() {   
             Random ran = new Random();             
    	     for(int i=0; i<pipeUps.size(); i++) {   
    	         if (pipeUps.get(i).getPositionX() <= -DEF.PIPE_WIDTH) { 
                         double nextX = pipeUps.get((i+1)%DEF.PIPE_COUNT).getPositionX() + ran.nextInt(250,300);
                         double nextY = ran.nextInt(-40, 0);
                         pipeUps.get(i).setPositionXY(nextX, nextY);
                         pipeUps.get(i).setNotPassed(pipeUps.get(i));                            
                 }
    	         pipeUps.get(i).render(gc);
    	         pipeUps.get(i).update(DEF.SCENE_SHIFT_TIME);      
    	      }      	         
    	 }
    	 
    	 private void movePipeDown() {   
             Random ran = new Random();             
             for(int i=0; i<pipeDowns.size(); i++) {   
                 if (pipeDowns.get(i).getPositionX() <= -DEF.PIPE_WIDTH) { 
                         double nextX = pipeDowns.get((i+1)%DEF.PIPE_COUNT).getPositionX() + ran.nextInt(250,300);
                         double nextY = ran.nextInt(380, 420);
                         pipeDowns.get(i).setPositionXY(nextX, nextY);
                         pipeDowns.get(i).setNotPassed(pipeDowns.get(i));                            
                 }
                 pipeDowns.get(i).render(gc);
                 pipeDowns.get(i).update(DEF.SCENE_SHIFT_TIME);      
              }                  
         }
    	 
    	 public void checkCollision() {   		 
    		// check collision      	        
			for (Sprite floor: floors) {
				GAME_OVER = GAME_OVER || blob.intersectsSprite(floor);
				if (blob.intersectsSprite(floor)) {
	                SCORE.updateLivesText(DEF.livesText, livesLeft--);
	                livesLeft--;         
	             }
			}
			
			for (Sprite pipe : pipeUps) {
			    GAME_OVER = GAME_OVER || blob.intersectsSprite(pipe);
			    if (blob.intersectsSprite(pipe)) {
                    livesLeft--;           
                    SCORE.updateLivesText(DEF.livesText, livesLeft--);
                    }
			}
			
			for (Sprite pipe : pipeDowns) {
                GAME_OVER = GAME_OVER || blob.intersectsSprite(pipe);
                if (blob.intersectsSprite(pipe)) {
                    livesLeft--;
                    SCORE.updateLivesText(DEF.livesText, livesLeft);
                }
            }						    
			// end the game when blob hit stuff
			if (GAME_OVER) {
				showHitEffect(); 
				for (Sprite floor: floors) {
					floor.setVelocity(0, 0);
				}
				timer.stop();
			}				
    	 }
    	 
    	
    	// show 
    	private void passPipeEffect() {
    	    for (Sprite pipe : pipeUps) {
                if (blob.getPositionX() > pipe.getPositionX() && !pipe.isPassed()) {
                    SCORE.updateScoreText(DEF.scoreText, totalScore++);
                    pipe.setPassed(pipe);
                    sound.play("point.mp3");
                    break; 
                }
            }
    	    for (Sprite pipe : pipeDowns) {
                if (blob.getPositionX() > pipe.getPositionX() && !pipe.isPassed()) {
                    SCORE.updateScoreText(DEF.scoreText, totalScore++);
                    pipe.setPassed(pipe);
                    sound.play("point.mp3");
                    break; 
                }
            }
    	}    	 

        private void showHitEffect() {
	        ParallelTransition parallelTransition = new ParallelTransition();
	        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(DEF.TRANSITION_TIME), gameScene);
	        fadeTransition.setToValue(0);
	        fadeTransition.setCycleCount(DEF.TRANSITION_CYCLE);
	        fadeTransition.setAutoReverse(true);
	        parallelTransition.getChildren().add(fadeTransition);
	        parallelTransition.play();
	     }
    	 
    } // End of MyTimer class

} // End of AngryFlappyBird Class

