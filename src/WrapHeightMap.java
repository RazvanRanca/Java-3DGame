import javax.vecmath.Point3d;


public class WrapHeightMap { // transforms height map into list of point3ds for creating quads
	private double[][] heights;
	private Point3d[] vertices;
	double OY;
	double maxHeight;
	
	public WrapHeightMap(int s, double f){
		HeightMap h = new HeightMap (s,f);
		heights = h.genMap();
		interpHeightMap(s);
		maxHeight = h.getMaxHeight();
		OY = h.getOY();
		vertices = new Point3d [s*s*4]; // every square specified
		int count = 0;
		int x;
		int y;
		for(int j=0;j<s;j++)
			for(int i=0;i<s;i++){
				x= j - s/2;
				y= i - s/2;
				vertices[count++]=new Point3d(x,heights[i+1][j],y+1); // for each square vertices are added
				vertices[count++]=new Point3d(x+1,heights[i+1][j+1],y+1);// in anticlockwise order from bottom left
				vertices[count++]=new Point3d(x+1,heights[i][j+1],y);
				vertices[count++]=new Point3d(x,heights[i][j],y);
			}
	}
	private void interpHeightMap (int s){
				
		for(int i=1;i<s;i++) // rest
			for(int j=1;j<s;j++)
				heights[i][j] = (heights[i][j] + heights[i-1][j-1] + heights[i-1][j] + heights[i-i][j+1]+heights[i][j+1]+heights[i+1][j+1]+heights[i+1][j]+heights[i+1][j-1]+heights[i][j-1])/9;
		for(int i=1;i<s;i++){
			heights[0][i] = (heights[1][i] + heights[1][i+1] + heights[1][i-1] + heights[0][i+1] + heights[0][i-1] + heights[0][i])/6; // top line
			heights[s][i] = (heights[s-1][i] + heights[s-1][i+1] + heights[s-1][i-1] + heights[s][i+1] + heights[s][i-1] + heights[s][i])/6;// bottom line
			heights[i][0] = (heights[i][1] + heights[i+1][1] + heights[i-1][1] + heights[i+1][0] + heights[i-1][0] + heights[i][0])/6; // first column
			heights[i][s] = (heights[i][s-1] + heights[i+1][s-1] + heights[i-1][s-1] + heights[i+1][s] + heights[i-1][s] + heights[i][s])/6; // last column
		}
		heights[0][0]= (heights[0][0]+heights[0][1]+heights[1][0]+heights[1][1])/4; // 4 corners
		heights[0][s]= (heights[0][s]+heights[0][s-1]+heights[1][s]+heights[1][s-1])/4;
		heights[s][0]= (heights[s][0]+heights[s][1]+heights[s-1][0]+heights[s-1][1])/4;
		heights[s][s]= (heights[s][s]+heights[s][s-1]+heights[s-1][s]+heights[s-1][s-1])/4;
	}
	public Point3d[] getVertices (){
			return vertices;
	}
		
	public double getOY(){
		return OY;
	}
	
	public double getMaxHeight(){
		return maxHeight;
	}
	
	public double getHeight(int x, int y){
		return heights[y][x];
	}
	

}
