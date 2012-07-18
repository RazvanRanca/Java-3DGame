import java.io.FileReader;
import java.util.ArrayList;
import java.util.Map;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Material;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture2D;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;

import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.image.TextureLoader;


public class ObjectiveManager extends BranchGroup{
	private Color3f red = new Color3f(0.8f,0.4f,0.3f);
	private Color3f white = new Color3f(1f,1f,1f);
	private Color3f black = new Color3f(0f,0f,0f);
	private ArrayList<Objective> objs;
	private String root;
	private TransformGroup appTG;
	private Scene oScene;
	private Terrain ter;
	private Point3d pnt = new Point3d();
	private Transform3D trans = new Transform3D();
	private String fnm =  "Images/bare.jpg"; 
	private Color3f objCol = red;
	private ArrayList<Shape3D> shapes = new ArrayList<Shape3D>();
	private EnemyManager enMan;
	
	public ObjectiveManager(Terrain t, String r){
		
		ter=t;
		root=r;
		loadObj ("Models/barrel.obj");//("Models\\star.obj");
		makeObjectives();
	}
	private void makeObjectives(){
		ArrayList<Point3d> loc = ter.getObjectiveHeightGrid();
		objs = new ArrayList<Objective>();
		if(oScene == null)
			for(int i=0; i<loc.size();i++){
				Objective o =new Objective(loc.get(i),null);
				addChild(o);
				objs.add(o);
			}
		else{		
			Map<String, Shape3D> nameMap = oScene.getNamedObjects();
			Shape3D mine = nameMap.get("default");//("superstar");
			BranchGroup rooot = oScene.getSceneGroup();
			rooot.removeAllChildren();
			for(int i=0;i<loc.size();i++){
				initObj((Shape3D)mine.cloneNode(true));
				Objective o =new Objective(loc.get(i),appTG);
				addChild(o);
				objs.add(o);
			}
		}
			
	}
	
	public ArrayList<Objective> getObjs(){
		return objs;
	}
	
	public void remObj (int index){
		objs.get(index).dissapear();
		objs.remove(index);
		shapes.get(index).setPickable(false);
		shapes.remove(index);
		enMan.removeJob(index+1);
		ter.removeObj(index);
	}
		
	public void loadObj(String loc){
		try{
			ObjectFile loader = new ObjectFile(ObjectFile.RESIZE);
				oScene = loader.load(new FileReader(root + loc)); 
			
		}
		catch (Exception e){
			System.out.println(e);
		}
	}
	
	private void initObj(Shape3D shape){
		appTG = new TransformGroup();
		Appearance app = new Appearance();
		TextureAttributes ta = new TextureAttributes();
		ta.setTextureMode(TextureAttributes.MODULATE);
		app.setTextureAttributes(ta);
		
		TextureLoader loader = new TextureLoader(root + fnm,TextureLoader.GENERATE_MIPMAP,null);
		Texture2D tex = (Texture2D) loader.getTexture();
		tex.setMinFilter(Texture2D.MULTI_LEVEL_LINEAR);
		app.setTexture(tex);
		
		Material mat = new Material (objCol,black,objCol,white,10f);
		mat.setLightingEnable(true);
		app.setMaterial(mat);
		shape.setAppearance(app);
		
		trans.set(3.0);
		appTG.setTransform(trans);
		appTG.addChild(shape);
		shapes.add(shape);
		shape.setCapability(ALLOW_PICKABLE_WRITE);
	}
	
	public void setEnemyMan(EnemyManager em){
		enMan = em;
	}

}
