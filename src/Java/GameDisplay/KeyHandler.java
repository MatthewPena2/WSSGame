package GameDisplay;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

//This class will keep track of what keys are being pressed
public class KeyHandler implements KeyListener{
    public boolean pressedUp, pressedDown, pressedLeft, pressedRight;

    @Override
    public void keyTyped(KeyEvent e) {
        //NOT USED AS OF THIS INSTANCE OF THE GAME
        //MIGHT BE USED FOR TRADING LATER ON
    }

    @Override
    public void keyPressed(KeyEvent e) {
        //When key on keyboard is pressed down
        //getKeyCode returns a number for a key that was pressed (A = 65, C = 67, etc.)
        //using these returned ints, we can do certain actions such as
        //moving the player when a certain key is pressed
        int code = e.getKeyCode();
        if(code == KeyEvent.VK_W) pressedUp = true;
        if(code == KeyEvent.VK_A) pressedLeft = true;
        if(code == KeyEvent.VK_S) pressedDown = true;
        if(code == KeyEvent.VK_D) pressedRight = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //When key on keyboard is released
        int code = e.getKeyCode();
        if(code == KeyEvent.VK_W) pressedUp = false;
        if(code == KeyEvent.VK_A) pressedLeft = false;
        if(code == KeyEvent.VK_S) pressedDown = false;
        if(code == KeyEvent.VK_D) pressedRight = false;

    }

    public void resetKeys(){
        //failsafe for trading - player cannot move during trading
        pressedDown = false;
        pressedUp = false;
        pressedLeft = false;
        pressedRight = false;
    }
}
