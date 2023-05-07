package angryflappybird;

import java.awt.event.ActionEvent;
import java.beans.EventHandler;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
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


/**
 * This class is contains the main method of the game, it runs the angryflappybird game
 * It creates the actual application, scene, and game elements.
 * Moves and updates game components as it is played.
 * @author Willow Kelleigh and Anh Thach
 */
public class AngryFlappyBird extends Application {
	
    // creates new instances of classes needed for game
	private Defines DEF = new Defines();
    private Sound sound = new Sound();
    private Score SCORE = new Score();


    // time related attributes
    private long clickTime, startTime, elapsedTime;   
    private AnimationTimer timer;
    
    // game components
    private Sprite kiki;
    private ArrayList<Sprite> floors;
    private ArrayList<Sprite> pipeUps;
    private ArrayList<Sprite> pipeDowns;
    private ArrayList<Sprite> breads;
    private ArrayList<Sprite> cactuses;
    private ArrayList<Sprite> clouds;
    private int totalScore;
    private int livesLeft = 3;
    private boolean isSnoozed = false;
    private long snoozingStart;
    private double snoozeRemaining;
    private int difficulty;
    private double scene_velocity;
    private double bread_velocity;
    
    Random ran = new Random();

    // game flags
    private boolean CLICKED, GAME_START, GAME_OVER, HIT_PIPE_OR_PIG;
    
    // scene graphs
    private Group gameScene;	 // the left half of the scene
    private VBox gameControl;	 // the right half of the GUI (control)
    private GraphicsContext gc;		
    
	/**
	 * the mandatory main method
	 * @param args
	 */ 
    public static void main(String[] args) {
        launch(args);
    }
       
    @Override
    /** 
     * sets the stage layer
     */
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
    
    /** 
     * sets the Scene layer
     */
    private void resetGameControl() {      
        DEF.startButton.setOnMouseClicked(this::mouseClickHandler);
        gameControl = new VBox(25);
        gameControl.getChildren().addAll(DEF.startButton,DEF.listView,DEF.cactusBox,DEF.snoozeCloudBox,DEF.breadBox); 
    }
    
