package com.apploidxxx.app.graphics;

/**
 * @author Arthur Kupriyanov on 08.04.2020
 */
public class Point extends java.awt.Point {
    public final double x;
    public final double y;

    private boolean isNotInGraph = false;

    public boolean isBreakPoint() {
        return isBreakPoint;
    }

    public void setBreakPoint(boolean breakPoint) {
        isBreakPoint = breakPoint;
    }

    private boolean isBreakPoint;


    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }


    public boolean isNotInGraph() {
        return isNotInGraph;
    }

    public void setNotInGraph(boolean notInGraph) {
        isNotInGraph = notInGraph;
    }
}
