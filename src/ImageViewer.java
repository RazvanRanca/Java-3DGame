import javax.media.j3d.Appearance;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Material;
import javax.media.j3d.QuadArray;
import javax.media.j3d.RenderingAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture;
import javax.media.j3d.Texture2D;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.TexCoord2f;

import com.sun.j3d.utils.image.TextureLoader;


public class ImageViewer extends Shape3D{
	private long milis = 100;
	private int nVerts = 4;
	private Texture2D[] ims;
	private Texture2D texture;
	private Appearance appT;
	
	
	public ImageViewer (Point3f cent, float size, Texture2D[] im){
		ims = im;
		createGeom(cent,size);
		createApp();
		setPickable(false);
	}
	
	private void createGeom(Point3f cent, float size){
		QuadArray plane = new QuadArray(nVerts,GeometryArray.COORDINATES | GeometryArray.TEXTURE_COORDINATE_2);
		
		Point3f p1 = new Point3f(cent.x-size/2, cent.y-size/2,cent.z);
		Point3f p2 = new Point3f(cent.x+size/2, cent.y-size/2,cent.z);
		Point3f p3 = new Point3f(cent.x+size/2, cent.y+size/2,cent.z);
		Point3f p4 = new Point3f(cent.x-size/2, cent.y+size/2,cent.z);
		
		plane.setCoordinate(0, p1);
		plane.setCoordinate(1, p2);
		plane.setCoordinate(2, p3);
		plane.setCoordinate(3, p4);
		
		TexCoord2f tex = new TexCoord2f(); 
		tex.set(0.0f,0.0f);
		plane.setTextureCoordinate(0,0,tex);
		tex.set(1.0f,0.0f);
		plane.setTextureCoordinate(0,1,tex);
		tex.set(1.0f,1.0f);
		plane.setTextureCoordinate(0,2,tex);
		tex.set(0.0f,1.0f);
		plane.setTextureCoordinate(0,3,tex);
		
		setGeometry(plane);
		
	}
	
	private void createApp(){
		Appearance app = new Appearance();
		
		TransparencyAttributes ta = new TransparencyAttributes();
		ta.setTransparencyMode(TransparencyAttributes.BLENDED);
		app.setTransparencyAttributes(ta);
			
		
		texture = new Texture2D(Texture2D.BASE_LEVEL,Texture.RGBA,ims[0].getWidth(),ims[0].getHeight());
		texture.setMagFilter(Texture2D.BASE_LEVEL_LINEAR);
		texture = ims[0];
		texture.setCapability(Texture.ALLOW_IMAGE_WRITE);
		
		app.setTexture(texture);
		app.setCapability(Appearance.ALLOW_TEXTURE_READ);
		app.setCapability(Appearance.ALLOW_TEXTURE_WRITE);
		setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
		setCapability(Shape3D.ALLOW_APPEARANCE_READ);
		setAppearance(app);
	}
	
	public void showSeries(Enemy e){ // must be called from within thread
		for(int i=0; i<ims.length;i++){
			appT=getAppearance();
			texture = ims[i];
			appT.setTexture(texture);
			//e.rotExplosion();
			setAppearance(appT);
			try{
				Thread.sleep(milis);
			}
			catch(Exception ex){
				System.out.println(ex);
			}
		}
	}

}
