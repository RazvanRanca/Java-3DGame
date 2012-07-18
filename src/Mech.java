import java.util.ArrayList;
import java.util.HashMap;

import javax.media.j3d.*;
import javax.vecmath.*;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.picking.PickIntersection;
import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.PickTool;


public class Mech {
	
	private boolean dead;
	private int health = 1000;
	private int nrHit = 0;
	private int nrMiss = 0;
	private int nrExplosion = 0;
	Color3f red= new Color3f (0.75f,0.25f,0.25f);
	Color3f white= new Color3f (1f,1f,1f);
	Color3f black= new Color3f (0f,0f,0f);
	Color3f blue= new Color3f (0.25f,0.25f,0.75f);
	Color3f darkAlum= new Color3f (61/255f,81/255f,102/255f);
	private Color3f lightAlum = new Color3f (139/255f,150/255f,169/255f);

	private BoundingSphere bounds;
	private PickTool picker;
	private BranchGroup mechBG;
	private TransformGroup mechPos;
	private TransformGroup bodyPos;
	private Transform3D trans, move, rot;
	private Limb limb;
	private TransformGroup temp;
	private Terrain ter;
	private TransformGroup View = new TransformGroup();
	private double ViewHeight=10.2;
	private ArrayList<Limb> limbs = new ArrayList<Limb>();
	private HashMap<String,Integer> limbNames = new HashMap<String,Integer>(); // Limbs are: body,bGun1,bGun2
													//mGun1,mGun2,mRest1,mRest2,uLeg1,uLeg2,lLeg1,lLeg2
	private Vector3d v3d1 = new Vector3d();
	private Vector3d v3d2 = new Vector3d();
	private TransformGroup laser1TG;
	private TransformGroup laser2TG;
	
	private float bodySize = 1;
	private float uLegx = 0.4f;
	private float uLegy = 1.0f;
	private float uLegz = 0.3f;
	private float lrad = 0.275f;
	private float lh = 2.5f;
	private float lAngle = 45;
	private float footx=0.6f;
	private float footy=0.1f;
	private float footz=0.13f;
	private float mGunRestx = 0.32f;
	private float mGunResty = 0.25f;
	private float mGunRestz = 1.1f;
	private float mGunr = 0.15f;
	private float mGunh = 2f;
	private float shootr = 0.04f;
	private float shooth = 0.2f;
	private float bGunRest1x = 0.2f;
	private float bGunRest1y = 0.25f;
	private float bGunRest1z = 0.4f;
	private float bGunRest2x = 0.15f;
	private float bGunRest2y = 0.15f;
	private float bGunRest2z = 0.5f;
	private float bGunr = 0.5f;
	private float bGunh = 1.5f;
	private double height = 3.6;	
	private double rotCon = Math.PI/36.0;
	private Enemy enemyHit;
	//private String root = "/afs/inf.ed.ac.uk/user/s09/s0954584/Java/Game/src/Images/";
	private String root;////////////////////1/////////////////2/////////////////3/////////////////4////////////////////5/////////////////6/////////////////7//////////////////8///////////////////9/
	private String[] textures = {"Images/treadPlate.jpg","Images/bare.jpg","Images/bare1.jpg","Images/painted.jpg","Images/copper.jpg","Images/base.jpg","Images/bronze.jpg","Images/scratch.jpg","Images/scratch1.jpg"};
	private Quat4f objRot = new Quat4f();
	private ArrayList<Objective> objs;
	private ObjectiveManager objMan;
	private double pickUpZone = 2;
	private BirdsEye bird;
	private InfoBars bars;
	private EnemyManager enMan;
	private BranchGroup sceneBG;
	private boolean music;
	
