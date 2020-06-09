package com.apploidxxx.app.core.exception;

/**
 * @author Arthur Kupriyanov on 05.03.2020
 */
public class FloatOverflowException extends RuntimeException {
    public FloatOverflowException() {
        super("Float overflowed", new RuntimeException(), false, false);
    }
}
