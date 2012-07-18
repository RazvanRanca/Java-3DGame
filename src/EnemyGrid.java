import java.util.ArrayList;

import javax.vecmath.Point2d;


public class EnemyGrid {
	private int enemyNo;
	private int cells;
	private ArrayList<Integer> availableLoc;
	private double cellSize;
	private double safeZone;
	private ArrayList<Point2d> enemyLoc;
	private int size;
	private ArrayList<Point2d> objectiveList;
	private int borderSize;
	
	public EnemyGrid(int b,int n, int s, int cs,double sz, int droneNo){
		borderSize = b;
		size=s;
		safeZone = sz;
		cellSize=cs;
		cells=s/cs;
		if(n>cells*cells){
			System.out.println("Too many enemies");
			n=cells*cells;
		}
		enemyNo = n;
		
		availableLoc = new ArrayList<Integer>();
		objectiveList = new ArrayList<Point2d>();
		for(int i=0;i<cells*cells;i++)
			availableLoc.add(i);
		
		enemyLoc = new ArrayList<Point2d>();
		for (int i=0;i<enemyNo;i++){
			enemyLoc.add(getEnemyPos());
			
			if(i<droneNo)
				objectiveList.add(getObjectivePosition(enemyLoc.get(i)));
		}
		
	}
	
	private Point2d getEnemyPos(){
		int x = (int) (Math.random()*availableLoc.size());
		int r = availableLoc.get(x);
		availableLoc.remove(x);
		double locx = ((r/cells)*cellSize - size/2) + borderSize;
		double locy = ((r%cells)*cellSize - size/2) + borderSize;
		if(locx*locx + locy*locy >= safeZone*safeZone)
			return new Point2d(((r/cells)*cellSize)+borderSize,((r%cells)*cellSize)+borderSize);
		else
			return getEnemyPos();
	}
	
	private Point2d getObjectivePosition (Point2d e){ // objectives are placed somewhere near mines
		double max = Math.sqrt((cellSize -2)*(cellSize-2) - 25);
		
		double x =(Math.random()) * (max-5) +5; 
		double y = (Math.random()) * (max-5) +5;
		if(Math.random()>0.5)
			x*=-1;
		if(Math.random()>0.5)
			y*=-1;
		if(e.x + x<borderSize+1 || e.x+x>size + borderSize-1)
			x*=-1;
		if(e.y+y<borderSize || e.y+y > size + borderSize -1)
			y*=-1;
		return new Point2d(e.x+x,e.y+y);
	}
	
	public ArrayList<Point2d> getEnemyGrid(){
		return enemyLoc;
	}
	
	public ArrayList<Point2d> getObjectiveList(){
		return objectiveList ;
	}

}



