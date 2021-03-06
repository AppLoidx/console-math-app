package com.apploidxxx.app.core.command.impl.util;

import com.apploidxxx.app.console.Console;
import core.impl.GaussMatrixSolver;
import lombok.SneakyThrows;
import model.Matrix;
import util.printer.MatrixPrinter;
import util.printer.impl.DatabaseLikePrinter;
import util.printer.impl.SimplePrettyPrinter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * @author Arthur Kupriyanov on 05.03.2020
 */
public final class ConsoleUtil {
    private ConsoleUtil() {
    }

    /**
     * @param prompt  left prompt
     * @param console IO interface
     * @return user's double
     */
    @SneakyThrows
    public static double readDouble(String prompt, Console console) {
        console.print(prompt);
        try {
            return Double.parseDouble(console.readLine());
        } catch (NumberFormatException e) {
            console.println("Введите правильное число");
            return readDouble(prompt, console);
        }
    }

    /**
     * Parse and get param from context
     *
     * @param key     value which starts with "--". For example, <code>--limit-off</code> -> limit-off
     * @param context context of command
     * @return key
     * TODO: rewrite with return value of key
     */
    public static Optional<String> getParam(String key, String context) {
        String[] args = context.split(" ");
        for (String arg : args) {
            if (arg.startsWith("--")) {
                String val = arg.substring(2);
                if (val.equals(key)) {
                    return Optional.of(val);
                }
            }
        }

        return Optional.empty();
    }

    /**
     * @see ConsoleUtil#readInt(Console, int, int)
     */
    public static int readInt(Console console) throws IOException {
        try {
            return Integer.parseInt(console.readLine());
        } catch (NumberFormatException e) {
            console.println("Введите цело число в области значений int");
            return readInt(console);
        }
    }

    /**
     * @param console  with out
     * @param minValue min value (include)
     * @param maxValue max value (include)
     * @return user's int
     */
    public static int readInt(Console console, int minValue, int maxValue) throws IOException {
        int numb = readInt(console);
        if (numb <= maxValue && numb >= minValue) {
            return numb;
        } else {
            console.println(String.format("Введите значение удовлетворяющее %d < x < %d", minValue, maxValue));
            return readInt(console, minValue, maxValue);
        }
    }

    /**
     * Reads two boundaries of function
     *
     * @param console IO interface
     * @return array of bounds [first, second]
     */
    public static double[] readBoundaries(Console console) {
        double[] boundaries = new double[2];
        boundaries[0] = readDouble("\nВведите нижнюю границу:", console);
        boundaries[1] = readDouble("\nВведите верхнюю границу:", console);

        if (boundaries[0] == boundaries[1]) {
            console.println("Вы ввели одинаковые значения для области. Возможно, вы ошиблись при вводе");
            return readBoundaries(console);
        }

        return boundaries;
    }

    /**
     * Print one long line on whole console width
     *
     * @param console Output interface
     */
    public static void printLine(Console console) {
        String line = "_".repeat(console.getSize());
        console.println(line);
    }

    /**
     * Select one of the passed select functions
     *
     * @param console   IO interface
     * @param functions list of functions
     * @param <T>       select function target
     * @return selected functions
     * @throws IOException IO
     */
    public static <T> SelectFunction<T> selectFunction(Console console, List<SelectFunction<T>> functions) throws IOException {
        console.println("Выберите функцию");
        IntStream.range(0, functions.size())
                .mapToObj(i -> String.format("[%d] %s", i, functions.get(i).getName()))
                .forEachOrdered(console::println);
        int function = ConsoleUtil.readInt(console, 0, functions.size() - 1);
        return functions.get(function);
    }

    /**
     * Pretty print for Matrix
     *
     * @param matrix  printable matrix
     * @param solver  getting calculate determinant from used instance
     * @param console IO interface
     * @throws Exception IO or {@link ProcessBuilder} exception
     */
    public static void printMatrixSolution(Matrix matrix, GaussMatrixSolver solver, Console console) throws Exception {


        console.println("В каком формате хотите вывести матрицу" +
                        "\nОбычный [1] или Database-like [2]");
        MatrixPrinter printer = readOneOrTwo(console) == 1 ?
                new SimplePrettyPrinter(console.getOut()) :
                new DatabaseLikePrinter(console.getOut());

        console.clearScreen();
        float determinant = solver.getDeterminant();
        if (0 == determinant || Float.isNaN(determinant)) {
            console.println("Система имеет бесконечное количество решений или не имеет вовсе");
            return;
        }

        console.println(" Исходная матрица:");
        printer.prettyPrint(matrix);

        console.println(" \nДетерминант:");
        console.println(String.format("%f", solver.getDeterminant()));

        console.println(" \nТреугольная матрица:");
        printer.prettyPrint(solver.getTriangleMatrix());

        console.println("\nПеременные:");
        printVariables(solver.getVariables(), console);

        console.println("\nНевязка:");
        printResidual(solver.getResidualColumn(), console);

        console.println("");
        ConsoleUtil.printLine(console);
    }

    private static int readOneOrTwo(Console console) throws IOException {
        int value = console.readInt();
        if (value < 1 || value > 2) {
            console.print("\nВыберите 1 или 2:");
            return readOneOrTwo(console);
        }

        return value;
    }

    private static void printVariables(float[] vars, Console console) {
        int index = 1;
        for (float val : vars) {
            console.print(String.format("x%d: %f, ", index, val));
            index++;
        }
    }

    private static void printResidual(float[] vars, Console console) {

        for (float val : vars) {
            console.print(String.format("%e, ", val));
        }
    }
}
