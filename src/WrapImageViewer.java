
public class WrapImageViewer extends Thread{
	private ImageViewer iv;
	private Enemy en;
	
	public WrapImageViewer (ImageViewer i, Enemy e){
		iv = i;
		en=e;
		
	}
	
	public void run(){
		iv.showSeries(en);
	}
}
