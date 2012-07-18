import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.vecmath.Point3d;


public class BirdsEye extends JPanel{
	private int sizeX = 270;
	private int sizeY = 270;
	private BufferedImage terrain;
	private ArrayList<Integer> heightIndex;
	private int terSize;
	private int origSize;
	private int imageSize = 256;
	private int boxSize;
	private int redFac = 1;
	private int maxFac;
	private Color col2 = new Color(154,205,50);
	private Color col1 = new Color(85,107,47);
	private Color col0 = new Color(108,66,38);
	private Color red = new Color(208,5,5);
	private Color blue = new Color(5,5,200);
	private ArrayList<Point3d> objs;
	private Graphics g;
	
	public BirdsEye (int s){
		terSize=s;
		origSize=s;
		maxFac = imageSize/origSize;
		setBackground(Color.white);
	    setPreferredSize( new Dimension(sizeX, sizeY));
	    terrain = new BufferedImage(imageSize, imageSize,BufferedImage.TYPE_INT_ARGB);
	}
	
	public void setHeightIndex(ArrayList<Integer> a){
		heightIndex=a;
		
		if(heightIndex.size()>256*256){
			ArrayList<Integer> temp; 
			while(heightIndex.size()>256*256){
				temp = new ArrayList<Integer>();
				for(int i=0;i<terSize;i+=2)
					for(int j=0;j<terSize;j+=2){
						int cur = i*terSize + j;
						temp.add((int) Math.round((heightIndex.get(cur)+heightIndex.get(cur+1)+heightIndex.get(cur+terSize)+heightIndex.get(cur+terSize+1))/4d));
					}
				terSize/=4;
				redFac*=2;
				heightIndex=temp;
			}
		}
		
		boxSize = imageSize/terSize;
		buildMap();
	}
	
	public void buildMap(){
		 g = (Graphics) terrain.createGraphics();
		 g.setColor(Color.white);
		 int c=0;
		 for(int i=0;i<terSize;i++)
			 for(int j=0;j<terSize;j++){
				 int index = heightIndex.get(c);
				 c++;
				 if(index == 0)
					 drawBox(i,j,col0);
				 else if(index==1)
					 drawBox(i,j,col1);
				 else
					 drawBox(i,j,col2);
			 }
		 
	}
	
	private void drawBox(int i, int j, Color col){
		g.setColor(col);
	    g.fillRect(i*boxSize, j*boxSize, boxSize,boxSize);
	}
	
	private void drawCircle(int i, int j, Color col){
		g.setColor(col);
	    g.fillOval(i, j, 10, 10);
	}
	
	public void paintComponent(Graphics g){
		super.paintComponents(g);
		g.drawImage(terrain, 0, 0, null);
		}
	
	public void setObjs(ArrayList<Point3d> o){ //called after setHeightIndex
		objs = o;
		for(Point3d p : objs){
			
			drawCircle((int)(maxFac*(p.x+origSize/2)/redFac),(int)(maxFac*(p.z+origSize/2)/redFac),red);
			
		}
		
	}
	
	public void setUser(int X, int Y){
		g.setColor(blue);
	    g.fillOval((X+terSize/2)*maxFac, (Y+terSize/2)*maxFac, 10, 10);
	    g.dispose();
		repaint();
	}

}
