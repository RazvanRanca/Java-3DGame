import java.io.FileReader;
import java.util.ArrayList;
import java.util.Map;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture2D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.image.TextureLoader;


public class EnemyManager extends BranchGroup{
	private Terrain ter;
	private ArrayList<Point3d> enemyGrid;
	private Mech User;
	//private String root = "/afs/inf.ed.ac.uk/user/s09/s0954584/Java/Game/src/Models/";
	private String root;
	private Scene eScene1;
	private Scene eScene2;
	private Texture2D[] ims;
	private BranchGroup sceneBG;
	private TransformGroup sceneTG;
	private int mineNo;
	private int droneNo;
	private ArrayList<Integer> jobs = new ArrayList<Integer>();
	private ObjectiveManager objMan;
	private ArrayList<Enemy> enemies = new ArrayList<Enemy>();
	private boolean music;
	
	public EnemyManager(Terrain t,Mech u, BranchGroup b, String r, TransformGroup tg, int nr1,int nr2, ObjectiveManager om, boolean mus){
		music = mus;
		objMan=om;
		objMan.setEnemyMan(this);
		mineNo = nr1;
		droneNo = nr2;
		root=r;
		sceneBG = b;
		sceneTG = tg;
		
		loadImages("Images/Explosion/explo",6);
		User=u;
		User.setEnemyManager(this);
		ter=t;
		enemyGrid = ter.getEnemyHeightGrid();
		for(int i=0;i<=ter.getObjectiveHeightGrid().size();i++)
			jobs.add(0);
		loadObj("Models/mine.obj",1);
		loadObj("Models/drone.obj",2);
		setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		makeEnemies();
	}
	
	private void makeEnemies(){
		Point3d loc;
		if(eScene1 == null || eScene2 == null){
			for(int i=0;i<enemyGrid.size();i++){
				
				loc = enemyGrid.get(i);
				if(i<mineNo){
					Enemy temp=new Enemy(this,1,loc.x,loc.z,loc.y,ter,User,ims,sceneBG,root,sceneTG,null,music);
					addChild(temp);
					enemies.add(temp);
				}
				else{
					Objective obj = getNextJob();		
					Enemy temp = new Enemy(this,2,loc.x,loc.z,loc.y,ter,User,ims,sceneBG,root,sceneTG,obj,music);
					addChild(temp);
					enemies.add(temp);
				}
			}
		}
		
		else{
				Map<String, Shape3D> nameMap = eScene1.getNamedObjects();
				Shape3D mine = nameMap.get("geosphere01"); 
				BranchGroup rooot = eScene1.getSceneGroup();
				rooot.removeAllChildren();
				for(int i=0;i<mineNo;i++){
					
					loc = enemyGrid.get(i);
					Enemy temp = new Enemy(this,1,loc.x,loc.z,loc.y,ter,User,(Shape3D)mine.cloneNode(true),ims,sceneBG,root,sceneTG,null,music);
					addChild(temp);
					enemies.add(temp);
				}
				nameMap = eScene2.getNamedObjects();
				mine = nameMap.get("geosphere03"); 
				rooot = eScene2.getSceneGroup();
				rooot.removeAllChildren();
				for(int i=mineNo;i<mineNo + droneNo;i++){
					Objective obj = getNextJob();
					loc = enemyGrid.get(i);
					Enemy temp = new Enemy(this,2,loc.x,loc.z,loc.y,ter,User,(Shape3D)mine.cloneNode(true),ims,sceneBG,root,sceneTG,obj,music);
					addChild(temp);
					enemies.add(temp);
				}

		}
	}
	
	public Objective getNextJob(){
		int u =jobs.get(0);
		if(u<2){
			jobs.set(0,u+1);
			return null;
		}
		else{
			int nr=1;
			while(true){
				for(int i=1;i<jobs.size();i++){
					u=jobs.get(i);
					if(u<nr){
						jobs.set(i, u+1);
						return objMan.getObjs().get(i-1);
					}
				}
				nr++;
			}
		}
	}
	
	public void loadObj(String loc, int in){
		try{
			ObjectFile loader = new ObjectFile(ObjectFile.RESIZE);
			if(in==1)
				eScene1 = loader.load(new FileReader(root + loc)); 
			else
				eScene2 = loader.load(new FileReader(root + loc)); 
		}
		catch (Exception e){
			System.out.println(e);
		}
	}
	
	private void loadImages (String fnm, int nr){
		
		TextureLoader loader;
		ims = new Texture2D[nr];
		for(int i=0;i<nr;i++){
			loader = new TextureLoader(root + fnm + i + ".gif",null);
			ims[i]=(Texture2D) loader.getTexture();
			if(ims[i]==null)
				System.out.println("Texture load failed in" + root + fnm + i);
		}
	}
	
	public void removeJob(int index){
		jobs.remove(index);
	}
	
	public void gameWon(){
		for(Enemy e : enemies)
			e.kill(true);
		
	}
}
