package com.apploidxxx.app.core.command.impl;

import com.apploidxxx.app.console.Console;
import com.apploidxxx.app.core.command.Command;
import com.apploidxxx.app.core.command.stereotype.Executable;

/**
 * @author Arthur Kupriyanov on 05.03.2020
 */
@Executable("help")
public class Help implements Command {
    @Override
    public void execute(Console console, String context) throws Exception {
        console.println("matrix, matrix-file, matrix-random <размер_матрицы>");
    }
}
