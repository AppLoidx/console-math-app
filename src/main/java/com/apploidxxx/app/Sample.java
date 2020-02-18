package com.apploidxxx.app;

import com.apploidxxx.app.console.Console;
import com.apploidxxx.app.console.impl.DefaultConsole;

import java.io.IOException;

/**
 * @author Arthur Kupriyanov on 18.02.2020
 */
public class Sample {
    public static void main(String[] args) throws IOException, InterruptedException {

        Console console = new DefaultConsole(System.in, System.out);

        System.out.println("Year screen is clear!");
        String numb = console.readLine();
        System.out.println("\nYour input" + numb);
    }
}
