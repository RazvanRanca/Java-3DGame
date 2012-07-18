import java.util.Random;

public class HeightMap { // uses diamond-square algorithm

	private int size; // should be 2^n
	private double[][] heights;
	private double factor; // higher means flatter terrain should be between 1.5 and 3
	private double maxHeight = 20; 
	private Random rand; // for generating random terrain that is the same at each program run
	
	public HeightMap(int s,double f){
		size = s;
		factor = f;
		
		heights = new double [size+1] [size+1];
		rand = new Random (73);
	}
	
	public double[][] genMap(){
		initMap();
		double var = maxHeight; // max random variation
		int side = size;// the side of the smallest square with all four points generated
		while (side >1){
			int xNr;
			int yNr;
			for (xNr=0;xNr<size;xNr+=side) // diamond step
				for(yNr=0;yNr<size;yNr+=side)
					makeDiamond(xNr,yNr,side,var);
			for(xNr=0;xNr<=size;xNr+=side/2) // square step
				for(yNr=(xNr+side/2)%side;yNr<=size;yNr+=side)
					makeSquare(xNr,yNr,side,var);
			side/=2;// next square stage
			var/=factor; // random variation decreases
		}
		return heights;
		
			
	}
	
	public void initMap(){
		heights[0][0]=randPoint();
		heights[0][size]=randPoint();
		heights[size][0]=randPoint();
		heights[size][size]=randPoint();
		
	}
	
	public double randPoint(){
		return (rand.nextDouble()-0.5)*maxHeight*2;
	}
	
	public void makeDiamond(int x,int y,int side,double var){
		heights[x+side/2][y+side/2]=((heights[x][y] + heights[x][y+side] + heights[x+side][y] + heights[x+side][y+side])/4 + ((rand.nextDouble()-0.5)*var*2));
	}
	
	public void makeSquare(int x,int y,int side, double var){
		heights[x][y]=((getPoint(x-side/2,y,side) + getPoint(x,y-side/2,side) + getPoint(x+side/2,y,side) + getPoint (x,y+side/2,side))/4 + ((rand.nextDouble()-0.5)*var*2));
	}
	
	public double getPoint (int x,int y,int side){
		if(x<0)
			x+=side;
		if(x>size)
			x-=side;
		if(y<0)
			y+=side;
		if(y>size)
			y-=side;
		return heights[x][y];
	}
	
	public double getOY (){
		return heights[size/2+1][size/2+1];
	}
	
	public double getMaxHeight(){
		return maxHeight;
	}
}
