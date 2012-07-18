import java.io.FileReader;
import java.util.Enumeration;
import java.util.Map;

import javax.media.j3d.Appearance;
import javax.media.j3d.Behavior;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.WakeupOnElapsedTime;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.picking.PickCanvas;
import com.sun.j3d.utils.picking.PickIntersection;
import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.behaviors.PickMouseBehavior;


public class ShootBehManager extends PickMouseBehavior{
	
	private long maxLaserTime;
	private long maxMissileTime;
	private AmmoManager amM1;
	private AmmoManager amM2;
	private AmmoManager amB1;
	private AmmoManager amB2;
	private Mech User;
	private Terrain ter;
	private boolean secFire = false;
	int nr;
	private Scene mScene;
	//private String root = "/afs/inf.ed.ac.uk/user/s09/s0954584/Java/Game/src/Models/";
	private String root;
	private TransformGroup appTG;
	private TransformGroup appTGRot;
	private Enemy enemyHit;
	private InfoBars bars;
	
	public ShootBehManager(Canvas3D canvas, BranchGroup sceneBG, TransformGroup sceneTG, Bounds bounds,Mech u,Terrain t,String r, InfoBars ib){
		super(canvas,sceneBG,bounds);
		bars=ib;
		root=r;
		ter = t;
		setSchedulingBounds(bounds);
		AmmoTimer timer = new AmmoTimer();
		timer.setSchedulingBounds(bounds);
		sceneBG.addChild(timer);
		appTG = new TransformGroup();
		appTGRot = new TransformGroup();
		
		loadObj("Models/missile.obj");
		makeApp();
		
		
		Transform3D gigi = new Transform3D();
		gigi.set(2);
		appTG.setTransform(gigi);
		appTGRot.setTransform(gigi);
		Transform3D rot = new Transform3D();
		rot.rotZ(Math.PI);
		appTGRot.getTransform(gigi);
		gigi.mul(rot);
		appTGRot.setTransform(gigi);
		User=u;
		amM1 = new AmmoManager(1,User,sceneTG,appTG,sceneBG,null,bars);
		amM2 = new AmmoManager(2,User,sceneTG,appTG,sceneBG,null,bars);
		amB1 = new AmmoManager(3,User,sceneTG,appTG,sceneBG,null,bars);
		amB2 = new AmmoManager(4,User,sceneTG,appTGRot,sceneBG,null,bars);
		maxLaserTime = amM1.getMaxLaser();
		maxMissileTime = amB1.getMaxMissile();
		pickCanvas.setMode(PickCanvas.GEOMETRY_INTERSECT_INFO);
		/*Vector3d vvv = new Vector3d(0,50,0);
		Transform3D gigi = new Transform3D();
		gigi.setTranslation(vvv);
		gigi.set(5);
		appTG.setTransform(gigi);
		
		sceneTG.addChild(appTG);*/
		
	}
	
	
	public void updateScene(int xpos, int ypos){
		
		pickCanvas.setShapeLocation(xpos, ypos);
		
		Point3d eye = pickCanvas.getStartPosition();
		PickResult pr = null;
		pr=pickCanvas.pickClosest();
				
		if(pr!=null){
			PickIntersection pi = pr.getClosestIntersection(eye);
			Point3d target = pi.getPointCoordinatesVW();
			try{
				Shape3D p = (Shape3D) pr.getObject();
				Enemy e = (Enemy) p.getParent().getParent().getParent().getParent().getParent();
				enemyHit=e;
			}
			catch (Exception ex){}
			if(!secFire){
				nr = amM1.availableLaser();
				if(nr!=-1)
					new FireLaser(target,amM1.getLaser(nr),User,1).start();
				
				nr = amM2.availableLaser();
				if(nr!=-1)
					new FireLaser(target,amM2.getLaser(nr),User,2).start();
			}
			else{
				nr = amB1.availableMissile();
				if(nr!=-1)
					new FireLaser(target,amB1.getMissile(nr),User,3,enemyHit).start();
				
				nr = amB2.availableMissile();
				if(nr!=-1)
					new FireLaser(target,amB2.getMissile(nr),User,4,enemyHit).start();
			}
							
		}
		
	}
	
	public void setSecFire (boolean state){
		secFire = state;
	}
	
	public void loadObj(String loc){
		try{
			ObjectFile loader = new ObjectFile(ObjectFile.RESIZE);
			mScene = loader.load(new FileReader(root+loc)); 
		}
		catch (Exception e){
			System.out.println(e + "gigi");
		}
	}
	
	public void makeApp(){
	Appearance app = new Appearance();
	if(mScene == null){
		ColoringAttributes ca = new ColoringAttributes();
		Color3f red = new Color3f(0.8f,0.4f,0.3f);
		ca.setColor(red);
		app.setColoringAttributes(ca);
	
		Cylinder beam = new Cylinder(0.1f,2,app);
		beam.setPickable(false);
		appTG.addChild(beam);
		appTGRot.addChild(beam);
	}
	else{
		Map<String, Shape3D> nameMap = mScene.getNamedObjects();
		BranchGroup root = mScene.getSceneGroup();
		root.removeAllChildren();
		for(String name:nameMap.keySet()){
			Shape3D missile = nameMap.get(name); 
			appTG.addChild((Shape3D)missile);
			appTGRot.addChild((Shape3D)missile.cloneNode(true));
		}
				
	}
}
	public class AmmoTimer extends Behavior{
		long milis = 50;
		
		public void initialize(){
			wakeupOn(new WakeupOnElapsedTime(milis));
		}
		
		public void processStimulus(Enumeration crit){
			if(amM1.getLaserTime() <= maxLaserTime)
				amM1.addLaserTime(25000000l); 
			if(amM2.getLaserTime() <= maxLaserTime)
				amM2.addLaserTime(25000000l);
			if(amB1.getMissileTime() <= maxMissileTime)
				amB1.addMissileTime(25000000l);
			if(amB2.getMissileTime() <= maxMissileTime)
				amB2.addMissileTime(25000000l);
			bars.updateLaser(amM1.getLaserTime());
			bars.updateMissile(amB1.getMissileTime());
			wakeupOn(new WakeupOnElapsedTime(milis));
			}
		
	
	}
}


