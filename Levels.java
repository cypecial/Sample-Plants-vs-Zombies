/**
 * @(#)Levels.java
 * this is the Level class that contains all the access specifiers for individual levels of the game, 
 * accessors methods and other useful methods that supports the main InGame Class.
 *
 * @author Yiping Che, Stephen Wang
 * @version 1.00 2013/6/6
 */

import java.util.*;
public class Levels {
	public int lvl, tmpCD, fwTimer;
	public int[]zombieNums, finalWave;
    public Levels(int lvl,int[]num, int[]finalWave) {
    	this.lvl = lvl;//current level
    	this.zombieNums = num;//number of each type in this level(same index as type)
    						  //[num of zombie, num of conehead, num of buckethead]
    	this.tmpCD=0;
    	this.finalWave = finalWave;//number of zombies in final wave
    	this.fwTimer = 750;
    }
    public int getLevel(){
    	//return level
    	return lvl;
    }
    public void countDown(){
    	//timer countDown before each zombies enters
    	if(tmpCD!=0){
    		tmpCD--;
    	}
    	else{
    		tmpCD=500;
    	}
    }
    public void waveCD(){
    	//countdown before zombies in the wave enters
    	if(fwTimer!=0){
    		fwTimer--;
    	}
    	else if(fwTimer<=0){
    		fwTimer=0;
    	}
    }
    
    
}