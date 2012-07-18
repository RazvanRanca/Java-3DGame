import javax.media.j3d.Behavior;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Group;
import javax.media.j3d.Node;
import javax.media.j3d.Switch;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;


public class WrapParticles extends Thread{
	private BranchGroup bg;
	private TransformGroup tg;
	private Bounds bounds;
	private int num;
	private int delay;
	private int pointSize;
	private Color3f initCol;
	private Color3f lastCol;
	
	public WrapParticles (int no, int del, int size, Color3f init, Color3f last, int bSize, Transform3D loc, BranchGroup scene){
		num=no;
		delay=del;
		pointSize=size;
		initCol = init;
		lastCol = last;		
		bounds = new BoundingSphere(new Point3d(0,0,0),bSize);
	    bg = new BranchGroup();
	    bg.setCapability(BranchGroup.ALLOW_DETACH);
	    tg = new TransformGroup();
	    tg.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
	    
	    tg.setTransform(loc);
	    bg.addChild(tg);
	    scene.addChild(bg);
	}
	
	public void run(){
		Particles p =new Particles(num,delay,pointSize,initCol,lastCol,this);
		tg.addChild(p.getparticleBG());
	    
	}
	
	public void switchOff(){
		bg.detach();
	}
	
	

}
