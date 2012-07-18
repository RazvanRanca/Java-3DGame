import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

public class HermiteCurves {
	private Point3d startP; // start and end points
	private Point3d endP;
	private Vector3d startT; // respective tangents
	private Vector3d endT;
	private double X;
	private double Y;
	private double Z;
	private Point3d nextP = new Point3d();
		
	public HermiteCurves(Point3d p1, Point3d p2, Vector3d v1, Vector3d v2){
		startP=p1;
		endP=p2;
		startT=v1;
		endT=v2;
	}
	
	private double f1 (double s){
		return 2*s*s*s - 3*s*s +1;
	}
	
	private double f2 (double s){
		return -2*s*s*s + 3*s*s;
	}
	
	private double f3 (double s){
		return s*s*s - 2*s*s +s;
	}
	
	private double f4 (double s){
		return s*s*s - s*s;
	}
	
	public Point3d getNextPoint(double step){
		X = f1(step)*startP.x + f2(step)*endP.x +f3(step)* startT.x + f4(step)*endT.x;
		Y = f1(step)*startP.y + f2(step)*endP.y +f3(step)* startT.y + f4(step)*endT.y;
		Z = f1(step)*startP.z + f2(step)*endP.z +f3(step)* startT.z + f4(step)*endT.z;
		nextP.set(X, Y, Z);
		return nextP;
	}
}
