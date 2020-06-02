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
import util.function.DiffEquation;
import util.function.ExtendedFunction;
import util.function.interfaces.Dot;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

        List<Dot> dots = calculateDotsFromConsoleInput(console);
        ExtendedFunction interpolatedFunc = interpolateDots(dots);

        double interpolatedYValue = interpolatedFunc.apply(0d);

        Score score = new Score();

        for (Dot d : dots) {
            score.addScore(d.getX(), d.getY());
        }

        score.addScore(0d, interpolatedYValue, true, Color.RED);
        GraphPanel.drawGraph(List.of(score));
        console.println("Interpolation result : " + interpolatedYValue);
    }

    private ExtendedFunction interpolateDots(List<Dot> dots) {
        ExtendedFunction interpolatedFunc = INTERPOLATOR.interpolate(dots);
        interpolatedFunc.setBoundaries(createBoundariesForInterpolationFunc(selectAbsMax(dots.get(0).getX(), dots.get(dots.size() - 1).getX())));
        return interpolatedFunc;
    }

    private List<Dot> calculateDotsFromConsoleInput(Console console) throws IOException {
        SelectFunction<DiffEquation> selectedFunc = ConsoleUtil.selectFunction(console, equations);
        double[] startValues = readStartValues(console);
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

        DiffEquation function1 = new DiffEquation((x, y) -> y + (1 + x) * y * y);
        DiffEquationCommand.equations.add(createEquation("y' = y + (1 + x) * y^2", function1));

        DiffEquation function2 = new DiffEquation((x, y) -> x - y);
        DiffEquationCommand.equations.add(createEquation("y' = x - y", function2));
    }

    private double[] readStartValues(Console console) {
        double x0 = ConsoleUtil.readDouble("x0 = ", console);
        double y0 = ConsoleUtil.readDouble("y0 = ", console);

        return new double[]{x0, y0};
    }

    private static SelectFunction<DiffEquation> createEquation(String name, DiffEquation equation) {
        return new SelectFunction<>(name, equation);
    }

}