    /** 
     * Handles the clicking of the button based on if the game is over and if it is during bounceback
     */
    private void mouseClickHandler(MouseEvent e) {
        if (GAME_OVER) {
            resetGameScene(false);
        }
        else if(!HIT_PIPE_OR_PIG) {
        	if (GAME_START){
                clickTime = System.nanoTime();   
            }
        	GAME_START = true;
        	CLICKED = true;
        	sound.play("wing.wav");
        }
    }
    /** 
     * Resets all the elements of the game, the scene and all the sprites.
     * If first time, create the canvases for the game.
     */
    private void resetGameScene(boolean firstEntry) {	
    	// reset variables        
        CLICKED = false;
        GAME_OVER = false;
        GAME_START = false;
        HIT_PIPE_OR_PIG = false;
        isSnoozed = false;
        floors = new ArrayList<>();
        pipeUps = new ArrayList<>();
        pipeDowns = new ArrayList<>();
        breads = new ArrayList<>();
        cactuses = new ArrayList<>();
        clouds = new ArrayList<>();
        // set difficulty as chosen by player
        difficulty = Math.max(DEF.listView.getSelectionModel().getSelectedIndex(),0);
        // set elements of the game based on difficulty
        if(difficulty==0) {
            scene_velocity = DEF.SCENE_SHIFT_INCR_EASY;
            bread_velocity = DEF.BREAD_VELOCITY_EASY;
        }
        else if(difficulty==1) {
            scene_velocity = DEF.SCENE_SHIFT_INCR_MED;
            bread_velocity = DEF.BREAD_VELOCITY_MED;
        }
        else {
            scene_velocity = DEF.SCENE_SHIFT_INCR_HARD;
            bread_velocity = DEF.BREAD_VELOCITY_HARD;
        }
        
        final ImageView[] backgrounds = new ImageView[]{
                DEF.IMVIEW.get("backgroundDay"),
                DEF.IMVIEW.get("backgroundAfternoon"),
                DEF.IMVIEW.get("backgroundNight"),
         };      
    	if(firstEntry) {
    	    // create two canvases
            Canvas canvas = new Canvas(DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);
            gc = canvas.getGraphicsContext2D();
            
            ImageView readyImage = DEF.IMVIEW.get("ready");
            readyImage.setX(DEF.SCENE_WIDTH / 2 - readyImage.getBoundsInLocal().getWidth() / 2);
            readyImage.setY(DEF.SCENE_HEIGHT / 3);
                   
            
            // create the game scene            
            gameScene = new Group();
            gameScene.getChildren().addAll(backgrounds[0], canvas, readyImage, DEF.scoreText, DEF.livesText);            
            DEF.startButton.setOnAction(event -> {
                gameScene.getChildren().remove(readyImage);
            });

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
    		floor.setVelocity(scene_velocity, 0);
    		floor.render(gc);    		
    		floors.add(floor);
    	}
        
        // initialize kiki
    	kiki = new Sprite(DEF.KIKI_POS_X, DEF.KIKI_POS_Y,DEF.IMAGE.get("kiki01"));
    	kiki.render(gc);
        
        // initialize pipeUp  
        Sprite pipeUpCopy = new Sprite(0, 0, DEF.IMAGE.get("pipeflap2"));
        for (int i = 0; i < DEF.PIPE_COUNT; i++) {
            int PIPEUP_POS_X = ran.nextInt((i+1)*250, (i+2)*250);
            int PIPEUP_POS_Y = ran.nextInt(-40, -30);            
            Sprite pipeUp = new Sprite(PIPEUP_POS_X, PIPEUP_POS_Y, DEF.IMAGE.get("pipeflap2"));
            if (pipeUpCopy.intersectsSprite(pipeUp)) {
                pipeUp = new Sprite(PIPEUP_POS_X + 150, PIPEUP_POS_Y, DEF.IMAGE.get("pipeflap2"));
            }
            pipeUpCopy = pipeUp;
            pipeUp.setVelocity(scene_velocity, 0);
            pipeUp.render(gc);         
            pipeUps.add(pipeUp);
        }   
        
        // initialize pipeDown
        Sprite pipeDownCopy = new Sprite(0, 0, DEF.IMAGE.get("pipeflap"));
        for (int i = 0; i < pipeUps.size(); i++) {
            double PIPEDOWN_POS_X = pipeUps.get(i).getPositionX();
            double PIPEDOWN_POS_Y = pipeUps.get(i).getPositionY() + 480;   
            Sprite pipeDown = new Sprite(PIPEDOWN_POS_X, PIPEDOWN_POS_Y, DEF.IMAGE.get("pipeflap"));
            if (pipeDownCopy.intersectsSprite(pipeDown)) {
                pipeDown = new Sprite(PIPEDOWN_POS_X + 150, PIPEDOWN_POS_Y, DEF.IMAGE.get("pipeflap"));
            }
            pipeDownCopy = pipeDown;
            pipeDown.setVelocity(scene_velocity, 0);
            pipeDown.render(gc);    

            pipeDowns.add(pipeDown);
        }      
        
        // initialize bread
        for (int i=0; i < pipeUps.size(); i++) {
            Sprite bread = new Sprite();
            if(i%2==0) {
                double breadPosX = pipeUps.get(i).getPositionX();
                double breadPosY = pipeUps.get(i).getPositionY();
                bread.setPositionXY(breadPosX, breadPosY);
                bread.setImage(DEF.IMAGE.get("bread"));
                bread.setVelocity(scene_velocity, bread_velocity);
            }    
            bread.render(gc);           
            breads.add(bread);
        }
        
        // initialize cactus and cloud
        for (int i=0; i < pipeDowns.size() ; i++) {
            Sprite cactus = new Sprite();
            Sprite cloud = new Sprite();
            double cloudPosX = pipeDowns.get(i).getPositionX();
            double cloudPosY = pipeDowns.get(i).getPositionY() - 80;
            if (i == 0) {                
                cactus.setPositionXY(cloudPosX, cloudPosY);
                cactus.setImage(DEF.IMAGE.get("cactus"));
                cactus.setVelocity(scene_velocity, 0);
            }
            else {
                cloud.setPositionXY(cloudPosX, cloudPosY);
                cloud.setImage(DEF.IMAGE.get("cloud"));
                cloud.setVelocity(scene_velocity, 0);
            } 
            cactus.render(gc);  
            cloud.render(gc);
            cactuses.add(cactus);
            clouds.add(cloud);
        }
        // initialize timer
        startTime = System.nanoTime();
        timer = new MyTimer();
        timer.start();  

    }
    
