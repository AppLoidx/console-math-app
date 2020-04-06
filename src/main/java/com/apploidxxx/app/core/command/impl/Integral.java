package com.apploidxxx.app.core.command.impl;

import com.apploidxxx.app.console.Console;
import com.apploidxxx.app.core.command.Command;
import com.apploidxxx.app.core.command.stereotype.Executable;
import core.impl.SimpsonSolverExtended;
import util.function.ExtendedFunction;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Arthur Kupriyanov on 19.03.2020
 */
@Executable("integral")
public class Integral implements Command {

    private static final Function<Double, Double> FUNCTION_1 = x -> (x * x - 1) / (x - 1);
    private static final Function<Double, Double> FUNCTION_2 = x -> x * x - 1;
    private static final Function<Double, Double> FUNCTION_3 = x -> 1d / (x * x);
    private static final Function<Double, Double> FUNCTION_4 = x -> Math.sin(1d / x);
    private static final Function<Double, Double> FUNCTION_5 = x -> 1d / x;

    private static final String FUNCTION_1_NAME = "(x^2 - 1) / (x-1) - removable";
    private static final String FUNCTION_2_NAME = "x^2 - 1";
    private static final String FUNCTION_3_NAME = "1 / x ^ 2 - infinite";
    private static final String FUNCTION_4_NAME = "sin(1/x) - essential";
    private static final String FUNCTION_5_NAME = "1 / x - infinite";

    private static final String[] funcNames = new String[]{
            FUNCTION_1_NAME,
            FUNCTION_2_NAME,
            FUNCTION_3_NAME,
            FUNCTION_4_NAME,
            FUNCTION_5_NAME
    };

    @Override
    public void execute(Console console, String context) throws Exception {

        console.clearScreen();

        int funcNumber = readFuncNumber(console);
        Function<Double, Double> function = getFunction(funcNumber);
        ExtendedFunction extFunction = createExtendedFunction(funcNumber, function);

        console.clearScreen();
        printInfo(funcNames[funcNumber - 1], console);

        console.println("-----------------------------");
        double accuracy = readAccuracy(console);

        console.clearScreen();
        printInfo(funcNames[funcNumber - 1], accuracy, console);

        console.println("-----------------------------");
        console.print("Введите верхний предел: ");
        int top = readInt(console, extFunction);
        console.println("");
        console.print("Введите нижний предел: ");
        int bottom = readInt(console, extFunction);

        console.clearScreen();
        printInfo(funcNames[funcNumber - 1], accuracy, top, bottom, console);

        int[] boundaries = redefineBoundaries(funcNumber, top, bottom);

        SimpsonSolverExtended solverExtended = new SimpsonSolverExtended();

        try {

            if ("integral --limit-off".equals(context.trim())) {
                solverExtended.setLimit(Double.MAX_VALUE);
            }


            double answer = solverExtended.solveWithAccuracy(extFunction, boundaries[1], boundaries[0], accuracy);

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
            console.println("\nНе хватает вычислительной мощности для поулчения ответа!");
            console.println("Попробуйте в следующий раз снизить точность");
        } catch (IllegalArgumentException e) {
            console.println("Лимит разбиений превышен (1_000_000)");
            console.println("Вы можете убрать лимит с параметром --limit-off");
            console.println("Последний ответ до превышения лимита: " + solverExtended.getLastNormalValue());
            console.println(String.format("Точность: %s", solverExtended.getLastAccuracy()));
        }
    }

    private ExtendedFunction createExtendedFunction(int funcNumber, Function<Double, Double> function) {
        ExtendedFunction extFunc = new ExtendedFunction(function);
        if (funcNumber == 1) {
            Map<Double, Function<Double, Double>> piecewiseMap = new HashMap<>();
            piecewiseMap.put(1d, x -> 2d);
            extFunc.setPiecewiseMap(piecewiseMap);
        }

        return extFunc;
    }

    private int[] redefineBoundaries(int funcNumber, int top, int bottom) {
        switch (funcNumber) {
            case 4:
            case 5:
                if ((top > 0 && bottom < 0) || (top < 0 && bottom > 0))
                    if (top > bottom) {
                        return new int[]{-bottom, top};
                    } else {
                        return new int[]{bottom, -top};
                    }
            default:
                return new int[]{bottom, top};
        }

    }

    private int readInt(Console console, ExtendedFunction function) throws IOException {
        try {
            int numb = Integer.parseInt(console.readLine());
            double val = function.apply(numb);
            if (Double.isInfinite(val) || Double.isNaN(val)) {
                console.println("В знаменателе получилось 0. Введите другое число");
                return readInt(console, function);
            }

            return numb;

        } catch (NumberFormatException e) {
            console.println("Введите цело число в области значений int");
            return readInt(console, function);
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

    private int readFuncNumber(Console console) throws IOException {
        console.println("Введите номер функции");
        int index = 1;
        for (String funcName : funcNames) {
            console.println("[" + index + "] " + funcName);
            index++;
        }
        console.println("__________________________________");
        int number = console.readInt();
        if (number < 1 || number > 5) {
            console.println("Выберите число от 1 до 5");
            return readFuncNumber(console);
        }

        return number;

    }

    private Function<Double, Double> getFunction(int number) {

        switch (number) {
            case 1:
                return FUNCTION_1;
            case 2:
                return FUNCTION_2;
            case 3:
                return FUNCTION_3;
            case 4:
                return FUNCTION_4;
            case 5:
                return FUNCTION_5;
            default:
                return null;
        }
    }

    private void printInfo(String function, Console console) {
        console.println("Функция: " + function);
    }

    private void printInfo(String function, double accuracy, Console console) {
        printInfo(function, console);
        console.println(String.format("Точность: %s", accuracy));
    }

    private void printInfo(String function, double accuracy, int top, int bottom, Console console) {
        printInfo(function, accuracy, console);
        console.println("Верхний предел: " + top);
        console.println("Нижний предел: " + bottom);
    }
}
