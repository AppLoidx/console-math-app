package com.apploidxxx.app.console;

import java.io.IOException;
import java.io.PrintStream;

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

    String readLine(String rightPrompt);
    String readLine(String leftPrompt, String rightPrompt);

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

    PrintStream getOut();

    /**
     * Size of console in chars
     * <br/>
     *
     * <bold>IMPORTANT</bold> : I don't recommend to set it as 0,
     * because this property used for pretty printers and other
     * operations which depends on the size of console. And I really
     * don't recommend to return a large size.
     * <br/>
     *
     * For example, it used in {@link com.apploidxxx.app.core.command.impl.util.ConsoleUtil#printLine(Console)}
     *
     * @return console width in chars
     */
    int getSize();

}
