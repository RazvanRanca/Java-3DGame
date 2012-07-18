import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.geometry.Cylinder;


public class Laser extends BranchGroup{
	
	private boolean finishedShot;
	private double step = 1;
	private long sleep = 30;
	
	private Switch swt;
	private TransformGroup laserTG;
	private Vector3d startV,curV,stepV;
	private Point3d startP;
	private float laserR = 0.1f;//0.02
	private float laserH = 0.7f;//0.5
	private double damage = 10;
	private Color3f al1 = new Color3f(0.9f,0.9f,0.5f);
	private Color3f al2 = new Color3f(0.3f,0.3f,0.3f);
	private Color3f brown = new Color3f(139/255f,69/255f,19/255f);
	private BranchGroup sceneBG;
	private TransformGroup appTG;
	
	private Transform3D cur = new Transform3D();
	
	public Laser(Transform3D t, BranchGroup b, TransformGroup a){
		
		appTG = a;
		sceneBG = b;
		finishedShot=true;
		curV=new Vector3d();
		stepV=new Vector3d();
		startV=new Vector3d();
		makeLaser(t);
		swt.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
		swt.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
		
	}
	
	private void makeLaser(Transform3D tran){
		tran.get(startV);
		startP = new Point3d(startV.x,startV.y,startV.z);
		laserTG = new TransformGroup();
		laserTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		laserTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		laserTG.setTransform(tran);
		swt = new Switch();
		swt.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		swt.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		if(appTG == null){
			appTG = new TransformGroup();
			makeApp();
		}
		
		swt.addChild(appTG);
		laserTG.addChild(swt);
		addChild(laserTG);
		swt.setWhichChild(Switch.CHILD_NONE);
	}
	
	private void makeApp(){
		Appearance app = new Appearance();
		ColoringAttributes ca = new ColoringAttributes();
		Color3f red = new Color3f(0.8f,0.4f,0.3f);
		ca.setColor(red);
		app.setColoringAttributes(ca);
		
		Cylinder beam = new Cylinder(laserR,laserH,app);
		beam.setPickable(false);
		appTG.addChild(beam);
	}
	
		
	public void shoot(Point3d target, Transform3D tran,Enemy enemy,Mech user){
		finishedShot=false;
		tran.get(startV);
		startP = new Point3d(startV.x,startV.y,startV.z);
		laserTG.setTransform(tran);
		swt.setWhichChild(Switch.CHILD_ALL);
		double dist = startP.distance(target);
		calcStepVec(target,dist);
		double curDist = 0;
		curV.set(startV);
		laserTG.getTransform(cur);
		
		while(curDist<=dist){
			cur.setTranslation(curV);
			laserTG.setTransform(cur);
			curV.add(stepV);
			curDist+=step;
			try{
				Thread.sleep(sleep);
			}
			catch(Exception e){
				System.out.println(e);
			}
		}
		
		
		finishedShot=true;
		cur.setIdentity();
		cur.set(new Vector3d(target.x,target.y,target.z));
		if(user==null){
			if(enemy!=null){
				enemy.enemyHit(damage);
				(new WrapParticles(10,20,1,al1,al2,1000,cur,sceneBG)).run();
			}	
			else
				(new WrapParticles(10,20,1,brown,brown,1000,cur,sceneBG)).run();
			swt.setWhichChild(1);	
		}
		else{
			swt.setWhichChild(Switch.CHILD_NONE);
		}
		
		
	}
	
	private void calcStepVec(Point3d target, double dist){
		double moveFrac = step/dist;
		double incX = (target.x - startP.x)*moveFrac;
		double incY = (target.y - startP.y)*moveFrac;
		double incZ = (target.z - startP.z)*moveFrac;
		stepV.set(incX,incY,incZ);
	}
	
	public boolean getFinishedShot(){
		return finishedShot;
	}

}
