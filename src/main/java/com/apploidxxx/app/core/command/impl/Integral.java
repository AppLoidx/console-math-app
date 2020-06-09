package com.apploidxxx.app.core.command.impl;

import com.apploidxxx.app.console.Console;
import com.apploidxxx.app.core.command.Command;
import com.apploidxxx.app.core.command.impl.util.ConsoleUtil;
import com.apploidxxx.app.core.command.impl.util.SelectFunction;
import com.apploidxxx.app.core.command.stereotype.Executable;
import core.impl.SimpsonSolverExtended;
import util.function.ExtendedFunction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Arthur Kupriyanov on 19.03.2020
 */
@Executable("integral")
public class Integral implements Command {


    private static final List<SelectFunction<ExtendedFunction>> functions = new ArrayList<>();

    static {

        ExtendedFunction rlyDudeFunction = new ExtendedFunction(x -> x * x - 1);
        Map<Double, Function<Double, Double>> piecewiseMap = new HashMap<>();
        piecewiseMap.put(1d, x -> 2d);
        rlyDudeFunction.setPiecewiseMap(piecewiseMap);

        functions.addAll(List.of(
                new SelectFunction<>("sin(1/x)             - essential", getExtendedFunctionForSymmetricFunction(x -> Math.sin(1d / x))),
                new SelectFunction<>("x^2 - 1              - rly dude?", rlyDudeFunction),  // piecewise-function
                new SelectFunction<>("1 / x                - infinite", getExtendedFunctionForSymmetricFunction(x -> 1d / x)),
                new SelectFunction<>("1 / x ^ 2            - infinite", new ExtendedFunction(x -> 1d / (x * x))),
                new SelectFunction<>("(x^2 - 1) / (x-1)    - removable", new ExtendedFunction(x -> (x * x - 1) / (x - 1)))
        ));
    }


    @Override
    public void execute(Console console, String context) throws Exception {

        console.clearScreen();

        SelectFunction<ExtendedFunction> selectedFunc = ConsoleUtil.selectFunction(console, functions);

        ExtendedFunction extFunction = selectedFunc.getFunc();

        console.clearScreen();
        printInfo(selectedFunc.getName(), console);

        ConsoleUtil.printLine(console);
        double accuracy = readAccuracy(console);

        console.clearScreen();
        printInfo(selectedFunc.getName(), accuracy, console);

        ConsoleUtil.printLine(console);
        console.print("Введите верхний предел: ");
        double top = readBoundary(console, extFunction);

        console.println("");
        console.print("Введите нижний предел: ");
        double bottom = readBoundary(console, extFunction);

        extFunction.setBoundaries(bottom, top);

        console.clearScreen();
        printInfo(selectedFunc.getName(), accuracy, top, bottom, console);

        SimpsonSolverExtended solverExtended = new SimpsonSolverExtended();

        try {

            if (ConsoleUtil.getParam("limit-off", context).isPresent()) {
                solverExtended.setLimit(Double.MAX_VALUE);
            }


            double answer = solverExtended.solveWithAccuracy(extFunction, extFunction.getBoundaries()[0], extFunction.getBoundaries()[1], accuracy);

            console.println("-----------------------------");
            console.println(String.format("Ответ: %f", answer));
            console.println("Точность: " + solverExtended.getLastAccuracy());
            console.println("Количество частей: " + solverExtended.getLastPartition());

            if (Double.isNaN(answer)) {
                console.println("WARNING : [Введенные пределы не соответствуют области значений функции]");
                if (solverExtended.getLastNormalValue() != null) {
                    console.println("Последнее нормальное значение: " + solverExtended.getLastNormalValue());
                }
            } else if (Double.isInfinite(answer)) {
                console.println("INFO : [Функция имеет точку разрыва в области введенных вами значений]");
                if (solverExtended.getLastNormalValue() != null) {
                    console.println("Последнее нормальное значение: " + solverExtended.getLastNormalValue());
                }
            }
        } catch (StackOverflowError | OutOfMemoryError e) {
            console.println("\nНе хватает вычислительной мощности для получения ответа!");
            console.println("Попробуйте в следующий раз снизить точность");
        } catch (IllegalArgumentException e) {
            console.println("Лимит разбиений превышен (1_000_000)");
            console.println("Вы можете убрать лимит с параметром --limit-off");
            console.println("Последний ответ до превышения лимита: " + solverExtended.getLastNormalValue());
            console.println(String.format("Точность: %s", solverExtended.getLastAccuracy()));
        }
    }

    private double readBoundary(Console console, ExtendedFunction function) throws IOException {
        try {
            int numb = Integer.parseInt(console.readLine());
            double val = function.apply(numb);
            if (Double.isInfinite(val) || Double.isNaN(val)) {
                console.println("В знаменателе получилось 0. Введите другое число");
                return readBoundary(console, function);
            }

            return numb;

        } catch (NumberFormatException e) {
            console.println("Введите цело число в области значений int");
            return readBoundary(console, function);
        }
    }

    private double readAccuracy(Console console) throws IOException {
        console.println("Введите необходимую точность ответа по правилу Рунге: ");
        try {
            return Double.parseDouble(console.readLine());
        } catch (NumberFormatException e) {
            console.println("Введите верный формат точности!");
            return readAccuracy(console);
        }
    }

    private void printInfo(String function, Console console) {
        console.println("Функция: " + function);
    }

    private void printInfo(String function, double accuracy, Console console) {
        printInfo(function, console);
        console.println(String.format("Точность: %s", accuracy));
    }

    private void printInfo(String function, double accuracy, double top, double bottom, Console console) {
        printInfo(function, accuracy, console);
        console.println("Верхний предел: " + top);
        console.println("Нижний предел: " + bottom);
    }

    private static ExtendedFunction getExtendedFunctionForSymmetricFunction(Function<Double, Double> function) {
        return new ExtendedFunction(function) {

            /**
             * Redefine boundaries
             *
             * For example, if function is symmetric we can manage it from [-3, 5] to [3, 5]
             *
             * @param bottom bottom boundary
             * @param top bottom boundary
             */
            @Override
            public void setBoundaries(double bottom, double top) {

                if ((top > 0 && bottom < 0) || (top < 0 && bottom > 0)) {

                    if (top > bottom) {
                        super.setBoundaries(-bottom, top);
                    } else {
                        super.setBoundaries(bottom, -top);
                    }
                }
            }
        };
    }
}
