package com.apploidxxx.app.core.command.impl.util;

/**
 * @author Arthur Kupriyanov on 01.06.2020
 */
public class SelectFunction<T> {
    private final String name;
    private final T func;

    public SelectFunction(String name, T func) {

        this.name = name;
        this.func = func;
    }

    public String getName() {
        return name;
    }

    public T getFunc() {
        return func;
    }

}
