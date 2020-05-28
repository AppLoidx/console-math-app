package com.apploidxxx.app.core.command.impl;

import com.apploidxxx.app.console.Console;
import com.apploidxxx.app.core.command.Command;
import com.apploidxxx.app.core.command.impl.util.ConsoleUtil;
import com.apploidxxx.app.core.command.stereotype.Executable;
import com.apploidxxx.app.graphics.GraphPanel;
import core.Interpolator;
import core.impl.NewtonInterpolator;
import lombok.SneakyThrows;
import util.function.ExtendedFunction;
import util.function.SimpleDot;
import util.function.interfaces.Dot;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Arthur Kupriyanov on 21.05.2020
 */
@Executable(value = "interpolation", aliases = {"1"})
public class Interpolation implements Command {
    private static final Random RANDOM = new Random();

    private static final SelectFunction[] FUNCTIONS = new SelectFunction[]
            {
                    new SelectFunction(Math::sin, "sin(x)"),
                    new SelectFunction(x -> x * x - 2 * x - 3, "x^2 - 2x - 3"),
                    new SelectFunction(x -> x * x * x - 6 * x * x, "x^3 - 6x^2")
            };

    @Override
    public void execute(Console console, String context) throws Exception {
        SelectFunction selectFunction = selectFunction(console);
        double[] boundaries = ConsoleUtil.readBoundaries(console);

        ExtendedFunction extFunc = extendFunction(selectFunction.func, boundaries);

        List<Dot> dots = generatePointsFrom(selectFunction.func, boundaries, 15);
        // todo: mutate dots handle
        dots.set(4, new SimpleDot(dots.get(4).getX(),dots.get(4).getY() - 0.2));

        Interpolator interpolator = new NewtonInterpolator();
        ExtendedFunction interpolation  = interpolator.interpolate(dots);
        interpolation.setBoundaries(boundaries[0], boundaries[1]);
        GraphPanel.drawGraph(List.of(extFunc, interpolation), createDotsMap(dots), 0.0001d);
    }

    private ExtendedFunction extendFunction(Function<Double, Double> function, double[] boundaries) {
        ExtendedFunction extFunc = new ExtendedFunction(function);
        extFunc.setBoundaries(Math.min(boundaries[0], boundaries[1]), Math.max(boundaries[0], boundaries[1]));
        return extFunc;
    }

    @SneakyThrows
    public SelectFunction selectFunction(Console console){
        console.println("Выберите функцию");
        for (int i = 0; i < FUNCTIONS.length; i++) {
            console.println(String.format("[%d] %s", i, FUNCTIONS[i].name));
        }
        int function = ConsoleUtil.readInt(console, 0, FUNCTIONS.length - 1);
        return FUNCTIONS[function];
    }

    private Map<Double, Double> createDotsMap(List<Dot> dots) {
       return dots.stream().collect(Collectors.toMap(Dot::getX, Dot::getY, (a, b) -> b));
    }

    private List<Dot> generatePointsFrom(Function<Double, Double> function, double[] boundaries, int amount) {
        double width = Math.abs(boundaries[0] - boundaries[1]);
        double start = Math.min(boundaries[0], boundaries[1]);
        double step = width / amount;
        List<Dot> pointList = new LinkedList<>();
        while (width > 0) {
            pointList.add(new SimpleDot(start, function.apply(start)));
            start += step;
            width -= step;
        }

        return pointList;
    }

    private static class SelectFunction {

        public final Function<Double, Double> func;
        public final String name;

        private SelectFunction(Function<Double, Double> func, String name) {
            this.func = func;
            this.name = name;
        }

    }
}
