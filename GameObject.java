import javax.swing.*;
import java.awt.*;

 abstract class GameObject {
    protected int x, y;          // position
    protected int width, height; // size
    protected int speed;         // movement speed

    public GameObject(int x, int y, int width, int height, int speed){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
    }

    // movement logic (can be overridden)
    public void move(){
        x += speed;
    }

    
}