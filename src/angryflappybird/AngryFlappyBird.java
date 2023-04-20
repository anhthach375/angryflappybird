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
    private ArrayList<Sprite> pipes;
    private Text scoreText;
    private int totalScore = 0;
    private int livesLeft = 3;

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
    
    private void resetGameScene(boolean firstEntry) {	
    	// reset variables
        CLICKED = false;
        GAME_OVER = false;
        GAME_START = false;
        floors = new ArrayList<>();
        pipes = new ArrayList<>();
        
        final ImageView[] backgrounds = new ImageView[]{
                DEF.IMVIEW.get("backgroundDay"),
                DEF.IMVIEW.get("backgroundAfternoon"),
                DEF.IMVIEW.get("backgroundNight"),
         };

       
    	if(firstEntry) {
            
    		// create two canvases
            Canvas canvas = new Canvas(DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);
            gc = canvas.getGraphicsContext2D();
            gameScene = new Group();
            gameScene.getChildren().addAll(backgrounds[0], canvas, DEF.scoreText, DEF.livesText);
            int[] currentBackgroundIndex = {0};
            
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.seconds(10), event -> {
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
        
        // initialize pipe  
        Random ran = new Random();
        for (int i = 0; i < DEF.PIPE_COUNT; i++) {
            int PIPEUP_POS_X = ran.nextInt((i+1)*200, (i+2)*200);
//            System.out.println("1:" + PIPEUP_POS_X);
            int PIPEUP_POS_Y = -20;
            int PIPEDOWN_POS_Y = 400;

            Sprite pipeUp = new Sprite(PIPEUP_POS_X, PIPEUP_POS_Y, DEF.IMAGE.get("pipeflap2"));
            pipeUp.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
            pipeUp.render(gc);
//                
//            Sprite pipeDown = new Sprite(PIPEUP_POS_X + 40, PIPEDOWN_POS_Y, DEF.IMAGE.get("pipeflap"));
//            pipeDown.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
//            pipeDown.render(gc);
////            
            pipes.add(pipeUp);
//            pipes.add(pipeDown);
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
    	    	 movePipe();
    	    	 
    	    	 // step2: update blob
    	    	 moveBlob();
    	    	 checkCollision();
    	    	 passPipeEffect();  	    	 
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
    	 private void movePipe() {   
             Random ran = new Random();             
    	     for(int i=0; i<pipes.size(); i++) {   
    	         if (pipes.get(i).getPositionX() <= -DEF.PIPE_WIDTH) { 
                         System.out.println("X location" + pipes.get(i).getPositionX());
                         double nextX1 = pipes.get((i+1)%DEF.PIPE_COUNT).getPositionX() + 300;
                         System.out.println(nextX1);
//                         double nextX2 = nextX1 + ran.nextInt(100, 250);
                         System.out.println("nextX1" + nextX1);
//                         System.out.println("nextX2" +nextX2);
                         double nextY = ran.nextInt(-30, 0);
                         pipes.get(i).setPositionXY(nextX1, nextY);
                         pipes.get(i).setNotPassed(pipes.get(i));                         
//                         double nextY1 = ran.nextInt(390, 420);
//                         pipes.get(i+1).setPositionXY(nextX1, nextY1);
//                         pipes.get(i+1).setNotPassed(pipes.get(i+2));    
 
                 }
    	         pipes.get(i).render(gc);
    	         pipes.get(i).update(DEF.SCENE_SHIFT_TIME);   
//    	         pipes.get(i + 1).render(gc);
//    	         pipes.get(i + 1).update(DEF.SCENE_SHIFT_TIME);    
    	      }
           	         
    	   }
    	 
    	 public void checkCollision() {
    		 
    		// check collision  
			for (Sprite floor: floors) {
				GAME_OVER = GAME_OVER || blob.intersectsSprite(floor);
				if (blob.intersectsSprite(floor)) {
	                SCORE.updateLivesText(DEF.livesText, livesLeft--);
	            }
			}
			
			for (Sprite pipe : pipes) {
			    GAME_OVER = GAME_OVER || blob.intersectsSprite(pipe);
			    if (blob.intersectsSprite(pipe)) {
                    SCORE.updateLivesText(DEF.livesText, livesLeft--);
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
    	    for (Sprite pipe : pipes) {
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