    /**
     * The MyTimer class is an extension of AnimationTimer. 
     * It keeps track of the time to manage the elements of the game.
     *
     */
    class MyTimer extends AnimationTimer { 	
    	int counter = 0;  	
    	 @Override
    	 /** 
          * Moves elements of the game and keeps track of elapsed time.
          * If there is a collision, appropriate changes are made.
          * If isSnoozed, element with snooze timer appears on screen.
          */
    	 public void handle(long now) {   		 
    		 // time keeping
    	     elapsedTime = now - startTime;
    	     startTime = now;
    	     
    	     // clear current scene
    	     gc.clearRect(0, 0, DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);   	     
    	     
    	     // if game is going, move elements and check for extras
    	     if (GAME_START) {
        	     moveFloor();
        	     movePipesCactusCloud();
                 moveBread();
    	    	 moveKiki();
    	    	 checkCollision();
    	    	 passPipeEffect();	 	    	  	    	   
    	     }
    	     // if isSnoozed, show timer on screen
    	     if (isSnoozed) {
                 double snoozeTime = (System.nanoTime() - snoozingStart)*DEF.NANOSEC_TO_SEC;
                 snoozeRemaining = (7-snoozeTime);
                 DEF.snoozeTime.setText(String.valueOf((int)snoozeRemaining) + " secs to go"); 
                 if ((int)(snoozeRemaining) <= 0) {
                     isSnoozed = false;
                     gameScene.getChildren().remove(DEF.snoozeTime);
                 }
             }    	     
    	 }    	 
    	 /** 
          * Helper function for handle() moves floor.
          */
    	 private void moveFloor() {            
             for(int i=0; i<DEF.FLOOR_COUNT; i++) {
                 // if floor is offscreen, move back around
                 if (floors.get(i).getPositionX() <= -DEF.FLOOR_WIDTH) {
                     double nextX = floors.get((i+1)%DEF.FLOOR_COUNT).getPositionX() + DEF.FLOOR_WIDTH;
                     double nextY = DEF.SCENE_HEIGHT - DEF.FLOOR_HEIGHT;
                     floors.get(i).setPositionXY(nextX, nextY);
                 }
                 //render floors
                 floors.get(i).render(gc);
                 floors.get(i).update(DEF.SCENE_SHIFT_TIME);
             }
         }   	

