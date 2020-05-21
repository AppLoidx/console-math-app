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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * @author Arthur Kupriyanov on 21.05.2020
 */
@Executable("interpolation")
public class Interpolation implements Command {
    private static final Random RANDOM = new Random();

    @Override
    public void execute(Console console, String context) throws Exception {
        double[] boundaries = new double[]{0, 5};
        ExtendedFunction function = new ExtendedFunction(Math::sin);
        List<Dot> points = generatePointsFrom(function, boundaries, 20);

        points.set(3, new SimpleDot(points.get(3).getX(), 1));
        Interpolator interpolator = new NewtonInterpolator();
        ExtendedFunction interpolationFunc = interpolator.interpolate(points);
        function.setBoundaries(boundaries[0], boundaries[1]);
        interpolationFunc.setBoundaries(boundaries[0], boundaries[1]);
        GraphPanel.drawGraph(List.of(interpolationFunc, function), new HashMap<>(), 0.0001);
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
    private void mutateDots(List<Dot> points) {

            int randomId = RANDOM.nextInt(points.size());
            points.set(randomId, new SimpleDot(points.get(randomId).getX(), points.get(randomId).getY() + 0.001));

    }
}
