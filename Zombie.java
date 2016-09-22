/**
 * @(#)Zombie.java
 * this is the Zombie class that contains all the access specifiers for individual zombies, 
 * accessors methods and other useful methods that supports the main InGame Class.
 *
 * @author 
 * @version 1.00 2013/5/29
 */
 
import java.util.*;
import java.awt.image.*;
import java.awt.Rectangle;
public class Zombie {
	String name;
	public int x,y,hp, dmg, speed, picNum, chompCd;
	ArrayList<ArrayList<BufferedImage>> pics;
	ArrayList<BufferedImage> drawPics;
	public BufferedImage pic = null;
	ArrayList<String> conds;
	public String condition;
	public ArrayList<BufferedImage> actionPics;
	public BufferedImage actionPic= null;
    public Zombie(String name, int hp, int dmg, int speed, ArrayList<ArrayList<BufferedImage>> pics, ArrayList<String> conditions) {
    	Random num = new Random();
    	this.name = name;
    	this.hp = hp;
    	this.x = 730;
    	this.y = 30+num.nextInt(5)*97;
    	this.dmg = dmg;
    	this.speed = speed;
    	this.pics = pics;
    	this.picNum = picNum;
    	this.chompCd = 100;
    	
    	if(name.equals("buckethead")||name.equals("conehead")){
    		condition = name+"walk";
    	}else{
    		condition = "walk";
    	}
    	conds = conditions;
    	drawPics = pics.get(conds.indexOf(condition));
    }
    
    int count = 0;
    int timerCount = 0;
    int actionCount = 0;
    int actionTimer = 0;
   	int die = 0;
   	public int ax;
   	public boolean death=false;
   	final int timer = 8;
    public void move(){
    	// zombie move 
    	pic = drawPics.get(count);
    	
    	if(hp<=0&&die==1){
    		// if zombie hp is 0, die action
			changeCondition("die");
			die = 2;
		}
		if(hp<=0&&die==3){
			//die after the boom
			changeCondition("boomdie");
			die = 2;
		}
    	if(timerCount==timer){// timer
			
			if(count==drawPics.size()-1){
			
				if(condition.equals("die") || condition.equals("boomdie")){
					//if the die action complete, change death to true 
					death=true;	
				}else {
					count = 0;//reset count
				}
				
			}
			else{
				count++;
				//update the counter
				if(condition.equals(name+"walk")||condition.equals("walk")||condition.equals("lostheadwalk")){
					alterCond("walk");
					x-=speed;
				}	
			}
			timerCount=0;
		}
		else{
			timerCount++;
		}
		actionMove();
		
    }
    private void actionMove(){
    	
    	if(hp == 2&&die ==0){
    		//action when zombie's head drops
			actionPics = pics.get(conds.indexOf("drophead"));
			actionPic = actionPics.get(actionCount);
			changeCondition("lostheadwalk");
			die=1;	
			ax = x+60;
		}
		
		//update the pictures
		if(actionPic!= null){
			if(actionTimer==5){// timer
				if(actionCount==actionPics.size()-1){
					actionPic = null;
				}
				else{
					actionCount++;
					actionPic = actionPics.get(actionCount);
				}
				actionTimer=0;
			}
			else{
				actionTimer++;
			}
		}
    }
    public void changeCondition(String c){
    	//change the conditon of the zombies
    	if(!condition.equals(c)){
    		condition = c;
    		drawPics = pics.get(conds.indexOf(c));
    		count = 0;
    	}
    }
    public Rectangle getRect(){
    	//get rectangle of the zombie
    	return new Rectangle(this.x+100,this.y+90,10,30);
    }
    public Rectangle getFrontRect(){
    	//get the front rectangle of the zombie
    	//used for checking bullet collision
    	return new Rectangle(this.x+10,this.y+90,50,30);
    }
    public void alterCond(String c){
    	//change some conditions of zombie based on its HP 
 
    	if(hp>12){
   			changeCondition(name+c);
   		}else if(hp>2){
   			changeCondition(c);
   		}else if(hp>0){
   			changeCondition("losthead"+c);
   		}
    }
    public void chompCD(){
    	//cooldown before playing "chomp" sound when attackig plants
    	if(chompCd!=0){
    		chompCd--;
    	}
    	else{
    		chompCd=100;
    	}
    }
   	public int attack(Plants p){
   		// zombie attack the plant
   		alterCond("attack");
   		
   		if(count ==drawPics.size()-1&&timerCount==timer-1){//if one "attack" action completes, plants hp is reduced by the damage  
   			return p.hp-dmg;
   		}
   		else{return p.hp; }
   	}
}