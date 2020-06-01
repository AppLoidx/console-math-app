package com.apploidxxx.app.core.command.impl;

import com.apploidxxx.app.console.Console;
import com.apploidxxx.app.core.command.Command;
import com.apploidxxx.app.core.command.impl.util.ConsoleUtil;
import com.apploidxxx.app.core.command.impl.util.SelectFunction;
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

    private static final List<SelectFunction<Function<Double, Double>>> FUNCTIONS = new ArrayList<>();

    static {
        FUNCTIONS.add(new SelectFunction<>("sin(x)", Math::sin));
        FUNCTIONS.add(new SelectFunction<>("x^2 - 2x - 3", x -> x * x - 2 * x - 3));
        FUNCTIONS.add(new SelectFunction<>("x^3 - 6x^2", x -> x * x * x - 6 * x * x));
    }

    @Override
    public void execute(Console console, String context) throws Exception {
        SelectFunction<Function<Double, Double>> selectFunction = ConsoleUtil.selectFunction(console, FUNCTIONS);
        double[] boundaries = ConsoleUtil.readBoundaries(console);

        ExtendedFunction extFunc = extendFunction(selectFunction.getFunc(), boundaries);

        List<Dot> dots = generatePointsFrom(selectFunction.getFunc(), boundaries);

        Interpolator interpolator = new NewtonInterpolator();
        ExtendedFunction interpolation = interpolator.interpolate(dots);
        interpolation.setBoundaries(boundaries[0], boundaries[1]);
        GraphPanel.drawGraph(List.of(extFunc, interpolation), createDotsMap(dots), 0.0001d);

        startLoop(console, dots, extFunc, interpolation);

    }

    @SneakyThrows
    private void startLoop(Console console, List<Dot> dots, final ExtendedFunction mainFunc, ExtendedFunction oldFunc) {
        while (true) {
            console.println("Выберите действие:");
            console.println("[0] Изменить координаты точки");
            console.println("[1] Найти Y для пользовательского X");
            console.println("[2] Выход");

            int choice = ConsoleUtil.readInt(console, 0, 2);
            switch (choice) {
                case 0: {
                    ExtendedFunction inter = mutateDotInterface(console, dots);
                    inter.setBoundaries(mainFunc.getBoundaries()[0], mainFunc.getBoundaries()[1]);
                    GraphPanel.drawGraph(List.of(mainFunc, oldFunc, inter), createDotsMap(dots), 0.0001d);
                    oldFunc = inter;
                    break;
                }

                case 1:
                    calculateX(console, oldFunc);
                    break;
                case 2:
                    return;
            }
        }


    }

    @SneakyThrows
    private void calculateX(Console console, ExtendedFunction interpolationFunc) {
        double x = ConsoleUtil.readDouble("Введите значение X = ", console);
        console.println(String.format("f(%f) = %f", x, interpolationFunc.apply(x)));
    }

    @SneakyThrows
    private ExtendedFunction mutateDotInterface(Console console, List<Dot> dots) {
        console.println("Выберите точку для изменения координаты Y");
        for (int i = 0; i < dots.size(); i++) {
            console.println(String.format("[%d] x = %f, y = %f", i, dots.get(i).getX(), dots.get(i).getY()));
        }
        int choice = ConsoleUtil.readInt(console, 0, dots.size() - 1);
        return mutateDot(console, choice, dots);
    }

    @SneakyThrows
    private ExtendedFunction mutateDot(Console console, int index, List<Dot> dots) {
        Dot d = dots.get(index);
        console.print(String.format("x = %f, y = ", d.getX()));
        String userInput = console.readLine();
        double val;
        try {
            val = Double.parseDouble(userInput);
        } catch (NumberFormatException e) {
            console.println("\nВведите правильное значение для y");
            return mutateDot(console, index, dots);
        }

        dots.set(index, new SimpleDot(d.getX(), val));
        console.println("");

        return new NewtonInterpolator().interpolate(dots);
    }

    private ExtendedFunction extendFunction(Function<Double, Double> function, double[] boundaries) {
        ExtendedFunction extFunc = new ExtendedFunction(function);
        extFunc.setBoundaries(Math.min(boundaries[0], boundaries[1]), Math.max(boundaries[0], boundaries[1]));
        return extFunc;
    }

    private Map<Double, Double> createDotsMap(List<Dot> dots) {
        return dots.stream().collect(Collectors.toMap(Dot::getX, Dot::getY, (a, b) -> b));
    }

    private List<Dot> generatePointsFrom(Function<Double, Double> function, double[] boundaries) {
        double width = Math.abs(boundaries[0] - boundaries[1]);
        double start = Math.min(boundaries[0], boundaries[1]);
        double step = width / 15;   // inline parameter of dots amount
        List<Dot> pointList = new LinkedList<>();
        while (width > 0) {
            pointList.add(new SimpleDot(start, function.apply(start)));
            start += step;
            width -= step;
        }

        return pointList;
    }

}
