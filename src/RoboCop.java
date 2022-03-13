package ul;

import robocode.*;
import robocode.util.*;

public class RoboCop extends Robot {

    /**
     * Main run method, moves the robot to the center of the battlefield to avoid
     * sentry, moves radar in constant circurlar motion to contiously scan all
     * surroundings.
     */
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

    /**
     * When the radar scans a robot, we run the {@code engageTarget()} method, which
     * begins the targetting and movement cycle.
     * 
     * @param e Event that occurs on scanning a robot.
     */
    public void onScannedRobot(ScannedRobotEvent e) {
        engageTarget(e);
    }

    /**
     * We first get the distance to the scanned robot to determine how much
     * firepower we should use, Large distances = low power, close distances = high
     * power. We then calculate the enemies current velocity, and the direction they
     * are facing. We then use this information to estimate where they will be in t
     * seconds. This t is gotten by simulating a shot inside a loop with increasing
     * time variables and applying estmated changes to the enemies predicted
     * position. When the predicted shot aligns with the predicted position, we
     * rotate the gun and fire. We then issue the movement command and continue the
     * process.
     * 
     * @param e The robot that has been scanned.
     */
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
        double velocity = e.getVelocity();
        double deltaTime = 0;
        double predictedX = enemyX, predictedY = enemyY;

        enemyHeadingOld = enemyHeading;
        enemyHeading = e.getHeadingRadians();
        enemyHeadingChange = enemyHeading - enemyHeadingOld;

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

    /**
     * Simple moveement function that turns the robot onto the 45* angle and
     * contiously moves in a square pattern. This keeps us inside the safezone and
     * makes linear targetting hard to predict us.
     */
    public void movement() {
        ahead(75);
        turnRight(90);
    }

    /**
     * Rotates the gun to face the current specified enemy position.
     * 
     * @param enemyX Current enemyX position.
     * @param enemyY Current enemyY position
     */
    public void rotateGun(double enemyX, double enemyY) {
        double angle = absoluteBearing(getX(), getY(), enemyX, enemyY);
        turnGunRight(normalizedBearing(angle - getGunHeading()));
    }

    /**
     * On hitting a wall, in order to avoid getting stuck we turn the robot away
     * from the wall and move forward 100 units.
     * 
     * @param e Event that occurs when the robot hits a wall.
     */
    public void onHitWall(HitWallEvent e) {
        double loc = e.getBearing();

        if (loc > 0) {
            turnLeft(90);
        } else {
            turnRight(90);
        }
        ahead(100);
    }

    /**
     * This method gets the robots current position, uses this to calculate where
     * the center is, turns the robot and moves towards it.
     * 
     * @param quadrant The current quadrant we are located in.
     */
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

    /**
     * Gets the current X and Y of the robot to determine which quadrant of the map
     * we are in. Bottom Left = 1, Top Left = 2, Bottom Right = 3, Top Right = 4.
     * 
     * @return {@code int} value representing the current quadrant of the map.
     */
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

    /**
     * Simple method to get the angle requried to make the robot face upright.
     * 
     * @return The angle required to turn upright.
     */
    public double turnUpright() {
        double head = getHeading();
        return (360 - head);
    }

    /* Math helper methods */

    /**
     * Gets the physical distance between two 2D points on a coordinate map.
     * 
     * @param x1 x1 Value.
     * @param x2 x2 Value.
     * @param y1 x3 Value.
     * @param y2 x4 Value.
     * @return Distance between two points in units.
     */
    public double distanceBetweenPoints(double x1, double x2, double y1, double y2) {
        return Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
    }

    /**
     * Gets the absolute angle between two points.
     * 
     * @param x1 x1 Value.
     * @param x2 x2 Value.
     * @param y1 x3 Value.
     * @param y2 x4 Value.
     * @return Absolute angle between two points.
     */
    double absoluteBearing(double x1, double y1, double x2, double y2) {
        double xo = x2 - x1;
        double yo = y2 - y1;
        double hyp = Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
        double arcSin = Math.toDegrees(Math.asin(xo / hyp));
        double bearing = 0;

        if (xo > 0 && yo > 0) {
            bearing = arcSin;
        } else if (xo < 0 && yo > 0) {
            bearing = 360 + arcSin;
        } else if (xo > 0 && yo < 0) {
            bearing = 180 - arcSin;
        } else if (xo < 0 && yo < 0) {
            bearing = 180 - arcSin;
        }

        return bearing;
    }

    /**
     * Normalizes a given angle.
     * 
     * @param angle Angle to be normalized.
     * @return The normalized angle.
     */
    double normalizedBearing(double angle) {
        while (angle > 180)
            angle -= 360;
        while (angle < -180)
            angle += 360;
        return angle;
    }
}