import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.geometry.*;
import javax.media.j3d.*;
import javax.vecmath.*;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.vp.*;

public class GameWrapper extends JPanel{ // wraps the Canvas3D in a JPanel
	private String root = "";
	private int width = 512; // for JPanel
	private int height = 512;
	private int size;
	private int boundSize;
	private double factor;
	
	private SimpleUniverse su;
	private BranchGroup sceneBG;
	private TransformGroup sceneTG;
	private BoundingSphere bounds;
	private Color3f skyCol = new Color3f(0.17f,0.07f,0.45f);
	private Terrain ter;
	private Mech User;
	private EnemyGrid enGrid;
	private int mineNo;
	private int enemyCellSize = 10;
	private double safeZone = 30;
	private Canvas3D cd;
	private ShootBehManager sbMan;
	private int borderSize = 10;
	private int droneNo;
	private ObjectiveManager objMan;
	private BirdsEye bird;
	private InfoBars bars;
	private boolean music;
	
	public GameWrapper(int s, double f, int nr1, int nr2,BirdsEye be,InfoBars ib, boolean mus){
		music = mus;
		bird=be;
		bars=ib;
		size = s;
		factor = f;
		mineNo = nr1;
		droneNo=nr2;
		boundSize = s+10; 
		setLayout( new BorderLayout() );
		setOpaque(false);
		setPreferredSize( new Dimension(width,height));
		
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		Canvas3D canvas = new Canvas3D(config);
		cd = canvas;
		add(canvas);
		canvas.setFocusable(true);
		canvas.requestFocus();
		
		
		su = new SimpleUniverse(canvas);
		
		createScene(canvas);
		
		createUserControls();
		//orbit(canvas);
		
		sceneBG.compile();
		su.addBranchGraph(sceneBG);
		if(music)
			new WrapPlayer("back.wav").start();
	}
	
	private void createScene(Canvas3D canvas)
	{
		sceneBG = new BranchGroup();
		sceneTG = new TransformGroup();
		bounds = new BoundingSphere(new Point3d(0,0,0),boundSize);
		
		addLights();
		addBackground();
		//addFog();
		enGrid = new EnemyGrid(borderSize,mineNo+droneNo,size-2*borderSize,enemyCellSize,safeZone,mineNo);
		ter = new Terrain(borderSize,size,factor,enGrid.getEnemyGrid(),root,enGrid.getObjectiveList(),bird);
		objMan= new ObjectiveManager(ter,root);
		sceneTG.addChild(objMan);
		sceneTG.addChild(ter.getBG() );
		User=new Mech(ter,sceneBG,bounds,root,objMan,bird,bars,music);
		sceneTG.addChild(User.getBG());
		sceneTG.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
		sceneTG.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
		initShotBeh();
		createUserControls();
		addEnemies();
		
		/*MouseRotate behavior = new MouseRotate();
		behavior.setTransformGroup(sceneTG);
		sceneTG.addChild(behavior);
		behavior.setSchedulingBounds(bounds);*/
		sceneTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		sceneBG.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		sceneBG.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		sceneBG.addChild(sceneTG);
		
	}
	
	private void addFog(){
		ExponentialFog fog = new ExponentialFog(skyCol,0.03f);
		//LinearFog fog = new LinearFog(skyCol,30f,60f);
		fog.setInfluencingBounds(bounds);
		sceneTG.addChild(fog);
	}
	
	
	private void addLights(){
		Color3f white = new Color3f(1.0f,1.0f,1.0f);
		
		AmbientLight aLight = new AmbientLight(white);
		aLight.setInfluencingBounds(bounds);
		sceneTG.addChild(aLight);
		
		Vector3f lightDir = new Vector3f(-1.0f,-1.0f,-1.0f);
		DirectionalLight dLight = new DirectionalLight(white, lightDir);
		sceneTG.addChild(dLight);
	}
	
	private void addBackground(){ //just blue sky
		Background bk = new Background();
		bk.setApplicationBounds(bounds);
		bk.setColor(skyCol);
		sceneTG.addChild(bk);
	}
	
	private void orbit (Canvas3D canvas){
		OrbitBehavior orbit = new OrbitBehavior(canvas, OrbitBehavior.REVERSE_ALL);
		orbit.setSchedulingBounds(bounds);
		
		ViewingPlatform vp = su.getViewingPlatform();
		vp.setViewPlatformBehavior(orbit);
	}
	
	private void createUserControls(){
		View view = su.getViewer().getView();
		view.setBackClipDistance(20);
		view.setFrontClipDistance(0.05);
		
		ViewingPlatform vp = su.getViewingPlatform();
		TransformGroup tg = vp.getViewPlatformTransform();
		
		KeyBehaviour kb = new KeyBehaviour(ter, tg, User,sceneBG,sbMan);
		//cd.addKeyListener(kb);
		kb.setSchedulingBounds(bounds);
		vp.setViewPlatformBehavior(kb);
	}
	
	private void addEnemies(){
		EnemyManager enm = new EnemyManager(ter,User,sceneBG,root,sceneTG,mineNo,droneNo,objMan,music);
		
		sceneTG.addChild(enm);
		
	}
	
	private void initShotBeh(){
		/*Transform3D loc1 = User.getLaser1Pos();
		Vector3d vloc1 = new Vector3d();
		loc1.get(vloc1);
		AmmoManager am1 = new AmmoManager(1,User,sceneTG);
		
		Transform3D loc2 = User.getLaser2Pos();
		Vector3d vloc2 = new Vector3d();
		loc2.get(vloc2);
		AmmoManager am2 = new AmmoManager(2,User,sceneTG);*/
		
		ShootBehManager sht = new ShootBehManager(cd,sceneBG,sceneTG,bounds,User,ter,root,bars);
		//ShootingBehaviour sht1 = new ShootingBehaviour(canvas,sceneBG,bounds,new Point3d(vloc1.x,vloc1.y,vloc1.z),am1,User,1,ter);
		//ShootingBehaviour sht2 = new ShootingBehaviour(canvas,sceneBG,bounds,new Point3d(vloc2.x,vloc2.y,vloc2.z),am2,User,2,ter);
		//sceneTG.addChild(sht1);
		//sceneTG.addChild(sht2);
		sbMan = sht;
		sceneTG.addChild(sht);
		
		
	}
	
	private void addBackgroundSound(String sound){
		
		MediaContainer cont = new MediaContainer("file:"+root + sound);
		BackgroundSound drone = new BackgroundSound(cont,1.0f);
		drone.setSchedulingBounds(bounds);
		drone.setEnable(true);
		drone.setLoop(BackgroundSound.INFINITE_LOOPS);
		sceneBG.addChild(drone);
	}
	
}
