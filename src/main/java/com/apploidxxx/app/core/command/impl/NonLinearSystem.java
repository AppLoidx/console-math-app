package com.apploidxxx.app.core.command.impl;

import com.apploidxxx.app.console.Console;
import com.apploidxxx.app.core.command.Command;
import com.apploidxxx.app.core.command.stereotype.Executable;
import com.apploidxxx.app.graphics.GraphPanel;
import core.impl.NonLinearSystemSolver;
import util.function.DerivativeFunction;
import util.function.ExtendedFunction;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Arthur Kupriyanov on 07.04.2020
 */
@Executable("nonlinear-system")
public class NonLinearSystem implements Command {

    private static final List<Map.Entry<String, ExtendedFunction>> functionList1 = new LinkedList<>();
    private static final List<Map.Entry<String, ExtendedFunction>> functionList2 = new LinkedList<>();

    static {
        initFirstFunctions();
        initSecondFunctions();
    }

    @Override
    public void execute(Console console, String context) throws Exception {

        int func1 = selectFunction(console, true);
        int func2 = selectFunction(console, false);
        double bottom = readBoundary("Левая граница: ", console);
        double top = readBoundary("Правая граница: ", console);

        ExtendedFunction firstFunc = functionList1.get(func1).getValue();

        firstFunc.setBoundaries(firstFunc.getRepresentation().apply(bottom), firstFunc.getRepresentation().apply(top));
        firstFunc.getRepresentation().setBoundaries(bottom, top);
        ExtendedFunction secondFunc = functionList2.get(func2).getValue();
        secondFunc.setBoundaries(bottom, top);

        NonLinearSystemSolver solver = new NonLinearSystemSolver();
        Map<Double, Double> answers = new HashMap<>();
        final double accuracy = readAccuracy(console);
        try {
            double[] answer = solver.nonlinearSystemSolver(List.of(firstFunc, secondFunc), accuracy);
            answers.put(answer[0], answer[1]);
        } catch (IllegalArgumentException e) {
            console.println("[ERROR] Метод не сходится");
            console.println(e.getMessage());
        }

        try {
            GraphPanel.drawGraph(List.of(firstFunc.getRepresentation(), secondFunc), answers, accuracy);
            console.println(answers.toString());
        } catch (IllegalArgumentException e) {
            console.println("Не удалось отобразить график: " + e.getMessage());
        }

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

    private double readBoundary(String s, Console console) throws IOException {
        console.println(s);
        try {
            return Double.parseDouble(console.readLine());
        } catch (NumberFormatException e) {
            console.println("Введите правильное число");
            return readBoundary(s, console);
        }
    }

    private static void initFirstFunctions() {
        ExtendedFunction func1 = new ExtendedFunction(y -> {
            if (y >= -1) {
                return Math.pow(y + 1, 1 / 3d);
            }
            return -1 * Math.pow(Math.abs(y) + 1, 1 / 3d);
        });

//        func1.getDerivativeFunction().setIsInRange(x -> x >= -1);
        func1.setRepresentation(new ExtendedFunction(x -> Math.pow(x, 3) - 1));
        func1.setDerivativeFunction(new DerivativeFunction(x -> 3 * x));
        createEntry(func1, "x^3 - y = 1", functionList1);

        ExtendedFunction func2 = new ExtendedFunction(y -> Math.pow(y, 3) - 1);
        func2.setRepresentation(new ExtendedFunction(x -> {
            if (x >= -1) return Math.pow(x + 1, 1 / 3d);
            else return -1 * Math.pow(Math.abs(x) + 1, 1 / 3d);
        }));
//        func2.getRepresentation().setIsInRange(x -> x > 0);
        func2.setDerivativeFunction(new DerivativeFunction(x -> 1 / (3d * (Math.pow(x + 1, 2 / 3d))))); //x -> Math.pow(Math.E, x)));
        createEntry(func2, "x = y^3 - 1", functionList1);

    }

    private static void initSecondFunctions() {
        ExtendedFunction func1 = new ExtendedFunction(x -> Math.pow(x + 1, 1 / 3d));
        func1.setDerivativeFunction(new DerivativeFunction((x -> 1 / (3d * (Math.pow(x + 1, 2 / 3d))))));
//        func1.getDerivativeFunction().setIsInRange(x -> x >= -1);

        ExtendedFunction func2 = new ExtendedFunction(x -> Math.pow(x, 2) - 4);
        func2.setDerivativeFunction(new DerivativeFunction(x -> 2 * x));

        createEntry(func1, "x - y^3 = -1", functionList2);
        createEntry(func2, "y = x^2 - 4", functionList2);

    }

    private static void createEntry(ExtendedFunction function, String name, List<Map.Entry<String, ExtendedFunction>> entryList) {
        entryList.add(new Map.Entry<>() {
            @Override
            public String getKey() {
                return name;
            }

            @Override
            public ExtendedFunction getValue() {
                return function;
            }

            @Override
            public ExtendedFunction setValue(ExtendedFunction functionWithRepresentation) {
                return null;
            }
        });
    }

    private int selectFunction(Console console, boolean initForFirst) throws IOException {
        console.println("\nВыберите функцию: ");
        int index = 0;

        for (Map.Entry<String, ExtendedFunction> entry : initForFirst ? functionList1 : functionList2) {

            console.println(String.format("[%d] %s", index, entry.getKey()));
            index++;
        }

        int select;

        try {
            select = Integer.parseInt(console.readLine());
            if (select >= (initForFirst ? functionList1 : functionList2).size()) {
                console.println("Введите правильное значение");
                return selectFunction(console, initForFirst);
            } else {
                return select;
            }
        } catch (NumberFormatException e) {
            console.println("Введите правильное значение");
            return selectFunction(console, initForFirst);
        } catch (IOException e) {
            console.println("[SYSTEM_ERROR] " + e.getMessage());
            console.println("По умолчанию выбрана 0 функция");
            return 0;
        }
    }
}
