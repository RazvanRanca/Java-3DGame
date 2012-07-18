import java.util.Map;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;

import com.sun.j3d.loaders.Scene;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.picking.PickTool;

public class Enemy extends BranchGroup{
	private boolean music;
	private int index;
	private boolean hasObj = false;
	private TransformGroup enemyTG = new TransformGroup();
	private TransformGroup holderTG = new TransformGroup();
	private TransformGroup explTG = new TransformGroup();
	private Primitive p;
	private Switch swt = new Switch();
	private double X;
	private double Z;
	private Color3f enemyCol = new Color3f(0.80f,0.1f,0.1f);
	private Color3f white= new Color3f (1f,1f,1f);
	private Color3f black= new Color3f (0f,0f,0f);
	private Color3f blue= new Color3f (0.1f,0.1f,0.9f);
	private Color3f red= new Color3f (0.9f,0.1f,0.1f);
	private float rad = 2f;
	private double height;
	private Terrain ter;
	private double eHealth;
	private double eHealth1 = 200;
	private double eHealth2 = 100;
	private EnemyBehavior beh;
	private BoundingSphere enemyBounds;
	private Mech User;
	private double terHeight;
	private double aboveTer=3.5;
	private Transform3D init = new Transform3D();
	private TransformGroup objTG = new TransformGroup();
	private Color3f objCol1 = new Color3f (139/255f,150/255f,169/255f);
	private Color3f objCol2 = new Color3f (0.65f,0.05f,0.2f);
	private Shape3D shape;
	//private String root = "/afs/inf.ed.ac.uk/user/s09/s0954584/Java/Game/src/Images/";
	private String root;
	private String fnm;
	private String fnm1 = "Images/galvanized.jpg";	
	private String fnm2 = "Images/bare.jpg"; 	
	private ImageViewer iv;
	double deltaX;
	double deltaZ;
	double angle;
	private Point3d loc= new Point3d();
	private Transform3D temp = new Transform3D();
	private Vector3d eye = new Vector3d();
	private BranchGroup sceneBG;
	private TransformGroup sceneTG;
	private Transform3D cur = new Transform3D();
	private AmmoManager amE;
	private TransformGroup appTG = new TransformGroup();
	private Vector3d vec = new Vector3d();
	private double safeZone = 40;
	private Objective obj;
	private EnemyManager enMan;
	
	public Enemy(EnemyManager em,int i,double x, double z, double th, Terrain t, Mech u, Texture2D[] exploImgs, BranchGroup b,String r,TransformGroup tg,Objective o, boolean mus){
		music = mus;
		enMan=em;
		obj = o;		
		index=i;
		root = r;
		sceneBG = b;
		sceneTG = tg;
		iv = new ImageViewer(new Point3f(0,0,0),2.0f,exploImgs);
		terHeight = th;
		User = u;
		X=x;
		Z=z;
		ter = t;
		height =terHeight + aboveTer;
		initPos();
		if(index==1){
			initApp1();
			eHealth = eHealth1;
		}
		else{
			initApp2();
			eHealth=eHealth2;
			createShot();
		}
		enemyBounds=new BoundingSphere(new Point3d(0,0,0),ter.getSize()+10);
		beh = new EnemyBehavior(index,this,User,aboveTer,ter,obj);
		beh.setSchedulingBounds(enemyBounds);
		holderTG.addChild(beh);
		swt.addChild(holderTG);
		explTG.addChild(iv);
		swt.addChild(explTG);
		enemyTG.addChild(swt);
		addChild(enemyTG);
		swt.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		swt.setWhichChild(0);
		enemyTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
	}
	
	public Enemy(EnemyManager em,int i,double x, double z, double th, Terrain t, Mech u,Shape3D s,Texture2D[] exploImgs, BranchGroup b,String r,TransformGroup tg,Objective o, boolean mus){
		music = mus;
		enMan=em;
		obj=o;		
		index=i;
		sceneTG = tg;
		if(index==1){
			fnm = fnm1;
			eHealth = eHealth1;
		}
		else{
			fnm = fnm2;
			eHealth = eHealth2;
			createShot();
		}
		root=r;
		sceneBG=b;
		iv = new ImageViewer(new Point3f(0,0,0),2.0f,exploImgs);
		shape = s;
		initObj();
		terHeight = th;
		User = u;
		X=x;
		Z=z;
		ter = t;
		height =terHeight + aboveTer;
		initPos();
		enemyBounds=new BoundingSphere(new Point3d(0,0,0),ter.getSize()+10);
		beh = new EnemyBehavior(index,this,User,aboveTer,ter,obj);
		beh.setSchedulingBounds(enemyBounds);
		holderTG.addChild(objTG);
		holderTG.addChild(beh);
		swt.addChild(holderTG);
		explTG.addChild(iv);
		swt.addChild(explTG);
		enemyTG.addChild(swt);
		addChild(enemyTG);
		swt.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		swt.setWhichChild(0);
		enemyTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		explTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		
		
			
	}
	
	private void initPos(){
		init.setTranslation(new Vector3d(X,height,Z));
		enemyTG.setTransform(init);
	}
	
	private void initApp1(){
		Appearance app = new Appearance();
		Material mat = new Material (enemyCol,black,enemyCol,white,25f);
		mat.setLightingEnable(true);
		app.setMaterial(mat);
		p = new Sphere(rad,app);
		holderTG.addChild(p);
		p.setCapability(ALLOW_PICKABLE_WRITE);
	}
	
