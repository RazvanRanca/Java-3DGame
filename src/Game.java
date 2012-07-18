import javax.swing.*;
import java.awt.*;

public class Game extends JFrame{ // top level class
		
	public Game(){
		this(32,2.3,3,7,false);
	}
	
	public Game (int s){
		this(s,2.3,3,7,false);
	}
	
	public Game (int s, double f, int n1, int n2,boolean music) {
		super ("Meaningful description");
		Container c = getContentPane();
		c.setLayout( new BoxLayout(c,BoxLayout.X_AXIS) );
		BirdsEye bird = new BirdsEye(s);
	    InfoBars bars = new InfoBars();
		GameWrapper wrap = new GameWrapper(s,f,n1,n2,bird,bars,music); // Jpanel w/ 3DCanvas in it
		c.add(wrap);
	    c.add( Box.createRigidArea( new Dimension(8,0)) );
	    
	    Box vertBox = Box.createVerticalBox();
	   
	    vertBox.add(bird);
	    vertBox.add( Box.createRigidArea( new Dimension(0,8)));
	    vertBox.add(bars);
	    c.add(vertBox);
	    
		setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		pack();
		setResizable(false);
		setVisible(true);
	}
	
	
	
	public static void main (String[] args){
		new Game(128,2,2,3,true); // ter size, ter flatness, mines, drones
	}

}
