package com.apploidxxx.app.core.command.impl;

import com.apploidxxx.app.console.Console;
import com.apploidxxx.app.core.command.Command;
import com.apploidxxx.app.core.command.impl.util.ConsoleUtil;
import com.apploidxxx.app.core.command.impl.util.SelectFunction;
import com.apploidxxx.app.core.command.stereotype.Executable;
import util.function.DiffEquation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Arthur Kupriyanov on 01.06.2020
 */
@Executable(value = "diff-equation", aliases = {"deq", "diff-eq"})
public class DiffEquationCommand implements Command {

    private List<SelectFunction<DiffEquation>> equations = new ArrayList<>();
    {
        initFunctions();
    }


    @Override
    public void execute(Console console, String context) throws Exception {
        SelectFunction<DiffEquation> func = ConsoleUtil.selectFunction(console, equations);

    }

    private void initFunctions() {
        DiffEquation function = new DiffEquation((x, y) -> y + (1 + x) * y * y);
        equations.add(createEquation("y' = y + (1 + x) * y^2", function));
    }

    private SelectFunction<DiffEquation> createEquation(String name, DiffEquation equation) {
        return new SelectFunction<>(name, equation);
    }

}
