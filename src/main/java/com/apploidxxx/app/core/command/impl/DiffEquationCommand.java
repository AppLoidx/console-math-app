package com.apploidxxx.app.core.command.impl;

import com.apploidxxx.app.console.Console;
import com.apploidxxx.app.core.command.Command;
import com.apploidxxx.app.core.command.impl.util.ConsoleUtil;
import com.apploidxxx.app.core.command.impl.util.SelectFunction;
import com.apploidxxx.app.core.command.stereotype.Executable;
import com.apploidxxx.app.graphics.GraphPanel;
import com.apploidxxx.app.graphics.Score;
import core.DiffEquationSolver;
import core.Interpolator;
import core.impl.ImprovedEulerDiffEquationSolver;
import core.impl.NewtonInterpolator;
import lombok.SneakyThrows;
import util.function.DiffEquation;
import util.function.ExtendedFunction;
import util.function.interfaces.Dot;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static java.lang.Math.sin;

/**
 * @author Arthur Kupriyanov on 01.06.2020
 */
@Executable(value = "diff-equation", aliases = {"deq", "diff-eq"})
public class DiffEquationCommand implements Command {

    private static final DiffEquationSolver SOLVER = new ImprovedEulerDiffEquationSolver();
    private static final Interpolator INTERPOLATOR = new NewtonInterpolator();
    private static final List<SelectFunction<DiffEquation>> equations = new ArrayList<>();

    static {
        initFunctions();
    }


    @Override
    public void execute(Console console, String context) throws Exception {
        SelectFunction<DiffEquation> selectedFunc = ConsoleUtil.selectFunction(console, equations);
        List<Dot> dots = calculateDotsFrom(selectedFunc, console, context);

        ExtendedFunction interpolatedFunc = interpolateDots(dots);

        double interpolatedYValue = interpolatedFunc.apply(0d);
        console.clearScreen();
        if (ConsoleUtil.getParam("show-newton", context).isPresent())
            GraphPanel.drawGraph(interpolatedFunc, Map.of(0d, interpolatedYValue, dots.get(0).getX(), dots.get(0).getY()), 0.001, String.format("(%s) Интерполированное методом Ньютона", selectedFunc.getName()));

        Score score = new Score();

        for (Dot d : dots) {
            score.addPoint(d.getX(), d.getY());
        }

        score.addPoint(0d, interpolatedYValue, true, Color.RED);
        score.addPoint(dots.get(0).getX(), dots.get(0).getY(), true, Color.CYAN);
        GraphPanel.drawGraph(List.of(score), selectedFunc.getName());

        console.println("Selected function : " + selectedFunc.getName());
        console.println("Interpolation result at x = 0, y = " + interpolatedYValue);
        console.println("Dots amount: " + dots.size());
    }

    private ExtendedFunction interpolateDots(List<Dot> dots) {
        ExtendedFunction interpolatedFunc = INTERPOLATOR.interpolate(dots);
        interpolatedFunc.setBoundaries(createBoundariesForInterpolationFunc(selectAbsMax(dots.get(0).getX(), dots.get(dots.size() - 1).getX())));
        return interpolatedFunc;
    }

    @SneakyThrows
    private List<Dot> calculateDotsFrom(SelectFunction<DiffEquation> selectedFunc, Console console, String context) {
        console.clearScreen();
        double[] startValues = readStartValues(console);
        if (ConsoleUtil.getParam("manual", context).isPresent()) {
            return calculateDotsWithManualConfig(selectedFunc, console, startValues);
        } else {
            return calculateDotsWithAccuracy(selectedFunc, console, startValues);
        }
    }

    @SneakyThrows
    private List<Dot> calculateDotsWithManualConfig(SelectFunction<DiffEquation> selectedFunc, Console console, double[] startValues) {
        console.print("Введите число точек: ");
        int pointAmount = ConsoleUtil.readInt(console, 10, 100);
        double step = ConsoleUtil.readDouble("Введите шаг: ", console);
        return SOLVER.solve(selectedFunc.getFunc(), startValues[0], startValues[1], pointAmount, step);
    }

    private List<Dot> calculateDotsWithAccuracy(SelectFunction<DiffEquation> selectedFunc, Console console, double[] startValues){
        double accuracy = ConsoleUtil.readDouble("Введите точность вычисляемого значения: ", console);
        return SOLVER.solve(selectedFunc.getFunc(), startValues[0], startValues[1], accuracy);
    }

    private double[] createBoundariesForInterpolationFunc(double x0) {
        return new double[]{x0 >= 0 ? 0d : x0, x0 >= 0 ? x0 : 0d};
    }

    private double selectAbsMax(double a1, double a2) {
        return Math.abs(a1) > Math.abs(a2) ? a1 : a2;
    }


    private static void initFunctions() {

        DiffEquationCommand.equations.add(createEquation("y' = sin(x)", (x, y) -> sin(x)));
        DiffEquationCommand.equations.add(createEquation("y' = x - y", (x, y) -> x - y));
        DiffEquationCommand.equations.add(createEquation("y' = y * ( 2 * sin(x) + 1)",  (x, y) -> y * (2 * sin(x) + 1)));
        DiffEquationCommand.equations.add(createEquation("y' = y",  (x, y) -> y));

    }

    private double[] readStartValues(Console console) {
        double x0 = ConsoleUtil.readDouble("x0 = ", console);
        double y0 = ConsoleUtil.readDouble("y0 = ", console);

        return new double[]{x0, y0};
    }

    private static SelectFunction<DiffEquation> createEquation(String name, BiFunction<Double, Double, Double> biFunction) {
        return new SelectFunction<>(name, new DiffEquation(biFunction));
    }

}
