
public class WrapPlayer extends Thread{
	private String[] name = new String[1];
	
	public WrapPlayer(String n){
		name[0] = n;
	}
	
	public void run(){
		new BufferedPlayer(name);
	}

}
