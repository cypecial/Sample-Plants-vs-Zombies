/**
 * @(#)Plants.java
 * this is the plants class that contains all the access specifiers for individual plants(sunflower, peashooter,etc), 
 * accessors methods and other useful methods that supports the main InGame Class.
 *
 * @author Yiping Che, Stephen Wang
 * @version 1.00 2013/5/13
 */
import java.util.*;
import java.awt.image.*;
import java.awt.Rectangle;

public class Plants {
	public ArrayList<ArrayList<BufferedImage>> pics;
	public ArrayList<BufferedImage> drawPics;
	public BufferedImage startPic;
	ArrayList<String> conds;
	public String name, att, condition;
	public int count, timerCount, cost, hp, dmg, cd, tmpCD, id;
	public boolean peashoot;
    public Plants(String name, int hp, String att, int dmg, int cd, int cost, ArrayList<ArrayList<BufferedImage>> pics, ArrayList<String> conditions) {
    	count = 0;
    	timerCount = 0;
    	this.name = name;
    	this.hp = hp;
    	this.att = att;
    	this.dmg = dmg;
    	this.cd = cd;//cooldown between actions of the plants
    	this.tmpCD = cd;
    	this.cost = cost;
    	this.pics = pics;
    	
    	this.peashoot = false;
    	this.condition = "normal";//determines which set of sprites to use
    	this.conds = conditions;
    	this.drawPics = pics.get(conds.indexOf(condition));//list of the sprites current using
    	this.startPic = drawPics.get(0);//starting picture of the plant
    }
   	// update which picture should be paint
    public void countUpdate(){
    	//countdown before next frame
    	if(timerCount==6){
			if(count==drawPics.size()-1){
				count=0;
			}
			else{
				count++;
			}
			timerCount=0;
		}
		else{
			timerCount++;
		}
    }
    public void changeCondition(String c){
    	//change the condition of the plant
    	//ex. cherry bomb > normal > bomb
    	if(!condition.equals(c)){
    		condition = c;
    		drawPics = pics.get(conds.indexOf(c));
    		count = 0;
    	}
    }
    public void coolDown(){
    	//coolDown for moves
    	if(tmpCD>0){
    		tmpCD--;
    	}
    	else{
    		tmpCD=cd;
    	}
    }
    public boolean checkCollide(Rectangle rect1, Rectangle rect2){
    	//check collision of given rectangles
    	return rect1.intersects(rect2);
    }
    public Rectangle getRect(int x, int y){ 
    	//gets rectangle of the plant
    	return new Rectangle(x,y,drawPics.get(0).getWidth(),drawPics.get(0).getHeight());
    }
    
}