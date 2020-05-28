package com.apploidxxx.app.core.command.impl;

import com.apploidxxx.app.console.Console;
import com.apploidxxx.app.core.command.Command;
import com.apploidxxx.app.core.command.stereotype.Executable;
import com.apploidxxx.app.graphics.GraphPanel;
import core.Interpolator;
import core.impl.NewtonInterpolator;
import util.function.ExtendedFunction;
import util.function.SimpleDot;
import util.function.interfaces.Dot;

import java.awt.Point;
import java.util.*;

/**
 * @author Arthur Kupriyanov on 21.05.2020
 */
@Executable(value = "interpolation", aliases = {"1"})
public class Interpolation implements Command {
    private static final Random RANDOM = new Random();

    @Override
    public void execute(Console console, String context) throws Exception {
        double[] boundaries = new double[]{1, 5};
        ExtendedFunction function = new ExtendedFunction(Math::sin);
        List<Dot> points = generatePointsFrom(function, boundaries, 10);

        points.set(0, new SimpleDot(points.get(0).getX(), 1));
        points.set(6, new SimpleDot(points.get(6).getX(), 0));
        Map<Double, Double> dots = new HashMap<>();
        for (Dot d : points) {
            dots.put(d.getX(), d.getY());
        }
        Interpolator interpolator = new NewtonInterpolator();
        ExtendedFunction interpolationFunc = interpolator.interpolate(points);
        function.setBoundaries(boundaries[0], boundaries[1]);
        interpolationFunc.setBoundaries(boundaries[0], boundaries[1]);

        points.set(0, new SimpleDot(points.get(0).getX(), 0.8d));
        points.set(6, new SimpleDot(points.get(6).getX(), -0.1d));
        dots.put(points.get(0).getX() + 0.000001, points.get(0).getY());
        dots.put(points.get(6).getX() + 0.000001, points.get(6).getY());

        ExtendedFunction interFunc2 = new NewtonInterpolator().interpolate(points);
        interFunc2.setBoundaries(boundaries[0] , boundaries[1]);

        points.set(0, new SimpleDot(points.get(0).getX(), 0.7d));
        points.set(6, new SimpleDot(points.get(6).getX(), -0.2d));
        dots.put(points.get(0).getX() + 0.000002, points.get(0).getY());
        dots.put(points.get(6).getX() + 0.000002, points.get(6).getY());

        ExtendedFunction interFunc3 = new NewtonInterpolator().interpolate(points);
        interFunc3.setBoundaries(boundaries[0] , boundaries[1]);

        GraphPanel.drawGraph(List.of(interpolationFunc, function, interFunc2, interFunc3), dots, 0.0001);
    }
    private List<Dot> generatePointsFrom(ExtendedFunction function, double[] boundaries, int amount) {
        double width = Math.abs(boundaries[0] - boundaries[1]);
        double start = Math.min(boundaries[0], boundaries[1]);
        double step = width / amount;
        List<Dot> pointList = new LinkedList<>();
        while (width > 0) {
            pointList.add(new SimpleDot(start, function.apply(start)));
            start +=step;
            width -= step;
        }

        return pointList;
    }
}
