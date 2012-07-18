import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;


public class InfoBars extends JPanel{
	private int sizeX = 230;
	private int sizeY = 270;
	private Color blue = new Color(5,5,200);
	private Color black = new Color(0,0,0);
	private BufferedImage bars;
	private Graphics g;
	private Font msgFont;
	private Font annFont;
	
	public InfoBars (){
		setBackground(Color.white);
	    setPreferredSize( new Dimension(sizeX, sizeY));
		bars = new BufferedImage(250,220,BufferedImage.TYPE_INT_ARGB);
		g = (Graphics) bars.createGraphics();
		g.setColor(Color.white);
		msgFont = new Font("SansSerif", Font.PLAIN, 24);
		annFont = new Font("SansSerif", Font.BOLD, 30);
	}
	
	public void updateHealth(int health){
		g.setColor(Color.red);
	    g.setFont(msgFont);
	    g.drawString("Health", 30, 20);
	    
		g.setColor(black);
	
	    g.fillRect(20, 30, 202,27);
	    
	    
	    g.setColor(blue);
	    g.fillRect(22, 35, (int)health/5 ,21);
	   
		repaint();
	}
	
	public void updateLaser(long laser){
		g.setColor(Color.red);
	    g.setFont(msgFont);
	    g.drawString("Laser Heat", 30, 80);
	    
	    int heat = 200 -(int)(laser/50000000);
		g.setColor(black);
		g.fillRect(20, 90, 202,27);
		g.setColor(blue);
		g.fillRect(22, 95, heat,21);
		if(heat>160){
			g.setColor(Color.red);
		    g.setFont(annFont);
		    g.drawString("DANGER", 40, 210);
		}
		else{
			g.setColor(Color.white);
			g.fillRect(5,180,200,60);
		}
		 repaint();
	}
	
	public void updateMissile(long missile){
		g.setColor(Color.red);
	    g.setFont(msgFont);
	    g.drawString("Missile Heat", 30, 140);
	    
	    int heat = 200-(int)(missile/25000000);
	    
		g.setColor(black);
		g.fillRect(20, 150, 202,27);
		g.setColor(blue);
		g.fillRect(22, 155, 200-(int)(missile/25000000),21);
		if(heat>140){
			g.setColor(Color.red);
		    g.setFont(annFont);
		    g.drawString("DANGER", 40, 210);
		}
		else{
			g.setColor(Color.white);
			g.fillRect(35,180,200,60);
		}
		 repaint();
		
	}
	
	public void paintComponent(Graphics g){
		super.paintComponents(g);
		g.drawImage(bars, 0, 0, null);
		}
	

}
