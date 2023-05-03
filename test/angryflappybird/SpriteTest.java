package angryflappybird;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SpriteTest {
    @Test
    // The purpose of the test is to see how the setPositionXY works
    // Given a value for positionX and a value for positionY
    // The expected result is sprite object is set to be at position (positionX, positionY)
    void TestSetPositionXY() {
      Sprite sprite = new Sprite();
      double positionX = 200.0;
      double positionY = 100.0;
      sprite.setPositionXY(positionX, positionY);
      assertEquals(200.0, sprite.getPositionX());
      assertEquals(100.0, sprite.getPositionY());
    }   
    
    @Test
    // The purpose of the test is to see how the setVelocityXY works
    // Given a value for velocityX and a value for velocityY
    // The expected result is sprite object is set to be at the velocity (velocityX, velocityY)
    void testSetVelocityXY() {
        Sprite sprite = new Sprite();
        double velocityX = 30.0;
        double velocityY = -10.0;
        sprite.setVelocity(velocityX, velocityY);
        assertEquals(30.0, sprite.getVelocityX());
        assertEquals(-10.0, sprite.getVelocityY());
    }
    
    @Test
    // The purpose of the test is to see how the velocity is updated in addVelocityXY funtion
    // Given a value for velocityX and a value for velocityY
    // Given two values (a,b) which will be added to velocityX and velocityY respectively
    // The expected result is velocityX = velocityX + a, and velocityY = velocityY + b
    void testAddVelocityXY() {
        Sprite sprite = new Sprite();
        double velocityX = 10.0;
        double velocityY = 20.0;
        sprite.setVelocity(velocityX, velocityY);
        sprite.addVelocity(10.0, -10.0);
        assertEquals(20.0, sprite.getVelocityX());
        assertEquals(10.0, sprite.getVelocityY());
    }
    
    @Test
    // The purpose of the test is to see how the update function works
    // Given a value for positionX and a value for positionY
    // Given a value for velocityX and a value for velocityY
    // Given a value for the time
    // The expected result is positionX and positionY are updated correctly as this formula: 
    // positionX += velocityX * time; positionY += velocityY * time;
    void testUpdate() {
        Sprite sprite = new Sprite();
        double positionX = 200.0;
        double positionY = 100.0;
        sprite.setPositionXY(positionX, positionY);        
        double velocityX = 10.0;
        double velocityY = 20.0;
        sprite.setVelocity(velocityX, velocityY);
        double time = 2.0;
        sprite.update(time);
        assertEquals(220.0, sprite.getPositionX());     
        assertEquals(140.0, sprite.getPositionY());     
    }
}
