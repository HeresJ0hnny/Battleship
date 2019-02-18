/**
 * Runs Battleship and the graphics.
 * See README for controls
 *
 * @author Jonathan Shi
 * @version 13 December 2018
 */
import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;
public class Battleship extends JPanel implements ActionListener, MouseListener
{   
    private BattleshipBoard[] players;
    private Ship[][] ships;
    private String mode;
    private boolean ai;
    private int turn;
    private int row;
    private int column;
    private int shipNum;
    private ArrayList<Integer> prevShipNum;
    private int[][] hits;
    private int numHits;
    private int[][] weirdHits;
    private int numWeird;
    
    private int[] select;
    private int[][] choose;
    private int[] shot;
    private int sinking;
    private int sinkingAi;
    
    private JFrame frame;
    private int height;
    private int width;
    private int topOffset;
    private int sideOffset;
    private final Color BG_COLOR = new Color(56,178,209);
    private final int[] SHIP_LENGTH = new int[] {5,4,3,3,2,0};
    private final String[] SHIP_NAME = new String[] {"Aircraft Carrier","Battleship","Submarine","Destroyer","Patrol Boat",""};
    private final int MIN = Integer.MIN_VALUE;
    /**
     * Initializes all the values for the Battleship game.
     */
    public Battleship()
    {
        BattleshipBoard player1 = new BattleshipBoard(1);
        BattleshipBoard player2 = new BattleshipBoard(2);
        players = new BattleshipBoard[] {player1,player2};
        Ship[] ships1 = new Ship[5];
        Ship[] ships2 = new Ship[5];
        ships = new Ship[][] {ships1,ships2};
        select = new int[] {0,0,0,0,0};
        choose = new int[][] {{-1,-1},{-1,-1},{-1,-1},{-1,-1}};
        shot = new int[] {-1,-1};
        mode = "start";
        ai = false;
        turn = 1;
        shipNum = 0;
        prevShipNum = new ArrayList<Integer>();
        hits = new int[][] {{-1,-1}};
        numHits = 0;
        weirdHits = new int[][] {{-1,-1}};
        numWeird = 0;
        sinking = MIN;
        sinkingAi = MIN;
    }
    
    /**
     * Creates the window for the game's graphics.
     */
    public void window()
    {
        frame = new JFrame();
        height = frame.getContentPane().getHeight();
        width = frame.getContentPane().getWidth();
        setBackground(BG_COLOR);
        frame.getContentPane().add(this);
        frame.getContentPane().setPreferredSize(new Dimension(1100, 1120));
        frame.setLocation(0, 0);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addMouseListener(this);
        frame.pack();
        frame.setVisible(true);
        topOffset = frame.getHeight() - frame.getContentPane().getHeight();
        sideOffset = frame.getWidth() - frame.getContentPane().getWidth();
    }
    
    /**
     * Displays a board and its contents on the screen.
     * @param board  the BattleshipBoard object to display
     * @param player  the player number that is viewing the board
     * @param g  the screen's graphics
     */
    public void drawBoard(BattleshipBoard board, int player, Graphics g)
    {
        int bottom = 1-Math.abs(board.getPlayer()-player);
        int boxSide = height/21;
        for (int r = 0; r < 10; r++)
        {
            for (int c = 0; c < 10; c++)
            {
                int space = board.getBoard(board.getPlayer() != player)[r][c];
                if (space == 0)
                {
                    g.setColor(BG_COLOR);
                }
                else if (space == 1)
                {
                    g.setColor(Color.WHITE);
                }
                else if (space == 2)
                {
                    g.setColor(Color.LIGHT_GRAY);
                }
                else if (space == 3)
                {
                    g.setColor(Color.RED);
                }
                g.fillRect(width/2+(c-5)*boxSide+1,(2*r+1)*boxSide/2+1 + bottom*(boxSide*10+5),boxSide-1,boxSide-1);
            }
        }
    }
    
