package com.apploidxxx.app.core.command.impl;

import com.apploidxxx.app.console.Console;
import com.apploidxxx.app.core.command.Command;
import com.apploidxxx.app.core.command.stereotype.Executable;
import com.apploidxxx.app.graphics.GraphPanel;
import core.NonLinearSolver;
import core.impl.NonLinearIterationSolver;
import core.impl.NonLinearSecantSolver;
import util.function.DerivativeFunction;
import util.function.ExtendedFunction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Arthur Kupriyanov on 07.04.2020
 */
@Executable("nonlinear")
public class NonLinear implements Command {

    private final static List<SelectFunction> selectFunctions = createFunctions();


    @Override
    public void execute(Console console, String context) throws Exception {

        SelectFunction selectedFunc = selectFunction(console);
        ExtendedFunction function = parseFunction(selectedFunc);

        double[] boundaries = readBoundaries(console);
        function.setBoundaries(boundaries[0], boundaries[1]);

        NonLinearSolver secantSolver = new NonLinearSecantSolver();
        NonLinearSolver iterationSolver = new NonLinearIterationSolver();

        double accuracy = readAccuracy(console);

        console.clearScreen();
        console.println("Краткая сводка: ");
        console.println("Выбранная функция: " + selectedFunc.name);
        console.println("Верхняя граница: " + boundaries[1]);
        console.println("Нижняя граница: " + boundaries[0]);
        console.println("Точность: " + accuracy);
        console.println("-------------------------------------");

        if (accuracy == 0d) {
            console.println("Вы переоцениваете мощность этого компьютера. Пожалуйста, снизьте точность");
            return;
        } else if (Math.abs(boundaries[0] - boundaries[1]) > 1000) {
            console.println("Выбрана слишком большая область. Это пагубно влияет на ЭВМ и график функции");
        }

        boolean haveOneRoot = isHaveOneRoot(function, accuracy);
        if (isHaveRoot(function)) {
            if (!haveOneRoot) {
                console.println("Возможно, уравнение имеет несколько корней в заданной области");
            }
        }

        final Map<Double, Double> answers = new HashMap<>();

        try {
            console.println("Iteration Solver:");
            final Map<Double, Double> answer = new HashMap<>();
            solveWithMethod(console, function, iterationSolver, accuracy, answer);
            answers.putAll(answer);
        } catch (IllegalArgumentException e) {
            console.println("[Iteration Solver] Error: " + e.getMessage());
            double lastAnsIteration = iterationSolver.getLastAnswer();
            if (!Double.isNaN(lastAnsIteration) && !Double.isInfinite(lastAnsIteration)) {
                console.println("[Iteration Solver] Последний полученный ответ до ошибки: " + lastAnsIteration);
                console.println("[WARN] Полученный ответ будет отличаться от желаемой точности");
                answers.put(lastAnsIteration, function.apply(lastAnsIteration));
            }
        }

        try {
            console.println("Secant Solver:");
            final Map<Double, Double> answer = new HashMap<>();
            solveWithMethod(console, function, secantSolver, accuracy, answer);
            answers.putAll(answer);
        } catch (IllegalArgumentException e) {
            console.println("[Secant Solver] Error: " + e.getMessage());
            double lastAnsSecant = secantSolver.getLastAnswer();
            if (!Double.isNaN(lastAnsSecant) && !Double.isInfinite(lastAnsSecant)) {
                console.println("[Secant Solver] Последний полученный ответ до ошибки: " + lastAnsSecant);
                console.println("[WARN] Полученный ответ будет отличаться от желаемой точности");
                answers.put(lastAnsSecant, function.apply(lastAnsSecant));
            }
        }

        GraphPanel.drawGraph(function, answers, accuracy);

    }

    private void solveWithMethod(Console console, ExtendedFunction function, NonLinearSolver secantSolver, double accuracy, Map<Double, Double> answers) {
        solve(secantSolver, function, accuracy, answers, 10, 0);
        for (Double key : answers.keySet()) {
            console.println("x = " + key);
        }
    }

    private void solve(NonLinearSolver solver, final ExtendedFunction func, double accuracy, final Map<Double, Double> answers, final int maxDepth, int counter) {

        double ans = solver.solve(func, accuracy);

        counter++;
        if (counter > maxDepth) {
            return;
        }
        if (ans < Math.max(func.getBoundaries()[0], func.getBoundaries()[1]) && ans > Math.min(func.getBoundaries()[0], func.getBoundaries()[1])) {
            answers.put(ans, func.apply(ans));
        } else {
            System.out.println("Ответ найдена за пределами области: x=" + ans);
        }
    }


    private boolean isHaveRoot(ExtendedFunction extFunc) {
        return extFunc.getBoundaries()[0] * extFunc.getBoundaries()[1] < 0;
    }