	public Mech (Terrain t, BranchGroup s, BoundingSphere b, String r,ObjectiveManager om, BirdsEye be,InfoBars ib,boolean mus){
		music = mus;
		sceneBG=s;
		bars = ib;
		bird=be;
		objMan = om;		 
		root=r;
		bounds = b;
		ter = t;
		trans = new Transform3D();
		move = new Transform3D();
		rot = new Transform3D();
		mechBG = new BranchGroup();
		mechPos = new TransformGroup();
		mechPos.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		mechPos.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		mechBG.setCapability(BranchGroup.ALLOW_DETACH);
		mechBG.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		mechBG.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		
		initPos();
		buildBody();
		bodyPos.addChild(View);
		buildLeg(1);
		buildLeg(2);
		//initLaser1(); for finetunning laser position
		//initLaser2();
		ter.buildBird();
		v3d2=getPos();
		bird.setUser((int)v3d2.x,(int)v3d2.y);
		mechBG.addChild(mechPos);
		picker = new PickTool(s);
		picker.setMode(PickTool.GEOMETRY_INTERSECT_INFO);
		bars.updateHealth(health);
		
	}
	
	public void initPos(){
		move.setTranslation(new Vector3d(0,ter.getOY()+height,0));
		rot.setIdentity();
		rot.rotY(toRad(90));
		move.mul(rot);
		mechPos.setTransform(move);
		move.setIdentity();
		trans.setIdentity();
		Vector3d vec = new Vector3d(-30,ViewHeight,0);
		trans.setTranslation(vec);
		rot.setIdentity();
		rot.rotY(toRad(-90));
		trans.mul(rot);		
		rot.rotX(-rotCon*5);
		trans.mul(rot);
		
		View.setTransform(trans);
		
				
	}
	
