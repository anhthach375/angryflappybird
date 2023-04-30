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
    private ArrayList<Sprite> breads;
    private ArrayList<Sprite> peaches;
    private ArrayList<Sprite> eggs;
    private Text scoreText;
    private int totalScore;
    private int livesLeft = 3;
    private boolean isSnoozed = false;
    private ImageView gameoverImage = DEF.IMVIEW.get("gameover");
    private ImageView readyImage = DEF.IMVIEW.get("ready");
    private long snoozingStart;
    private double snoozeRemaining;
    

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
        gameControl.getChildren().addAll(DEF.startButton,DEF.listView,DEF.peachBox,DEF.snoozeEggBox,DEF.breadBox); 
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
//        System.out.println(isSnoozed);

    	// reset variables        
        CLICKED = false;
        GAME_OVER = false;
        GAME_START = false;
        floors = new ArrayList<>();
        pipeUps = new ArrayList<>();
        pipeDowns = new ArrayList<>();
        breads = new ArrayList<>();
        peaches = new ArrayList<>();
        eggs = new ArrayList<>();    
        isSnoozed = false;
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
    		floor.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
    		floor.render(gc);    		
    		floors.add(floor);
    	}
        
        // initialize blob
        blob = new Sprite(DEF.BLOB_POS_X, DEF.BLOB_POS_Y,DEF.IMAGE.get("kiki01"));
        blob.render(gc);
        
        // initialize pipeUp  
        Random ran = new Random();
        Sprite pipeUpCopy = new Sprite(0, 0, DEF.IMAGE.get("pipeflap2"));
        for (int i = 0; i < DEF.PIPE_COUNT; i++) {
            int PIPEUP_POS_X = ran.nextInt((i+1)*250, (i+2)*250);
            int PIPEUP_POS_Y = ran.nextInt(-40, -30);            
            Sprite pipeUp = new Sprite(PIPEUP_POS_X, PIPEUP_POS_Y, DEF.IMAGE.get("pipeflap2"));
            if (pipeUpCopy.intersectsSprite(pipeUp)) {
                pipeUp = new Sprite(PIPEUP_POS_X + 100, PIPEUP_POS_Y, DEF.IMAGE.get("pipeflap2"));
            }
            pipeUpCopy = pipeUp;
            pipeUp.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
            pipeUp.render(gc);         
            pipeUps.add(pipeUp);
        }   
        
        // initialize pipeDown
        Sprite pipeDownCopy = new Sprite(0, 0, DEF.IMAGE.get("pipeflap"));
        for (int i = 0; i < DEF.PIPE_COUNT; i++) {
            int PIPEDOWN_POS_X = ran.nextInt((i+1)*250, (i+2)*250);      
            int PIPEDOWN_POS_Y = ran.nextInt(430, 450);   
            Sprite pipeDown = new Sprite(PIPEDOWN_POS_X, PIPEDOWN_POS_Y, DEF.IMAGE.get("pipeflap"));
            if (pipeDownCopy.intersectsSprite(pipeDown)) {
                pipeDown = new Sprite(PIPEDOWN_POS_X + 100, PIPEDOWN_POS_Y, DEF.IMAGE.get("pipeflap"));
            }
            pipeDownCopy = pipeDown;
            pipeDown.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
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
                bread.setVelocity(DEF.SCENE_SHIFT_INCR, DEF.GODMOTHER_VELOCITY);
            }    
            bread.render(gc);           
            breads.add(bread);
        }
        
        // initialize egg and peach
        for (int i=0; i < pipeDowns.size() ; i++) {
            int ranValue = ran.nextInt(2);
            Sprite peach = new Sprite();
            Sprite egg = new Sprite();
            double eggPosX = pipeDowns.get(i).getPositionX();
            double eggPosY = pipeDowns.get(i).getPositionY() - 80;
            if (i == 0) {                
                peach.setPositionXY(eggPosX, eggPosY);
                peach.setImage(DEF.IMAGE.get("peach"));
                peach.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
            }
            else if (ranValue == 0){
                egg.setPositionXY(eggPosX, eggPosY);
                egg.setImage(DEF.IMAGE.get("egg"));
                egg.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
            } 
            peach.render(gc);  
            egg.render(gc);
            peaches.add(peach);
            eggs.add(egg);
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
    	    	 // step1: update non-player objects
    	    	 moveFloor();
    	    	 movePipeUp();
                 movePipeDown();
                 // step2: update blob
    	    	 moveBlob();   
    	    	 // step3: check for extras
    	    	 checkCollision();
    	    	 passPipeEffect();	 	    	  	    	   
    	     }      	     
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
			
			if (isSnoozed) {	            
			    blob.setPositionXY(80, 150);
                blob.setImage(DEF.IMAGE.get("kiki01"));
                blob.setVelocity(DEF.SNOOZEDFAIRY_VELOCITY, DEF.SCENE_SHIFT_INCR);              
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
			else if (!CLICKED && diffTime <= DEF.BLOB_DROP_TIME) {		

				int imageIndex = Math.floorDiv(counter++, DEF.BLOB_IMG_PERIOD);
				imageIndex = Math.floorMod(imageIndex, DEF.BLOB_IMG_LEN);
				blob.setImage(DEF.IMAGE.get("kiki0"+String.valueOf(imageIndex+1)));
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
    	   	
    	 // step 3: update pipeUp and randomize godmother
    	 private void movePipeUp() {
             Random ran = new Random(); 
             int ranValue = ran.nextInt(2);
    	     for(int i=0; i<pipeUps.size(); i++) {   
    	         if (pipeUps.get(i).getPositionX() <= -DEF.PIPE_WIDTH) { 
                         double nextX = pipeUps.get((i+1)%DEF.PIPE_COUNT).getPositionX() + ran.nextInt(250,300);
                         double nextY = ran.nextInt(-40, -20);
                         pipeUps.get(i).setPositionXY(nextX, nextY);
                         pipeUps.get(i).setNotPassed(pipeUps.get(i));  
                         if (ranValue == 0) {
                             breads.get(i).setPositionXY(nextX, nextY);
                         }
                         if (isSnoozed) {
                             pipeUps.get(i).setPositionXY(1000, pipeUps.get(i).getPositionY());
                             breads.get(i).setPositionXY(1000, breads.get(i).getPositionY());
                         }
                 }    	         
    	         pipeUps.get(i).render(gc);
    	         pipeUps.get(i).update(DEF.SCENE_SHIFT_TIME);      
    	         breads.get(i).render(gc);
    	         breads.get(i).update(DEF.SCENE_SHIFT_TIME);
    	      }    
    	 }
    	 // step 4: update pipeDown and randomize peach
    	 private void movePipeDown() {   
             Random ran = new Random(); 
             int ranValue = ran.nextInt(10);
             for(int i=0; i<pipeDowns.size(); i++) {   
                 if (pipeDowns.get(i).getPositionX() <= -DEF.PIPE_WIDTH) { 
                         double nextX = pipeDowns.get((i+1)%DEF.PIPE_COUNT).getPositionX() + 400;
                         double nextY = ran.nextInt(420, 450);
                         pipeDowns.get(i).setPositionXY(nextX, nextY);
                         pipeDowns.get(i).setNotPassed(pipeDowns.get(i));   
                         if (ranValue % 2 == 0 | ranValue % 5 == 0) {
                             peaches.get(i).setPositionXY(nextX, nextY - 80);
                             peaches.get(i).setImage(DEF.IMAGE.get("peach"));
                             peaches.get(i).setNotPassed(peaches.get(i));  
                         }
                         if (ranValue % 3 == 0) {
                             eggs.get(i).setPositionXY(nextX, nextY - 80);
                         }
                         if (isSnoozed) {
                             pipeDowns.get(i).setPositionXY(1000, pipeDowns.get(i).getPositionY());
                             peaches.get(i).setPositionXY(1000, peaches.get(i).getPositionY());
                             eggs.get(i).setPositionXY(1000, eggs.get(i).getPositionY());
                         }
                 }
                 pipeDowns.get(i).render(gc);
                 pipeDowns.get(i).update(DEF.SCENE_SHIFT_TIME);    
                 peaches.get(i).render(gc);
                 peaches.get(i).update(DEF.SCENE_SHIFT_TIME);
                 eggs.get(i).render(gc);
                 eggs.get(i).update(DEF.SCENE_SHIFT_TIME);                                      
              }                  
         } 	 
    	 public void checkCollision() {   	
    	     ImageView gameoverImage = DEF.IMVIEW.get("gameover");
             gameoverImage.setX(DEF.SCENE_WIDTH / 2 - gameoverImage.getBoundsInLocal().getWidth() / 2);
             gameoverImage.setY(DEF.SCENE_HEIGHT / 3);
    		// check collision                  
    	    if (!isSnoozed) {
    	        for (Sprite floor: floors) {
                    GAME_OVER = GAME_OVER || blob.intersectsSprite(floor);
                    if (blob.intersectsSprite(floor)) {
                        livesLeft--;           
                        SCORE.updateLivesText(DEF.livesText, livesLeft);
                     }
                }                
                for (Sprite pipe : pipeUps) {
                    GAME_OVER = GAME_OVER || blob.intersectsSprite(pipe);
                    if (blob.intersectsSprite(pipe)) {
                        livesLeft--;           
                        SCORE.updateLivesText(DEF.livesText, livesLeft);
                        }
                }               
                for (Sprite pipe : pipeDowns) {
                        GAME_OVER = GAME_OVER || blob.intersectsSprite(pipe);
                        if (blob.intersectsSprite(pipe)) {
                            livesLeft--;
                            SCORE.updateLivesText(DEF.livesText, livesLeft);
                    }            
                }           
                for (Sprite peach : peaches) {
                    if (blob.intersectsSprite(peach) && !peach.isPassed()) {
                        peach.setVisible(false);
                        SCORE.updateScoreText(DEF.scoreText, totalScore++);
                        peach.setPassed(peach);
                        sound.play("point.mp3");
                        break;
                    }
                }
                for (Sprite egg : eggs) {
                    if (blob.intersectsSprite(egg) && !egg.isPassed()) {
                        egg.setVisible(false);                  
                        isSnoozed = true;
                        egg.setPassed(egg);
                        sound.play("snooze.mp3");
                        snoozingStart = System.nanoTime();
                    }              
                }                                 
    	    }  
    	    
    	 // end the game when blob hit stuff
            if (GAME_OVER) {
                showHitEffect(); 
                for (Sprite floor: floors) {
                    floor.setVelocity(0, 0);
                }
              gameScene.getChildren().add(gameoverImage); 
              DEF.startButton.setOnAction(event -> {
                  gameScene.getChildren().remove(gameoverImage);
              });
                timer.stop();
            }
            
            if (livesLeft <= 0) {
                livesLeft = 3;
                SCORE.updateLivesText(DEF.livesText, livesLeft);
            }
            
    	 }   	     	
    	// show 
    	private void passPipeEffect() {
    	    if (!isSnoozed) {
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
    	}    	 
    	
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

