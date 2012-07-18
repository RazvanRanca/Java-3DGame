import java.util.ArrayList;

import javax.media.j3d.BranchGroup;
import javax.vecmath.Point3d;


public class TerSquare extends BranchGroup{
	private int X;
	private int Y;
	private int numZones;
	private String[] textures = {"Images/gravel.jpg","Images/grass.jpg","Images/moss.jpg"};
	private ArrayList<ArrayList<Point3d>> zones = new ArrayList<ArrayList<Point3d>>();
	private String root;
	public TerSquare(int x,int y, int n,String r){
		root=r;
		X=x;
		Y=y;
		numZones=n;
		for(int i=0;i<numZones;i++)
			zones.add(new ArrayList<Point3d>());
	}
	
	public void addZone (int index, Point3d elem){
		zones.get(index).add(elem);
	}
	
	public void makeTer (){
		for(int i=0; i<numZones;i++)
			if(zones.get(i).size()>0)
				addChild(new TextureZone(zones.get(i),textures[i],root));
	}
	
	
}
