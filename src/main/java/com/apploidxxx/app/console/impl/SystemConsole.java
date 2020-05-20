package com.apploidxxx.app.console.impl;

import com.apploidxxx.app.console.Console;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * @author Arthur Kupriyanov on 20.05.2020
 */
public class SystemConsole implements Console {

    private final static Scanner scanner = new Scanner(System.in);

    @Override
    public String readLine() throws IOException {
        return scanner.nextLine();
    }

    @Override
    public String readLine(String rightPrompt) {
        return scanner.nextLine();
    }

    @Override
    public String readLine(String leftPrompt, String rightPrompt) {
        return scanner.nextLine();
    }

    @Override
    public String readPath() throws IOException {
        return scanner.nextLine();
    }

    @Override
    public int readInt() throws IOException {
        return Integer.parseInt(scanner.nextLine());
    }

    @Override
    public void clearScreen() throws Exception {

    }

    @Override
    public void print(String output) {
        System.out.print(output);
    }

    @Override
    public void println(String output) {
        System.out.println(output);
    }

    @Override
    public PrintStream getOut() {
        return System.out;
    }

    @Override
    public int getSize() {
        return 300;
    }
}
