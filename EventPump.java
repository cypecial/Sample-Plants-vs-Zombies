/**
 * @(#)EventPump.java
 * this is the EventPump class that is linked with the InGame class and process all the events in 
 * the main InGame class using a eventQueue and a HashMap that send the information needed to be processed
 * to InGame
 *
 * @author Yiping Che, Stephen Wang
 * @version 1.00 2013/4/26
 */
import java.awt.*;
import java.util.*;
import java.util.Hashtable.*;
import java.util.HashMap.*;
import java.util.HashSet.*;
import java.util.LinkedList.*;

public class EventPump {
	//this class process all the events in the queue and pass it back to the main InGame class
	private LinkedList<PvZEvent> queue;
	private GamePanel myGame;
	
    public EventPump(GamePanel gameStuff) {
    	queue = new LinkedList<PvZEvent>();
    	this.myGame = gameStuff;
    	//ingame implements some commands that can be accessed by this class
    }
    
    public void addEvent(PvZEvent e){
    	//add new event to queue
    	queue.add(e);
    }
    
    public void resolveEvents(){
    	/*While my queue is not empty:
    	 *    check each event: 
    	 *             if the event.type.equals("something"):
    	 *                      perform what you need to do
    	 *             else if the event.type.equals("event").... etc.*/
    	while (queue.peekFirst() != null){
    		PvZEvent evt = queue.poll();
    		if (evt.getType().equals("collectSuns")){
				myGame.removeSun(evt.getData("id"));
				myGame.addMoney();
    		}
    		else if(evt.getType().equals("collectFlowerSuns")){
    			myGame.removeFlowerSun(evt.getData("id"));
    			myGame.addMoney();
    		} 
    		else if (evt.getType().equals("addSun")){
    			myGame.addSun();
    		}
    		else if(evt.getType().equals("fadeSun")){
    			myGame.removeSun(evt.getData("id"));
    		}
    		else if(evt.getType().equals("fadeFlowerSun")){
    			myGame.removeFlowerSun(evt.getData("id"));
    		}
    		else if(evt.getType().equals("removeBullet")){
    			myGame.removeBullet(evt.getData("id"));
    		}
    	}
    }
    
}


class PvZEvent{
	//this is the PvZEvent class that handles all the events passed in by the main InGame class
	//it formats the information given to a hashtable and is then passed into the EventPump to be processed
	private String type;
	private Hashtable<String,Integer> myHashtable = new Hashtable<String,Integer>();
	public PvZEvent(String type,String[] info,int[] data){
		//adds event to hashtable given the type, info and data
		this.type = type;
		for (int i = 0; i < info.length; i++){
			myHashtable.put(info[i],data[i]);
		}
	}
	public String getType(){
		return type;
	}
	public int getData(String key){
		return myHashtable.get(key);
	}
	
}

class CollectSun extends PvZEvent{
	//collect dropping suns
	public CollectSun(int id){
		super("collectSuns", new String[]{"id"},new int[]{id});
	}
}

class CollectFlowerSun extends PvZEvent{
	//collect sun generated suns
	public CollectFlowerSun(int id){
		super("collectFlowerSuns", new String[]{"id"},new int[]{id});
	}
}

class AddSun extends PvZEvent{
	//add new sun
	public AddSun(){
		super("addSun",new String[]{},new int[]{});
	}
}

class FadeSun extends PvZEvent{
	//fade dropping sun after a certain amount of time
	public FadeSun(int id){
		super("fadeSun",new String[]{"id"},new int []{id});
	}
}

class FadeFlowerSun extends PvZEvent{
	//fade sunflower generated sun
	public FadeFlowerSun(int id){
		super("fadeFlowerSun",new String[]{"id"},new int []{id});
	}
}
class RemoveBullet extends PvZEvent{
	//remove bullet
	public RemoveBullet(int id){
		super("removeBullet",new String[]{"id"}, new int[] {id});
	}
}