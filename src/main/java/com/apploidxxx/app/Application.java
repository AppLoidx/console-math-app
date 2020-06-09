package com.apploidxxx.app;

import com.apploidxxx.app.console.Console;
import com.apploidxxx.app.console.ConsoleManager;
import com.apploidxxx.app.console.impl.SystemConsole;
import com.apploidxxx.app.core.Shell;
import com.apploidxxx.app.core.impl.DefaultShell;
import org.jline.reader.UserInterruptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InterruptedIOException;

/**
 * @author Arthur Kupriyanov on 18.02.2020
 */
public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws Exception {

        Console console = createConsole(args);

        Shell shell = new DefaultShell(console);

        try {
            shell.run();
        } catch (UserInterruptException | InterruptedException | InterruptedIOException e) {
            console.clearScreen();
        }

    }

    private static Console createConsole(String[] args) {
        if (args.length > 0) {
            try {

                logger.info("Try to find console from args : " + args[0]);

                Console console = ConsoleManager.getConsoleByName(args[0]);

                logger.info("Found console : " + console.getClass().getSimpleName());

                return console;


            } catch (InstantiationException e) {
                logger.error("Can't create console from args");
                logger.warn("Please check your args with list of consoles: " + ConsoleManager.getConsoleNames());
                logger.warn("Using default system console...");
            }
        }

        return new SystemConsole();
    }
}