	private void buildBody(){
		move.setTranslation(new Vector3d(0,bodySize-0.2,0));
		
		limb = (new Limb((new Box(bodySize,bodySize,bodySize,Box.GENERATE_TEXTURE_COORDS | Box.GENERATE_NORMALS,getApp(lightAlum,textures[0]))),move,mechPos));
		
		limbs.add(limb);
		TransformGroup bodyStart = limb.getStart();
		bodyStart.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		bodyStart.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		limbNames.put("body", limbs.size()-1);
		bodyPos = limb.getInit();
		TransformGroup seat1 = new TransformGroup();
		TransformGroup seat2 = new TransformGroup();
		float sx = 0.45f;
		float sy = 0.35f;
		temp = limb.getEnd();
		
		move.setTranslation(new Vector3d((bodySize + sx - 0.05f),(-1*(bodySize -sy)),0));
		seat1.setTransform(move);
		seat1.addChild(new Box(sx,sy,bodySize-0.2f,getApp(black)));
		limb.getEnd().addChild(seat1);
		float rad = 0.9f;
		float h = 1.5f;
		move.setTranslation(new Vector3d((bodySize-0.2f),0.45f,0));
		seat2.setTransform(move);
		seat2.addChild(new Cone(rad,h,getApp(black)));
		limb.getEnd().addChild(seat2);
		
		buildBigGun(temp,1);
		buildBigGun(temp,2);
		buildMachineGun(temp,1);
		buildMachineGun(temp,2);
		
	}
	private void buildBigGun(TransformGroup p, int nr){
		int ind;
		if(nr==1)
			ind=1;
		else
			ind=-1;
		move.setTranslation(new Vector3d (-0.3,bodySize-bGunRest1y-0.1,ind*(bodySize + bGunRest1z -0.1))); 
		limb = new Limb(new Box(bGunRest1x,bGunRest1y,bGunRest1z,Box.GENERATE_TEXTURE_COORDS | Box.GENERATE_NORMALS,getApp(white,textures[2])),move,p);
		
		move.setTranslation(new Vector3d(0,0,ind*(bGunRest1z-0.2)));
		limb.setEndTG(move);
		move.setTranslation(new Vector3d (0,0,ind* bGunRest2z ));
		limb = new Limb(new Box(bGunRest2x,bGunRest2y,bGunRest2z,Box.GENERATE_TEXTURE_COORDS | Box.GENERATE_NORMALS,getApp(white,textures[6])),move,limb.getEnd());
		rot.rotX(toRad(-ind*90));
		limb.setxOrt(rot);
		
		move.setTranslation(new Vector3d (0,0,ind*(bGunRest2z-0.1)));
		limb.setEndTG(move);
		move.setTranslation(new Vector3d (-ind*bGunr/2,-ind*(1.3*bGunh/5),ind*bGunr/2));
		limb = new Limb(new Cylinder(bGunr,bGunh,Cylinder.GENERATE_TEXTURE_COORDS | Cylinder.GENERATE_NORMALS,getApp(white,textures[3])),move,limb.getEnd());
		rot.rotZ(toRad(ind*90));
		limb.setzOrt(rot);
		
		limbs.add(limb);
		limb.getStart().setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		limb.getStart().setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		limbNames.put("bGun"+nr, limbs.size()-1);
	}
	private void buildMachineGun(TransformGroup p, int nr){
		int ind;
		if(nr==1)
			ind=1;
		else
			ind=-1;
		move.setTranslation(new Vector3d(0,-bodySize/2,ind*(bodySize+mGunRestz-0.2)));
		limb = new Limb( new Box (mGunRestx,mGunResty,mGunRestz,Box.GENERATE_TEXTURE_COORDS | Box.GENERATE_NORMALS,getApp(white,textures[0])),move,p);
		
		move.setTranslation(new Vector3d(0,0,ind*(mGunRestz-0.2)));
		limb.setEndTG(move);
		limbs.add(limb);
		limb.getStart().setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		limb.getStart().setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		limbNames.put("mRest"+nr, limbs.size()-1);
		move.setTranslation(new Vector3d(0.2,0,ind*(mGunh/2-0.1)));
		rot.rotX(toRad(ind*90));
		move.mul(rot);
		
		limb = new Limb( new Cylinder(mGunr,mGunh,Cylinder.GENERATE_TEXTURE_COORDS | Cylinder.GENERATE_NORMALS,getApp(lightAlum,textures[8])),move,limb.getEnd());
		
		rot.rotY(toRad(ind*90));
		limb.setyOrt(rot);
		
		move.setIdentity();
		move.setTranslation(new Vector3d(0,(mGunh/2-0.1),0));
		limb.setEndTG(move);
		
		limbs.add(limb);
		limb.getStart().setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		limb.getStart().setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		limbNames.put("mGun"+nr, limbs.size()-1);
		p = limb.getEnd();
				
		float delta = 0.07f;
		move.setTranslation(new Vector3d(ind*(mGunr - delta),(shooth/2),0));
		limb = new Limb(new Cylinder(shootr,shooth,getApp(black)),move,p);
		
		move.setTranslation(new Vector3d(ind*(mGunr - delta)/(-2),(shooth/2),(mGunr-delta)*0.67));
		limb = new Limb(new Cylinder(shootr,shooth,getApp(black)),move,p);
		
		move.setTranslation(new Vector3d(ind*(mGunr - delta)/(-2),(shooth/2),(mGunr-delta)*-0.67));
		limb = new Limb(new Cylinder(shootr,shooth,getApp(black)),move,p);
		
		move.setIdentity();
	}
	
