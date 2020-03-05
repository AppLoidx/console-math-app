package com.apploidxxx.app.core.command.impl.util;

import com.apploidxxx.app.console.Console;
import model.Matrix;
import model.impl.SquareMatrix;

import java.util.Arrays;

/**
 * @author Arthur Kupriyanov on 05.03.2020
 */
public class MatrixReader {
    public static Matrix readMatrix(Console console, int dimension) throws Exception {

        float[][] matrixArr = new float[dimension][dimension + 1];
        console.clearScreen();
        console.println("ВВОД ДАННЫХ МАТРИЦЫ:");
        ConsoleUtil.printLine(console);
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < i; j++) {
                console.println(Arrays.toString(matrixArr[j]));
            };
            matrixArr[i] = readLineOfFloats(dimension, console);
            console.clearScreen();
        }

        Matrix matrix = new SquareMatrix();
        matrix.init(matrixArr);

        return matrix;
    }

    public static float[] readLineOfFloats(int dimension, Console console){

        String line = console.readLine("\n\nВведите " + dimension + " коэффициента и один свободный член в конце ");
        try {
            float[] values = parseLine(line);
            if (values.length != dimension + 1){
                console.println("Вы ввели неверное количество чисел");
                return readLineOfFloats(dimension, console);
            }

            return values;
        } catch (NumberFormatException e){
            console.println("Неверный формат!");
            return readLineOfFloats(dimension, console);
        }

    }

    public static float[] parseLine(String line){
        String[] args = line.split(" ");
        float[] values = new float[args.length];
        int index = 0;
        for (String arg : args){
            float number = Float.parseFloat(arg);
            values[index] = number;
            index++;
        }

        return values;
    }

}
