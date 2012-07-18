import javax.media.j3d.*;
import javax.vecmath.AxisAngle4d;

import com.sun.j3d.utils.geometry.*;


public class Limb {

	private Primitive shape;
	private TransformGroup start;
	private TransformGroup end = new TransformGroup();
	private TransformGroup init = new TransformGroup();
	private TransformGroup xOrt = new TransformGroup();
	private TransformGroup yOrt = new TransformGroup();
	private TransformGroup zOrt = new TransformGroup();
	
		
	public Limb(Primitive p, Transform3D t, TransformGroup s){
		shape = p;
		init.setTransform(t);
		start=s;
		start.addChild(xOrt);
		xOrt.addChild(yOrt);
		yOrt.addChild(zOrt);
		zOrt.addChild(init);
		init.addChild(end);
		init.addChild(shape);
		xOrt.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		xOrt.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		yOrt.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		yOrt.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		zOrt.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		zOrt.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		shape.setPickable(false);
		init.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		init.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

	}
	
	public TransformGroup getStart (){
		return start;
	}
	
	public TransformGroup getEnd (){
		return end;
	}
	
	public TransformGroup getInit (){
		return init;
	}
	
	public void setEndTG (Transform3D t){
		end.setTransform(t);
	}
	
	public void setxOrt (Transform3D t){
		xOrt.setTransform(t);
	}
	
	public void setyOrt (Transform3D t){
		yOrt.setTransform(t);
	}
	
	public void setzOrt (Transform3D t){
		zOrt.setTransform(t);
	}
	
	}
