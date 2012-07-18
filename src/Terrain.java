import java.util.*;

import javax.media.j3d.*;
import javax.vecmath.*;

import com.sun.j3d.utils.picking.*;





public class Terrain {
	
	private BranchGroup terBG = new BranchGroup();
	private ArrayList<ArrayList<TerSquare>> squares = new ArrayList<ArrayList<TerSquare>>();
	private int nSquares;
	private int numZones = 3;
	private int SquareSize = 16; // must be 2^n
	private double[] boundaries = {12,6,-10}; // boundaries for different textures (between a height of 5 and -2 first texture, etc)
	private double OY;
	private int size;
	private Vector3d down = new Vector3d(0,-1,0);
	private PickTool picker;
	private double maxHeight;
	private double borderSize;
	private Point3d[] vert;
	private ArrayList<Point3d> enemyHeightGrid;
	private ArrayList<Point3d> objectiveHeightGrid;
	private String root;
	public ArrayList<Integer> heightIndex = new ArrayList<Integer>();
	public BirdsEye bird;
	
	public Terrain (int b,int s, double f, ArrayList<Point2d> eg,String r,ArrayList<Point2d> ol,BirdsEye br){
		bird=br;
		borderSize=b;
		root=r;
		nSquares = s/SquareSize;
		WrapHeightMap hm = new WrapHeightMap (s,f);
		maxHeight = hm.getMaxHeight();
		OY = hm.getOY();
		size=s;
		vert = hm.getVertices();
		enemyHeightGrid = new ArrayList<Point3d>();
		objectiveHeightGrid = new ArrayList<Point3d>();
		getEnemyHeights(eg,hm);
		getObjectiveHeights(ol,hm);
		makeSquares();
		
		picker = new PickTool(terBG);
		picker.setMode(PickTool.GEOMETRY_INTERSECT_INFO);
		
		for(int i=0; i<vert.length;i+=4){
			int index = findIndex(i);
			heightIndex.add(index);
			int squareX = (int) (vert[i+3].x+size/2)/SquareSize;
			int squareY = (int) (vert[i+3].z+size/2)/SquareSize;
			if(squareX == nSquares)
				squareX--;
			if(squareY == nSquares)
				squareY--;
			
			TerSquare sq = squares.get(squareX).get(squareY);
			sq.addZone(index,vert[i]);
			sq.addZone(index,vert[i+1]);
			sq.addZone(index,vert[i+2]);
			sq.addZone(index,vert[i+3]);
		}
		
		for(int i=0;i<nSquares;i++)
			for(int j=0;j<nSquares;j++)
				squares.get(i).get(j).makeTer();
		
	}
	private void makeSquares(){
		for(int i=0;i<nSquares;i++)
			squares.add(new ArrayList<TerSquare>());
		
		for(int i=0;i<nSquares;i++)
			for(int j=0;j<nSquares;j++){
				squares.get(i).add(new TerSquare(i,j,numZones,root));
				terBG.addChild(squares.get(i).get(j));
			}
			
		
		
	}
	
	public BranchGroup getBG(){
		return terBG;
	}
	
	private int findIndex (int x){
		double h = quadHeight(x);
		for(int i=0;i<numZones;i++)
			if(h>boundaries[i])
				return i;
		return 0;
	}
	
	private double quadHeight(int x){
		return (vert[x].y + vert[x+1].y + vert[x+2].y + vert[x+3].y)/4;
	}
	
	public double getOY(){
		return OY;
	}
	
	public boolean inBound (double x, double y)	{
		if(Math.abs(x)>size/2-borderSize || Math.abs(y)>size/2-borderSize)
			return false;
		return true;
	}
	
	public double getHeight(double x, double y, double curY){
		Point3d p = new Point3d(x,maxHeight*2,y);
		picker.setShapeRay(p, down);
		PickResult picked = picker.pickClosest();
		if(picked!=null && picked.numIntersections()!=0){
			PickIntersection pi = picked.getIntersection(0);
			Point3d next;
			try{
				next=pi.getPointCoordinates();
			}
			catch(Exception e){
				System.out.println(e);
				return curY;
			}
			return next.y;
		}
		return curY;
	}
	
	private void getEnemyHeights(ArrayList<Point2d> h, WrapHeightMap w){
		for(Point2d p : h){
			enemyHeightGrid.add(new Point3d(p.x - size/2,w.getHeight((int)p.x, (int)p.y),p.y - size/2)); // p.x and p.y should already be ints
			
		}
	}																									// so the casting changes nothing
	
	private void getObjectiveHeights(ArrayList<Point2d> h, WrapHeightMap w){
		for(Point2d p : h){
			objectiveHeightGrid.add(new Point3d((int)p.x - size/2,w.getHeight((int)p.x, (int)p.y),(int)p.y - size/2)); 
			
		}
	}	
	
	public ArrayList<Point3d> getEnemyHeightGrid(){
		return enemyHeightGrid;
	}
	
	public ArrayList<Point3d> getObjectiveHeightGrid(){
		return objectiveHeightGrid;
	}
	
	public int getSize(){
		return size;
	}
	
	public void removeObj(int index){
		objectiveHeightGrid.remove(index);
		bird.setHeightIndex(heightIndex);
		bird.setObjs(objectiveHeightGrid);
		
	}
	
	public void buildBird(){
		bird.setHeightIndex(heightIndex);
		bird.setObjs(objectiveHeightGrid);
	}
}