    	 /** 
          * Helper function for handle() moves kiki, cycling through the different images as she moves.
          * If isSnoozed, moves on her own.
          * If button has been clicked recently, kiki moves up.
          * If button hasn't been clicked in a little while, she falls.
          * Kiki cannot be moved by player during bounceback.
          */
    	 private void moveKiki() {  	
			long diffTime = System.nanoTime() - clickTime;
			// if isSnoozed, Kiki moves on her own for 6 seconds
			if (isSnoozed) {	            
			    kiki.setPositionXY(80, 150);
			    kiki.setImage(DEF.IMAGE.get("kiki01"));
			    kiki.setVelocity(DEF.SNOOZEDKIKI_VELOCITY, scene_velocity);              
              if (!gameScene.getChildren().contains(DEF.snoozeTime)) {
                  gameScene.getChildren().add(DEF.snoozeTime);
              }              
              Timeline snoozeTimeline = new Timeline(
                      new KeyFrame(Duration.seconds(6), event -> {
                          isSnoozed = false;
                      })
                  );
                snoozeTimeline.play();              
			}
			// else if button has been clicked recently, kiki moves up
			else if (!CLICKED && diffTime <= DEF.KIKI_DROP_TIME) {		

				int imageIndex = Math.floorDiv(counter++, DEF.KIKI_IMG_PERIOD);
				imageIndex = Math.floorMod(imageIndex, DEF.KIKI_IMG_LEN);
				kiki.setImage(DEF.IMAGE.get("kiki0"+String.valueOf(imageIndex+1)));
				kiki.setVelocity(0, DEF.KIKI_FLY_VEL);
			}
			// else if haven't hit pipe or pig, Kiki drops after a period of time without button click 
			else if(!HIT_PIPE_OR_PIG){
			    kiki.setVelocity(0, DEF.KIKI_DROP_VEL); 
			    CLICKED = false;
			}

			// render kiki on GUI
			kiki.update(elapsedTime * DEF.NANOSEC_TO_SEC);
			kiki.render(gc);
    	 }  
    	 
    	 /** 
          * Helper function for handle() moves pipes and bonus objects after they go off screen.
          * Randomizes appearances of cactuses or clouds every time a downpipe moves.
          * If isSnoozed, moved farther away based on speed of the scene.
          */
    	 private void movePipesCactusCloud() {  
             for(int i=0; i<pipeDowns.size(); i++) {   
                 // if pipe is offscreen
                 if (pipeDowns.get(i).getPositionX() <= -DEF.PIPE_WIDTH) { 
                     // set new positions for pipes 
                     int ranValue = ran.nextInt(10);
                     double nextX = pipeDowns.get((i+1)%DEF.PIPE_COUNT).getPositionX() + 300;
                     double nextY_down = ran.nextInt(400, 460);
                     double nextY_up = nextY_down - 480;
                     pipeDowns.get(i).setPositionXY(nextX, nextY_down);
                     pipeDowns.get(i).setNotPassed(pipeDowns.get(i)); 
                     pipeUps.get(i).setPositionXY(nextX, nextY_up);
                     pipeUps.get(i).setNotPassed(pipeUps.get(i));
                     // if snoozed, move them farther away
                     if (isSnoozed) {
                         double snoozeX = 1000*(scene_velocity/DEF.SCENE_SHIFT_INCR_MED);
                         pipeDowns.get(i).setPositionXY(snoozeX, pipeDowns.get(i).getPositionY());
                         pipeUps.get(i).setPositionXY(snoozeX, pipeUps.get(i).getPositionY());
                         cactuses.get(i).setPositionXY(snoozeX, cactuses.get(i).getPositionY());
                         clouds.get(i).setPositionXY(snoozeX, clouds.get(i).getPositionY());
                     }
                     // else randomize appearance of cactus
                     else if (ranValue % 2 == 0 | ranValue % 5 == 0) {
                         cactuses.get(i).setPositionXY(nextX, nextY_down - 80);
                         cactuses.get(i).setImage(DEF.IMAGE.get("cactus"));
                         cactuses.get(i).setNotPassed(cactuses.get(i));  
                     }
                  // else randomize appearance of cloud
                     else if (ranValue % 3 == 0)  {
                         clouds.get(i).setPositionXY(nextX, nextY_down - 80);
                         clouds.get(i).setImage(DEF.IMAGE.get("cloud"));
                         clouds.get(i).setNotPassed(clouds.get(i));
                     }
                         
                 }
                 // render them all
                 pipeDowns.get(i).render(gc);
                 pipeDowns.get(i).update(DEF.SCENE_SHIFT_TIME);
                 pipeUps.get(i).render(gc);
                 pipeUps.get(i).update(DEF.SCENE_SHIFT_TIME);
                 cactuses.get(i).render(gc);
                 cactuses.get(i).update(DEF.SCENE_SHIFT_TIME);
                 clouds.get(i).render(gc);
                 clouds.get(i).update(DEF.SCENE_SHIFT_TIME);                                      
             }                  

         }
    	 
