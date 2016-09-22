/**
 * @(#)Bullet.java
 * this is the bullet class that contains all the access specifiers for individual bullets(shot by peashooter), 
 * accessors methods and other useful methods that supports the main InGame Class.
 *
 * @author Yiping Che, Stephen
 * @version 1.00 2013/5/20
 */
import java.awt.Rectangle;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

public class Bullet {
	public int x,y,dmg,id;
	BufferedImage pic;
    public Bullet(int id, int x, int y,int dmg) {
    	this.id = id;
    	this.x = x;
    	this.y = y;
    	this.dmg = dmg;
    	try{
    		pic = ImageIO.read(new File("plants/bullet.png"));
    	}
    	catch(IOException e){
    	}
    	
    }
    public int getId(){
    	//get id of the bullet
    	return id;
    }
    public void shoot(){
    	//shoot the bullet
    	if(x!=800){
    		//speed
    		x+=4;
    	}	
    }
    public Rectangle getRect(){
    	//get rectangle of the bullet
    	return new Rectangle(x,y,pic.getWidth(),pic.getHeight());
    }
    
    
    
}