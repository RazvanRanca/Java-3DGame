import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.media.j3d.*;
import javax.vecmath.*;

import com.sun.j3d.utils.behaviors.vp.*;


public class KeyBehaviour extends ViewPlatformBehavior{ // Moves Mech and keeps view in a fixed point 
	private Mech User;                                  // behind it
	private double rotCon = Math.PI/36.0;
	private double moveCon = 0.4;
		
	private Vector3d Ufwd = new Vector3d(moveCon,0,0);
	private Vector3d Uback = new Vector3d(-moveCon,0,0);
	
	
	private int fwdK = KeyEvent.VK_W;
	private int backK = KeyEvent.VK_S;
	private int leftK = KeyEvent.VK_A;
	private int rightK = KeyEvent.VK_D;
	private int rotUpperLeft = KeyEvent.VK_Q;
	private int rotUpperRight = KeyEvent.VK_E;
	private int shift = KeyEvent.VK_SHIFT;
	
	private WakeupCondition keyPress;
	private Terrain ter;
	private double curY;
	
	private Transform3D trans = new Transform3D();
	private Transform3D move = new Transform3D();
	private Vector3d temp = new Vector3d();
	
	private boolean isFwd;
	private boolean isBack;
	private boolean isLeft;
	private boolean isRight;
	private boolean isULeft;
	private boolean isURight;
	
	private ShootBehManager sbMan;
	
	public KeyBehaviour(Terrain t, TransformGroup orient, Mech u,BranchGroup b, ShootBehManager sbm ){
		ter=t;
		sbMan = sbm;
		User = u;
		initView(orient);
		WakeupCriterion[]wc= new WakeupCriterion[2];
		wc[0] = new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED);
		wc[1] = new WakeupOnAWTEvent(KeyEvent.KEY_RELEASED);
		
		keyPress = new WakeupOr(wc);
		BoundingSphere keyBounds=new BoundingSphere(new Point3d(0,0,0),ter.getSize()+10);
		KeyTimer kt = new KeyTimer();
		kt.setSchedulingBounds(keyBounds);
		b.addChild(kt);
	}
	
	private void initView(TransformGroup orient){
		orient.setTransform(User.getFirstView());
		
		curY = ter.getOY() + User.getHeight();
		
	}
	
	public void initialize(){
		wakeupOn(keyPress);
	}
	
	public void processStimulus(Enumeration crit){
		WakeupCriterion wu;
		AWTEvent[] ev;
		
		while(crit.hasMoreElements()){
			wu = (WakeupCriterion) crit.nextElement();
			if(wu instanceof WakeupOnAWTEvent){
				ev = ((WakeupOnAWTEvent)wu).getAWTEvent();
				for(int i=0;i<ev.length;i++){
					if(ev[i].getID() == KeyEvent.KEY_PRESSED)
						processEvent((KeyEvent)ev[i],true);
					if(ev[i].getID() == KeyEvent.KEY_RELEASED)
						processEvent((KeyEvent)ev[i],false);
					}
			}
		}
		wakeupOn(keyPress);
	}
	
	private void processEvent (KeyEvent evK,boolean state){
		int code = evK.getKeyCode();
		if(code==fwdK)
			isFwd=state;
		if(code==backK)
			isBack=state;
		if(code==leftK)
			isLeft=state;
		if(code==rightK)
			isRight=state;
		if(code==rotUpperLeft)
			isULeft=state;
		if(code==rotUpperRight)
			isURight=state;
		//if(code == shift)
			if(evK.isShiftDown())
				sbMan.setSecFire(true);
			else
				sbMan.setSecFire(false);
		
	}

	public void moveUser(){
		if(isFwd && !isBack)
			moveBy(Ufwd);
		if(isBack && !isFwd)
			moveBy(Uback);
		if(isLeft && !isRight)
			rotateBy(rotCon);
		if(isRight && !isLeft)
			rotateBy(-rotCon);
		if(isULeft && !isURight)
			rotateUpper(rotCon);
		if(isURight && !isULeft)
			rotateUpper(-rotCon);
	}
	
	public class KeyTimer extends Behavior{
		private long milis = 20;
		
		public void initialize() {
			wakeupOn(new WakeupOnElapsedTime(milis));
		}

		public void processStimulus(Enumeration crit) {
			moveUser();
			wakeupOn(new WakeupOnElapsedTime(milis));
		}
		
	}

	private void moveBy(Vector3d Umv){
		Vector3d vec1 = tryMove(Umv);
		if(!ter.inBound(vec1.x, vec1.z))
			return;
		
		double nextY = ter.getHeight(vec1.x,vec1.z,curY-User.getHeight()) + User.getHeight();
		double deltaY = nextY-curY;
		
		curY=nextY;
		
		Umv.set(Umv.x,deltaY,Umv.z);
		doMove(Umv);
	}
	
	private Vector3d tryMove(Vector3d mv){
		trans=User.getTrans();
		move.setIdentity();
		move.setTranslation(mv);
		trans.mul(move);
		trans.get(temp);
		return temp;
	}
	
	private void doMove(Vector3d Umv){
		User.moveBy(Umv);
		targetTG.setTransform(User.getView());
	}
	
	private void rotateBy(double rad){
		User.rotBy(rad);
		targetTG.setTransform(User.getView());
	}
	
	private void rotateUpper(double rad){
		User.rotateBody(rad);
		targetTG.setTransform(User.getView());
	}
}