    	 /** 
          * Helper function for handle() moves bread to another pipe after it goes off screen.
          * Waits for a randomized interval of time before doing so.
          * Doesn't move if HIT_PIPE_OR_PIG or isSnoozed.
          */
    	 private void moveBread() {             
             for(int i=0; i<breads.size(); i++) {
                 double waitDistance = ran.nextInt(200,2000);
                 // if bread is offscreen and not HIT_PIPE_OR_PIG or isSnoozed
                 if (breads.get(i).getPositionX() <= -waitDistance && !HIT_PIPE_OR_PIG && !isSnoozed) {
                     //get position from farthest pipe
                     double nextX = 0;
                     for (int j=0; j<pipeUps.size(); j++){
                         if (pipeUps.get(j).getPositionX()>nextX) {
                             nextX = pipeUps.get(j).getPositionX();
                         }
                     }
                     // set new position
                     double nextY = 0;
                     breads.get(i).setPositionXY(nextX, nextY);
                     breads.get(i).setNotPassed(breads.get(i));                            
                 }
                 // render the bread
                 breads.get(i).render(gc);
                 breads.get(i).update(DEF.SCENE_SHIFT_TIME);      
              }                  
         }	 
    	 /**
    	     * Helper function for handle().
    	     * Checks to see if there was a collision between kiki and any of the elements of the game.
    	     * and updates the game and imposes consequences.
    	     * If isSnoozed, does not check for collisions for anything .
    	     * If kiki hits pipe or pig, bounces back and waits to hit floor.
    	     * If hits floor or pig life is taken, if hit pig score is set to 0.
    	     * Cactuses and clouds are checked for intersections with both Kiki and bread.
    	     */
    	 public void checkCollision() {   	
    	     ImageView gameoverImage = DEF.IMVIEW.get("gameover");
             gameoverImage.setX(DEF.SCENE_WIDTH / 2 - gameoverImage.getBoundsInLocal().getWidth() / 2);
             gameoverImage.setY(DEF.SCENE_HEIGHT / 3);
    		  // check collision between kiki and non-bonus elements unless snoozed                
    	    if (!isSnoozed) { 
    	      for (Sprite floor: floors) {
                  GAME_OVER = GAME_OVER || kiki.intersectsSprite(floor);
                  if (kiki.intersectsSprite(floor)) {
                      livesLeft--;           
                      SCORE.updateLivesText(DEF.livesText, livesLeft);
                   }
              }                
              for (Sprite pipe : pipeUps) {
                  if (kiki.intersectsSprite(pipe)) {
                      HIT_PIPE_OR_PIG = true;
                   }
              }               
              for (Sprite pipe : pipeDowns) {
                  if (kiki.intersectsSprite(pipe)) {
                      HIT_PIPE_OR_PIG = true;
                   }            
               }           
              for (Sprite bread : breads) {
                  if (kiki.intersectsSprite(bread)) {
                      if (!HIT_PIPE_OR_PIG) {
                          sound.play("pig_sound.mp3");
                          totalScore = 0;
                          SCORE.resetScoreText(DEF.scoreText);
                      }
                      HIT_PIPE_OR_PIG = true;
                  }
              }
                  
               for (Sprite cactus : cactuses) {
                   //kiki collision
                  if (kiki.intersectsSprite(cactus) && !cactus.isPassed()) {
                      cactus.setVisible(false);
                      SCORE.updateScoreText(DEF.scoreText, totalScore++);
                      cactus.setPassed(cactus);
                      sound.play("point.mp3");
                      break;
                  }
                  // bread collision
                  for (int i=0; i<breads.size(); i++) {
                      if (!HIT_PIPE_OR_PIG) {
                          if (cactus.intersectsSprite(breads.get(i))) {
                              cactus.setVisible(false);
                              totalScore = Math.max(0, totalScore - 3 );
                              SCORE.updateScoreText(DEF.scoreText, totalScore);
                          }
                      }
                  }
             }
               for (Sprite cloud : clouds) {
                   //kiki collision
                   if (kiki.intersectsSprite(cloud) && !cloud.isPassed()) {
                       if (!HIT_PIPE_OR_PIG) {
                           cloud.setVisible(false);                  
                           isSnoozed = true;
                           cloud.setPassed(cloud);
                           sound.play("snooze.mp3");
                           snoozingStart = System.nanoTime();
                       }
                   } 
                   //bread collision
                   for (int i=0; i<breads.size(); i++) {
                       if (!HIT_PIPE_OR_PIG) {
                           if (cloud.intersectsSprite(breads.get(i))) {
                               cloud.setVisible(false);
                               totalScore = Math.max(0, totalScore - 3 );
                               SCORE.updateScoreText(DEF.scoreText, totalScore);
                           }
                       }
                   }
               }           
               // if bird hits bread or pipe, bounce and wait to hit floor
               if(HIT_PIPE_OR_PIG) {
                   kiki.setVelocity(DEF.KIKI_FLY_BACK_VEL, DEF.KIKI_DROP_VEL); 
                   stopMotion();
               }
           } // end of if not snoozed
    	    
            // end the game when kiki hit stuff
            if (GAME_OVER) {
              showHitEffect(); 
              stopMotion();
              gameScene.getChildren().add(gameoverImage); 
              DEF.startButton.setOnAction(event -> {
                  gameScene.getChildren().remove(gameoverImage);
              });
              timer.stop();
            }
    
            if (livesLeft <= 0) {
                totalScore = 0;
                SCORE.resetScoreText(DEF.scoreText);
                livesLeft = 3;
                SCORE.updateLivesText(DEF.livesText, livesLeft);
            }
        }  	 
    	 /** 
    	  * Helper function for checkCollision() that stops motion of game elements except kiki.
    	  */
    	 private void stopMotion() {
    	    for (Sprite floor: floors) {
                floor.setVelocity(0, 0);
            }
    	    for (Sprite pipe: pipeUps) {
                pipe.setVelocity(0, 0);
            }
    	    for (Sprite pipe: pipeDowns) {
                pipe.setVelocity(0, 0);
            }
    	    for (Sprite bread: breads) {
                bread.setVelocity(0, 0);
            }
    	    for (Sprite cactus: cactuses) {
    	        cactus.setVelocity(0, 0);
            }
    	    for (Sprite cloud : clouds) {
    	        cloud.setVelocity(0, 0);
    	    }    	     
    	}
    	 
    	 /** 
          * Helper function for handle() updates game when kiki passes through a set of pipes 
          * successfully.
          */     	 
    	private void passPipeEffect() {
    	    if (!isSnoozed) {
    	        for (Sprite pipe : pipeUps) {
                    if (kiki.getPositionX() > pipe.getPositionX() && !pipe.isPassed()) {
                        SCORE.updateScoreText(DEF.scoreText, totalScore++);
                        pipe.setPassed(pipe);
                        sound.play("point.mp3");
                        break; 
                    }
                }
    	    }
    	}   
    	/** 
         * Helper function for checkCollision() visually shows when the game is over.
         */
        private void showHitEffect() {
	        ParallelTransition parallelTransition = new ParallelTransition();
	        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(DEF.TRANSITION_TIME), gameScene);
	        fadeTransition.setToValue(0);
	        fadeTransition.setCycleCount(DEF.TRANSITION_CYCLE);
	        fadeTransition.setAutoReverse(true);	        
	        parallelTransition.play();
	     }
    	 
    } // End of MyTimer class

} // End of AngryFlappyBird Class

