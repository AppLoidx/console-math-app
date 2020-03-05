package com.apploidxxx.app.core.command.impl;

import com.apploidxxx.app.console.Console;
import com.apploidxxx.app.core.command.Command;
import com.apploidxxx.app.core.command.impl.util.ConsoleUtil;
import com.apploidxxx.app.core.command.impl.util.MatrixReader;
import com.apploidxxx.app.core.command.stereotype.Executable;
import com.apploidxxx.app.core.exception.FloatOverflowException;
import core.impl.GaussMatrixSolver;

/**
 * @author Arthur Kupriyanov on 05.03.2020
 */
@Executable("matrix")
public class Matrix implements Command {

    @Override
    public void execute(Console console, String context) throws Exception {
        int dimension = readDimension(console);

        model.Matrix matrix = MatrixReader.readMatrix(console, dimension);
        console.clearScreen();
        if (!GaussMatrixSolver.isCanBeSolved(matrix)) {
            console.println("Система не пригодна для решения стандартным методом Гаусса");
            return;
        }
        GaussMatrixSolver solver = new GaussMatrixSolver(matrix);
        ConsoleUtil.printSolution(matrix, solver, console);

    }


    private int readDimension(Console console) {
        console.print("Введите размерность матрицы: ");
        String dimensionString = console.readLine("\nЧисло в диапозоне от 1 до 20 ");

        try {
            int dimension = Integer.parseInt(dimensionString);
            if (dimension <= 0 || dimension > 20) {
                console.println("Размерность матрицы должна быть в диапазоне : [1, 20]");
                return readDimension(console);
            }

            return dimension;
        } catch (NumberFormatException e) {
            console.println("Введите число от 1 до 20");
            return readDimension(console);
        }

    }

}