    private boolean isHaveOneRoot(ExtendedFunction extFunc, double accuracy) {
        DerivativeFunction dFunc = extFunc.getDerivativeFunction();
        boolean changedSign = false;

        int sections = Math.min((int) (Math.abs(extFunc.getBoundaries()[0] - extFunc.getBoundaries()[1]) / accuracy), 4);
        double oldVal = dFunc.apply(extFunc.getBoundaries()[0]);
        double currentValue = Math.min(extFunc.getBoundaries()[0], extFunc.getBoundaries()[1]);
        double step = 1d * Math.abs(extFunc.getBoundaries()[0] - extFunc.getBoundaries()[1]) / sections;

        while (sections > 0) {
            double nowVal = dFunc.apply(currentValue + step);
            changedSign = nowVal * oldVal < 0;
            if (changedSign) {
                break;
            }
            oldVal = nowVal;
            currentValue += step;
            sections--;
        }

        return !changedSign;
    }

    private double readAccuracy(Console console) throws IOException {
        console.print("\nВведите желаемую точность ответа: ");
        try {
            return Double.parseDouble(console.readLine());
        } catch (NumberFormatException e) {
            console.println("\nВведите правильное число типа double");
            return readAccuracy(console);
        }
    }

    private NonLinearSolver selectSolver(Console console) throws IOException {
        console.println("Выберите метод решения уравнения:");
        console.println("[1] Метод хорд");
        console.println("[2] Метод простых итераций");
        boolean error = false;
        int choice = 0;
        try {
            choice = Integer.parseInt(console.readLine());
        } catch (NumberFormatException e) {
            error = true;
        }
        if (error || (choice != 1 && choice != 2)) {
            console.println("Некорректный номер. Выберите из предложенных методов");
            return selectSolver(console);
        } else {
            // решил не выделываться и захаркодил
            return choice == 1 ? new NonLinearSecantSolver() : new NonLinearIterationSolver();
        }
    }


    // TODO: rewrite boundaries to double

    private double[] readBoundaries(Console console) throws IOException {
        double[] boundaries = new double[2];
        console.print("\nВведите нижнюю границу:");
        boundaries[0] = readDouble(console);
        console.print("\nВведите верхнюю границу:");
        boundaries[1] = readDouble(console);

        if (boundaries[0] == boundaries[1]) {
            console.println("Вы ввели одинаковые значения для области. Возможно, вы ошиблись при вводе");
            return readBoundaries(console);
        }

        return boundaries;
    }

    private int readInt(Console console) throws IOException {
        try {
            return Integer.parseInt(console.readLine());
        } catch (NumberFormatException e) {
            console.println("Введите правильное число типа int");
            return readInt(console);
        }
    }

    private double readDouble(Console console) throws IOException {
        try {
            return Double.parseDouble(console.readLine());
        } catch (NumberFormatException e) {
            console.println("Введите правильное число типа double");
            return readDouble(console);
        }
    }

    private SelectFunction selectFunction(Console console) throws IOException {
        console.println("Выберите функцию: ");
        int index = 1;
        for (SelectFunction function : selectFunctions) {
            console.println("[" + index + "] " + function.name);
            index++;
        }
        int choice = console.readInt();
        if (choice > selectFunctions.size() || choice < 1) {
            console.println("Пожалуйста, введите корректное число!");
            return selectFunction(console);
        } else {
            return selectFunctions.get(choice - 1);
        }
    }

    private ExtendedFunction parseFunction(SelectFunction selectedFunc) throws IOException {

        ExtendedFunction extFunction = new ExtendedFunction(selectedFunc.func);
        extFunction.setDerivativeFunction(selectedFunc.dFunc);

        return extFunction;

    }

    private static class SelectFunction {

        public final Function<Double, Double> func;
        public final DerivativeFunction dFunc;
        public final String name;

        private SelectFunction(Function<Double, Double> func, DerivativeFunction dFunc, String name) {

            this.func = func;
            this.dFunc = dFunc;
            this.name = name;
        }


    }

    private static List<SelectFunction> createFunctions() {
        List<SelectFunction> list = new ArrayList<>();
        list.add(new SelectFunction(Math::sin, new DerivativeFunction(Math::cos), "sin(x)"));
        list.add(new SelectFunction(x -> Math.pow(x, 3) - 4 * x + 4, new DerivativeFunction(x -> Math.pow(x, 2) * 3 - 4), "x^3 - 4x + 4"));
        // -2.38
        list.add(new SelectFunction(x -> Math.sin(1 / x), new DerivativeFunction(x -> -Math.cos(1 / x) / Math.pow(x, 2)), "sin(1/x)"));
//        list.add(new SelectFunction(x -> 1 / Math.pow(x, 2), new DerivativeFunction(x -> -2 / Math.pow(x, 3)), "1/x^2"));

        return list;
    }

}