	private void buildLeg(int nr){
		if(nr==1)
			move.setTranslation(new Vector3d(-1*(bodySize-(uLegx*1.75)),(-1*uLegy +0.2f),(bodySize-uLegz)-0.1f));
		else
			move.setTranslation(new Vector3d(-1*(bodySize-(uLegx*1.75)),(-1*uLegy +0.2f),-1*(bodySize-uLegz)+0.1f));
		limb = new Limb (new Box(uLegx,uLegy,uLegz,Box.GENERATE_TEXTURE_COORDS | Box.GENERATE_NORMALS,getApp(white,textures[7])),move,mechPos);
		
		rot.rotZ(toRad(-20));
		limb.setzOrt(rot);
		
		limbs.add(limb);
		limb.getStart().setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		limb.getStart().setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		limbNames.put("uLeg"+nr, limbs.size()-1);
		
		move.setTranslation(new Vector3d (0,(-1*uLegy + 0.2),0));
		limb.setEndTG(move);
		move.setTranslation(new Vector3d (-0.1,(-1*lh/2 +0.2),0));
		rot.rotZ(toRad(lAngle));
		limb = new Limb (new Cylinder(lrad,lh,Cylinder.GENERATE_TEXTURE_COORDS | Cylinder.GENERATE_NORMALS,getApp(white,textures[5])),move,limb.getEnd());
		limb.setzOrt(rot);
		
		limbs.add(limb);
		limb.getStart().setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		limb.getStart().setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		limbNames.put("lLeg"+nr, limbs.size()-1);
		
		temp=limb.getEnd();
		move.setTranslation(new Vector3d (0,(-1*lh/2 +0.2),0));
		limb.setEndTG(move);
		move.setTranslation(new Vector3d ((footx -0.2f),-2*footy,0));
		limb = new Limb (new Box(footx,footy,footz,Box.GENERATE_TEXTURE_COORDS | Box.GENERATE_NORMALS,getApp(darkAlum,textures[4])),move,temp);
		limb = new Limb (new Box(footx,footy,footz,Box.GENERATE_TEXTURE_COORDS | Box.GENERATE_NORMALS,getApp(darkAlum,textures[4])),move,temp);
		rot.rotY(toRad(30));
		limb.setyOrt(rot);
		limb = new Limb (new Box(footx,footy,footz,Box.GENERATE_TEXTURE_COORDS | Box.GENERATE_NORMALS,getApp(darkAlum,textures[4])),move,temp);
		rot.rotY(toRad(-30));
		limb.setyOrt(rot);
	}
	
	private Appearance getApp (Color3f col, String fnm){
		Appearance app = new Appearance();
		
		TextureAttributes ta = new TextureAttributes();
		ta.setTextureMode(TextureAttributes.MODULATE);
		app.setTextureAttributes(ta);
		
		TextureLoader loader = new TextureLoader(root + fnm,TextureLoader.GENERATE_MIPMAP,null);
		Texture2D tex = (Texture2D) loader.getTexture();
		tex.setMinFilter(Texture2D.MULTI_LEVEL_LINEAR);
		app.setTexture(tex);
		
		Material mat = new Material (col,black,col,white,25f);
		mat.setLightingEnable(true);
		app.setMaterial(mat);
		return app;
		
	}
	
	private Appearance getApp (Color3f col){
		Appearance app = new Appearance();
		
		Material mat = new Material (col,black,col,white,25f);
		mat.setLightingEnable(true);
		app.setMaterial(mat);
		return app;
	}
	
	private void initLaser1(){
		move.setIdentity();
		move.setTranslation(new Vector3d(0,0,0));
		laser1TG = new TransformGroup();
		laser1TG.setTransform(move);
		(limbs.get(limbNames.get("mGun1"))).getEnd().addChild(laser1TG);
		
	}
	private void initLaser2(){
		move.setIdentity();
		move.setTranslation(new Vector3d(0,0,0));
		laser2TG = new TransformGroup();
		laser2TG.setTransform(move);
		(limbs.get(limbNames.get("mGun2"))).getEnd().addChild(laser2TG);
		
	}
	
	public Transform3D getLaser1Pos(){
		move.setIdentity();
		(limbs.get(limbNames.get("mGun1"))).getEnd().getLocalToVworld(move);
		return move;
		}
	
	public Transform3D getLaser2Pos(){
		move.setIdentity();
		(limbs.get(limbNames.get("mGun2"))).getEnd().getLocalToVworld(move);
		return move;
		}
	
	public Transform3D getMissile1Pos(){
		move.setIdentity();
		(limbs.get(limbNames.get("bGun1"))).getEnd().getLocalToVworld(move);
		return move;
		}
	
