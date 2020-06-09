package com.apploidxxx.app.console.impl;

import com.apploidxxx.app.console.Console;
import org.jline.builtins.Completers;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.MaskingCallback;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;
import org.jline.utils.NonBlockingReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

/**
 *
 * Works only in bash (not work in idea console)
 *
 * @author Arthur Kupriyanov on 18.02.2020
 */
public class DefaultConsole implements Console {
    private final PrintStream out;
    private final Terminal terminal;

    public DefaultConsole(InputStream in, PrintStream out) throws IOException {
        this.out = out;
        terminal = TerminalBuilder.builder()
                .jna(true)
                .system(true)
                .streams(in, out)
                .build();
    }

    @Override
    public String readLine() {
        LineReader reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .variable(LineReader.SECONDARY_PROMPT_PATTERN, "> ")
                .variable(LineReader.INDENTATION, 2)
                .build();

        return reader.readLine("");
    }

    @Override
    public String readLine(String rightPrompt) {
        LineReader reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .variable(LineReader.SECONDARY_PROMPT_PATTERN, "> ")
                .variable(LineReader.INDENTATION, 2)
                .build();

        return reader.readLine("", rightPrompt, (MaskingCallback) null , null);
    }

    @Override
    public String readLine(String leftPrompt, String rightPrompt) {
        LineReader reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .variable(LineReader.SECONDARY_PROMPT_PATTERN, "> ")
                .variable(LineReader.INDENTATION, 2)
                .build();

        return reader.readLine(leftPrompt, rightPrompt, (MaskingCallback) null , null);
    }

    @Override
    public String readPath() {
        Completers.FileNameCompleter filesCompleter = new Completers.FileNameCompleter();
        LineReader reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .completer(filesCompleter)
                .variable(LineReader.SECONDARY_PROMPT_PATTERN, "> ")
                .variable(LineReader.INDENTATION, 2)
                .build();

        return reader.readLine("");
    }

    /**
     * Reads int from terminals and ignores all literal chars input
     * @return User input
     * @throws IOException  If an I/O error occurs
     */
    @Override
    public int readInt() throws IOException {
        StringBuilder sb = new StringBuilder();
        terminal.enterRawMode();
        NonBlockingReader reader = terminal.reader();
        while (true) {

            int read = reader.read();
            char inp = (char) read;

            if (Character.isDigit(inp)) {
                sb.append(Character.getNumericValue(inp));
                out.print(Character.getNumericValue(inp));
            } else if (Character.isSpaceChar(inp) || Character.LINE_SEPARATOR == inp) {

                break;
            }

        }

        return Integer.parseInt(sb.toString());
    }

    @Override
    public void clearScreen() {
        terminal.puts(InfoCmp.Capability.clear_screen);
        terminal.flush();
    }


    @Override
    public void print(String output) {
        out.print(output);
    }


    @Override
    public void println(String output) {
        out.println(output);
    }

    @Override
    public PrintStream getOut() {
        return out;
    }

    @Override
    public int getSize() {
        return terminal.getWidth();
    }
}
