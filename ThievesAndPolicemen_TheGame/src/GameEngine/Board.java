package GameEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class Board extends Canvas {

	/*******************/
	
	int gameStep = 0;
	int k = 5;
	int Delta = 500;
	int T = 100;
	int ThiefPoints = 0;
	int PolicemenPoints = 0;
	
	boolean IsCaught = false;
	boolean IsEscaped = false;
	
	MainGame m;
	PolicemenGroup PG;
	ThiefGroup TG;
	List<Group> Groups;
	
	ExecutorService pool = null;
	
	List<MainGame> GameList;
	
	/*******************/
	
	private final int ELEMENT_SIZE = 25;
    private final int BOARD_WIDTH = ELEMENT_SIZE * m._ne;
    private final int BOARD_HEIGHT = ELEMENT_SIZE * m._ne;
    private final int DELAY = 1000;
    private final int ALL_FIELDS = m._ne * m._ne;
    
    /*******************/
    
	private int x[] = new int[ALL_FIELDS];
	private int y[] = new int[ALL_FIELDS];
	private int xMoves[] = new int[5 * (m._numberOfPolicemen + m._numberOfThieves)];
	private int yMoves[] = new int[5 * (m._numberOfPolicemen + m._numberOfThieves)];
    
    private boolean inGame = true;

    private Image floor;
    private Image wall;
    private Image gateway;
    private Image obstacle;
    private Image policeman;
    private Image policeman1;
    private Image policeman2;
    private Image policeman3;
    private Image policeman4;
    private Image policeman5;
    
    private Image policemanArea;
    private Image thief;
    private Image moveStop;
    private Image moveLeft;
    private Image moveUp;
    private Image moveRight;
    private Image moveDown;

    private Display display;
    private Shell shell;
    private Runnable runnable;

    /*******************/
    
    public Board(Shell shell) {
        super(shell, SWT.DOUBLE_BUFFERED);
        
        m = new MainGame();
        PG = new PolicemenGroup(m); // PolicemenGroup
        TG = new MyThiefGroup(m); // ThiefGroup
        Groups = new ArrayList<Group>();
        Groups.add(PG);
    	Groups.add(TG);
        GameList = new ArrayList<MainGame>();
        pool = Executors.newFixedThreadPool(Groups.size());
        
        this.shell = shell;
        display = shell.getDisplay();

        setSize(BOARD_WIDTH, BOARD_HEIGHT);

        this.addPaintListener(new BoardPaintListener());

        Color col = new Color(shell.getDisplay(), 255, 255, 255);
        this.setBackground(col);
        col.dispose();

        ImageData floorImageData = new ImageData("./floor.png");
        floor = new Image(display, floorImageData);

        ImageData wallImageData = new ImageData("./wall.png");
        wall = new Image(display, wallImageData);
        
        ImageData obstacleImageData = new ImageData("./obstacle.png");
        obstacle = new Image(display, obstacleImageData);
        
        ImageData policemanImageData = new ImageData("./policeman.png");
        policeman = new Image(display, policemanImageData);
        
        ImageData policeman1ImageData = new ImageData("./policeman1.png");
        policeman1 = new Image(display, policeman1ImageData);
        
        ImageData policeman2ImageData = new ImageData("./policeman2.png");
        policeman2 = new Image(display, policeman2ImageData);
        
        ImageData policeman3ImageData = new ImageData("./policeman3.png");
        policeman3 = new Image(display, policeman3ImageData);
        
        ImageData policeman4ImageData = new ImageData("./policeman4.png");
        policeman4 = new Image(display, policeman4ImageData);
        
        ImageData policeman5ImageData = new ImageData("./policeman5.png");
        policeman5 = new Image(display, policeman5ImageData);
        
        ImageData policemanAreaImageData = new ImageData("./policemanArea.png");
        policemanArea = new Image(display, policemanAreaImageData);
        
        ImageData thiefImageData = new ImageData("./thief.png");
        thief = new Image(display, thiefImageData);
        
        ImageData moveStopImageData = new ImageData("./moveStop.png");
        moveStop = new Image(display, moveStopImageData);
        
        ImageData moveLeftImageData = new ImageData("./moveLeft.png");
        moveLeft = new Image(display, moveLeftImageData);
        
        ImageData moveUpImageData = new ImageData("./moveUp.png");
        moveUp = new Image(display, moveUpImageData);
        
        ImageData moveRightAreaImageData = new ImageData("./moveRight.png");
        moveRight = new Image(display, moveRightAreaImageData);
        
        ImageData moveDownImageData = new ImageData("./moveDown.png");
        moveDown = new Image(display, moveDownImageData);
        
        

        initGame(shell.getDisplay());
    }


    public void initGame(final Display display)
    {
    	int z= 0;
    	for(int i = 0; i < m._ne; i++)
    	{
    		for(int j = 0; j < m._ne; j++)
    		{
    			x[z] = 2 + j*ELEMENT_SIZE;
    			y[z] = 2 + i*ELEMENT_SIZE;
    			z++;
    		}
    	}
    	
    	z = 0;
    	
    	for(int i = 0; i < (m._numberOfPolicemen + m._numberOfThieves); i++)
    	{
    		for(int j = 0; j < 5; j++)
    		{
    			xMoves[z] = 640 + j*ELEMENT_SIZE;
    			yMoves[z] = 25 + i*ELEMENT_SIZE;
    			z++;
    		}
    	}

        runnable = new Runnable() 
        {
        	public void run() 
        	{
        		
        		display.timerExec(700, this);
	        	redraw();
        		
        		inGame = !IsCaught && !IsEscaped && (gameStep < T);
        		if(inGame)
        		{
	        			m.ElementMovement();
	        			m.RedrawBoard();
	        			if(gameStep % k == 0)
	        			{
	        				if (gameStep != 0)
	        				{
	        					for(Group g : Groups)
	        					{
	        						g.getHistory(cloneList(GameList));
	        					}
	        				}
	        				
	        				try {
								pool.invokeAll(Groups, Delta, TimeUnit.MILLISECONDS);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
	        			}
	        			
	        			m.ReloadBoard(gameStep % k);
	        			m.RedrawBoard();
	        			
	        			//System.out.println(m.toString());
	        			
	        			IsCaught = m.CheckIfCaught();
	        			
	        			IsEscaped = m.CheckIfEscaped();
	        			
	        			GameList.add(new MainGame(m));
	        			
	        			if(IsCaught)
	        	        ThiefPoints = gameStep;
	        	    		
	        	    	if(IsEscaped)
	        	    		ThiefPoints = 2*T - gameStep - 1;
	        	    	else if(!IsCaught)
	        	    		ThiefPoints = gameStep;
	        	    			
	        	    	PolicemenPoints = -ThiefPoints;
	        			
	        			
        		}
        		
        		
	        	gameStep++;
        	}
        };
        
        display.timerExec(DELAY, runnable);
    }
    
    private static List<MainGame> cloneList(List<MainGame> list)
	{
		List<MainGame> clone = new ArrayList<MainGame>(list.size());
		for(MainGame item : list)
			clone.add(new MainGame(item));
		
		return clone;
	}


    private class BoardPaintListener implements PaintListener {

        public void paintControl(PaintEvent e) {

            Color col = new Color(shell.getDisplay(), 255, 255, 255);
            e.gc.setBackground(col);
            col.dispose();

            e.gc.setAntialias(SWT.ON);

            if (inGame) {
                drawObjects(e);
            } else {
                gameOver(e);
            }

            e.gc.dispose();

        }
    }


    public void drawObjects(PaintEvent e)
    {
    	List<Integer> policemanAreaList = new ArrayList<Integer>();
    	int z = 0;
    	for(int i = 0; i < m._ne; i++)
    	{
    		for(int j = 0; j < m._ne; j++)
    		{
    			boolean someFlag = policemanAreaList.contains(z);
    			
    			if(!someFlag)
    			{
	    			if(m._board[i][j] == 2) 
	    			{
	    				int k = 1;
	    				for(Position p : m.PolicemenDict.values())
	    				{
	    					if(p.x == i && p.y == j)
	    					{
	    						if(k == 1)
	    						{
		    						e.gc.drawImage(policeman1, x[z], y[z]);
		    						break;
	    						}
	    						if(k == 2)
	    						{
		    						e.gc.drawImage(policeman2, x[z], y[z]);
		    						break;
	    						}
	    						if(k == 3)
	    						{
		    						e.gc.drawImage(policeman3, x[z], y[z]);
		    						break;
	    						}
	    						if(k == 4)
	    						{
		    						e.gc.drawImage(policeman4, x[z], y[z]);
		    						break;
	    						}
	    						if(k == 5)
	    						{
		    						e.gc.drawImage(policeman5, x[z], y[z]);
		    						break;
	    						}
	    					}
	    					k++;
	    				}
	    				
	    				if(m._board[i - 1][j] == 0)
	    				{
	    					policemanAreaList.add(z - m._ne);
	    					e.gc.drawImage(policemanArea, x[z], y[z - m._ne]);
	    				}
	    				if(m._board[i + 1][j] == 0)
	    				{
	    					policemanAreaList.add(z + m._ne);
	    					e.gc.drawImage(policemanArea, x[z], y[z + m._ne]);
	    				}
	    				if(m._board[i][j - 1] == 0)
	    				{
	    					policemanAreaList.add(z - 1);
	    					e.gc.drawImage(policemanArea, x[z - 1], y[z]);
	    				}
	    				if(m._board[i][j + 1] == 0)
	    				{
	    					policemanAreaList.add(z + 1);
	    					e.gc.drawImage(policemanArea, x[z + 1], y[z]);
	    				}
	    			}
	    			if(m._board[i][j] == 0 || m._board[i][j] == 5)
	    			{
	    				e.gc.drawImage(floor, x[z], y[z]);
	    			}
	    			if(m._board[i][j] == 1)
	    			{
	    				e.gc.drawImage(thief, x[z], y[z]);
	    			}
	    			
	    			if(m._board[i][j] == 3)
	    			{
	    				e.gc.drawImage(obstacle, x[z], y[z]);
	    			}
	    			if(m._board[i][j] == 4)
	    			{
	    				e.gc.drawImage(wall, x[z], y[z]);
	    			}
    			}
    			
    			z++;
    		}
    	}
    	
    	
    	
    	//******************************
    	
    	switch((gameStep - 1) % k)
    	{
    	case 0:
    		e.gc.drawImage(moveDown, 640 + ELEMENT_SIZE * 0, 0);
    		break;
    		
    	case 1:
    		e.gc.drawImage(moveDown, 640 + ELEMENT_SIZE * 1, 0);
    		break;
    		
    	case 2:
    		e.gc.drawImage(moveDown, 640 + ELEMENT_SIZE * 2, 0);
    		break;
    		
    	case 3:
    		e.gc.drawImage(moveDown, 640 + ELEMENT_SIZE * 3, 0);
    		break;
    		
    	case 4:
    		e.gc.drawImage(moveDown, 640 + ELEMENT_SIZE * 4, 0);
    		break;
    	}
    	
    	//******************************
    	
    	
    	
    	z = 0;
    	int numer = 1;
    	List<Integer> moves = new ArrayList<Integer>();
    	for(Person p : m.Persons.keySet())
		{
    		String msg = "";
    		if(p instanceof Thief)
    			msg = "T";
    		else
       	 		msg = "P" + (numer++);

            Font font = new Font(e.display, "Helvetica",
                    12, SWT.NORMAL);
            Color black = new Color(shell.getDisplay(),
                    0, 0, 0);

            Point size = e.gc.textExtent (msg);
                    
            e.gc.setForeground(black);
            e.gc.setFont(font);
            e.gc.drawText(msg, xMoves[z] - 20, yMoves[z]);
    		
    		moves = m.get(p);
    		if(moves != null)
    		{
	    		for(int move : moves)
	    		{
	    			if(move == 5)
	    				e.gc.drawImage(moveStop, xMoves[z], yMoves[z]);
	    			else if(move == 4)
	    				e.gc.drawImage(moveLeft, xMoves[z], yMoves[z]);
	    			else if(move == 8)
	    				e.gc.drawImage(moveUp, xMoves[z], yMoves[z]);
	    			else if(move == 6)
	    				e.gc.drawImage(moveRight, xMoves[z], yMoves[z]);
	    			else if(move == 2)
	    				e.gc.drawImage(moveDown, xMoves[z], yMoves[z]);
	    			else
	    				e.gc.drawImage(moveStop, xMoves[z], yMoves[z]);
	    				
	    				
	    			z++;
	    		}
	    		if(moves.size() != 5)
	    		{
	    			for(int i = 0; i < 5 - moves.size(); i++)
	    			{
	    				e.gc.drawImage(moveStop, xMoves[z], yMoves[z]);
	    				z++;
	    			}
	    		}
    		}
    		else if(moves == null)
    		{
    			for(int i = 0; i < 5; i++)
    			{
    				e.gc.drawImage(moveStop, xMoves[z], yMoves[z]);
    				z++;
    			}
    		}

    		
		}
    	
    	String msg = "Punkty Policjantów: " + PolicemenPoints;
    	Font font = new Font(e.display, "Helvetica",
                12, SWT.NORMAL);
        Color black = new Color(shell.getDisplay(),
                0, 0, 0);

        Point size = e.gc.textExtent (msg);
        e.gc.setForeground(black);
        e.gc.setFont(font);
        e.gc.drawText(msg, 620, 300);
        
        msg = "Punkty Z³odzieja: " + ThiefPoints;
        e.gc.setForeground(black);
        e.gc.setFont(font);
        e.gc.drawText(msg, 620, 350);
        
        font.dispose();
        e.gc.dispose();
    }

    public void gameOver(PaintEvent e) {
    	int z = 0;
    	for(int i = 0; i < m._ne; i++)
    	{
    		for(int j = 0; j < m._ne; j++)
    		{
    			if(m._board[i][j] == 0 || m._board[i][j] == 5)
    			{
    				e.gc.drawImage(floor, x[z], y[z]);
    			}
    			if(m._board[i][j] == 1)
    			{
    				e.gc.drawImage(thief, x[z], y[z]);
    			}
    			if(m._board[i][j] == 2)
    			{
    				e.gc.drawImage(policeman, x[z], y[z]);
    				
    				//policemanArea
    			}
    			if(m._board[i][j] == 3)
    			{
    				e.gc.drawImage(obstacle, x[z], y[z]);
    			}
    			if(m._board[i][j] == 4)
    			{
    				e.gc.drawImage(wall, x[z], y[z]);
    			}
    			
    			z++;
    		}
    	}
    	
    	
    	
    	z = 0;
    	int numer = 1;
    	List<Integer> moves = new ArrayList<Integer>();
    	for(Person p : m.Persons.keySet())
		{
    		String msg = "";
    		if(p instanceof Thief)
    			msg = "T";
    		else
       	 		msg = "P" + (numer++);

            Font font = new Font(e.display, "Helvetica",
                    12, SWT.NORMAL);
            Color black = new Color(shell.getDisplay(),
                    0, 0, 0);

            Point size = e.gc.textExtent (msg);
                    
            e.gc.setForeground(black);
            e.gc.setFont(font);
            e.gc.drawText(msg, xMoves[z] - 20, yMoves[z]);
    		
    		moves = m.get(p);
    		if(moves != null)
    		{
	    		for(int move : moves)
	    		{
	    			if(move == 5)
	    				e.gc.drawImage(moveStop, xMoves[z], yMoves[z]);
	    			else if(move == 4)
	    				e.gc.drawImage(moveLeft, xMoves[z], yMoves[z]);
	    			else if(move == 8)
	    				e.gc.drawImage(moveUp, xMoves[z], yMoves[z]);
	    			else if(move == 6)
	    				e.gc.drawImage(moveRight, xMoves[z], yMoves[z]);
	    			else if(move == 2)
	    				e.gc.drawImage(moveDown, xMoves[z], yMoves[z]);
	    			else
	    				e.gc.drawImage(moveStop, xMoves[z], yMoves[z]);
	    				
	    				
	    			z++;
	    		}
	    		if(moves.size() != 5)
	    		{
	    			for(int i = 0; i < 5 - moves.size(); i++)
	    			{
	    				e.gc.drawImage(moveStop, xMoves[z], yMoves[z]);
	    				z++;
	    			}
	    		}
    		}
    		else if(moves == null)
    		{
    			for(int i = 0; i < 5; i++)
    			{
    				e.gc.drawImage(moveStop, xMoves[z], yMoves[z]);
    				z++;
    			}
    		}

    		
		}
    	
    	String msg = "Punkty Policjantów: " + PolicemenPoints;
    	Font font = new Font(e.display, "Consolas",
                12, SWT.NORMAL);
        Color black = new Color(shell.getDisplay(),
                0, 0, 0);

        Point size = e.gc.textExtent (msg);
        e.gc.setForeground(black);
        e.gc.setFont(font);
        e.gc.drawText(msg, 620, 300);
        
        msg = "Punkty Z³odzieja: " + ThiefPoints;
        e.gc.setForeground(black);
        e.gc.setFont(font);
        e.gc.drawText(msg, 620, 350);
        
        font.dispose();
        e.gc.dispose();
        pool.shutdown();
    }
}