	public Transform3D getMissile2Pos(){
		move.setIdentity();
		(limbs.get(limbNames.get("bGun2"))).getEnd().getLocalToVworld(move);
		return move;
		}
	
	public BranchGroup getBG(){
		return mechBG;
	}
	
	private float toRad(float deg){
		return (float)((Math.PI*deg)/180);
	}
	
	public void moveBy(Vector3d mv){
		mechPos.getTransform(trans);
		move.setIdentity();
		move.setTranslation(mv);
		trans.mul(move);
		mechPos.setTransform(trans);
		trans.get(v3d1);
		objs=objMan.getObjs();
		for(int i=0;i<objs.size();i++){
			
			Point3d pnt = objs.get(i).getPos();
			if (new Point2d(pnt.x,pnt.z).distance(new Point2d(v3d1.x,v3d1.z)) <pickUpZone){
				if(music)
					new WrapPlayer("jingle.wav").start();
				objMan.remObj(i);
				if(objs.size()==0)
					enMan.gameWon();
			}
					
		}
		ter.buildBird();
		v3d2=getPos();
		bird.setUser((int)v3d2.x,(int)v3d2.z);
	}
	
	public void rotBy(double r){
		mechPos.getTransform(trans);
		rot.rotY(r);
		trans.mul(rot);
		mechPos.setTransform(trans);
		
	}
	
	public double getHeight(){
		return height;
	}
	
	public Transform3D getTrans(){
		Vector3d vec = new Vector3d();
		mechPos.getTransform(move);
		return move;
	}
	
	public Transform3D getView(){
		trans.setIdentity();
		move.setIdentity();
		View.getTransform(move);
		View.getLocalToVworld(trans);
		trans.mul(move);
		return trans;
	}
	
	public Transform3D getFirstView(){
		trans.setIdentity();
		move.setIdentity();
		View.getTransform(move);
		Vector3d first = new Vector3d(0,10,0);
		trans.setTranslation(first);
		move.mul(trans);
		return move;
	}
	
	public void rotateBody(double rad){
		bodyPos.getTransform(trans);
		rot.rotY(rad);
		trans.mul(rot);
		bodyPos.setTransform(trans);
	}
	
	public Limb getLimb (int index){
		return limbs.get(index);
	}
	
	public int getLimbIndex (String name){
		return limbNames.get(name);
	}
	
	public Point3d rotLaser1(Point3d target){
		Limb curL = limbs.get(limbNames.get("mGun1"));
		Vector3d dir = new Vector3d();
		this.getLaser1Pos().get(dir);
		
		Point3d loc = new Point3d(dir.x,dir.y,dir.z);
		
		double deltaX = target.x-dir.x;
		double deltaY = target.y-dir.y;
		double deltaZ = target.z-dir.z;
		dir.set(target.x-dir.x,target.y-dir.y,target.z-dir.z);
		float alfaX;
		float alfaY;
		
		if(deltaZ>=0)
			alfaX = (float)Math.atan(deltaX/deltaZ);
		else
			alfaX = (float)(Math.atan(deltaX/deltaZ) + Math.PI);
		
		
		alfaY = (float)Math.atan(deltaY/Math.sqrt(deltaX*deltaX+deltaZ*deltaZ));
		
			
		rot.rotY(alfaX);
		Matrix3d max = new Matrix3d();
		View.getLocalToVworld(move);
		move.getRotationScale(max);
		max.invert();
		move.setIdentity();
		move.setRotation(max);
		rot.mul(move);
		
		curL.setyOrt(rot);
		
		rot.setIdentity();
		rot.rotZ(alfaY);
		curL.setxOrt(rot);
		
		return checkObstacles(loc,dir,target);
	}
	