	private void initApp2(){
		Appearance app = new Appearance();
		Material mat = new Material (red,black,red,white,25f);
		mat.setLightingEnable(true);
		app.setMaterial(mat);
		p = new Box(rad,rad,rad,app);
		holderTG.addChild(p);
		p.setCapability(ALLOW_PICKABLE_WRITE);
	}
	
	public void enemyHit(double ammount){
		if(ammount>20 && music)
			new WrapPlayer("damage.wav").start();
		eHealth -= ammount;
		if(eHealth<=0 && eHealth>=-ammount){
			if(music)
				new WrapPlayer("ruble.wav").start();
			if(hasObj)
				shape.setPickable(false);
			else
				p.setPickable(false);
			swt.setWhichChild(1);
			rotExplosion();
			new WrapImageViewer(iv,this).run();
			swt.setWhichChild(Switch.CHILD_NONE);
		if(index==2){
		randomPos();
		initPos();
		eHealth = 100;
		swt.setWhichChild(0);
		if(hasObj)
			shape.setPickable(true);
		else
			p.setPickable(true);
		obj = enMan.getNextJob();
		}
		}
	}
	
	public void randomPos(){
		cur = User.getTrans();
		cur.get(vec);
		int max = ter.getSize();
		do{
			X = (Math.random()-0.5)*max-5; 
			Z = (Math.random()-0.5)*max-5;
		}
			while(Math.sqrt(X*X + Z*Z) <safeZone+Math.sqrt(vec.x*vec.x + vec.y*vec.y));
		height = ter.getHeight(X, Z, height-aboveTer) + aboveTer;
	}
	
	public void kill(boolean endGame){
		if(!endGame){
		if(music)
			new WrapPlayer("explosion.wav").start();
		User.explode();
		User.doDamage(100);
		eHealth=-1;
		swt.setWhichChild(Switch.CHILD_NONE);
		cur.setIdentity();
		cur.set(new Vector3d(X,height,Z));
	    (new WrapParticles(200,30,2,blue,red,1000,cur,sceneBG)).run();
	    
		if(hasObj)
			shape.setPickable(false);
		else
			p.setPickable(false);
		}
		else{
			swt.setWhichChild(Switch.CHILD_NONE);
			cur.setIdentity();
			cur.set(new Vector3d(X,height,Z));
		    (new WrapParticles(400,30,3,blue,red,1000,cur,sceneBG)).run();
		}
	}
	
	public Point3d getLoc(){
		return new Point3d(X,height,Z);
	}
	
	public Transform3D getLocTrans(){
		Point3d pnt = getLoc();
		temp.setTranslation(new Vector3d(pnt.x,pnt.y,pnt.z));
		return temp;
	}
	
	public double getTerHeight(){
		return terHeight;
	}
	
	public void setTG(Vector3d v){
		X=v.x;
		terHeight=v.y;
		Z=v.z;
		height = aboveTer + terHeight;
		init.setTranslation(new Vector3d(X,height,Z));
		enemyTG.setTransform(init);
	}
	
	private void initObj(){
		Appearance app = new Appearance();
		TextureAttributes ta = new TextureAttributes();
		ta.setTextureMode(TextureAttributes.MODULATE);
		app.setTextureAttributes(ta);
		
		TextureLoader loader = new TextureLoader(root + fnm,TextureLoader.GENERATE_MIPMAP,null);
		Texture2D tex = (Texture2D) loader.getTexture();
		tex.setMinFilter(Texture2D.MULTI_LEVEL_LINEAR);
		app.setTexture(tex);
		Material mat;
		if(index==1)
			mat = new Material (objCol1,black,objCol1,white,10f);
		else
			mat = new Material (objCol2,black,objCol2,white,10f);
		mat.setLightingEnable(true);
		app.setMaterial(mat);
		shape.setAppearance(app);
		Transform3D tr = new Transform3D();
		tr.set(3.0);
		objTG.setTransform(tr);
		objTG.addChild(shape);
		hasObj = true;
		shape.setCapability(ALLOW_PICKABLE_WRITE);
	}
	
	public void rotExplosion(){
		User.getView().get(eye);
		loc = getLoc();
		deltaX = eye.x - loc.x;
		deltaZ = eye.z - loc.z;
		angle = Math.atan2(deltaX, deltaZ);
		temp.setIdentity();
		temp.rotY(angle);
		explTG.setTransform(temp);
	}
	public void createShot(){
		makeApp();
		amE = new AmmoManager(5,User,sceneTG,appTG,sceneBG,this,null);
	}
	public void makeApp(){
		Appearance app = new Appearance();
		ColoringAttributes ca = new ColoringAttributes();
		Color3f red = new Color3f(0.8f,0.4f,0.3f);
		ca.setColor(blue);
		app.setColoringAttributes(ca);
		
		Sphere beam = new Sphere(0.1f,app);
		beam.setPickable(false);
		appTG.addChild(beam);
		
	}
	
	public void Shot(Point3d target){
		int nr = amE.availableEnemyLaser();
		if(nr!=-1)
			new FireLaser(target,amE.getLaser(nr),this,5,User).start();
	}
	
}