    /**
     * Creates the Ship objects and adds them to a list of the ships
     * @param end1  the coordinates of the first end of the ship
     * @param end2  the coordinates of the second end of the ship
     */
    public void newShip(int[] end1,int[] end2)
    {
        Ship ship = new Ship(end1,end2,SHIP_NAME[shipNum]);
        for (int[] i : ship.getSegs())
        {
            players[turn-1].set(i[0],i[1],2);
        }
        ships[turn-1][shipNum] = ship;
        if (shipNum < ships[turn-1].length) shipNum++;
        if (prevShipNum.size() > 0)
        {
            shipNum = prevShipNum.remove(0);
        }
        if (shipNum >= ships[turn-1].length)
        {
            shipNum = ships[turn-1].length+1;
        }
        repaint();
    }
    
    /**
     * Draws everything on the game screen
     * @param g  the screen's graphics
     */
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        height = frame.getContentPane().getHeight();
        width = frame.getContentPane().getWidth();
        int boxSide = height/21;
        g.setColor(Color.BLACK);
        
        for (int i = boxSide/2; i < boxSide*20; i += boxSide)
        {
            g.drawLine((width-boxSide*10)/2,i,(width+boxSide*10)/2,i);
            if (i == 21*boxSide/2) i += 5;
        }
        for (int i = (width-boxSide*10)/2 + boxSide; i < (width+boxSide*10)/2; i += boxSide)
        {
            g.drawLine(i,boxSide/2,i,boxSide*21/2);
            g.drawLine(i,boxSide*21/2+5,i,boxSide*41/2+5);
        }
        g.setColor(Color.BLUE);
        g.drawRect((width-boxSide*10)/2,boxSide/2,boxSide*10,boxSide*10);
        g.drawRect((width-boxSide*10)/2,boxSide*21/2+5,boxSide*10,boxSide*10);
        
        drawBoard(players[0],turn,g);
        drawBoard(players[1],turn,g);
        
        if (select[4] == height*width)
        {
            g.setColor(new Color(255,255,0,127));
            g.fillRect(select[0],select[1],select[2],select[3]);
        }
        else
        {
            select = new int[] {0,0,0,0,0};
            choose = new int[][] {{-1,-1},{-1,-1},{-1,-1},{-1,-1}};
        }

        if (sinking >= 0)
        {
            g.setColor(Color.RED);
            g.setFont(new Font("SansSerif",Font.PLAIN,30));
            g.drawString("You sank their",(width+11*boxSide)/2,boxSide);
            g.drawString(SHIP_NAME[sinking] + "!", (width+11*boxSide)/2,2*boxSide);
            ships[2-turn][sinking] = null;
            sinking = MIN;
            boolean end = true;
            for (Ship boat : ships[2-turn]) 
            {
                if (boat != null) 
                {
                    end = false;
                    break;
                }
            }
            if (end)
            {
                mode = "finished";
            }
        }
        
        if (sinkingAi >= 0 && ai)
        {
            ships[0][sinkingAi] = null;
            sinkingAi = MIN;
            boolean end = true;
            for (Ship boat : ships[0]) 
            {
                if (boat != null) 
                {
                    end = false;
                    break;
                }
            }
            if (end)
            {
                mode = "finished" + "AI";
            }
        }
        
        if (mode.equals("finished") || mode.equals("finished"+"AI"))
        {
            g.setColor(new Color(0, 255, 0));
            g.setFont(new Font("SansSerif",Font.BOLD,30));
            if (mode.equals("finished")) g.drawString("Player " + turn + " wins!",(width+11*boxSide)/2,height/2);
            else g.drawString("AI wins!",(width+11*boxSide)/2,height/2);
        }
        
