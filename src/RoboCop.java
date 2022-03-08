package ul;

import robocode.*;
import java.awt.Color;


public class RoboCop extends Robot {
    int arenaHeight;
    int arenaWidth;
    
    public void run() {
        turnRight(turnUpright());
        toCenter(getQuadrant());        
        turnRight(turnUpright());
        turnLeft(45);

        while(true) {
            movement();
        }
    }

    public void movement() {
        ahead(100);
        turnRight(90);
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        double robotDistance = e.getDistance();
        
        if (robotDistance > 400) {
            fire(1);
        } else if (robotDistance > 200) {
            fire(2);
        } else {
            fire(3);
        }
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
    
    public double turnUpright(){
        double head = getHeading();
        return (360 - head);
    }

    public int getQuadrant(){
        double x = getX();
        double y = getY();
        int store = 0;
        if(x <= 400 && y <= 400){
            store =  1;
        } else if(x <= 400 && y <= 800) {
            store =  2;
        } else if(x <= 800 && y <= 400) {
            store =  3;
        } else {
            store =  4;
        }
        System.out.println("THE QUADRANT: " + store);
        return store;
    }

    public void toCenter(int quadrant) {
        double x, y;
        x = getX();
        y = getY();
        
        if(quadrant == 1 || quadrant == 2) {
            turnRight(90);
        } else {
            turnLeft(90);
        }
        ahead(Math.abs(400 - x));

        if(quadrant == 1 || quadrant == 4) {
            turnLeft(90);
        } else {
            turnRight(90);
        }
        ahead(Math.abs(400 - y));
    }
}