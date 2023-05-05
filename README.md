# AngryFlappyBird

## How to run this code

This code runs on JavaFX 11. In order to run this game you will need to download the JavaFX 11 library. You will also need to add the following as a VM argument in the run configurations: --module-path="C:\Program Files\AdoptOpenJDK\javafx-sdk-11.0.2\lib" --add modules="javafx.base,javafx.controls,javafx.media

## Features implemented in this game

This game has a Kiki's Delivery Theme twist to the combination of Angry Birds and Flappy Birds! Help Kiki pass through pipes, avoid bread, collect cacti for extra points and windy coulds to take a break. 

* **Player Character:** Using a button, control Kiki’s flight. There is an animation of her moving in her flight. 
* **Pipe:**  Appear in pairs every in semi-random intervals. One life is taken from Kiki when a collision with any pipe occurs. Kiki bounces backward and drops immediately upon collision. 
* **Bonus Objects:** Appear randomly on the upward facing pipes and could be collected either by the bread or Kiki. If a bread collects a bonus object, points are lost. If the bird collects a bonus object, two different things could happen. If a cactus is collected, 1 extra point is added. If a windy cloud is collected, 6 seconds of autopilot mode will be triggered.
* **Bread:** drop randomly from downward facing pipes and capable of collecting bonus objects on the pipe below it. If the bread collections the bonus object before Kiki, 3 points are lost. The game is over and score is reset to 0 if Kiki collides with the bread. Kiki bounces backward and drops immediately upon collision.
* **Scene:** the background changes every 10 seconds between the places Kiki flys along her delivery routes.
* **Floor:** moves along with the pipes and bonus objects. 
* **Interactive User Panel:** has information about the bread and bonus objects, and has the button which starts the game and makes Kiki fly. 
* **Score and Lives:** points earned are kept track of, and there are 3 lives, which can be lost when Kiki runs into a pipe, bread, or the floor.