        g.setColor(Color.LIGHT_GRAY);
        if (mode.equals("place") || mode.equals("shoot") || mode.equals("taken") || mode.equals("finished") || mode.equals("finished"+"AI"))
        {
            if (!mode.equals("place"))
            {
                g.fillRect((width+12*boxSide)/2,3*boxSide,boxSide/2,boxSide/2 * 5);
                g.fillRect((width+12*boxSide)/2,6*boxSide,boxSide/2,boxSide/2 * 4);
                g.fillRect((width+14*boxSide)/2,3*boxSide,boxSide/2,boxSide/2 * 3);
                g.fillRect((width+14*boxSide)/2,5*boxSide,boxSide/2,boxSide/2 * 3);
                g.fillRect((width+14*boxSide)/2,7*boxSide,boxSide/2,boxSide/2 * 2);
            }
            g.fillRect((width+12*boxSide)/2,13*boxSide+5,boxSide/2,boxSide/2 * 5);
            g.fillRect((width+12*boxSide)/2,16*boxSide+5,boxSide/2,boxSide/2 * 4);
            g.fillRect((width+14*boxSide)/2,13*boxSide+5,boxSide/2,boxSide/2 * 3);
            g.fillRect((width+14*boxSide)/2,15*boxSide+5,boxSide/2,boxSide/2 * 3);
            g.fillRect((width+14*boxSide)/2,17*boxSide+5,boxSide/2,boxSide/2 * 2);
            if (mode.equals("place") && shipNum < ships[turn-1].length)
            {
                g.setColor(new Color(220,220,220));
                if (shipNum > 1) g.fillRect((width+14*boxSide)/2,((shipNum-2)*2+13)*boxSide+5,boxSide/2,boxSide/2 * SHIP_LENGTH[shipNum]);
                else g.fillRect((width+12*boxSide)/2,(shipNum*3+13)*boxSide+5,boxSide/2,boxSide/2 * SHIP_LENGTH[shipNum]);
            }
            else if (!mode.equals("place"))
            {
                g.setColor(Color.RED);
                for (int i = 0; i < ships[2-turn].length; i++)
                {
                    if (ships[2-turn][i] == null)
                    {
                        if (i > 1) g.fillRect((width+14*boxSide)/2,((i-2)*2+3)*boxSide,boxSide/2,boxSide/2 * SHIP_LENGTH[i]);
                        else g.fillRect((width+12*boxSide)/2,(i*3+3)*boxSide,boxSide/2,boxSide/2 * SHIP_LENGTH[i]);
                    }
                }
                for (int i = 0; i < ships[turn-1].length; i++)
                {
                    if (ships[turn-1][i] == null)
                    {
                        if (i > 1) g.fillRect((width+14*boxSide)/2,((i-2)*2+13)*boxSide+5,boxSide/2,boxSide/2 * SHIP_LENGTH[i]);
                        else g.fillRect((width+12*boxSide)/2,(i*3+13)*boxSide+5,boxSide/2,boxSide/2 * SHIP_LENGTH[i]);
                    }
                }
            }
        }
        
        g.setColor(new Color(0,0,255,127));
        for (int[] i : choose)
        {
            if (i[0] != -1)
            {
                g.fillRect((width-boxSide*10)/2+boxSide*i[1]+1,boxSide*21/2+boxSide*i[0]+6,boxSide-1,boxSide-1);
            }
        }
        
        if (shot[0] != -1)
        {
            g.fillRect((width-boxSide*10)/2+boxSide*shot[1]+1,boxSide/2+boxSide*shot[0]+1,boxSide-1,boxSide-1);
        }
        
        ImageIcon play = new ImageIcon();
        if (mode.equals("place"))
        {
            g.setColor(Color.ORANGE);
            g.fillRect(15,height-63,175,50);
            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif",Font.PLAIN,30));
            g.drawString("Randomize",29,height-28);
            if (shipNum >= ships[turn-1].length)
            {
                play = new ImageIcon("icons/play2.png");
            }
            else
            {
                play = new ImageIcon("icons/play1.png");
            }
        }
        else if (mode.equals("shoot"))
        {
            if (shot[0] != -1)
            {
                play = new ImageIcon("icons/play2.png");
            }
            else
            {
                play = new ImageIcon("icons/play1.png");
            }
        }
        else if (mode.equals("finished") || mode.equals("finished"+"AI"))
        {
            play = new ImageIcon("icons/play2.png");
        }
        
