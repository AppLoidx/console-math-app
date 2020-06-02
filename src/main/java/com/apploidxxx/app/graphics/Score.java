package com.apploidxxx.app.graphics;


import java.awt.*;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Arthur Kupriyanov on 08.04.2020
 */
public class Score implements Iterable<Point>{
    private final LinkedList<Point> scores = new LinkedList<>();
    private final LinkedList<Point> outOfGraphScore = new LinkedList<>();
    private boolean notInGraph;
    public void addScore(Double x, Double y) {
        addScore(x, y, false, Color.GREEN);
    }

//    public void newGraph() {
//        if (scores.size() > 0) {
//            Point lastPoint = scores.getLast();
//            Point breakPoint = new Point(lastPoint.getX(), lastPoint.getY());
//            breakPoint.setBreakPoint(true);
//            scores.add(breakPoint);
//        }
//    }

    public void addScore(Double x, Double y, boolean isNotInGraph, Color color) {
        Point p = new Point(x, y);
        p.setNotInGraph(isNotInGraph);
        p.setColor(color);
        if (isNotInGraph) {
            outOfGraphScore.add(p);
        } else {
            scores.add(p);
//            scores.sort(Comparator.comparingDouble(Point::getX));
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

    public Point get(int i) {
        return scores.get(i);
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