	public Point3d rotLaser2(Point3d target){
		Vector3d vec = new Vector3d();
		move.setIdentity();
		Limb curL = limbs.get(limbNames.get("mGun2"));
		curL.getInit().getLocalToVworld(move);
		move.get(vec);
		double deltaX = target.x-vec.x;
		double deltaY = target.y-vec.y;
		double deltaZ = target.z-vec.z;
		Vector3d dir = new Vector3d();
		this.getLaser2Pos().get(dir);
		Point3d loc = new Point3d(dir.x,dir.y,dir.z);
		dir.set(target.x-loc.x,target.y-loc.y,target.z-loc.z);
		float alfaX;
		float alfaY = (float)Math.atan(deltaY/Math.sqrt(deltaX*deltaX+deltaZ*deltaZ));
		
		if(deltaZ>0)
			alfaX = (float)(Math.atan(deltaX/deltaZ) + Math.PI);
		else
			alfaX = (float)(Math.atan(deltaX/deltaZ));
		rot.rotY(alfaX);
		Matrix3d max = new Matrix3d();
		
		View.getLocalToVworld(move);
		move.getRotationScale(max);
		max.invert();
		move.setIdentity();
		move.setRotation(max);
		rot.mul(move);
		curL.setyOrt(rot);
		
		
		rot.setIdentity();
		rot.rotZ(alfaY);
		curL.setxOrt(rot);
		
		return checkObstacles(loc,dir,target);
	}
	
	/*public Point3d rotMissile1(Point3d target){
		
		Vector3d dir = new Vector3d();
		this.getMissile1Pos().get(dir);
		
		Point3d loc = new Point3d(dir.x,dir.y,dir.z);
		dir.set(target.x-loc.x,target.y-loc.y,target.z-loc.z);
		return checkObstacles(loc,dir,target);
	}
	
	public Point3d rotMissile2(Point3d target){
	
		Vector3d dir = new Vector3d();
		this.getMissile2Pos().get(dir);
		Point3d loc = new Point3d(dir.x,dir.y,dir.z);
		dir.set(target.x-loc.x,target.y-loc.y,target.z-loc.z);
		return checkObstacles(loc,dir,target);
	}*/
	
	private Point3d checkObstacles(Point3d loc,Vector3d dir,Point3d target){
		if(music)
			new WrapPlayer("phasers3.wav").start();
		picker.setShapeRay(loc, dir);
		PickResult picked = picker.pickClosest();
		if(picked!=null && picked.numIntersections()!=0){
			PickIntersection pi = picked.getIntersection(0);
			Point3d next;
			try{
				next=pi.getPointCoordinatesVW();
			}
			catch(Exception e){
				System.out.println(e);
				return target;
			}
			
			
			try{
				Shape3D p = (Shape3D) picked.getObject();
				Enemy e = (Enemy) p.getParent().getParent().getParent().getParent().getParent();
				enemyHit=e;
			}
			catch (Exception ex){}
			return next;
		}
		return target;
	}
	
	public Enemy getEnemy(){
		Enemy e = enemyHit;
		enemyHit=null;
		return e;
	}
	
	public double getNormalAngle(){
		trans.setIdentity();
		View.getLocalToVworld(trans);
		trans.get(objRot);
		double angle = (double)(2*Math.acos(objRot.w)) - Math.PI/2;
		
		return angle;
		
	}
	
	public Vector3d getPos(){
		trans.setIdentity();
		bodyPos.getLocalToVworld(trans);
		trans.get(v3d1);
		return v3d1;
	}
	
	public void doDamage(int damage){
		health -=damage;
		if(health<=0 && !dead){
			if(music)
				new WrapPlayer("pac.wav").start();
			trans.set(getPos());
		    (new WrapParticles(200,30,2,blue,red,1000,trans,sceneBG)).run();
		    mechBG.detach();
		    dead = true;
		}
		bars.updateHealth(health);
	}
	
	public void miss(){
		nrMiss++;
	}
	
	public void hit(){
		nrHit++;
	}
	
	public void explode(){
		nrExplosion++;
	}
	
	public int getHealth(){
		return health;
	}
	
	public void setEnemyManager(EnemyManager em){
		enMan = em;
	}
}
