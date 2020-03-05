package com.apploidxxx.app.core.command.stereotype;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Arthur Kupriyanov on 18.02.2020
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Executable {
    /**
     * @return Command name
     */
    String value();
    String[] aliases() default {};
}
