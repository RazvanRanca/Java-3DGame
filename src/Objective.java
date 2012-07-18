import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Material;
import javax.media.j3d.Switch;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import com.sun.j3d.utils.geometry.Sphere;


public class Objective extends BranchGroup{
	private TransformGroup appTG;
	private Color3f red = new Color3f(0.8f,0.4f,0.3f);
	private Color3f white = new Color3f(1f,1f,1f);
	private Color3f black = new Color3f(0f,0f,0f);
	private double X;
	private double Y;
	private double Z;
	private Switch swt;
	private double aboveTer = 2;
	
	public Objective(Point3d loc, TransformGroup a){
		swt = new Switch();
		appTG = a;
		if(appTG==null)
			makeApp();
		setPos(loc);
		swt.addChild(appTG);
		
		addChild(swt);
		swt.setCapability(Switch.ALLOW_SWITCH_WRITE);
		swt.setWhichChild(Switch.CHILD_ALL);
	}
	
	public void setPos(Point3d x){
		Vector3d vec = new Vector3d(x.x,x.y+aboveTer,x.z);
		X=x.x;
		Y=x.y + aboveTer;
		Z=x.z;
		Transform3D trans = new Transform3D();
		trans.setTranslation(vec);
		appTG.setTransform(trans);
	}
	
	public void makeApp(){
		appTG = new TransformGroup();
		Appearance app = new Appearance();
		ColoringAttributes ca = new ColoringAttributes();
		ca.setColor(red);
		app.setColoringAttributes(ca);
		
		Material mat = new Material (red,black,red,white,25f);
		mat.setLightingEnable(true);
		app.setMaterial(mat);
		
		Sphere beam = new Sphere(1f,app);
		beam.setPickable(false);
		appTG.addChild(beam);
		
	}
	
	public Point3d getPos(){
		return new Point3d(X,Y,Z);		
	}
	
	public void dissapear(){
		swt.setWhichChild(Switch.CHILD_NONE);
	}

}
