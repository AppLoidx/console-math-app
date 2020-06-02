package com.apploidxxx.app.core.command.impl;

import com.apploidxxx.app.console.Console;
import com.apploidxxx.app.core.command.Command;
import com.apploidxxx.app.core.command.impl.util.ConsoleUtil;
import com.apploidxxx.app.core.command.impl.util.MatrixReader;
import com.apploidxxx.app.core.command.stereotype.Executable;
import core.impl.GaussMatrixSolver;
import model.Matrix;
import model.impl.SquareMatrix;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Arthur Kupriyanov on 05.03.2020
 */
@Executable("matrix-file")
public class MatrixFile implements Command {

    @Override
    public void execute(Console console, String context) throws Exception {
        console.println("Введите имя файла:");
        String path = console.readPath().trim();
        File file = new File(path);

        if (!file.canRead()){
            console.println("Нету прав доступа для чтения файла!");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))){

            String st;
            List<Float[]> floatArray = new LinkedList<>();
            while((st = br.readLine()) != null){
                Float[] values = from(MatrixReader.parseLine(st));
                floatArray.add(values);
            }

            Matrix matrix = getMatrix(floatArray);
            if (!GaussMatrixSolver.isCanBeSolved(matrix)){
                console.println("Система не пригодна для решения стандартным методом Гаусса");
                return;
            }
            GaussMatrixSolver solver = new GaussMatrixSolver(matrix);
            ConsoleUtil.printMatrixSolution(matrix, solver, console);
        } catch (FileNotFoundException e) {
            console.println("Файл с таким именем не найден\nНажмите любую клавишу для продолжения...");
            console.readLine();
            console.clearScreen();
        } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e){
            console.println("Файл имеет неверное форматирование!\nНажмите любую клавишу для продолжения...");
            console.readLine();
            console.clearScreen();
        }
    }

    private Float[] from(float[] array){
        Float[] arrayFloat = new Float[array.length];
        int index = 0;
        for( float val : array){
            arrayFloat[index] = val;
            index++;
        }

        return arrayFloat;
    }
    private float[] from(Float[] array){
        float[] arrayFloat = new float[array.length];
        int index = 0;
        for( float val : array){
            arrayFloat[index] = val;
            index++;
        }

        return arrayFloat;
    }

    private Matrix getMatrix(List<Float[]> floatArray){
        float[][] floats = new float[floatArray.size()][floatArray.get(0).length];
        int index = 0;
        for (Float[] arr: floatArray){
            floats[index] = from(arr);
            index++;
        }

        Matrix matrix = new SquareMatrix();
        matrix.init(floats);
        return matrix;
    }
}
