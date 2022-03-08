package ul;

import robocode.*;
import robocode.util.*;

import java.util.Arrays;

public class RoboCop extends Robot {
    public void run() {
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);
        setAdjustGunForRobotTurn(true);

        turnRight(turnUpright());
        toCenter(getQuadrant());
        turnRight(turnUpright());
        turnLeft(45);

        while (true) {
            turnRadarRight(Double.POSITIVE_INFINITY);
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        engageTarget(e);
    }

    public void engageTarget(ScannedRobotEvent e) {
        double distance = e.getDistance();
        int firepower = 0;

        if (distance >= 500)
            firepower = 1;
        else if (distance >= 300)
            firepower = 2;
        else
            firepower = 3;

        double absoluteBearing = (Math.toRadians(getHeading())) + e.getBearingRadians();
        double enemyX = getX() + e.getDistance() * Math.sin(absoluteBearing);
        double enemyY = getY() + e.getDistance() * Math.cos(absoluteBearing);

        double enemyHeading = 0, enemyHeadingOld = 0, enemyHeadingChange = 0;
        enemyHeadingOld = enemyHeading;
        enemyHeading = e.getHeadingRadians();
        enemyHeadingChange = enemyHeading - enemyHeadingOld;
        double velocity = e.getVelocity();

        double deltaTime = 0;
        double predictedX = enemyX, predictedY = enemyY;
        while (deltaTime * (20 - 3 * firepower) < distanceBetweenPoints(getX(), getY(), predictedY, predictedY)) {
            deltaTime++;
            predictedX += Math.sin(enemyHeading) * velocity;
            predictedY += Math.cos(enemyHeading) * velocity;
            enemyHeading += enemyHeadingChange;
        }

        double angle = Utils.normalAbsoluteAngle(Math.atan2(predictedX - getX(), predictedY - getY()));
        rotateGun(predictedX, predictedY);
        fire(firepower);
        movement();
    }

    public double distanceBetweenPoints(double x1, double x2, double y1, double y2) {
        return Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
    }

    public void rotateGun(double enemyX, double enemyY) {
        double angle = absoluteBearing(getX(), getY(), enemyX, enemyY);
        turnGunRight(normalizedBearing(angle - getGunHeading()));
    }

    double absoluteBearing(double x1, double y1, double x2, double y2) {
        double xo = x2 - x1;
        double yo = y2 - y1;
        double hyp = Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
        double arcSin = Math.toDegrees(Math.asin(xo / hyp));
        double bearing = 0;

        if (xo > 0 && yo > 0) { // both pos: lower-Left
            bearing = arcSin;
        } else if (xo < 0 && yo > 0) { // x neg, y pos: lower-right
            bearing = 360 + arcSin; // arcsin is negative here, actuall 360 - ang
        } else if (xo > 0 && yo < 0) { // x pos, y neg: upper-left
            bearing = 180 - arcSin;
        } else if (xo < 0 && yo < 0) { // both neg: upper-right
            bearing = 180 - arcSin; // arcsin is negative here, actually 180 + ang
        }

        return bearing;
    }

    double normalizedBearing(double angle) {
        while (angle > 180)
            angle -= 360;
        while (angle < -180)
            angle += 360;
        return angle;
    }

    public void movement() {
        ahead(75);
        turnRight(90);
    }

    public void onHitWall(HitWallEvent e) {

        double loc;
        loc = e.getBearing();

        if (loc > 0) {
            turnLeft(90);
        } // close if
        else {
            turnRight(90);
        } // close else
        ahead(100);
    }// closes onHitWall

    public void checkLocation() {
        double value = getX();
    }

    /**
     * This method is very verbose to explain how things work.
     * Do not obfuscate/optimize this sample.
     */
    public double turnUpright() {
        double head = getHeading();
        return (360 - head);
    }

    public int getQuadrant() {
        double x = getX();
        double y = getY();
        int store = 0;
        if (x <= 400 && y <= 400) {
            store = 1;
        } else if (x <= 400 && y <= 800) {
            store = 2;
        } else if (x <= 800 && y <= 400) {
            store = 3;
        } else {
            store = 4;
        }
        System.out.println("THE QUADRANT: " + store);
        return store;
    }

    public void toCenter(int quadrant) {
        double x, y;
        x = getX();
        y = getY();

        if (quadrant == 1 || quadrant == 2) {
            turnRight(90);
        } else {
            turnLeft(90);
        }
        ahead(Math.abs(400 - x));
        if (quadrant == 1 || quadrant == 4) {
            turnLeft(90);
        } else {
            turnRight(90);
        }

        ahead(Math.abs(400 - y));
    }
}