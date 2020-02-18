package com.apploidxxx.app.console;

import java.io.IOException;

/**
 * @author Arthur Kupriyanov on 18.02.2020
 */
public interface Console {
    /**
     * Read line from terminal
     *
     * @return User input
     * @throws IOException If an I/O error occurs
     */
    String readLine() throws IOException;

    /**
     * Read file system path from terminal
     *
     * @return String relative path
     * @throws IOException If an I/O error occurs
     */
    String readPath() throws IOException;

    /**
     * Read first Integer appear
     *
     * @return User input
     * @throws IOException If an I/O error occurs
     */
    int readInt() throws IOException;

    /**
     * Clear terminal screen
     *
     * @throws IOException If an I/O error occurs
     * @throws Exception   If an error with {@link ProcessBuilder} occurs
     */
    void clearScreen() throws Exception;

    /**
     * Prints a string
     *
     * @param output The String to be printed
     */
    void print(String output);

    /**
     * Prints a String and then terminate the line
     *
     * @param output The String to be printed
     */
    void println(String output);
}
