import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class Game implements MouseListener
{
    private static final String ASSET_FOLDER = "assets/";
    private static final String BACKGROUND_IMAGE = ASSET_FOLDER + "back.JPG";
    private static final String PLAYER_NORMAL = ASSET_FOLDER + "sub.gif";
    private static final String PLAYER_UP = ASSET_FOLDER + "sub.gif";
    // Obstacle    s
    private static final String CHEST = ASSET_FOLDER + "chest.png";
    private static final String OBSTACLE = ASSET_FOLDER + "mine.png";

    private static final String PARTICLE = ASSET_FOLDER + "trail.png";
    private static final String BAR = ASSET_FOLDER + "bar.png";
    public static void main (String [] args)
    {
        Game _ = new Game();
    }

    private JFrame background;
    private Container container;
    private JButton button;
    private ImagePanel back;

    public static boolean paused;
    public static boolean crashed;
    public static boolean started;
    public static boolean playedOnce;   

    public static boolean goingUp;
    private double upCount;

    public static int distance;
    public static int maxDistance;

    public final int XPOS;
    public final int NUMRECS;
    public final int RECHEIGHT;
    public final int RECWIDTH;

    private int moveIncrement;
    private int numSmoke;

    private ArrayList<MovingImage> toprecs;
    private ArrayList<MovingImage> bottomrecs;
    private ArrayList<MovingImage> middlerecs;
    private ArrayList<MovingImage> recs;
    private ArrayList<MovingImage> smoke;
    private MovingImage helicopter;

    public static int chestCount = 0;
    public Game()
    {
        NUMRECS = 28;
        RECHEIGHT = 73;
        RECWIDTH = 29;
        XPOS = 200;
        playedOnce = false;
        maxDistance = 0;

        load(new File("Best.txt"));

        initiate();
    }

    public void load(File file)
    {
        try
        {
            Scanner reader = new Scanner(file);
            while(reader.hasNext())
            {
                int value = reader.nextInt();
                if(value > maxDistance)
                    maxDistance = value;
            }
        }
        catch(IOException i )
        {
            System.out.println("Error. "+i);
        }
    }

    public void save()
    {
        FileWriter out;
        try
        {
            out = new FileWriter("Best.txt");
            out.write("" + maxDistance);
            out.close();
        }
        catch(IOException i)
        {
            System.out.println("Error: "+i.getMessage());
        }
    }

    public void initiate()
    {
        if(!playedOnce)
        {
            background = new JFrame("Helicopter Game"); 
            background.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //closes the program when the window is closed
            background.setResizable(false); //don't allow the user to resize the window
            background.setSize(new Dimension(818,568));
            background.setVisible(true);

            back = new ImagePanel(BACKGROUND_IMAGE);
            background.add(back);

            back.addMouseListener(this);
        }
        playedOnce = true;
        goingUp = false;
        paused = false;
        crashed = false;
        started = false;

        distance = 0;
        upCount = 0;

        moveIncrement = 2;
        numSmoke = 15;

        recs = new ArrayList<MovingImage>();
        toprecs = new ArrayList<MovingImage>();
        middlerecs = new ArrayList<MovingImage>();
        bottomrecs = new ArrayList<MovingImage>();
        smoke = new ArrayList<MovingImage>();

        helicopter = new MovingImage(PLAYER_NORMAL,XPOS,270);

        for(int x = 0; x < NUMRECS; x++)
            toprecs.add(new MovingImage(BAR,RECWIDTH*x,0));
        for(int x = 0; x < NUMRECS; x++)
            bottomrecs.add(new MovingImage(BAR,RECWIDTH*x,537-RECHEIGHT));

        middlerecs.add(new MovingImage(OBSTACLE,1392,randomMidHeight()));
        middlerecs.add(new MovingImage(OBSTACLE,1972,randomMidHeight()));
        middlerecs.add(new MovingImage(CHEST, 1700, randomMidHeight(), 100));

        drawRectangles();
    }

    public void drawRectangles()
    {
        long last = System.currentTimeMillis();
        long lastCopter = System.currentTimeMillis();
        long lastSmoke = System.currentTimeMillis();
        int firstUpdates = 0;
        double lastDistance = (double)System.currentTimeMillis();
        while(true)
        {
            if(!paused && !crashed && started && (double)System.currentTimeMillis() - (double)(2900/40) > lastDistance)
            {   
                lastDistance = System.currentTimeMillis();
                distance++;
            }   

            if(!paused && !crashed && started && System.currentTimeMillis() - 10 > lastCopter)
            {
                lastCopter = System.currentTimeMillis();
                updateCopter();
                updateMiddle();
            }
            if(!paused && !crashed && started && System.currentTimeMillis() - 100 > last)
            {
                last = System.currentTimeMillis();
            }
            if(!paused && !crashed && started && System.currentTimeMillis() - 75 > lastSmoke)
            {
                lastSmoke = System.currentTimeMillis();
                if (firstUpdates < numSmoke)
                {
                    firstUpdates++;
                    smoke.add(new MovingImage("smoke.GIF",187,helicopter.getY()));
                    for(int x = 0; x < firstUpdates; x++)
                        smoke.set(x,new MovingImage(PARTICLE,smoke.get(x).getX() - 12, smoke.get(x).getY()));
                }
                else
                {
                    for(int x = 0; x < numSmoke - 1; x++)
                        smoke.get(x).setY(smoke.get(x+1).getY());
                    smoke.set(numSmoke - 1,new MovingImage("smoke.GIF",187,helicopter.getY()));
                }
                    }
                    back.updateImages(toprecs,middlerecs,bottomrecs,helicopter,smoke);
                }
    }

    public void randomDrop()
    {
        toprecs.get(26).setY(toprecs.get(26).getY() + (463 - bottomrecs.get(26).getY()));
        bottomrecs.get(26).setY(463);
    }

    public void moveDown()
    {
        toprecs.set((NUMRECS - 1),new MovingImage(OBSTACLE,RECWIDTH*(NUMRECS - 1),toprecs.get(26).getY() + moveIncrement));
        bottomrecs.set((NUMRECS - 1),new MovingImage(OBSTACLE,RECWIDTH*(NUMRECS - 1),bottomrecs.get(26).getY() + moveIncrement));
    }

    public void moveUp()
    {
        bottomrecs.set((NUMRECS - 1),new MovingImage(OBSTACLE,RECWIDTH*(NUMRECS - 1),bottomrecs.get(26).getY() - moveIncrement));
        toprecs.set((NUMRECS - 1),new MovingImage(OBSTACLE,RECWIDTH*(NUMRECS - 1),toprecs.get(26).getY() - moveIncrement));
    }

    public int randomMidHeight()
    {
        int max = 10000;
        int min = 0;

        for(int x = 0; x < NUMRECS; x++)
        {
            if(toprecs.get(x).getY() > min)
                min = (int)toprecs.get(x).getY();
            if(bottomrecs.get(x).getY() < max)
                max = (int)bottomrecs.get(x).getY();
        }
        min += RECHEIGHT;
        max -= (RECHEIGHT + min);
        return min + (int)(Math.random() * max);
    }

    public void updateMiddle()
    {
        if (middlerecs.get(0).getX() > -1 * RECWIDTH) {
            middlerecs.set(0, new MovingImage(middlerecs.get(0).getImage(), middlerecs.get(0).getX() - (RECWIDTH / 5), middlerecs.get(0).getY()));
            middlerecs.set(1, new MovingImage(middlerecs.get(1).getImage(), middlerecs.get(1).getX() - (RECWIDTH / 5), middlerecs.get(1).getY()));
        } else {
            middlerecs.set(0, new MovingImage(middlerecs.get(1).getImage(), middlerecs.get(1).getX() - (RECWIDTH / 5), middlerecs.get(1).getY()));

            // Randomly decide between adding a chest or obstacle
            if (Math.random() < 0.3) { // 30% chance to spawn a chest
                middlerecs.set(1, new MovingImage(CHEST, middlerecs.get(0).getX() + 580, randomMidHeight()));
            } else { // Otherwise, spawn an obstacle
                middlerecs.set(1, new MovingImage(OBSTACLE, middlerecs.get(0).getX() + 580, randomMidHeight()));
            }
        }
    }

    public boolean isHit() {
        for (int x = 3; x <= 7; x++) {
            if (helicopter.getY() + 48 >= bottomrecs.get(x).getY())
                return true;
        }

        for (int y = 3; y <= 7; y++) {
            if (helicopter.getY() <= toprecs.get(y).getY() + RECHEIGHT)
                return true;
        }

        for (int z = 0; z <= 1; z++) {
            if (isInMidRange(z)) {
                if (middlerecs.get(z).type > 0) {
                    collectChest(z); // Collect the chest if intersecting
                    return false;    // Don't treat chest collision as a crash
                }
                System.out.println(middlerecs.get(z).type);
                return true; // If it's not a chest, it's a crash
            }
        }

        return false;
    }

    public void collectChest(int index) {
        chestCount++;
        middlerecs.set(index, new MovingImage(OBSTACLE, middlerecs.get(index).getX(), randomMidHeight())); // Replace the chest with an obstacle

        saveChestCount(); // Save the chest count to Best.txt
    }

    public void saveChestCount() {
        try (FileWriter out = new FileWriter("Best.txt", true)) { // Append chest count to the file
            out.write("\nChest Count: " + chestCount);
        } catch (IOException i) {
            System.out.println("Error: " + i.getMessage());
        }
    }

    public boolean isInMidRange(int num)
    {
        Rectangle middlecheck = new Rectangle((int)middlerecs.get(num).getX(),(int)middlerecs.get(num).getY(),RECWIDTH,RECHEIGHT);
        Rectangle coptercheck = new Rectangle((int)helicopter.getX(),(int)helicopter.getY(),106,48);
        return middlecheck.intersects(coptercheck);
    }

    public void crash()
    {
        crashed = true;
        if(distance > maxDistance) 
        {
            maxDistance = distance;
            save();
        }

        initiate();
    }
    public void updateCopter()
    {
        upCount += .08;
        if(goingUp)
        {
            if(upCount < 3.5)
                helicopter.setPosition(XPOS,(double)(helicopter.getY() - (.3 + upCount)));
            else
                helicopter.setPosition(XPOS,(double)(helicopter.getY() - (1.2 + upCount)));
            helicopter.setImage(PLAYER_UP);    
        }
        else
        {
            if(upCount < 1)
                helicopter.setPosition(XPOS,(double)(helicopter.getY() + upCount));
            else
                helicopter.setPosition(XPOS,(double)(helicopter.getY() + (1.2 + upCount)));
            helicopter.setImage(PLAYER_NORMAL);
        }
        if(isHit())
            crash();
    }

    public void mouseExited(MouseEvent e){paused = started;}
    public void mouseReleased(MouseEvent e)
    {
        goingUp = false;
        upCount = 0;
        if(paused)
            paused = false;
    }
    public void mousePressed(MouseEvent e)
    {
        if (!started)
            started = true;
        goingUp = true;
        upCount = 0;
    }

    public void mouseEntered(MouseEvent e){}
    public void mouseClicked(MouseEvent e){}

}
