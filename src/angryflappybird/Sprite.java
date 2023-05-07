package angryflappybird;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Creates components of the game with image, position, velocity, and information about them 
 * @author Willow Kelleigh and Anh Thach
 */
public class Sprite {  
	
    private Image image;
    private double positionX;
    private double positionY;
    private double velocityX;
    private double velocityY;
    private double width;
    private double height;
    private String IMAGE_DIR = "../resources/images/";
    private boolean isPassed = false;
    
    /**
     * if the sprite is currently on a break
     */
    public boolean isSnoozed;

    /**
     * Sprite constructor with no arguments. 
     * Creates sprite with no image, position at origin, and no velocity
     */
    public Sprite() {
        this.positionX = 0;
        this.positionY = 0;
        this.velocityX = 0;
        this.velocityY = 0;
    }
    
    /**
     * Sprite constructor with arguments. 
     * Creates sprite with image and position given, and no velocity
     * @param pX X-axis position
     * @param pY Y-axis position
     * @param image Image of sprite
     */
    public Sprite(double pX, double pY, Image image) {
    	setPositionXY(pX, pY);
        setImage(image);
        this.velocityX = 0;
        this.velocityY = 0;
    }

    /**
     * Method to set image of a sprite using the image given. 
     * Image must have width and height
     * @param image Image to be set 
     */
    public void setImage(Image image) {
        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
    }

    /**
     * Method to set the position of a Sprite
     * @param positionX X-axis position
     * @param positionY Y-axis position
     */
    public void setPositionXY(double positionX, double positionY) {
        this.positionX = positionX;
        this.positionY = positionY;
    }

    /**
     * Method to get the X-axis position of a Sprite
     * @return X-axis position of a Sprite
     */
    public double getPositionX() {
        return positionX;
    }

    /**
     * Method to get the Y-axis position of a Sprite
     * @return Y-axis position of a Sprite
     */
    public double getPositionY() {
        return positionY;
    }

    /**
     * Method to set the velocity of a Sprite
     * @param velocityX the X-axis velocity
     * @param velocityY the Y-axis velocity
     */
    public void setVelocity(double velocityX, double velocityY) {
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    /**
     * Method to increase the velocity of a Sprite
     * @param x amount to increase the X-axis velocity by
     * @param y amount to increase the Y-axis velocity by
     */
    public void addVelocity(double x, double y) {
        this.velocityX += x;
        this.velocityY += y;
    }

    /**
     * Method to get the  X velocity of a Sprite
     * @return the  X velocity of a Sprite
     */
    public double getVelocityX() {
        return velocityX;
    }

    /**
     * Method to get the Y velocity of a Sprite
     * @return Y velocity of a Sprite
     */
    public double getVelocityY() {
        return velocityY;
    }

    /**
     * Method to get the width of a sprite
     * @return width of a sprite
     */
    public double getWidth() {
        return width;
    }

    /**
     * Renders a sprite at its coordinates with its image
     * @param gc the sprite to be rendered
     */
    public void render(GraphicsContext gc) {
        gc.drawImage(image, positionX, positionY);
    }

    /**
     * Method to get the boundary of a Sprite
     * @return a rectangle of the sprite's location based on its position and size
     */
    public Rectangle2D getBoundary() {
        return new Rectangle2D(positionX, positionY, width, height);
    }

    /**
     * Method to see if a given sprite insetsects with another one
     * @param s the Sprite seen if it intersected with
     * @return true if they intersect, false otherwise
     */
    public boolean intersectsSprite(Sprite s) {
        return s.getBoundary().intersects(this.getBoundary());
    }

    
    /**
     * Method to update the position of the sprite vased on time elapsed and velocity of the sprite
     * @param time Time elapsed
     */
    public void update(double time) {
        positionX += velocityX * time;
        positionY += velocityY * time;
    }

    /**
     * Method to see if the sprite has passed the pipe
     * @return true if if the sprite has passed the pipe, false otherwise
     */
    public boolean isPassed() {
        return isPassed;
    }
    
    /**
     * Method to set isPassed to true
     * @param pipe pipe that was passed
     */
    public void setPassed(Sprite pipe) {
        isPassed = true;
    }
    /**
     * Method to set isPassed to false
     * @param pipe pipe that was not passed
     */
    public void setNotPassed(Sprite pipe) {
        isPassed = false;
    }

    /**
     * Method to set a sprite to be invisible if it should be
     * @param visible If the sprite should be invisible
     */
    public void setVisible(boolean visible) {
        if (!visible) {
            image = null;
        }
    }
    
}