        g.setColor(BG_COLOR);
        ImageIcon player = new ImageIcon("icons/player.png");
        g.drawImage(player.getImage(),width-110,height-110,null);
        g.setFont(new Font("SansSerif",Font.PLAIN,40));
        g.drawString(""+turn,width-71,height-63);
        if (!mode.equals("taken")) g.drawImage(play.getImage(),width-180,height-63,null);
        
        if (mode.equals("nextPlace") || mode.equals("transition") || mode.equals("nextShoot"))
        {
            g.setColor(new Color(0,191,255));
            g.fillRect(-100,-100,width+100,height+100);
            g.setColor(new Color(240,248,255));
            int size = Math.min(width,height)/10;
            g.setFont(new Font("SansSerif",Font.PLAIN,size));
            g.drawString("Switch to Player "+turn,width/2-size*4,height/2-size/2);
        }
        if (mode.equals("start"))
        {
            g.setColor(new Color(0,191,255));
            g.fillRect(-100,-100,width+100,height+100);
            g.setColor(Color.ORANGE);
            g.fillRect(width/2-75,height/2-75,150,50);
            g.fillRect(width/2-75,height/2,150,50);
            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif",Font.PLAIN,25));
            g.drawString("Singleplayer",width/2-68,height/2-43);
            g.drawString("2-player",width/2-44,height/2+32);
            g.setFont(new Font("SansSerif",Font.PLAIN,100));
            g.drawString("Battleship",width/2-210,height/2-120);
        }
    }
    
    /**
     * Randomizes the placement of ships.
     */
    public void randomPlace()
    {
        ships[turn-1] = new Ship[5];
        players[turn-1] = new BattleshipBoard(turn);
        shipNum = 0;
        prevShipNum = new ArrayList<Integer>();
        while (shipNum < 5)
        {
            Random rand = new Random();
            int[] coords;
            do
            {
                coords = new int[] {rand.nextInt(10),rand.nextInt(10)};
            } while (players[turn-1].getBoard(false)[coords[0]][coords[1]] != 0);
            int[][] options = players[turn-1].place(coords[0],coords[1],SHIP_LENGTH[shipNum]);
            int pick;
            do
            {
                pick = rand.nextInt(4);
            }
            while (options[pick][0] == -1) ;
            newShip(coords,options[pick]);
        }
    }
    
    /**
     * Computes where the AI should shoot.
     */
    public void shoot()
    {
        Random rand = new Random();
        int[] pick = new int[] {-1,-1};
        if (numHits == 0 && numWeird < 1)
        {
            do
            {
                pick = new int[] {rand.nextInt(10),rand.nextInt(10)};
            } while (players[0].getBoard(true)[pick[0]][pick[1]] != 0);
        }
        else if (numHits == 1)
        {
            pick = players[0].adjacent(hits[0][0],hits[0][1],"");
        }
        else if (numHits > 1)
        {
            if (hits[0][0] == hits[1][0])
            {
                pick = players[0].adjacent(hits[numHits-1][0],hits[numHits-1][1],"horizontal");
                if (pick[0] == -1)
                {
                    pick = players[0].adjacent(hits[0][0],hits[0][1],"horizontal");
                }
            }
            else
            {
                pick = players[0].adjacent(hits[numHits-1][0],hits[numHits-1][1],"vertical");
                if (pick[0] == -1)
                {
                    pick = players[0].adjacent(hits[0][0],hits[0][1],"vertical");
                }
            }
        }
        if (pick[0] == -1)
        {
            if (numHits > 1)
            {
                do
                {
                    int[] first = Arrays.copyOf(hits[0],2);
                    weirdHits = new int[numWeird+numHits][2];
                    for (int i = 1; i < numHits; i++)
                    {
                        weirdHits[i-1+numWeird] = Arrays.copyOf(hits[i],2);
                    }
                    numWeird = weirdHits.length-1;
                    weirdHits[numWeird] = new int[] {-1,-1};
                    hits = new int[][] {first,{-1,-1}};
                    numHits = 1;
                    pick = players[0].adjacent(hits[0][0],hits[0][1],"");
                } while (pick[0] == -1);
            }
            else if (numWeird > 0)
            {
                do
                {
                    int[] first = Arrays.copyOf(weirdHits[0],2);
                    int[][] newWeird = new int[weirdHits.length-1][2];
                    for (int i = 1; i <= numWeird; i++)
                    {
                        newWeird[i-1] = Arrays.copyOf(weirdHits[i],2);
                    }
                    weirdHits = new int[newWeird.length][2];
                    for (int i = 0; i < weirdHits.length; i++)
                    {
                        weirdHits[i] = newWeird[i];
                    }
                    numWeird--;
                    hits = new int[][] {first,{-1,-1}};
                    numHits = 1;
                    pick = players[0].adjacent(hits[0][0],hits[0][1],"");
                } while (pick[0] == -1);
            }
            else
            {
                do
                {
                    pick = new int[] {rand.nextInt(10),rand.nextInt(10)};
                } while (players[0].getBoard(true)[pick[0]][pick[1]] != 0);
            }
        }
        players[2-turn].shoot(pick[0],pick[1]);
        if (players[2-turn].getBoard(false)[pick[0]][pick[1]] == 3)
        {
            hits[numHits][0] = pick[0];
            hits[numHits][1] = pick[1];
            numHits++;
            hits = Arrays.copyOf(hits,numHits+1);
            hits[numHits] = new int[] {-1,-1};
            int boatIndex = -1;
            for (int i = 0; i < ships[0].length; i++)
            {
                if (ships[0][i] != null)
                {
                    int[][] segs = ships[0][i].getSegs();
                    for (int[] segment : segs)
                    {
                        if (segment[0] == pick[0] && segment[1] == pick[1])
                        {
                            boatIndex = i;
                            break;
                        }
                    }
                    if (boatIndex != -1) break;
                }
            }
            boolean sunk = true;
            for (int[] segment : ships[0][boatIndex].getSegs())
            {
                if (players[0].getBoard(false)[segment[0]][segment[1]] != 3)
                {
                    sunk = false;
                    break;
                }
            }
            if (sunk)
            {
                int[][] oldHits = new int[hits.length][2];
                for (int i = 0; i < oldHits.length; i++)
                {
                    oldHits[i][0] = hits[i][0];
                    oldHits[i][1] = hits[i][1];
                }
                hits = new int[][] {{-1,-1}};
                numHits = 0;
                for (int[] shot : oldHits)
                {   
                    boolean sunkPart = false;
                    for (int[] segment : ships[0][boatIndex].getSegs())
                    {
                        if ((shot[0] == segment[0] && shot[1] == segment[1]) || shot[0] == -1)
                        {
                            sunkPart = true;
                        }
                    }
                    if (!sunkPart)
                    {
                        hits[numHits][0] = shot[0];
                        hits[numHits][1] = shot[1];
                        numHits++;
                        hits = Arrays.copyOf(hits,numHits+1);
                        hits[numHits] = new int[] {-1,-1};
                    }
                }
                sinkingAi = boatIndex;
            }
        }
    }
    
    /**
     * Does all the actions during the AI's turn.
     */
    public void aiTurn()
    {
        if (mode.equals("shoot")) shoot();
        else if (mode.equals("place")) randomPlace();
        turn = 1;
        mode = "shoot";
    }
    
    /**
     * Actions that occur when the mouse is pressed.
     * @param me  the object of the mouse being pressed
     */
    public void mousePressed(java.awt.event.MouseEvent me)
    {
        int x = me.getX()-sideOffset;
        int y = me.getY()-topOffset;
        int boxSide = height/21;
        if (x > (width-boxSide*10)/2-10 && x < (width+boxSide*10)/2-10 && y > boxSide/2-11 && y < boxSide*21/2-11 && mode.equals("shoot") &&
            players[2-turn].getBoard(true)[(y-(boxSide/2-9))/boxSide][(x-(width-boxSide*10)/2+8)/boxSide] == 0)
        {
            row = (y-(boxSide/2-9))/boxSide;
            column = (x-(width-boxSide*10)/2+8)/boxSide;
            if (row < 0) row = 0;
            else if (row > 9) row = 9;
            if (column < 0) column = 0;
            else if (column > 9) column = 9;
            select = new int[] {(width-boxSide*10)/2+boxSide*column+1,(boxSide/2)+boxSide*row+1,boxSide-1,boxSide-1,height*width};
            if (row == shot[0] && column == shot[1]) shot = new int[] {-1,-1};
        }
        else if (x > (width-boxSide*10)/2-10 && x < (width+boxSide*10)/2-10 && y > boxSide*21/2-7 && y < boxSide*41/2-7 && mode.equals("place"))
        {
            int[] newCoords = new int[] {(y-(boxSide*21/2-5))/boxSide,(x-(width-boxSide*10)/2+8)/boxSide};
            for (int i = 0; i < 2; i++)
            {
                if (newCoords[i] < 0)
                {
                    newCoords[i] = 0;
                }
                else if (newCoords[i] > 9)
                {
                    newCoords[i] = 9;
                }
            }
            select = new int[] {(width-boxSide*10)/2+boxSide*newCoords[1]+1,(boxSide*21/2+5)+boxSide*newCoords[0]+1,boxSide-1,boxSide-1,height*width};
            if (!Arrays.equals(newCoords,new int[] {-1,-1}) &&
                row != -1 && column != -1 &&
                players[turn-1].getBoard(false)[row][column] == 0 &&
                 (Arrays.equals(newCoords,choose[0]) ||
                  Arrays.equals(newCoords,choose[1]) ||
                  Arrays.equals(newCoords,choose[2]) ||
                  Arrays.equals(newCoords,choose[3])))
            {
                newShip(newCoords,new int[] {row,column});
            }
            row = newCoords[0];
            column = newCoords[1];
            int[] coords = new int[] {row,column};
            choose = new int[][] {{-1,-1},{-1,-1},{-1,-1},{-1,-1}};
        }
        else
        {
            select = new int[] {0,0,0,0,height*width};
            row = -1;
            column = -1;
            choose = new int[][] {{-1,-1},{-1,-1},{-1,-1},{-1,-1}};
        }
        if (mode.equals("taken"))
        {
            mode = "nextShoot";
        }  
        else if (mode.equals("nextPlace"))
        {
            mode = "place";
        }
        else if (mode.equals("transition"))
        {
            select = new int[] {0,0,0,0,height*width};
            mode = "shoot";
        }
        else if (mode.equals("nextShoot"))
        {
            turn = 3-turn;
            mode = "shoot";
        }
        else if (mode.equals("place") && x > 5 && x < 180 && y > height-75 && y < height-25)
        {
            randomPlace();
        }
        else if (mode.equals("start") && x > width/2-85 && x < width/2+65)
        {
            if (y > height/2-85 && y < height/2-35)
            {
                ai = true;
                mode = "place";
            }
            else if (y > height/2-10 && y < height/2+40)
            {
                ai = false;
                mode = "place";
            }
        }
        else if (x > width-190 && x < width-140 && y > height-75 && y < height-25)
        {
            if (mode.equals("place") && shipNum >= ships[turn-1].length)
            {
                if (turn == 1) 
                {
                    turn = 2;
                    shipNum = 0;
                    if (!ai)
                    {
                        mode = "nextPlace";
                    }
                    else
                    {
                        aiTurn();
                    }
                }
                else if (!ai)
                {
                    turn = 1;
                    shipNum = 0;
                    mode = "transition";
                }
            }
            else if (mode.equals("shoot") && shot[0] != -1)
            {
                players[2-turn].shoot(shot[0],shot[1]);
                if (players[2-turn].getBoard(false)[shot[0]][shot[1]] == 3)
                {
                    int boatIndex = -1;
                    for (int i = 0; i < ships[2-turn].length; i++)
                    {
                        if (ships[2-turn][i] != null)
                        {
                            int[][] segs = ships[2-turn][i].getSegs();
                            for (int[] segment : segs)
                            {
                                if (segment[0] == shot[0] && segment[1] == shot[1])
                                {
                                    boatIndex = i;
                                    break;
                                }
                            }
                            if (boatIndex != -1) break;
                        }
                    }
                    boolean sunk = true;
                    for (int[] segment : ships[2-turn][boatIndex].getSegs())
                    {
                        if (players[2-turn].getBoard(false)[segment[0]][segment[1]] != 3)
                        {
                            sunk = false;
                            break;
                        }
                    }
                    if (sunk)
                    {
                       sinking = boatIndex; 
                    }
                }
                shot = new int[] {-1,-1};
                if (!ai)
                {
                    mode = "taken";
                }
                else
                {
                    turn = 3-turn;
                    aiTurn();
                }
            }
            if (mode.equals("finished") || mode.equals("finished"+"AI"))
            {
                BattleshipBoard player1 = new BattleshipBoard(1);
                BattleshipBoard player2 = new BattleshipBoard(2);
                players = new BattleshipBoard[] {player1,player2};
                Ship[] ships1 = new Ship[5];
                Ship[] ships2 = new Ship[5];
                ships = new Ship[][] {ships1,ships2};
                select = new int[] {0,0,0,0,0};
                choose = new int[][] {{-1,-1},{-1,-1},{-1,-1},{-1,-1}};
                shot = new int[] {-1,-1};
                mode = "start";
                turn = 1;
                shipNum = 0;
                prevShipNum = new ArrayList<Integer>();
                sinking = MIN;
            }
        }
        repaint();
    }
    
    /**
     * Runs all the actions for when the mouse is clicked twice
     * @param me  the object for when the mouse is clicked
     */
    public void mouseClicked(MouseEvent me)
    {
        if (me.getClickCount() >= 2)
        {
            if (row != -1 && column != -1)
            {
                if (mode.equals("place"))
                {
                    int[] coords = new int[] {row,column};
                    if (players[turn-1].getBoard(false)[row][column] > 0)
                    {
                        int num = -1;
                        for (int i = 0; i < ships[turn-1].length; i++)
                        {
                            int[][] segs = ships[turn-1][i].getSegs();
                            for (int j = 0; j < segs.length; j++)
                            {
                                if (Arrays.equals(ships[turn-1][i].getSegs()[j],coords))
                                {
                                    num = i;
                                    for (int[] k : segs)
                                    {
                                        players[turn-1].set(k[0],k[1],0);
                                    }
                                    break;
                                }
                            }
                            if (num > -1) break;
                        }
                        prevShipNum.add(0,shipNum);
                        shipNum = num;
                    }
                    else
                    {
                        if (shipNum < ships[turn-1].length)
                        {
                            choose = players[turn-1].place(row,column,SHIP_LENGTH[shipNum]);
                        }
                    }
                }
                else if (mode.equals("shoot"))
                {
                    shot = new int[] {row,column};
                }
                repaint();
            }
        }
    }
    
    public static void main(String[] args)
    {
        Battleship b = new Battleship();
        b.window();
    }
    
    public void actionPerformed(ActionEvent me) {}
    public void mouseReleased(MouseEvent me) {}
    public void mouseEntered(MouseEvent me) {}
    public void mouseExited(MouseEvent me) {}
}
