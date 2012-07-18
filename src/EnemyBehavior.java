import java.util.ArrayList;
import java.util.Enumeration;

import javax.media.j3d.Behavior;
import javax.media.j3d.Transform3D;
import javax.media.j3d.WakeupOnElapsedTime;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;


public class EnemyBehavior extends Behavior{
	private Enemy myEnemy;
	private long milis = 40;
	private Mech User;
	private double boundSize1 = 25; // how close user can get without triggering the behavior
	private double killZone1 = 5; // how close before it explodes
	private double boundSize2 = 10; // distance droid will try to stay at
	private double killZone2 = 30; // distance where droid start shooting
	private double step1 = 0.3; // how fast it moves
	private double step2away = 0.4;
	private double step2to = 0.3;
	private double aboveTer;
	private Terrain ter;
	private boolean chase = false;
	private int index;
	private Transform3D temp = new Transform3D();
	Vector3d vec = new Vector3d();
	private Objective obj;
	
	public EnemyBehavior(int i,Enemy e, Mech u, double at, Terrain t,Objective j){
		obj = j;
		index=i;
		ter=t;
		aboveTer = at;
		User=u;
		myEnemy = e;
		
	}
		
	public void initialize(){
		wakeupOn(new WakeupOnElapsedTime(milis));
	}

	public void processStimulus(Enumeration enm) {
		if (index==1)
			processMine();
		else
			processDroid();
		wakeupOn(new WakeupOnElapsedTime(milis));
	}
	
	public void processMine(){
		Vector3d vec = new Vector3d();
		User.getTrans().get(vec);
		Point3d eLoc = myEnemy.getLoc();
		double deltaX = (vec.x-eLoc.x);
		double deltaZ = (vec.z-eLoc.z);
		double dist = Math.sqrt(deltaX*deltaX + deltaZ*deltaZ);
		if(dist<=boundSize1)
			chase = true;
		if(chase){
			double factX = deltaX/dist;
			double factZ = deltaZ/dist;
			deltaX=eLoc.x + factX*step1;
			deltaZ=eLoc.z + factZ*step1;
			myEnemy.setTG(new Vector3d(deltaX,ter.getHeight(deltaX,deltaZ,myEnemy.getTerHeight()),deltaZ));
		}
		if(dist<=killZone1){
			myEnemy.kill(false);
		}
		
			
	}
	
	public void processDroid(){
		Vector3d vec = new Vector3d();
		User.getTrans().get(vec);
		Point3d eLoc = myEnemy.getLoc();
		double deltaX = (vec.x-eLoc.x);
		double deltaZ = (vec.z-eLoc.z);
		double dist = Math.sqrt(deltaX*deltaX + deltaZ*deltaZ);
		if(dist<killZone2){
			temp=User.getTrans();
			temp.get(vec);
			double tx = vec.x + (Math.random()-0.5)*(dist*dist)/70;
			double ty = vec.y + (Math.random()-0.5)*(dist*dist)/70;
			double tz = (Math.sqrt(vec.x*vec.x+vec.y*vec.y+vec.z*vec.z -tx*tx - ty*ty))+Math.random();
			if(vec.z<0)
				tz*=-1;
			if(new Point3d(tx,ty,tz).distance(new Point3d(vec.x,vec.y,vec.z)) < 0.5){
				User.hit();
				User.doDamage(15);
			}
			else
				User.miss();
			myEnemy.Shot(new Point3d(tx,ty,tz));
		}
		
		if(dist<boundSize2){
			goAway(eLoc,deltaX,deltaZ,dist);
		}
		
		if(obj==null){ 
			if(dist>killZone2/2){
				goTo(eLoc,deltaX,deltaZ,dist);
			}
		}
		else{
			Point3d pnt = new Point3d();
			pnt = obj.getPos();
			eLoc = myEnemy.getLoc();
			deltaX = (pnt.x-eLoc.x);
			deltaZ = (pnt.z-eLoc.z);
			dist = Math.sqrt(deltaX*deltaX + deltaZ*deltaZ);
			if(dist>(Math.random()*(killZone2-5)))
				goTo(eLoc,deltaX,deltaZ,dist);
		}
		
	}
		
		private void goTo (Point3d eLoc, double deltaX,double deltaZ,double dist){
			
			double factX = deltaX/dist;
			double factZ = deltaZ/dist;
			double X=eLoc.x + factX*step2to;
			double Z=eLoc.z + factZ*step2to;
			myEnemy.setTG(new Vector3d(X,ter.getHeight(X,Z,myEnemy.getTerHeight()),Z));
		
	}
		
private void goAway (Point3d eLoc, double deltaX,double deltaZ,double dist){
			
			double factX = deltaX/dist;
			double factZ = deltaZ/dist;
			double X=eLoc.x - factX*step2away;
			double Z=eLoc.z - factZ*step2away;
			myEnemy.setTG(new Vector3d(X,ter.getHeight(X,Z,myEnemy.getTerHeight()),Z));
		
	}

}
