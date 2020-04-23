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
import java.util.*;
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

        NonLinearSolver solver = selectSolver(console);
        double accuracy = readAccuracy(console);

        console.clearScreen();
        console.println("Краткая сводка: ");
        console.println("Выбранная функция: " + selectedFunc.name);
        console.println("Верхняя граница: " + boundaries[1]);
        console.println("Нижняя граница: " + boundaries[0]);
        console.println("Точность: " + accuracy);
        console.println("-------------------------------------");

        boolean haveOneRoot = isHaveOneRoot(function, accuracy);
        if (isHaveRoot(function)) {
            if (!haveOneRoot) {
                console.println("Возможно, уравнение имеет несколько корней в заданной области");
            }
        }

        final Map<Double, Double> answers = new HashMap<>();

        try {
            solve(solver, function, accuracy, answers, 10, 0);
            console.println("Найдено решений : " + answers.size());
            int index = 1;
            for (Double key : answers.keySet()) {
                console.println("x" + index + " = " + key);
                index++;
            }
//            double ans = solver.solve(function, accuracy);
//            answers.put(ans, function.apply(ans));
            GraphPanel.drawGraph(function, answers, accuracy);
        } catch (IllegalArgumentException e) {
            console.println("Error: " + e.getMessage());
            double lastAns = solver.getLastAnswer();
            if (!Double.isNaN(lastAns) && !Double.isInfinite(lastAns)) {
                console.println("Последний полученный ответ до ошибки: " + lastAns);
                console.println("[WARN] Полученный ответ будет отличаться от желаемой точности");
                answers.put(lastAns, function.apply(lastAns));
                GraphPanel.drawGraph(function, answers, accuracy);
            }
        }

    }

    private void solve(NonLinearSolver solver, final ExtendedFunction func, double accuracy, final Map<Double, Double> answers, final int maxDepth, int counter) {

        double ans = solver.solve(func, accuracy);
        System.out.println("bounds " + Arrays.toString(func.getBoundaries()));
//        boolean alreadyIn = false;
//        for (Double key : answers.keySet()) {
//            if (DoubleUtil.isEqual(key, ans, accuracy)) {
//                System.out.println(key + " is already in");
//                alreadyIn = true;
//                break;
//            }
//        }
        counter++;
        if (counter > maxDepth) {
            return;
        }
//        if (!alreadyIn && (ans > func.getBoundaries()[0] && ans < func.getBoundaries()[1])) {
            answers.put(ans, func.apply(ans));
//        }

//        try {
//            if (!isHaveOneRoot(func, accuracy)) {
//                double bottom = Math.min(func.getBoundaries()[0], func.getBoundaries()[1]);
//                double top = Math.max(func.getBoundaries()[0], func.getBoundaries()[1]);
//                ExtendedFunction funcLeftSide = new ExtendedFunction(func);
//                ExtendedFunction funcRightSide = new ExtendedFunction(func);
//                if (alreadyIn) {
//                    funcLeftSide.setBoundaries(bottom, top/2);
//                    funcRightSide.setBoundaries(top/2 + accuracy * 100, top);
//                } else {
//                    funcLeftSide.setBoundaries(bottom, Math.min(ans - accuracy * 100, top));
//                    funcRightSide.setBoundaries(Math.max(ans + accuracy * 100, bottom), top);
//                }
//                solve(solver, funcLeftSide, accuracy, answers, maxDepth, counter);
//                solve(solver, funcRightSide, accuracy, answers, maxDepth, counter);
//            }
//        } catch (StackOverflowError e) {
//            System.err.println("\nStack overflow in recursive method");
//        } catch (IllegalArgumentException e) {
//            System.err.println(e.getMessage());
//            System.err.println("Будет использован последний удавшийся ответ (точность будет не совпадать)");
//            answers.put(solver.getLastAnswer(), func.apply(solver.getLastAnswer()));
//        }
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
                System.out.println("Changed sign");
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
        list.add(new SelectFunction(x -> Math.sin(1 / x), new DerivativeFunction(x -> - Math.cos(1/x) / Math.pow(x, 2)), "sin(1/x)"));
//        list.add(new SelectFunction(x -> 1 / Math.pow(x, 2), new DerivativeFunction(x -> -2 / Math.pow(x, 3)), "1/x^2"));

        return list;
    }

}
