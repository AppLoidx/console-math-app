package com.apploidxxx.app.core.command.impl;

import com.apploidxxx.app.console.Console;
import com.apploidxxx.app.core.command.Command;
import com.apploidxxx.app.core.command.impl.util.ConsoleUtil;
import com.apploidxxx.app.core.command.stereotype.Executable;
import core.impl.GaussMatrixSolver;
import model.Matrix;
import model.impl.SquareMatrix;

import java.util.Random;
import java.util.stream.IntStream;

/**
 * @author Arthur Kupriyanov on 05.03.2020
 */
@Executable("matrix-random")
public class MatrixRandom implements Command {
    @Override
    public void execute(Console console, String context) throws Exception {
        String[] args = context.split(" ");
        if (args.length < 2) {
            console.println("Формат команды matrix-random <размер>");
            return;
        }
        try {
            int dimension = Integer.parseInt(args[1]);

            if (dimension <= 0 || dimension > 20) {
                console.println("Размер матрицы не входит допустимые значения");
            } else {
                solve(console, dimension);
            }
        } catch (NumberFormatException e) {
            console.println("Размер матрицы должен быть указан числом");
        }
    }

    private void solve(Console console, int dimension) throws Exception {
        float[][] randMatrix = RandomizerUtil.getRandomFloatMatrix(dimension + 1, dimension);
        Matrix matrix = new SquareMatrix();
        matrix.init(randMatrix);

        GaussMatrixSolver solver = new GaussMatrixSolver(matrix);
        ConsoleUtil.printMatrixSolution(matrix, solver, console);
    }

    private static class RandomizerUtil {

        private static final Random random = new Random();


        public static float[][] getRandomMatrix(int x, int y) {
            return getRandomFloatMatrix(x, y);
        }


        private static float[][] getRandomFloatMatrix(int x, int y) {
            float[][] matrix = new float[y][x];
            return matrixFill(matrix, x, y);
        }


        private static float[][] matrixFill(final float[][] matrix, int x, int y) {
            IntStream.range(0, y)
                    .forEach(i -> {
                        IntStream.range(0, x)
                                .forEach(j -> {
                                    matrix[i][j] = Math.round(getRandomFloat());
                                });
                    });

            return matrix;
        }

        private static float getRandomFloat() {
            final int min = 12;
            final int max = 20;
            return min + random.nextFloat() * (max - min);
        }

    }
}
