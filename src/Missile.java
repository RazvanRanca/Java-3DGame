import java.io.FileReader;
import java.util.Map;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Switch;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.geometry.Cylinder;


public class Missile extends BranchGroup{
	
	private boolean finishedShot;
	private double step = 1;
	private long sleep = 30;
	
	private Switch swt;
	private TransformGroup missileTG;
	private Vector3d startV,curV,stepV;
	private Point3d startP;
	private Point3d nextP;
	private double damage = 50;
	private Transform3D cur = new Transform3D();
	private Transform3D rot = new Transform3D();
	private AxisAngle4d axis = new AxisAngle4d();
	private Color3f al1 = new Color3f(0.9f,0.9f,0.5f);
	private Color3f al2 = new Color3f(0.3f,0.3f,0.3f);
	private Color3f brown = new Color3f(139/255f,69/255f,19/255f);
	private Mech User;
	private HermiteCurves hc;
	private Vector3d normal = new Vector3d();
	private BranchGroup sceneBG;
	
	public Missile(Transform3D t,TransformGroup trans, Mech u, BranchGroup b){
		sceneBG = b;
		User=u;
		missileTG = new TransformGroup();
		swt= new Switch();
		swt.addChild(trans.cloneTree());
		finishedShot=true;
		curV=new Vector3d();
		stepV=new Vector3d();
		startV=new Vector3d();
		
		makeMissile(t);
		
	}
	
	private void makeMissile(Transform3D tran){
		tran.get(startV);
		startP = new Point3d(startV.x,startV.y,startV.z);
		missileTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		missileTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		missileTG.setTransform(tran);
		
		swt.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		swt.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		
			/*Appearance app = new Appearance();
			ColoringAttributes ca = new ColoringAttributes();
			Color3f red = new Color3f(0.8f,0.4f,0.3f);
			ca.setColor(red);
			app.setColoringAttributes(ca);
		
			Cylinder beam = new Cylinder(0.1f,2,app);
			beam.setPickable(false);
			missileTG.addChild(beam);*/
		
		missileTG.addChild(swt);
		addChild(missileTG);
		swt.setWhichChild(Switch.CHILD_NONE);
		swt.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
		swt.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
	}
	
		
	/*public void shoot(Point3d target, Transform3D tran,Enemy enemy){
		finishedShot=false;
		tran.get(startV);
		startP = new Point3d(startV.x,startV.y,startV.z);
		missileTG.setTransform(tran);
		swt.setWhichChild(Switch.CHILD_ALL);
		double dist = startP.distance(target);
		calcStepVec(target,dist);
		double curDist = 0;
		curV.set(startV);
		missileTG.getTransform(cur);
		
		while(curDist<=dist){
			cur.setTranslation(curV);
			missileTG.setTransform(cur);
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
		
		if(enemy!=null){
			enemy.enemyHit((Math.random() -0.5)*10 + damage);
			swt.addChild(new WrapParticles(30,20,2,al1,al2,1000));
		}
		else
			swt.addChild(new WrapParticles(30,20,2,brown,brown,1000));
		swt.setWhichChild(1);		
			
	}*/
	
	public void shoot(Point3d target, Transform3D tran,Enemy enemy){
	finishedShot=false;
	tran.get(startV);
	startP = new Point3d(startV.x,startV.y,startV.z);
	missileTG.setTransform(tran);
	swt.setWhichChild(Switch.CHILD_ALL);
	double dist = startP.distance(target);
	curV.set(startV);
	missileTG.getTransform(cur);
	int steps = (int) dist;
	double angle = User.getNormalAngle();
	if(target.z>(User.getPos()).z){
		angle-=(Math.PI/2 + angle)*2;
		
	}
	
	normal.set((-100*Math.cos(Math.PI/2-angle)),30,(-100*Math.sin(Math.PI/2-angle)));
	hc = new HermiteCurves(startP,target,normal, new Vector3d(0,0,0));
	
	for(double i=0;i<=steps;i++){
		nextP = hc.getNextPoint(i/steps);
		curV.set(nextP.x, nextP.y, nextP.z);
		double rad = startV.angle(curV);
		if(target.z>(User.getPos()).z)
			rad*=-1;
		startV.cross(startV,curV);
		axis.set(startV, rad);
		rot.setIdentity();
		rot.setRotation(axis);
		
		cur.setTranslation(curV);
		rot.mul(cur);
		missileTG.setTransform(rot);
		
		startV.set(curV);
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
	if(enemy!=null){
		enemy.enemyHit((Math.random() -0.5)*10 + damage);
		(new WrapParticles(30,20,2,al1,al2,1000,cur,sceneBG)).run();
	}
	else
		(new WrapParticles(30,20,2,brown,brown,1000,cur,sceneBG)).run();
	swt.setWhichChild(1);		
		
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


