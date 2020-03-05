package com.apploidxxx.app;

import com.apploidxxx.app.console.Console;
import com.apploidxxx.app.console.impl.DefaultConsole;
import com.apploidxxx.app.core.Shell;
import com.apploidxxx.app.core.impl.DefaultShell;
import org.jline.reader.UserInterruptException;

import java.io.InterruptedIOException;

/**
 * @author Arthur Kupriyanov on 18.02.2020
 */
public class Application {
    public static void main(String[] args) throws Exception {

        Console console = new DefaultConsole(System.in, System.out);
        Shell shell = new DefaultShell(console);
        try {
            shell.run();
        } catch (UserInterruptException | InterruptedException | InterruptedIOException e){
            console.clearScreen();
            System.out.println("Программа завершила свою работу");
        }

    }
}
