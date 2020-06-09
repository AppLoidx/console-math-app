package com.apploidxxx.app.graphics;


import java.awt.*;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Contains points for Graphs
 *
 *
 * @see Point
 * @author Arthur Kupriyanov on 08.04.2020
 */
public class Score implements Iterable<Point>{

    private final LinkedList<Point> scores = new LinkedList<>();
    private final LinkedList<Point> outOfGraphScore = new LinkedList<>();
    private boolean notInGraph;

    /**
     * Add new point
     *
     * @param x coordinate
     * @param y coordinate
     */
    public void addPoint(Double x, Double y) {
        addPoint(x, y, false, Color.GREEN);
    }

    /**
     * Add new point
     *
     * @param x coordinate
     * @param y coordinate
     * @param isNotInGraph instruction for graph about drawing
     *                     with dow as single dot (not in graph) {@link Point#isNotInGraph()}
     * @param color color of the dot
     */
    public void addPoint(Double x, Double y, boolean isNotInGraph, Color color) {
        Point p = new Point(x, y);
        p.setNotInGraph(isNotInGraph);
        p.setColor(color);
        if (isNotInGraph) {
            outOfGraphScore.add(p);
        } else {
            scores.add(p);
        }

    }

    public Iterator<Point> getIterator() {
        LinkedList<Point> merged = new LinkedList<>(scores);
        merged.addAll(outOfGraphScore);
        return merged.iterator();
    }

    public List<Point> getList(){
        return scores.stream().sorted(Comparator.comparingDouble(Point::getX)).collect(Collectors.toList());
    }

    public void sort() {
        scores.sort(Comparator.comparingDouble(Point::getX));
    }

    public int graphSize(){
        return scores.size();
    }

    @Override
    public Iterator<Point> iterator() {
        return getIterator();
    }

    public boolean isNotInGraph() {
        return notInGraph;
    }

    public void setNotInGraph(boolean notInGraph) {
        this.notInGraph = notInGraph;
    }
}
