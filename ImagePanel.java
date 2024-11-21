import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;


class ImagePanel extends JPanel {

    private Image background;                   //The background image
    private ArrayList<MovingImage> top; //An array list of foreground images
    private ArrayList<MovingImage> bottom;
    private ArrayList<MovingImage> middle;
    private MovingImage copter;
    private ArrayList<MovingImage> smoke;

    //Constructs a new ImagePanel with the background image specified by the file path given
    public ImagePanel(String img) 
    {
        this(new ImageIcon(img).getImage());    
            //The easiest way to make images from file paths in Swing
    }

    //Constructs a new ImagePanel with the background image given
    public ImagePanel(Image img)
    {
        background = img;
        Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));    
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setSize(size);

        top = new ArrayList<MovingImage>();
        middle = new ArrayList<MovingImage>();
        bottom = new ArrayList<MovingImage>();

        smoke = new ArrayList<MovingImage>();
    }

    public void paintComponent(Graphics g) 
    {
        g.drawImage(background, 0, 0, null); 
        for(MovingImage img : top)
            g.drawImage(img.getImage(), (int)(img.getX()), (int)(img.getY()), null);
        for(MovingImage img : middle)
            g.drawImage(img.getImage(), (int)(img.getX()), (int)(img.getY()), null);
        for(MovingImage img : bottom)
            g.drawImage(img.getImage(), (int)(img.getX()), (int)(img.getY()), null);
        for(MovingImage img : smoke)
            g.drawImage(img.getImage(), (int)(img.getX()), (int)(img.getY()), null);
        if(copter != null)
            g.drawImage(copter.getImage(), (int)(copter.getX()), (int)(copter.getY()), null);
        drawStrings(g);
    }

    public void drawStrings(Graphics g)
    {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial",Font.BOLD,20));
        g.drawString("Distance: " + Game.distance,30,500);
        g.drawString("Submersible Adventure",250,40);
        g.setFont(new Font("Arial", Font.BOLD, 15));
        g.drawString("Hold space to go up, P to pause",250,65);
        g.drawString("Malachy O'Connor",0,25);
        g.drawString("Period 2",0,45);
        g.setFont(new Font("Arial",Font.BOLD,20));
        if (Game.distance > Game.maxDistance)
            g.drawString("Best: " + Game.distance,650,500);
        else
            g.drawString("Best: " + Game.maxDistance,650,500);
        if(Game.paused)
        {
                g.setFont(new Font("Chiller",Font.BOLD,72));
                g.drawString("Paused",325,290);
                g.setFont(new Font("Chiller",Font.BOLD,30));
                g.drawString("Click to unpause.",320,340);
        }
    }

    //Replaces the list of foreground images with the one given, and repaints the panel
    public void updateImages(ArrayList<MovingImage> newTop,ArrayList<MovingImage> newMiddle,ArrayList<MovingImage> newBottom,MovingImage newCopter,ArrayList<MovingImage> newSmoke)
    {
        top = newTop;
        copter = newCopter;
        middle = newMiddle;
        bottom = newBottom;
        smoke = newSmoke;
        repaint();  //This repaints stuff... you don't need to know how it works
    }
}
