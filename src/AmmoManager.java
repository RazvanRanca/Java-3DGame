import java.io.FileReader;
import java.util.Enumeration;
import java.util.Map;

import javax.media.j3d.Appearance;
import javax.media.j3d.Behavior;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.WakeupOnElapsedTime;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import com.sun.j3d.loaders.Loader;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.*;
import com.sun.j3d.utils.geometry.Cylinder;

public class AmmoManager {
	private int noLasers = 20;
	private int noMissiles = 5;
	private Laser[] lasers;
	private Missile[] missiles;
	private Enemy myEnemy;
	private Mech User;
	private int index;
	private TransformGroup sceneTG;
	private TransformGroup appTG;
	private BranchGroup sceneBG;
	private long maxLaserTime = 10000000000l;
	private long maxMissileTime = 5000000000l;
	private long laserTime = maxLaserTime; // 10 seconds
	private long missileTime = maxMissileTime;
	private long lLastTime; //laser
	private long lCurTime; 
	private long mLastTime; //missile
	private long mCurTime;
	private long eLastTime; //enemy
	private long eCurTime;
	private long eWait = 1000000000l; // period between enemy shots
	private long mStartHeat = 2000000000l;
	private long lStartHeat = 1000000000l;
	private boolean lSparks=true;
	private boolean mSparks=true;
	private Color3f init = new Color3f(0.35f,0.55f,0.75f);
	private Color3f last = new Color3f(0.45f,0.45f,0.45f);
	private Transform3D temp = new Transform3D();
	private Transform3D rot = new Transform3D();
	private InfoBars bars;
	
	public AmmoManager(int in,Mech u, TransformGroup s, TransformGroup a, BranchGroup b,Enemy e,InfoBars ib){
		bars = ib;
		lLastTime = System.nanoTime();
		mLastTime = System.nanoTime();
		eLastTime = System.nanoTime();
		sceneBG = b;
		appTG = a;
		User=u;
		index=in;
		sceneTG = s;
		myEnemy = e;
		if(index!=5){
			bars.updateLaser(laserTime);
			bars.updateMissile(missileTime);
		}
		if(index==1){
			lasers=new Laser[noLasers];
			for(int i=0;i<noLasers;i++){
				lasers[i]=new Laser(User.getLaser1Pos(),sceneBG,null);
				sceneTG.addChild(lasers[i]);
			}
		}
		else if(index==2){
			lasers=new Laser[noLasers];
			for(int i=0;i<noLasers;i++){
				lasers[i]=new Laser(User.getLaser2Pos(),sceneBG,null);
				sceneTG.addChild(lasers[i]);
			}
		}
		else if(index==3){
			missiles=new Missile[noMissiles];
			for(int i=0;i<noMissiles;i++){
				missiles[i]=new Missile(User.getMissile1Pos(),appTG,User,sceneBG);
				sceneTG.addChild(missiles[i]);
			}
		}
		else if(index==4){
			missiles=new Missile[noMissiles];
			for(int i=0;i<noMissiles;i++){
				missiles[i]=new Missile(User.getMissile2Pos(),appTG,User,sceneBG);
				sceneTG.addChild(missiles[i]);
			}
			
		}
		else if(index==5){
			lasers=new Laser[noLasers];
			for(int i=0;i<noLasers;i++){
				lasers[i]=new Laser(myEnemy.getLocTrans(),sceneBG,(TransformGroup)appTG.cloneTree());
				sceneTG.addChild(lasers[i]);
			}
		}
		else System.out.println("Ammo manager index out of bounds");
		
	}
	
	public Laser getLaser(int nr){
		return lasers[nr];		
	}
	
	public Missile getMissile(int nr){
		return missiles[nr];		
	}
	
	public int availableLaser(){ // for player's lasers, enemy uses different method
		lCurTime=System.nanoTime();
		
		if(laserTime<=2000000000){
			if(lSparks){
				if(index==1){
					temp = User.getLaser1Pos();
					rot.rotX(-Math.PI/2);
					temp.mul(rot);
					new WrapParticles(50,20,1,init,last,10000,temp,sceneBG).run();
				}
				if(index==2){
					temp = User.getLaser2Pos();
					rot.rotX(Math.PI/2);
					temp.mul(rot);
					new WrapParticles(50,20,1,init,last,10000,temp,sceneBG).run();
				}
				lSparks = false;
			}
		}
		else {
			if(lCurTime - lLastTime < lStartHeat){
				laserTime -= (lStartHeat - (lCurTime - lLastTime));
				bars.updateLaser(laserTime);
			}
			lLastTime = lCurTime;
			
			lSparks = true;
			for(int i=0;i<noLasers;i++)
				if(lasers[i].getFinishedShot())
					return i;
		}
		return -1;
		
	}
	
	public int availableMissile(){
		mCurTime=System.nanoTime();
		
		if(missileTime<=1500000000l){
			if(mSparks){
				if(index==3){
					temp = User.getMissile1Pos();
					rot.rotX(Math.PI/2);
					temp.mul(rot);
					new WrapParticles(75,20,1,init,last,10000,temp,sceneBG).run();
				}
				if(index==4){
					temp = User.getMissile2Pos();
					rot.rotX(-Math.PI/2);
					temp.mul(rot);
					new WrapParticles(75,20,1,init,last,10000,temp,sceneBG).run();
				}
				mSparks = false;
			}
		}
		else {
			if(mCurTime - mLastTime < mStartHeat){
				missileTime -= (mStartHeat - (mCurTime - mLastTime));
				bars.updateMissile(missileTime);
			}
			mLastTime = mCurTime;
			mSparks = true;
		for(int i=0;i<noMissiles;i++)
			if(missiles[i].getFinishedShot())
				return i;
		}
		return -1;
		
	}
	
	public int availableEnemyLaser(){
		eCurTime=System.nanoTime();
		if(eCurTime - eLastTime > eWait){
			eLastTime = eCurTime;
			for(int i=0;i<lasers.length;i++)
				if(lasers[i].getFinishedShot())
					return i;
			
		}
		return -1;
	}
	
	public void addLaserTime(long time){
		laserTime +=time;
	}
	
	public void addMissileTime(long time){
		missileTime +=time;
	}
	
	public long getLaserTime(){
		return laserTime;
	}
	
	public long getMissileTime(){
		return missileTime;
	}
	
	public long getMaxLaser(){
		return maxLaserTime;
	}
	
	public long getMaxMissile(){
		return maxMissileTime;
	}
}
