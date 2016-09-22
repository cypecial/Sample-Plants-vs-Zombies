/**
 * @(#)GameMenu.java
 * This is the GameMenu of the game Plants Vs Zombies, it contains accessor methods that allows the user to
 * change the current game screen. If Play is selected, it will run the InGame class which includes all the InGame methods
 *
 * @author Yiping Che, Stephen Wang
 * @version 1.00 2013/6/9
 */

import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*; 
import java.io.*; 
import javax.imageio.*;
import java.applet.*;
import javax.sound.sampled.AudioSystem;

public class GameMenu extends JFrame implements ActionListener{
	MenuPanel game;
	javax.swing.Timer myTimer;
	
    public GameMenu() {
    	super("Plants Vs Zombies");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800,625);//screen size
		setLayout(new BorderLayout());
		game = new MenuPanel();
		add(game);
		myTimer = new javax.swing.Timer(5,this); //trigger every 5 ms
		myTimer.start();
		
		setVisible(true);
		setResizable(false);
    }
     public void actionPerformed(ActionEvent evt){
		Object source = evt.getSource();
		if(source==myTimer){
			game.gameScreens();
			if(game.myScreen.equals("")){
				setVisible(false);
			}
		}
		game.repaint();
	}
    public static void main(String[] args){
    	GameMenu start = new GameMenu();
    }
    
}
class MenuPanel extends JPanel implements MouseMotionListener, MouseListener{
	private boolean mousePressed, selPlay, selExit, selHelp, selMenu;
	int mx,my,money,flowerTimer,menuSel;
	public String myScreen = "main";
	
	//images
	BufferedImage main = null;
	BufferedImage play = null;
	BufferedImage playSel = null;
	BufferedImage help = null;
	BufferedImage helpScreen = null;
	BufferedImage menuButton = null;
	BufferedImage exit = null;
	
	BufferedImage menu2 = null;
	
	AudioClip crazyDave;//main menu music
	
	public MenuPanel(){
		addMouseMotionListener(this);
		addMouseListener(this);
        setSize(800,600);
        this.mousePressed=false;
        
        String dir = "menuPics/";
        try{ //load all images
			main = ImageIO.read(new File (dir+"menu.png"));
			play = ImageIO.read(new File (dir+"quickPlay.png"));
			playSel = ImageIO.read(new File (dir+"quickPlaySel.png"));
			help = ImageIO.read(new File (dir+"helpSign.png"));
			helpScreen = ImageIO.read(new File (dir+"helpScreen.png"));
			menuButton = ImageIO.read(new File (dir+"menuButton.png"));
			exit = ImageIO.read(new File (dir+"exitSign.png"));
			
			menu2 = ImageIO.read(new File (dir+"menu2.png"));
		}
		catch (IOException e){
		}
		crazyDave = Applet.newAudioClip(getClass().getResource("sound/crazy dave.wav"));
		crazyDave.loop();
	}
	//controls game screens (menu)------------------------------------
	public void gameScreens(){
		//controls current page/ screen the game is on
		if(myScreen.equals("main")){
			mainMenu();
		}
		else if(myScreen.equals("game")){
			//stop main menu music
			crazyDave.stop();
			InGame game = new InGame();
			//continues to InGame
			myScreen ="";
		}
		else if(myScreen.equals("help")){
			help();
		}
	}
	public void mainMenu(){
		//main menu screen methods
		//checks for actions in the main menu screen
		Rectangle playRect = getRect(419,219,play.getWidth(),play.getHeight());
		Rectangle exitRect = getRect(1,342,exit.getWidth(),exit.getHeight());
		Rectangle helpRect = getRect(596,373,help.getWidth(),help.getHeight());
		if(checkCollide(mx,my,playRect)){
			//if Play is selected
			if(getAlpha(play,mx,my,419,219)==255){
				selPlay = true;
				if(mousePressed){
					myScreen="game";
				}
			}
			else{
				selPlay = false;
			}
		}
		else if(checkCollide(mx,my,exitRect)){
			//if exit is selected
			if(getAlpha(exit,mx,my,1,342)==255){
				selExit = true;
				if(mousePressed){
					System.exit(0);
				}
			}
			else{
				selExit = false;
			}
		}
		else if(checkCollide(mx,my,helpRect)){
			//help screen
			if(getAlpha(help,mx,my,596,373)==255){
				selHelp = true;
				if(mousePressed){
					myScreen="help";
				}
			}
			else{
				selHelp = false;
			}
		}
		else{
			selPlay = false;
			selExit = false;
			selHelp = false;
			mousePressed = false;
		}
	}
	
	
	public void help(){
		//detects actions performed in the help screen
		Rectangle menuRect = getRect(325,522,menuButton.getWidth(),menuButton.getHeight());
		if(checkCollide(mx,my,menuRect)){
			//return to main menu
			selMenu = true;
			if(mousePressed){
				myScreen = "main";
			}
		}
		else{
			selMenu = false;
			mousePressed = false;
		}
	}
	//get ARGB-----------------------------------------------------------
	public int[] convert(int c){
		//converts colour value "c" to ALPHA,RED,GREEN,BLUE values
		int a = (c >> 24) & 0xFF;
		int r = (c>>16) & 0xFF;
		int g = (c>>8) & 0xFF;
		int b = c & 0xFF;
		int[]c2 = new int[]{a,r,g,b};
		return c2;
	}
	public int getAlpha(BufferedImage pic, int x, int y, int px, int py){
		//get transparency value
		int[] c = convert(pic.getRGB(x-px,y-py));
		return c[0];
	}
	//---------------------------------------------------------------------
	
	public Rectangle getRect(int x, int y, int w, int h){
		//given x,y,w,h return rectangle
		return new Rectangle(x,y,w,h);
	}
	public boolean checkCollide(int mx, int my, Rectangle rect){
		//given cursor location, check collision with given rect
    	Rectangle mRect = new Rectangle (mx,my,1,1);
    	return mRect.intersects(rect);
	}
	//-------------MouseListener ------------------------------------------------
	
	public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}   
    	 
    public void mouseClicked(MouseEvent e){}  
    public void mousePressed(MouseEvent e){
    	mousePressed=true;
    }
    
	//------------Mouse Motion Listener ------------------------------------------
	
	public void mouseDragged(MouseEvent e){}
    public void mouseMoved(MouseEvent e){
    	//get cursor position
    	mx = e.getX();
		my = e.getY();
	}
    
    //---------------------------------------------------------------------
    
	public void paintComponent(Graphics g){
		//display menu graphics
		if(myScreen.equals("main")){
			//main menu screen
			g.drawImage(main,0,0,this);
			if(selPlay){
				//play selected
				g.drawImage(playSel,419,219,this);
			}
			if(selExit){
				//exit selected
				g.drawImage(exit,1,342,this);
			}
			if(selHelp){
				//help selected
				g.drawImage(help,596,373,this);
			}
		}
		if(myScreen.equals("help")){
			//help screen
			g.drawImage(helpScreen,0,0,this);
			if(selMenu){
				g.drawImage(menuButton,325,522,this);
			}
		}
    }
}