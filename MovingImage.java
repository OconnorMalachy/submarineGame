import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

class MovingImage
{
    public int type;
    private Image image;        
    private double x;           
    private double y;           
    
    public MovingImage(String path, double xPos, double yPos, int type){
        this(path, xPos, yPos);
        this.type = type;
    }
    public MovingImage(Image img, double xPos, double yPos)
    {
        image = img;
        x = xPos;
        y = yPos;
    }

    public MovingImage(String path, double xPos, double yPos)
    {
        this(new ImageIcon(path).getImage(), xPos, yPos);   
    }

    public void setPosition(double xPos, double yPos)
    {
        x = xPos;
        y = yPos;
    }
    public void setImage(String path){image = new ImageIcon(path).getImage();}
    public void setY(double newY){y = newY;}
    public void setX(double newX){x = newX;}

    public double getX(){return x;}
    public double getY(){return y;}
    public Image getImage(){return image;}
}
