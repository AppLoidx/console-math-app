package com.apploidxxx.app.core.command.impl.util;

import com.apploidxxx.app.console.Console;
import core.impl.GaussMatrixSolver;
import model.Matrix;
import util.printer.MatrixPrinter;
import util.printer.impl.DatabaseLikePrinter;
import util.printer.impl.SimplePrettyPrinter;

import java.io.IOException;

/**
 * @author Arthur Kupriyanov on 05.03.2020
 */
public class ConsoleUtil {
    public static void printLine(Console console){
        String line = "_".repeat(console.getSize());
        console.println(line);
    }

    public static void printSolution(Matrix matrix, GaussMatrixSolver solver, Console console) throws Exception {


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

    private static void printVariables(float[] vars, Console console){
        int index = 1;
        for (float val : vars){
            console.print(String.format("x%d: %f, ", index, val));
            index++;
        }
    }
    private static void printResidual(float[] vars, Console console){

        for (float val : vars){
            console.print(String.format("%e, ", val));
        }
    }
}
