/**
 * @(#)PlantsSlot.java
 * this is the plantSlot class that contains all the access specifiers for individual plantSlot(toolbar on the top), 
 * accessors methods and other useful methods that supports the main InGame Class.
 *
 * @author Yiping Che, Stephen Wang
 * @version 1.00 2013/5/11
 */
import java.util.*;
import java.awt.image.*;
import java.awt.Rectangle;
public class PlantSlot{
	int x, y, cost,cd,tmpCD;
	boolean canBuy;
	String name;
	boolean clicked;
	BufferedImage pic;
	Rectangle rect;
	public boolean selected;
    public PlantSlot(String name, int x, int y, int cost,int cd, BufferedImage pic) {
    	this.x = x;
    	this.y = y;
    	this.name = name;
    	this.cost = cost;
    	this.pic = pic;
    	this.tmpCD = 0;
    	this.cd = cd*100;//cooldown time of each plantslot (before you can rebuy)
    	this.clicked = false;
    	this.canBuy = false;
    	selected = false;
    }
    public void coolDown(){
    	//cooldown for plantSlot after clicked
    	//resetCD();
    	if(tmpCD!=0){
    		tmpCD--;
    	}else if(selected){
    		resetCD();
    		selected = false;
    	}
    }
    public void resetCD(){
    	//reset cooldown
    	tmpCD = cd;
    }
    public boolean canBuy(){
    	//check if cooldown is over
    	return tmpCD==0;
    }
    public boolean collide(int mx, int my){
    	//check if cursor collides with plant slot
    	Rectangle mRect = new Rectangle (mx,my,1,1);
    	return mRect.intersects(getRect());
    }
    public Rectangle getRect(){ 
    	//gets rectangle of the slot
    	return new Rectangle(x,y,50,71);
    }
}