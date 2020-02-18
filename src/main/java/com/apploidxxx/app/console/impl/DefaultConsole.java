package com.apploidxxx.app.console.impl;

import com.apploidxxx.app.console.Console;
import org.jline.builtins.Completers;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;
import org.jline.utils.NonBlockingReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

/**
 * @author Arthur Kupriyanov on 18.02.2020
 */
public class DefaultConsole implements Console {
    private static final int SPACE_CODE = 32;
    private static final int NEWLINE_CODE = 10;
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
    public String readLine() throws IOException {
        LineReader reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .variable(LineReader.SECONDARY_PROMPT_PATTERN, "> ")
                .variable(LineReader.INDENTATION, 2)
                .build();

        return reader.readLine();
    }

    @Override
    public String readPath() throws IOException {
        Completers.FileNameCompleter filesCompleter = new Completers.FileNameCompleter();
        LineReader reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .completer(filesCompleter)
                .variable(LineReader.SECONDARY_PROMPT_PATTERN, "> ")
                .variable(LineReader.INDENTATION, 2)
                .build();

        return reader.readLine();
    }

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
}
