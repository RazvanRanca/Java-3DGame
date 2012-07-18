import java.util.ArrayList;

import javax.media.j3d.*;
import javax.vecmath.*;


import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.picking.*;


public class TextureZone extends Shape3D{
	
	private Color3f green = new Color3f(0.25f,0.75f,0.15f);
	private Color3f black = new Color3f(0.0f,0.0f,0.0f);
	private Color3f white = new Color3f(1.0f,1.0f,1.0f);
	//private String root = "/afs/inf.ed.ac.uk/user/s09/s0954584/Java/Game/src/Images/";
	private String root;
	
	public TextureZone (ArrayList<Point3d> p, String fnm,String r){
		root = r;
		zoneGeom(p);
		zoneApp(fnm);
		PickTool.setCapabilities(this, PickTool.INTERSECT_COORD);
	}
	public void zoneGeom(ArrayList<Point3d> p){
		int nr = p.size();
		QuadArray zone = new QuadArray(nr,GeometryArray.COORDINATES | GeometryArray.NORMALS | GeometryArray.TEXTURE_COORDINATE_2);
		
		Point3d[] points = new Point3d[nr];
		p.toArray(points);
		
		TexCoord2f[] texc = new TexCoord2f[nr];
		for(int i=0; i<nr;i+=4){
			texc[i] = new TexCoord2f(0.0f,0.0f); // same order as the points
			texc[i+1] = new TexCoord2f(1.0f,0.0f);
			texc[i+2] = new TexCoord2f(1.0f,1.0f);
			texc[i+3] = new TexCoord2f(0.0f,1.0f);
		}
		
		GeometryInfo gi = new GeometryInfo(GeometryInfo.QUAD_ARRAY);
		gi.setCoordinates(points);
		gi.setTextureCoordinateParams(1,2); // 1 set, 2D
		gi.setTextureCoordinates(0, texc);
		
		NormalGenerator ng = new NormalGenerator();
		ng.setCreaseAngle(90); // only make separate normals for quads with angles > 90, makes it smoother
		ng.generateNormals(gi);
		
		Stripifier st = new Stripifier(); // transform from quad to triangles, improves speed
		st.stripify(gi);
		
		setGeometry(gi.getGeometryArray());
		
	}
	
	public void zoneApp (String fnm){
		Appearance app = new Appearance();
		
		//PolygonAttributes pa = new PolygonAttributes();
		//pa.setCullFace(PolygonAttributes.CULL_NONE);
		//app.setPolygonAttributes(pa);
		
		TextureAttributes ta = new TextureAttributes();
		ta.setTextureMode(TextureAttributes.MODULATE);
		app.setTextureAttributes(ta);

		TextureLoader loader = new TextureLoader(root + fnm,TextureLoader.GENERATE_MIPMAP,null);
		Texture2D tex = (Texture2D) loader.getTexture();
		tex.setMinFilter(Texture2D.MULTI_LEVEL_LINEAR);
		app.setTexture(tex);
		
		Material mat = new Material(white,black,white,white,25.0f);// ambient,emissive,diffuse,specular,shinniness
		mat.setLightingEnable(true);
		app.setMaterial(mat);
		
		setAppearance(app);
	}

	
}
