package com.apploidxxx.app.core.command;

import com.apploidxxx.app.console.Console;

/**
 * @author Arthur Kupriyanov on 18.02.2020
 */
@FunctionalInterface
public interface Command {
    /**
     *
     * @param console Terminal
     * @param context Executed command
     */
    void execute(Console console, String context) throws Exception;
}
