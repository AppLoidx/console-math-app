package com.apploidxxx.app.console;

import java.io.IOException;

/**
 * @author Arthur Kupriyanov on 18.02.2020
 */
public interface Console {
    String readLine() throws IOException;

    int readInt() throws IOException;

    void clearScreen() throws IOException, InterruptedException;

    void print(String output);

    void println(String output);
}
