package ul;

import robocode.*;
import java.awt.Color;

public class RoboCop extends Robot {
    int arenaHeight;
	int arenaWidth;

    public void run() {
        while (true) {
            turnRight(45);
        }
    }

    public void movement() {
        ahead(100);
		turnRight(90);
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        fire(3);
    }

    public void onHitByBullet(HitByBulletEvent e) {
        turnRight(e.getBearing());
    }

    public void onHitWall(HitWallEvent e) {
        double loc;
		loc=e.getBearing();
		
		if(loc>0){
			turnLeft(90);
		} else {
			turnRight(90);
		}

		ahead(100);
    }

    public void checkLocation(){
		double value = getX();
	}

    public void smartFire(double robotDistance) {
		if (robotDistance > 400) {
			fire(1);
		} else if (robotDistance > 200) {
			fire(2);
		} else {
			fire(3);
		}
	}
}