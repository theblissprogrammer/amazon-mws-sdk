package com.theblissprogrammer.common.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface RoomQuery {
    /**
     * The SQLite query to be run.
     * @return The query to be run.
     */
    String value();
}