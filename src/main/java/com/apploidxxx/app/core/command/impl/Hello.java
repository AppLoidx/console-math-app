package com.apploidxxx.app.core.command.impl;

import com.apploidxxx.app.console.Console;
import com.apploidxxx.app.core.command.Command;
import com.apploidxxx.app.core.command.stereotype.Executable;

/**
 * @author Arthur Kupriyanov on 18.02.2020
 */
@Executable(
        value = "hello",
        aliases = {
                "hi", "привет"
        }
)
public class Hello implements Command {

    @Override
    public void execute(Console console, String context) {
        console.println("Привет-Привет!");
    }
}
