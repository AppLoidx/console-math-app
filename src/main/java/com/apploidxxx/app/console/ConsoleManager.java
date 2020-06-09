package com.apploidxxx.app.console;

import lombok.SneakyThrows;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * Searches consoles in {@link com.apploidxxx.app} package (but I recommend store console in {@link com.apploidxxx.app.console.impl} package)
 *
 * @author Arthur Kupriyanov on 09.06.2020
 */
public class ConsoleManager {
    private static final List<Class<? extends Console>> consoles = new ArrayList<>();
    private final static PrintStream DEFAULT_OUT = System.out;
    private final static InputStream DEFAULT_IN = System.in;
    private final static String CONSOLES_PACKAGE = "com.apploidxxx.app";

    static {
        scanConsoles();
    }

    private static void scanConsoles() {
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(true);

        scanner.addIncludeFilter(new AssignableTypeFilter(Console.class));
        for (BeanDefinition bd : scanner.findCandidateComponents(CONSOLES_PACKAGE)) {
            processBean(bd);
        }
    }

    @SneakyThrows
    private static void processBean(BeanDefinition bd) {
        Class<?> clazz = Class.forName(bd.getBeanClassName());

        if (isImplementedInterface(clazz)) {
            //noinspection unchecked
            consoles.add((Class<? extends Console>) clazz);
        }
    }

    private static boolean isImplementedInterface(Class<?> clazz) {
        boolean implemented = false;
        for (Class<?> i : clazz.getInterfaces()) {
            if (i == Console.class) {
                implemented = true;
                break;
            }
        }
        return implemented;
    }

    public static List<String> getConsoleNames() {
        List<String> names = new ArrayList<>();
        for (Class<? extends Console> clazz : consoles) {
            names.add(clazz.getSimpleName());
        }

        return names;
    }

    public static Console getConsoleByName(String consoleSimpleName, InputStream in, PrintStream out) throws InstantiationException {
        for (Class<? extends Console> console : consoles) {
            if (console.getSimpleName().equals(consoleSimpleName)) {
                return getConsole(console, in, out);
            }
        }
        throw new InstantiationException("Can't find console with name " + consoleSimpleName);
    }

    public static Console getConsoleByName(String consoleSimpleName) throws InstantiationException {
        return getConsoleByName(consoleSimpleName, DEFAULT_IN, DEFAULT_OUT);
    }

    private static Console getConsole(Class<? extends Console> clazz, InputStream in, PrintStream out) throws InstantiationException {
        for (Class<? extends Console> console : consoles) {
            if (console.equals(clazz)) {
                return createInstance(clazz, in, out);
            }
        }
        throw new InstantiationException("Can't find console " + clazz.getCanonicalName());
    }

    private static Console createInstance(Class<? extends Console> clazz, InputStream in, PrintStream out) throws InstantiationException {
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            int parameters = constructor.getParameterCount();
            if (parameters == 2) {
                return createInstance(constructor, in, out);
            } else if (parameters == 0) {
                return createInstance(constructor);
            } else {
                throw new InstantiationException("Find class with invalid amount of types in constructor " + clazz.getCanonicalName());
            }
        }
        throw new InstantiationException("Can't find valid constructor " + clazz.getCanonicalName());

    }

    @SneakyThrows
    private static Console createInstance(Constructor<?> constructor, InputStream is, PrintStream out) {
        java.io.Closeable param1 = null;
        java.io.Closeable param2 = null;
        for (Class<?> parameterType : constructor.getParameterTypes()) {
            if (parameterType.equals(InputStream.class)) {
                if (param1 == null) param1 = is;
                else param2 = is;
            } else {
                if (param1 == null) param1 = out;
                else param2 = out;
            }
        }

        return (Console) constructor.newInstance(param1, param2);
    }

    @SneakyThrows
    private static Console createInstance(Constructor<?> constructor) {
        return (Console) constructor.newInstance();
    }
}
