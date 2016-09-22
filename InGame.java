/**
 * @(#InGame.java
 * this is the main game class that contains all the methods that operates the game
 *
 * @author Yiping Che, Stephen Wang
 * @version 1.00 2013/4/23
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

public class InGame extends JFrame implements ActionListener{
    javax.swing.Timer myTimer;
	GamePanel game = new GamePanel();
	
    public InGame() {
    	super("Plants Vs Zombies");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800,625);//screen size
		setLayout(new BorderLayout());
		
		add(game, BorderLayout.CENTER);
		
	//	Toolkit tk = Toolkit.getDefaultToolkit();
	//	Image cursorImage = new ImageIcon("pics/cursor.png").getImage();
	//	Cursor c = tk.createCustomCursor( cursorImage, new Point(5,5), "My Cursor" );
	//	setCursor(c);
		
		myTimer = new javax.swing.Timer(10,this); //trigger every 5 ms
		myTimer.start();
		
		
		setVisible(true);
		setResizable(false);
    }
    public void actionPerformed(ActionEvent evt){
		Object source = evt.getSource();
		if(source==myTimer){
			game.gameScreens();
		}
		if(game.myScreen.equals("mm")){
			GameMenu m = new GameMenu();
			setVisible(false);
			myTimer.stop();
		}
		game.repaint();
		game.resolveEvents();
	}
    public static void main(String[]arguments)throws IOException{
    	InGame myGame = new InGame();
    }
    
    
}
class GamePanel extends JPanel implements MouseMotionListener, MouseListener{
	//this is the GamePanel class that contains all main methods that controls the main frame of the game Plants Vs Zombies
							//plantSlot
	public static final int PSNAME = 0, PSX = 1, PSY = 2, PSCOST = 3, PSCD = 4,
							//plant
							PNAME = 0, PHP = 1, PATT = 2, PDMG = 3, PCD = 4, PCOST = 5,
							//zombie
							ZNAME = 0, ZHP = 1, ZDMG = 2, ZSPEED = 3,
							//level
							LNUM = 0, ZNUM1 = 1, ZNUM2 = 2, ZNUM3 = 3,ZF1 = 4,ZF2 = 5,ZF3 = 6;
	private Plants[][] field;
	private Plants selectedPlant = null;
	ArrayList<Levels> allLevels = new ArrayList<Levels>();
	
	ArrayList<PlantSlot> toolbar = new ArrayList<PlantSlot>();
	ArrayList<Plants> allPlants = new ArrayList<Plants>();
	
	ArrayList<Zombie> allZombies = new ArrayList<Zombie>();//zombie types in the level
	ArrayList<Zombie> currentZombies;//zombies in the current level (excluding final wave)
	ArrayList<Zombie> inFieldZombie;//zombies currently in the field
	
	ArrayList<Integer> zombieCount;
	
	ArrayList<Zombie> finalWave;
	ArrayList<Integer> finalCount;
	
	ArrayList<Sun>suns;
	ArrayList<Sun>flowerSuns;
	
	ArrayList<Bullet> allBullets;
	
	ArrayList<String> conds = new ArrayList<String>();
	
	ArrayList<AudioClip> allSounds;
	ArrayList<String> allSoundNames;
	
	private boolean mousePressed,selMenu2,selMm,selBtg,selRLvl,selTryAgain;
	public String myScreen;
	int mx,my,money,flowerTimer,level;
	int sunId = 0;//used to track the id's of suns in the game
	int bulletId=0;
	int gameOverTimer = 350;//timer before "try again" option is displayed
	int[][] fieldx; 
	int[][] fieldy;
	
	Levels curLevel;
	
	Rectangle[][] allField;
	
	EventPump myEvents;
	
	Scanner plantSlotFile;
	Scanner plantsFile;
	Scanner zombieFile;
	Scanner levelFile;
	
	//images
	
	BufferedImage back = null;
	BufferedImage bulletHit = null;
	BufferedImage psPlants = null;
	BufferedImage waveNote = null;
	BufferedImage shade = null;
	BufferedImage menu2 = null;
	BufferedImage menuOption = null;
	BufferedImage mm = null;
	BufferedImage btg = null;
	BufferedImage rLvl = null;
	BufferedImage endGame = null;
	BufferedImage tryAgain = null;
	BufferedImage tryAgainSel = null;
	BufferedImage resetSel = null;
	BufferedImage[] levelPics = new BufferedImage[5];
	BufferedImage finalWavePic = null;
	BufferedImage winGame = null;
	int levelPicCount;
	Rectangle resetRect = new Rectangle(419,8,30,30);//rectangle for reset button in toolbar

	public GamePanel(){
		addMouseMotionListener(this);
		addMouseListener(this);
        setSize(800,600);
        
        // field 
        allField = new Rectangle[5][9];
        field =new Plants[5][9];
        fieldx= new int[5][9];
        fieldy  = new int[5][9];
        
        this.myScreen = "game";
        this.level = 1;
        
        this.flowerTimer = 400;//timer used for dropping suns
        
        //load images
        back = getImage("pics","background");
        bulletHit = getImage("plants","PeaBulletHit");
        psPlants = getImage("pics","psPlants");
        waveNote = getImage("pics","note");
        shade = getImage("pics","shade");
        menu2 = getImage("menuPics","menu2");
        menuOption = getImage("menuPics","menuOption");
        mm = getImage("menuPics","mm");
        btg = getImage("menuPics","btg");
        rLvl = getImage("menuPics","rLvl");
        resetSel = getImage("pics","resetSel");
        finalWavePic = getImage("pics","finalWave");
        for(int i =1;i<=5;i++){
        	levelPics[i-1]= getImage("pics/level","level"+i);
        }
        endGame = getImage("pics","endGame");
        tryAgain = getImage("pics","tryAgain");
        tryAgainSel = getImage("pics","tryAgainSel");
        winGame = getImage("pics","winGame");
        try{ 
        	//import text files
        	plantSlotFile = new Scanner(new BufferedReader(new FileReader("plantSlotTxt.txt")));
        	plantsFile = new Scanner(new BufferedReader(new FileReader("plants.txt")));
        	zombieFile = new Scanner(new BufferedReader(new FileReader("zombies.txt")));
        	levelFile = new Scanner(new BufferedReader(new FileReader("levels.txt")));
			
		}
		catch (IOException e){
		}
		//import sounds
		allSounds = getAllSounds("sound");
		allSoundNames = getSoundNames("sound");
		
		//import plantSlot info
		int psNum = Integer.parseInt(plantSlotFile.nextLine());//number of lines in the text file
	    while(plantSlotFile.hasNextLine()){	//splits the plants text file
	   		String psLine = plantSlotFile.nextLine();
	    	String[]psPlant= psLine.split(",");
	    	//make new plantSlot
	    	PlantSlot pSlot = new PlantSlot(psPlant[PSNAME],
	    				Integer.parseInt(psPlant[PSX]),
	    				Integer.parseInt(psPlant[PSY]),
	    				Integer.parseInt(psPlant[PSCOST]),
	    				Integer.parseInt(psPlant[PSCD]),
	    				getImage("plants/cards",psPlant[PSNAME]));
	    				
			toolbar.add(pSlot);
	    }
	    //import plants info
	    int pNum = Integer.parseInt(plantsFile.nextLine());
	    while(plantsFile.hasNextLine()){
	    	String pLine = plantsFile.nextLine();
	    	String[]pPlant = pLine.split(",");
	    	Plants newPlant = new Plants(pPlant[PNAME],
	    				Integer.parseInt(pPlant[PHP]),
	    								pPlant[PATT],
	    				Integer.parseInt(pPlant[PDMG]),
	    				Integer.parseInt(pPlant[PCD]),
	    				Integer.parseInt(pPlant[PCOST]),
	    				getImage2D("plants",pPlant[PNAME]),
	    				conds);
	    				conds = new ArrayList<String>();
	    	allPlants.add(newPlant);
	    }
	    //import zombies info
	    int zNum = Integer.parseInt(zombieFile.nextLine());
	    
	    while(zombieFile.hasNextLine()){
	    	String zLine = zombieFile.nextLine();
	    	String[] zombieInfo = zLine.split(",");
	    	conds = new ArrayList<String>();
	    	Zombie newZombie = new Zombie(zombieInfo[ZNAME],
	    				Integer.parseInt(zombieInfo[ZHP]),
	    				Integer.parseInt(zombieInfo[ZDMG]),
	    				Integer.parseInt(zombieInfo[ZSPEED]),
	    				getImage2D("zombies",zombieInfo[ZNAME]),
	    				conds);
	    				
	    	allZombies.add(newZombie);
	    }
	    
	    //import levels info
	    int lNum = Integer.parseInt(levelFile.nextLine());
	    
	    while(levelFile.hasNextLine()){
	    	String lLine = levelFile.nextLine();
	    	String[]levelInfo = lLine.split(",");
	    	//amount of each zombies in the level before the final wave
	    	int[]zombieNums = new int[3];
	    	zombieNums[0]=Integer.parseInt(levelInfo[ZNUM1]);
	    	zombieNums[1]=Integer.parseInt(levelInfo[ZNUM2]);
	    	zombieNums[2]=Integer.parseInt(levelInfo[ZNUM3]);
	    	//amount of zombies in the final wave
	    	int[]finalNum = new int [3];
	    	finalNum[0]=Integer.parseInt(levelInfo[ZF1]);
	    	finalNum[1]=Integer.parseInt(levelInfo[ZF2]);
	    	finalNum[2]=Integer.parseInt(levelInfo[ZF3]);
	    	Levels newLevel = new Levels(Integer.parseInt(levelInfo[LNUM]),
					    				zombieNums,
					    				finalNum);
	    	allLevels.add(newLevel);
	    }
	    //set fieldx, fieldy
		for(int m=0;m<5;m++){
			for(int n=0;n<9;n++){
				//stores all x and y values of the minifield
				fieldx[m][n]=40+n*80;
				fieldy[m][n]= 100+m*100;
			}
		}
		
		//set field rectangles
		for(int i=0; i<5; i++) {
        	for (int j=0; j<9; j++) {
        		//create minifields
        		Rectangle miniField = new Rectangle(25+82*j,80+97*i,82,97);
        		allField[i][j] = miniField;
        	}
        }
        
        resetLevel();
        levelPic = levelPics[0];
        levelPicCount = 300;
	}
	public void resetLevel(){
		//restart level
		//stop all playing sounds
		getSound("chomp").stop();
		//play background music
		getSound("grasswalk").loop();
		//reset all features
		money = 50;
		gameOverTimer = 350;
		setField();
		setSuns();
		setBullets();
		setZombie();
		finalW = false;
		for(PlantSlot each: toolbar){
			//reset all cd on plants
			each.tmpCD=0;
		}
		myEvents = new EventPump(this);
		
	}
	public void setField(){
	 	//create 5x9 game field
    	for(int i=0;i<5;i++){
    		for(int j=0;j<9;j++){
    			field[i][j]= null;
    		}
    	}
    }
	public void setSuns(){
		//prepares empty sun and flower sun arraylist
		//clears all existing suns in game
		sunId=0;
		suns = new ArrayList<Sun>();
		flowerSuns = new ArrayList<Sun>();
	}
	public void setBullets(){
		//sets empty bullet arraylist
		//clears all bullets existing in game
		bulletId=0;
		allBullets = new ArrayList<Bullet>();
	}
	public void setZombie(){
		//set all zombie arraylist in the level
		currentZombies = new ArrayList<Zombie>();//zombies in the current level (excluding final wave)
		inFieldZombie =new ArrayList<Zombie>();//zombies currently in the field
		finalWave = new ArrayList<Zombie>();//zombies in a large wave that's in the field
		zombieCount= new ArrayList<Integer>();
		finalCount= new ArrayList<Integer>();
		zombieFunc(level);
		for(int c=0;c<currentZombies.size();c++){
        	zombieCount.add(1000);
        }
        for(int d=0;d<finalWave.size();d++){
        	finalCount.add(200);
        }
	}
	//get ARGB
	public int[] convert(int c){
		//convert colour values to ALPHA, RED, GREEN, BLUE value
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
	public AudioClip getSound(String name){
		//returns sound with name
		for(int i = 0; i < allSoundNames.size();i++){
			if(allSoundNames.get(i).equals(name+".wav")){
				return allSounds.get(i);
			}
		}
		return null;	
	}
	public ArrayList<AudioClip> getAllSounds(String loc){
		//takes sound location (folder name) returns all sounds in that folder as an array list
		ArrayList<AudioClip>tmp = new ArrayList<AudioClip>();
		AudioClip audioTmp;
		File folder = new File(loc);//find the main folder
		int count = 1;
		for(File i:folder.listFiles()){
			//add all files from folder to tmp
			audioTmp = Applet.newAudioClip(getClass().getResource(loc+"/"+i.getName()));
			tmp.add(audioTmp);
		}
		return tmp;
	}
	public ArrayList<String> getSoundNames(String loc){
		//takes sound location (folder name) returns all the names of the sound in that folder as an array list
		ArrayList<String>tmp = new ArrayList<String>();
		String name;
		File folder = new File(loc);//find the main folder
		int count = 1;
		for(File i:folder.listFiles()){
			name = i.getName();
			tmp.add(name);
		}
		return tmp;
	}
	public BufferedImage getImage(String loc,String name){
		//takes image name and returns the image
		BufferedImage pic = null;;
		try{
			pic = ImageIO.read(new File (loc+"/"+name+".png"));
		}
		catch(IOException e){}
		
		return pic;
	}
	/*public BufferedImage[]getImages(String loc, String name,int len){
		//takes image location and return an array of images (sprites)
		BufferedImage pics[]= new BufferedImage[len];
		try{
			for(int i=1;i<=len;i++){
				pics[i-1] = ImageIO.read(new File(loc+"/"+name+"/"+i+".png"));
			}
		}
		catch(IOException e){}
		
		return pics;
	}*/
	
	public ArrayList<ArrayList<BufferedImage>> getImage2D(String loc,String name) {
		//returns a 2D list of the images in the main folder
		//each individual zombies have different types of sprites (eg. walk, attack, losthead) 
		ArrayList<ArrayList<BufferedImage>> pics = new ArrayList<ArrayList<BufferedImage>>();
		BufferedImage[] subs;
		String path = loc+"/"+name;
		File folder = new File(path);//find the main folder
		int count = 1;
		File subFolder;
		for(File i:folder.listFiles()){// get the subfolders
			subFolder = new File(path+"/"+i.getName());
			
			subs = new BufferedImage[subFolder.listFiles().length];
			conds.add(i.getName());
			for(File j:subFolder.listFiles()){
				
				try{
					//add the images in each folder
					subs[Integer.parseInt(j.getName().replace(".png",""))-1]=ImageIO.read(j);
				}
				catch(IOException e){}
			}
			//convert array to ArrayList
			ArrayList<BufferedImage> sub = new ArrayList<BufferedImage>();
			for(BufferedImage b:subs){
				sub.add(b);
			}
			pics.add(sub);
		}
		return pics;
	}
	public void gameScreens(){
		//Controls the different screens in the game
		if(myScreen.equals("game")){
			getSound("brainiac maniac").stop();
			gameMove();
		}
		else if(myScreen.equals("menu2")){
			//menu screen controlled by top right button
			getSound("grasswalk").stop();
			menu2();
		}
		else if(myScreen.equals("gameOver")){
			//game over message screen
			getSound("grasswalk").stop();
			lostGame();
		}
		else if(myScreen.equals("tryAgain")){
			//try again option screen
			tryAgainNotice();
		}
	}
	int finalWavecount = 400;
	
	public boolean checkGameOver(){
		//checks if zombies have crossed the final defense line
		for(Zombie each: inFieldZombie){
			return each.x<-100;
		}
		return false;
	}
	BufferedImage levelPic;
	
	//update the the level
	public void changeLevel(){
		if(finalWave.size()==0&&inFieldZombie.size()==0){//check is th finalWave is done 
			if(level<5){
				//advance to next level once all zombies are cleared
				level++;
				levelPic = levelPics[level-1];
				resetLevel();
				levelPicCount = 300;
			}
			else{
				//cleared all zombies in 5 levels
				getSound("grasswalk").stop();
				getSound("winMusic").play();
			}
		}
	}
	public void gameMove(){
		//controls game's main frame
		//ex. dropping suns, all plants/zombie functions
		changeLevel();
		if(!checkGameOver()){
			checkZombieDeath();
			for(PlantSlot each: toolbar){
				each.coolDown();
				//cooldown between each time a plant is selected
			}
			getLevel().countDown();
			//generate suns from sunflower
			for(int i=0;i<5;i++){
				for(int j=0;j<9;j++){
					//controls functions of each plant on the field
					if(field[i][j]!=null){
						if(field[i][j].name.equals("sunflower")){
							field[i][j].coolDown();
							//generate suns from sunflower
							if(field[i][j].tmpCD==0){
								addFlowerSun(fieldx[i][j],fieldy[i][j]);
							}
						}
						else if(field[i][j].name.equals("peashooter")){
							//shoots only when zombie appears in its lane
							for(Zombie each: inFieldZombie){
								if(fieldy[i][j]-each.y<=90 && fieldy[i][j]-each.y>=70){
									field[i][j].peashoot= true;
								}
							}
							if(field[i][j].peashoot){
								field[i][j].coolDown();
								field[i][j].peashoot = false;
							}
							//shoot bullet
							if(field[i][j].tmpCD<=0){
								addBullet(bulletId++,fieldx[i][j],fieldy[i][j],field[i][j].dmg);
							}
						}
						else if(field[i][j].name.equals("cherry bomb")){
							if(field[i][j].count==6 && field[i][j].condition.equals("normal")){
								//change display condition to explosion after cherry
								field[i][j].changeCondition("bomb");
								getSound("cherryBomb").play();
							}
							if(field[i][j].condition.equals("bomb")){
								if(field[i][j].count==0){
									for(Zombie each: inFieldZombie){
										//checks if zombies are within bombing range
										if(fieldy[i][j]-each.y<=180 && fieldy[i][j]-each.y>=-30 && fieldx[i][j]-each.x>=-75 && fieldx[i][j]-each.x<=215){
											each.hp=0;
											each.die=3;
										}
									}
								}
								else if(field[i][j].count==11){
									getSound("ignite").play();
									//remove cherry bomb (one time use)
									field[i][j]=null;
								}
							}
						}
						else if(field[i][j].name.equals("wall - nut")){
							if(field[i][j].hp<=38 && field[i][j].hp>20){
								//change display sprites when damaged to certain hp
								field[i][j].changeCondition("cracked1");
							}
							else if(field[i][j].hp<=20 && field[i][j].hp>0){
								field[i][j].changeCondition("cracked2");
							}
						}
						/*to add new plant*
						 *else if(field[i][j].name.equals("plant name")){
						 	    perform plant functions...
						  }*/
						  
						//controls functions of zombies on the field
						if(inFieldZombie.size()!=0){
							for(Zombie each:inFieldZombie){
								
								if(field[i][j]!=null&&field[i][j].getRect(fieldx[i][j],fieldy[i][j]).intersects(each.getRect())){
									//attacks plant it collides with
									field[i][j].hp = each.attack(field[i][j]);
									each.chompCD();
									if(each.chompCd==0){
										getSound("chomp").play();
									}
									if(field[i][j].hp<=0){
										//check if the plant is dead
										getSound("chomp").stop();
										for(Zombie z:inFieldZombie){
											if(z.getRect().intersects(field[i][j].getRect(fieldx[i][j],fieldy[i][j]))){
												z.alterCond("walk");
											}
										}
										field[i][j] = null;
									}
								}
							}
						}
					}		
				}
				Zombie delIndex=null;
				Bullet bulDel=null;
				if(allBullets.size()!=0){
					for(Bullet b:allBullets){
						if(inFieldZombie.size()!=0){
							for(Zombie z:inFieldZombie){
								//check for bullet collidion with zombie
								if(b.getRect().intersects(z.getFrontRect())){
									getSound("bulletHit").play();
									z.hp--;
									bulDel = b;
								}
							}
						}
					}
				}
				removeBullet(bulDel);	
			}
			if(currentZombies.size()==0 && inFieldZombie.size()==0){
				//all zombie prior to huge wave died
				//prepares for huge wave
				
				getLevel().waveCD();
				
			}
			dropSuns();
			bulletFunc();
			flowerSunFunc();
			checkPlantReset();
			
			if(zombieCount.size()>0){
				zombieUpdate(zombieCount,currentZombies);
				//getSound("brains").play();
			}
			else if(finalWavecount<=0&&inFieldZombie.size()==0&&finalCount.size()!=0){
				//prepares for final wave
				getSound("finalWave").play();
				finalW = true;
			}
			if(finalW&&finalCount.size()!=0){
				zombieUpdate(finalCount,finalWave);
			}
			checkZombieDeath();
			
			Rectangle menu2Rect = getRect(680,0,menu2.getWidth(),menu2.getHeight());
			if(checkCollide(mx,my,menu2Rect)){
				//if menu is selected
				selMenu2 = true;
				if(mousePressed){
					myScreen="menu2";
				}
			}
			else{
				selMenu2 = false;
				mousePressed = false;
			}
		}
		else{
			//game over
			myScreen = "gameOver";
			getSound("scream").play();
			getSound("brainiac maniac").loop();
		}
	}
	public void gameOverCD(){
		//countDown to display game over message
		if(gameOverTimer!=0){
			gameOverTimer--;
		}
		if(gameOverTimer==0){
			gameOverTimer=0;
		}
	}
	public void menu2(){
		//all methods that controls the menu screen
		Rectangle mmRect = getRect(283,375,mm.getWidth(),mm.getHeight());
		Rectangle btgRect = getRect(213,441,btg.getWidth(),btg.getHeight());
		Rectangle rLvlRect = getRect(283,333,rLvl.getWidth(),rLvl.getHeight());
		//main menu
		if(checkCollide(mx,my,mmRect)){
			if(getAlpha(mm,mx,my,283,375)==255){
				selMm = true;
				if(mousePressed){
					myScreen="mm";
				}
			}
			else{
				selMm = false;
			}
		}
		//back to game
		else if(checkCollide(mx,my,btgRect)){
			if(getAlpha(btg,mx,my,213,441)==255){
				selBtg = true;
				if(mousePressed){
					getSound("grasswalk").loop();
					myScreen="game";
				}
			}
			else{
				selBtg = false;
			}
		}
		//restart level
		else if(checkCollide(mx,my,rLvlRect)){
			if(getAlpha(rLvl,mx,my,283,333)==255){
				selRLvl = true;
				if(mousePressed){
					myScreen="game";
					resetLevel();
				}
			}
			else{
				selRLvl = false;
			}
		}
		else{
			selMm = false;
			selBtg = false;
			selRLvl = false;
			mousePressed = false;
		}
		
	}
	public void lostGame(){
		//zombies has crossed the final defense line
		gameOverCD();
		if(gameOverTimer==0){
			myScreen = "tryAgain";
		}
	}
	public void tryAgainNotice(){
		//after displaying game over message, a "try again" option is provided
		Rectangle tryRect = new Rectangle(283,343,203,40);
		if(checkCollide(mx,my,tryRect)){
			if(getAlpha(tryAgainSel,mx,my,283,343)==255){
				selTryAgain = true;
				if(mousePressed){
					myScreen="game";
					resetLevel();
				}
			}
			else{
				selTryAgain = false;
			}
		}
		else{
			selTryAgain = false;
			mousePressed = false;
		}
	}
	//plants-----------------------------------------------------------
	private void selectPlants(){
		//checks if each plant in toolbar is selected
    	for(int i=0;i<toolbar.size();i++){
    		if(checkCollide(mx,my,toolbar.get(i).getRect())){
    			if(canBuy(toolbar.get(i))){
    				selectedPlant = allPlants.get(i);
    				toolbar.get(i).selected=true;
    				getSound("seedLift").play();
    			}
    		}
    	}
    }
    public void checkPlantReset(){
		//check if plant resect has been clicked
		if(checkCollide(mx,my,resetRect)){
			if(mousePressed){
				//reset selected plant
				if(selectedPlant!=null){
					selectedPlant.tmpCD=0;
					selectedPlant = null;
				}
				
			}
		}
	}    
    private void addPlant(int col, int row){
    	//add the plant to the field
    	if(field[col][row]==null){
    		Plants tmp = selectedPlant;
    		field[col][row]=new Plants(tmp.name,tmp.hp,tmp.att,tmp.dmg,tmp.cd,tmp.cost,tmp.pics,tmp.conds);
    		selectedPlant = null;
    		money-= field[col][row].cost;
    		//deduct money
    		
    	}
    }
    //plant toolbar-------------------------------------------------------
    public boolean canBuy(PlantSlot plant){
    	//check if cooldown is over and has enough money
    	if(plant.canBuy() && money>=plant.cost){
    		return true;
    	}
    	return false;
    }
    //FALLING SUNS----------------------------------------------------------
    public void addSun(){
    	//add new falling sun
    	Sun newSun = new Sun(sunId++);
	   	suns.add(newSun);
	   	//cooldown before new sun is added
	   	flowerTimer=400;
    }
    public void dropSuns(){
    	//drop falling sun to field
    	if(flowerTimer>0){
    		flowerTimer--;
    	}
    	if(flowerTimer==0){
    		myEvents.addEvent(new AddSun());
    	}
    	for(int i=0;i<suns.size();i++){
    		suns.get(i).drop();
    		//every sun stays on the field for a limited time
    		suns.get(i).coolDown();
    		if(suns.get(i).cd==0){
    			myEvents.addEvent(new FadeSun(suns.get(i).getId()));
    		}
    			
    	}
    	
    }
    public void removeSun(int id){
    	//remove sun with given id
    	for (int i = 0; i < suns.size();i++){
    		if (suns.get(i).getId()==id){
    			suns.remove(i);
    		}
    	}
    }
    public void addMoney(){
    	//add money when sun is collected 
    	money+=25;
    }
    //SUNFLOWER SUNS--------------------------------------------------------
    public void addFlowerSun(int x, int y){
    	//sunflowers generate stationary suns (do not fall)
    	Sun newSun = new Sun(sunId++,x,y);
    	flowerSuns.add(newSun);
    }
    public void removeFlowerSun(int id){
    	//find matching id and remove sun
    	for (int i = 0; i < flowerSuns.size();i++){
    		if (flowerSuns.get(i).getId()==id){
    			flowerSuns.remove(i);
    		}
    	}
    }
    public void flowerSunFunc(){
    	//suns only stays on field for limited time
    	for(int i=0;i<flowerSuns.size();i++){
    		flowerSuns.get(i).coolDown();
    		if(flowerSuns.get(i).cd==0){
    			try{
    				myEvents.addEvent(new FadeFlowerSun(suns.get(i).getId()));
    			}
    			catch(IndexOutOfBoundsException e){
    			}
    		}
    			
    	}
    }
    //PEASHOOTER------------------------------------------------------------
    public void addBullet(int id, int x, int y, int dmg){
    	//add new bullet to peashooter
    	Bullet newBullet = new Bullet(id,x,y,dmg);
    	allBullets.add(newBullet);
    }
    public void removeBullet(Bullet b){
    	//remove Bullet b from allBullets
    	allBullets.remove(b);
    }
    public void removeBullet(int id){
    	//remove bullet with given id
    	Bullet delBul= null;
	    for (Bullet each: allBullets){
	    	if(each.id==id){
	    		delBul = each;
	    	}
	   	}
	  	allBullets.remove(delBul);
    	
    }
    public void bulletFunc(){
    	//checks for collision with zombie or if it goes off screen
    	for(Bullet each: allBullets){
    		each.shoot();
    		if(each.x>=800){
    		//removes bullet if it goes off screen or collides with zombie
    			myEvents.addEvent(new RemoveBullet(each.getId()));
    		}
    	}
    }
    //ZOMBIES--------------------------------------------------------------
    public void addZombie(String name,ArrayList<Zombie> loc){
    	//add given type(name) of zombie to loc
    	Zombie tmp;
    	for(Zombie each: allZombies){
    		if(each.name.equals(name)){
    			tmp = new Zombie(each.name, each.hp, each.dmg, each.speed, each.pics, each.conds);
    			loc.add(tmp);
    		}
    	}
    }
   public void waveFunc(int level){
    	//gets all zombies in the large wave
    	for(Levels each:allLevels){
			if(each.getLevel()==level){
				for(int i=0;i<each.finalWave[0];i++){
					addZombie("zombie",finalWave);
				}
				for(int j=0;j<each.finalWave[1];j++){
					addZombie("conehead",finalWave);
				}
				for(int k=0;k<each.finalWave[2];k++){
					addZombie("buckethead",finalWave);
				}
			}
    	}
    	//zombies appear in random order
    	Collections.shuffle(finalWave);
    }	 
    public void zombieFunc(int level){  	
		//add all zombies for this level to currentZombie list
		for(Levels each:allLevels){
			if(each.getLevel()==level){
				//add amount of zombies according to text file
				for(int i=0;i<each.zombieNums[0];i++){
					addZombie("zombie",currentZombies);
				}
				for(int j=0;j<each.zombieNums[1];j++){
					addZombie("conehead",currentZombies);
				}
				for(int k=0;k<each.zombieNums[2];k++){
					addZombie("buckethead",currentZombies);
				}
			}
		}
		//zombies appear in random order
		Collections.shuffle(currentZombies);
		waveFunc(level);
    }
    boolean finalW = false;
    
    public void zombieUpdate(ArrayList<Integer>zCnt, ArrayList<Zombie>allZomb){
		//update zombie movements in the field
		if(zCnt.size()!=0){
			int tmpInt = zCnt.get(0);
			if(tmpInt==0){
				//add the zombie to the field after a certain time
				//time interval between each entrance of the zombies
				addZombie(allZomb.get(0).name,inFieldZombie);
				getSound("brains").play();
				allZomb.remove(0);
				zCnt.remove(0);
			}
			else{
				//remove the count
				zCnt.add(1,tmpInt-1);
				zCnt.remove(0);
			}	
		}
		
		
		
	}
	
	private void checkZombieDeath(){
		//check if zombie is dead
    	for(int i=0;i<inFieldZombie.size();i++){
    		if(inFieldZombie.get(i).death){
    			inFieldZombie.remove(i);
    			//remove the zombie if its dead
    		}
    	}
    	
    }
    public Levels getLevel(){
    	// get the current level 
    	for(Levels lvl:allLevels){
    		if(lvl.getLevel()==level){
    			curLevel=lvl;
    		}
    	}
    	return curLevel;
    }
	//---------------------------------------------------------------------
	
	public Rectangle getRect(int x, int y, int w, int h){
		//given x,y,w,h, return rectangle
		return new Rectangle(x,y,w,h);
	}
	public boolean checkCollide(int mx, int my, Rectangle rect){
		//if cursor collides with the given rectangle
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
    	//selecting plants from toolbar and adding plants to the field
    	selectPlants();
    	if(xy!=null&&xy[0]!=-1&&selectedPlant!=null){
    		//adding plants to the field
			addPlant(xy[0],xy[1]);
			getSound("plant").play();
    	}
		//collecting suns---------------------------------------------------
		for(int i=0;i<suns.size();i++){
			if(suns.get(i).checkCollide(mx,my)){
				getSound("collectSun").play();
	    		myEvents.addEvent(new CollectSun(suns.get(i).getId()));
	    	}
    	}
    	//collect sunflower generated suns---------------------------------------
    	for(int i=0;i<flowerSuns.size();i++){
    		if(flowerSuns.get(i).checkCollide(mx,my)){
    			getSound("collectSun").play();
    			myEvents.addEvent(new CollectFlowerSun(flowerSuns.get(i).getId()));
    		}
    	}
    }
    
	//------------Mouse Motion Listener ------------------------------------------
	
	public void mouseDragged(MouseEvent e){}
    public void mouseMoved(MouseEvent e){
    	//get cursor position
    	mx = e.getX();
		my = e.getY();
	}
    private int[] xy = new int[2];
    public void fieldCollide(){
    	//check cursor collision with the field
    	xy[0]=-1;
    	for(int i=0;i<5;i++){
    		for(int j=0;j<9;j++){
    			if(checkCollide(mx,my,allField[i][j])){
    				xy[0]=i;
    				xy[1]=j;
    			}
    		}
    	}	
    }
    
    //---------------------------------------------------------------------
    public void resolveEvents(){
    	//process events in order
    	//eg. addSun, addBullet, removeSun etc.
    	myEvents.resolveEvents();
    }
    static int a = 0;
    
	public void paintComponent(Graphics g){
		//displays all graphics of the game
		
		//draws each plant in toolbar first
		g.drawImage(psPlants,0,0,this);
		
		g.drawImage(back,0,0,this);
		//draw background
		
        if(levelPic!=null){
        	//display current level
        	if(levelPicCount>200){
        		levelPicCount--;
        		return;
        	}
        	else if(levelPicCount>0){
        		g.drawImage(levelPic,0,0,this);
        		levelPicCount--;
        		return;
        	}
        	else if(levelPicCount>5){
        		//cleared game
        		g.drawImage(winGame,0,0,this);
        	}
        	else{
        		levelPic = null;
        	}
        }
        
		for(PlantSlot each:toolbar){
	    	
	    	if(each.tmpCD!=0){
	    		//cooldown display for plantslot
	    		g.drawImage(shade,each.x,9+(71*each.tmpCD)/each.cd,this);
	    	}
	    	if(canBuy(each)){
	    		//draw bright image of plant if canBuy
	    		g.drawImage(each.pic,each.x,each.y,this);
	    	}
	    	else if(!canBuy(each) && each.tmpCD==0){
	    		
	    		g.drawImage(shade,each.x,each.y,this);
	    	}
	    }
	    //redraw background to cover the shade from plant slot cooldown
	    g.drawImage(back,0,0,this);
	    
	    //display lives, and score-------------------------------------------------
        g.setFont(new Font("Calibri",Font.BOLD,18));
        g.setColor(new Color(0,0,0));
        g.drawString(String.format("%d",money),40,78);
        
        g.setFont(new Font("Calibri",Font.LAYOUT_LEFT_TO_RIGHT,12));
		
        
        
		if(myScreen.equals("game")){
			fieldCollide();
			if(selectedPlant!=null&&xy[0]!=-1&&field[xy[0]][xy[1]]==null){
				//display selected plant in field as cursor passes through
				g.drawImage(selectedPlant.startPic,fieldx[xy[0]][xy[1]],fieldy[xy[0]][xy[1]],this);
			}
			//draw the plants 
			for(int x=0;x<5;x++){
				for(int y=0;y<9;y++){
					if(field[x][y]!=null){
						if(field[x][y].condition.equals("bomb")){
							//cherry bomb as area of effect; (x,y) values are different
							g.drawImage(field[x][y].drawPics.get(field[x][y].count),fieldx[x][y]-50,fieldy[x][y]-50,this);
						}
						else{
							//display plants in motion at its destination
							g.drawImage(field[x][y].drawPics.get(field[x][y].count),fieldx[x][y],fieldy[x][y],this);
						}
						field[x][y].countUpdate();
						// update which picture to draw
					}
				}
			}
			//draw bullets
			for(Bullet each: allBullets){
				g.drawImage(each.pic,each.x+60,each.y+3,this);
				if(inFieldZombie.size()!=0 && allBullets.size()!=0){
					for(Zombie z:inFieldZombie){
						if(each.getRect().intersects(z.getFrontRect())){
							//draw bullet splitting when it collides with zombie
							g.drawImage(bulletHit,each.x+50,each.y,this);
						}
					}
				}
			}
			//draw falling suns
			for(Sun each: suns){
				g.drawImage(each.pics[each.count],each.getX(),each.getY(),this);
		        each.countUpdate();
			}
		    //draw suns generated by sunflower
		    for(Sun each: flowerSuns){
		        g.drawImage(each.pics[each.count],each.getX(),each.getY(),this);
		        each.countUpdate();
		    }
		    
		    for(PlantSlot each:toolbar){
		    	//display name of plant if cursor is over the plant
	        	if(checkCollide(mx,my,each.getRect())){
	        		g.setColor(new Color(255,249,144));
	        		g.fillRect(each.x-10,each.y+65,75,13);
	        		g.setColor(new Color(0,0,0));
	        		g.drawString(String.format("%s",each.name),each.x-5,each.y+75);
				}	
	        }
	        if(checkCollide(mx,my,resetRect)){
	        	//display resetRect
	        	g.drawImage(resetSel,418,6,this);
	        	g.setColor(new Color(255,249,144));
	        	g.fillRect(415,43,30,13);
        		g.setColor(new Color(0,0,0));
        		g.drawString(String.format("reset"),415,53);
	        }
	        //zombie----------------------------------------------------------------------------
	        if(inFieldZombie.size()!=0){
	        	for(Zombie each:inFieldZombie){
	        		//draw zombies in motion
	        		each.move();
	        		g.drawImage(each.pic,each.x,each.y,this);
	        		
	        		if(each.actionPic!=null){
	        			g.drawImage(each.actionPic,each.ax,each.y,this );
	        		}
	        	}
	        	
	      	}
	      	if(finalWavecount>0&&getLevel().fwTimer<=749 && getLevel().fwTimer>=349){
	      		finalWavecount--;
	      		if(finalWavecount>200){
	      			//display large wave message
	      			g.drawImage(waveNote,0,0,this);
	      			return;
	      		}
	      		else{
	      			//display final wave message
	      			g.drawImage(finalWavePic,0,0,this);
	      			getSound("finalWave").play();
	      			return;
	      		}
		      }
	      	
	        if(selMenu2){
	        	g.drawImage(menu2,680,0,this);
	        }
		}
        else if(myScreen.equals("menu2")){
        	//display each selections (if selected) in menu2
        	g.drawImage(menuOption,175,50,this);
        	if(selMm){
        		//main menu selection
        		g.drawImage(mm,283,375,this);
        	}
        	else if(selBtg){
        		//back to game selection
        		g.drawImage(btg,213,441,this);
        	}
        	else if(selRLvl){
        		//restart level selection
        		g.drawImage(rLvl,283,333,this);
        	}
        }
        else if(myScreen.equals("gameOver")){
        	//display game over message
        	g.drawImage(endGame,0,0,this);
        }
        else if(myScreen.equals("tryAgain")){
        	//display "try again" option
        	g.drawImage(tryAgain,0,0,this);
        	if(selTryAgain){
        		g.drawImage(tryAgainSel,283,343,this);
        	}
        }
        
	}	
		
}