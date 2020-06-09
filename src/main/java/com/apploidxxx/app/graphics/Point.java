package com.apploidxxx.app.graphics;

import java.awt.*;

/**
 * @author Arthur Kupriyanov on 08.04.2020
 */
public class Point extends java.awt.Point {
    public final double x;
    public final double y;
    private Color color = Color.GREEN;

    private boolean isNotInGraph = false;


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

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean isNotInGraph() {
        return isNotInGraph;
    }

    public void setNotInGraph(boolean notInGraph) {
        isNotInGraph = notInGraph;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "Point{" +
               "x=" + x +
               ", y=" + y +
               '}';
    }
}
