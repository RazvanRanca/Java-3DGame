	import javax.media.j3d.*;
	import javax.vecmath.*;
import java.util.Enumeration;


public class Particles extends Shape3D{


	  private int POINTSIZE;
	  private final static float FADE_INCR = 0.05f;

	  private static final float GRAVITY = 9.8f;
	  private static final float TIMESTEP = 0.05f;
	  private static final float XZ_VELOCITY = 4.0f; 
	  private static final float Y_VELOCITY = 6.0f; 

	  
	  private Color3f initialCol;
	  private Color3f finalCol;

	  private PointArray pointParts;    
	  private PartclesControl partBeh;  

	  private float[] cs, vels, accs, cols;     
	  private int numPoints;
	  private double deltaR;
	  private double deltaG;
	  private double deltaB;
	  private WrapParticles wrap;
	  private BranchGroup particleBG;
	  
	  public Particles(int nps, int delay, int ps, Color3f i, Color3f f, WrapParticles wp) {
		wrap=wp;
		initialCol = i;
		finalCol = f;
		deltaR = (finalCol.x - initialCol.x)*FADE_INCR;
		deltaG = (finalCol.y - initialCol.y)*FADE_INCR;
		deltaB = (finalCol.z - initialCol.z)*FADE_INCR;
		POINTSIZE=ps;
		  
	    numPoints = nps;

	    pointParts = new PointArray(numPoints, 
					PointArray.COORDINATES | PointArray.COLOR_3 |
	                PointArray.BY_REFERENCE );

	    pointParts.setCapability(GeometryArray.ALLOW_REF_DATA_READ);
	    pointParts.setCapability(GeometryArray.ALLOW_REF_DATA_WRITE);

	    PointsUpdater updater = new PointsUpdater();
	    partBeh = new PartclesControl(delay, updater);

	    createGeometry();
	    createAppearance();
	    particleBG = new BranchGroup();
	    particleBG.addChild(this);
	    Behavior partBeh = this.getParticleBeh();
		partBeh.setSchedulingBounds(new BoundingSphere(new Point3d(0,0,0),10000));
		particleBG.addChild(partBeh);
		setPickable(false);
	  } 

	  public BranchGroup getparticleBG(){
		  return particleBG;
	  }
	  public Behavior getParticleBeh(){
	    return partBeh;  
	   }


	  private void createGeometry()
	  { 
	    cs = new float[numPoints*3];  
	    vels = new float[numPoints*3];
	    accs = new float[numPoints*3];
	    cols = new float[numPoints*3];

	    for(int i=0; i < numPoints*3; i=i+3)
	      initParticle(i);

	    pointParts.setCoordRefFloat(cs);    
	    pointParts.setColorRefFloat(cols);

	    setGeometry(pointParts);
	  }  


	  private void initParticle(int i){ 
	    cs[i] = 0.0f; cs[i+1] = 0.0f; cs[i+2] = 0.0f;   

	    double xvel = Math.random()*XZ_VELOCITY;
	    double zvel = Math.sqrt((XZ_VELOCITY*XZ_VELOCITY) - (xvel*xvel));
	    vels[i] = (float)((Math.random() < 0.5) ? -xvel : xvel);    // x vel
	    vels[i+2] = (float)((Math.random() < 0.5) ? -zvel : zvel);  // z vel
	    // y velocity
	    vels[i+1] = (float)(Math.random() * Y_VELOCITY);
	    
	    // unchanging accelerations, downwards in y direction
	    accs[i] = 0.0f; accs[i+1] = -GRAVITY; accs[i+2] = 0.0f;

	    cols[i] = initialCol.x;  cols[i+1] = initialCol.y; cols[i+2] = initialCol.z;
	  }  // end of initParticle()



	  private void createAppearance()
	  {
	    Appearance app = new Appearance();

	    PointAttributes pa = new PointAttributes();
	    pa.setPointSize( POINTSIZE );    // causes z-ordering bug
	    app.setPointAttributes(pa);

	    setAppearance(app);
	  }  // end of createAppearance()



	   public class PointsUpdater implements GeometryUpdater
	  {
	    public void updateData(Geometry geo)
	    
	    { boolean anyLeft = false;
	      for(int i=0; i < numPoints*3; i=i+3) {
	        if (cs[i+1] >= -5.0f){    
	          updateParticle(i);
	          anyLeft=true;
	        }
	       
	      }
	      if(!anyLeft)
	    	  wrap.switchOff();
	    }  


	    private void updateParticle(int i)
	    /* Calculate the particle's new position and velocity (treating 
	       is as a projectile). The acceleration is constant.
	    */
	    { cs[i] += vels[i] * TIMESTEP +
	                      0.5 * accs[i] * TIMESTEP * TIMESTEP;     // x coord
	      cs[i+1] += vels[i+1] * TIMESTEP +
	                      0.5 * accs[i+1] * TIMESTEP * TIMESTEP;   // y coord
	      cs[i+2] += vels[i+2] * TIMESTEP +
	                      0.5 * accs[i+2] * TIMESTEP * TIMESTEP;   // z coord

	      // calculate new velocities
	      vels[i] += accs[i] * TIMESTEP;      // x vel
	      vels[i+1] += accs[i+1] * TIMESTEP;  // y vel
	      vels[i+2] += accs[i+2] * TIMESTEP;  // z vel

	      updateColour(i);
	    } // end of updateParticle()


	    private void updateColour(int i){
	    /* Fade colour to finalCol*/
	    if(cols[i]!=finalCol.x){
	    	cols[i]+=deltaR;
	    	cols[i+1] += deltaG;   
	        cols[i+2] += deltaB; 
	    	}
	    }
	    
	  } 



	  // ---------------- PartclesControl inner class --------------


	  public class PartclesControl extends Behavior
	  // Request an update every timedelay ms by using the updater object.
	  {
	    private WakeupCondition timedelay;
	    private PointsUpdater updater;

	    public PartclesControl(int delay, PointsUpdater updt)
	    {  timedelay = new WakeupOnElapsedTime(delay); 
	       updater = updt;
	    }

	    public void initialize( )
	    { wakeupOn( timedelay );  }

	    public void processStimulus(Enumeration criteria)
	    { 
	      pointParts.updateData(updater);  
	      wakeupOn( timedelay );
	    }
	  } // end of PartclesControl class


	} // end of PointParticles class



