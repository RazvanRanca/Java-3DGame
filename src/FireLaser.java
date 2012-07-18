import javax.vecmath.Point3d;


public class FireLaser extends Thread{
	
	private Point3d target;
	private Laser las;
	private Missile mis;
	private Mech User;
	private int index;
	private Enemy enemyHit;
	private Enemy myEnemy;
		
	public FireLaser(Point3d t,Laser l, Mech u, int n){// only called w/ in 1or2
		target=t;
		las=l;
		User=u;
		index=n;
		
	}
	
	public FireLaser(Point3d t,Missile m, Mech u, int n, Enemy en){ // only called w/ in 3or4
		target=t;
		mis=m;
		User=u;
		index=n;
		enemyHit = en;
		
	}
	
	public FireLaser(Point3d t,Laser l,Enemy e, int n, Mech u){// only called w/ in 5
		target=t;
		las=l;
		index=n;
		myEnemy=e;
		User = u;
	}
	
	
	public void run(){
		if(index==1){
			target = User.rotLaser1(target);
			Enemy newEnemyHit=User.getEnemy();
			
			las.shoot(target, User.getLaser1Pos(),newEnemyHit,null);
			
		}
			
		else if(index==2){
			target = User.rotLaser2(target);
			Enemy newEnemyHit=User.getEnemy();
			
			las.shoot(target, User.getLaser2Pos(),newEnemyHit,null);
			
		}
		
		else if(index==3){
			/*target = User.rotMissile1(target);
			Enemy newEnemyHit=User.getEnemy();*/
			
			mis.shoot(target, User.getMissile1Pos(),enemyHit);
		}
		
		else if(index==4){
			//target = User.rotMissile2(target);
			//Enemy newEnemyHit=User.getEnemy();
			
			mis.shoot(target, User.getMissile2Pos(),enemyHit);
		}
		
		else if(index==5){
			las.shoot(target, myEnemy.getLocTrans(), null,User);
		}
		
		else
			System.out.println("FireLaser index out of bounds");
		
	}

}
