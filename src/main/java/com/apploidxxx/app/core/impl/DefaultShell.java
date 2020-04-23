package com.apploidxxx.app.core.impl;

import com.apploidxxx.app.console.Console;
import com.apploidxxx.app.core.Shell;
import com.apploidxxx.app.core.command.CommandManager;

/**
 * @author Arthur Kupriyanov on 18.02.2020
 */
public class DefaultShell implements Shell {
    private final Console console;
    private final CommandManager commandManager;
    public DefaultShell(Console console) {
        this.console = console;
        commandManager = new CommandManager();
        commandManager.init("com.apploidxxx.app.core.command");
    }

    @Override
    public void run() throws Exception {
        console.clearScreen();
        printWelcomeText();

        startMainLoop();

    }

    private void startMainLoop() throws Exception {
        String command;
        do {
            command = console.readLine();
            if (!command.equals("") && command.split(" ").length > 0)
            commandManager.getCommand(command.split(" ")[0]).execute(console, command);
        } while (isNotExitCommand(command));
    }

    private boolean isNotExitCommand(String command){
        return !"exit".equals(command);
    }

    private void printWelcomeText(){
        console.println(" _           _          _____ \n" +
                        "| |         | |        |____ |\n" +
                        "| |     __ _| |__ ______   / /\n" +
                        "| |    / _` | '_ \\______|  \\ \\\n" +
                        "| |___| (_| | |_) |    .___/ /\n" +
                        "\\_____/\\__,_|_.__/     \\____/ \n" +
                        "                              \n" +
                        "                              ");
        printAuthor();
    }

    private void printAuthor(){
        console.println("author: Arthur Kupriyanov");
    }
}
