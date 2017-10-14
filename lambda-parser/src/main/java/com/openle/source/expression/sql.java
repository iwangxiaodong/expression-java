package com.openle.source.expression;

import java.util.function.Function;

public class sql {

    // select * from ...
    public static <T> From select() {
        return new From();
    }

    public static <T> From select(final Function<T, ?>... getter) {
        return new From(getter);
    }

    public static <T> From delete() {
        return new From(DML.DELETE);
    }

    public static <T> Values insert(Class c) {
        return new Values(c);
    }

    public static <T> Values insert(Class c, final Function<T, ?>... getter) {
        return new Values(c,getter);
    }

//    public static <T> Set update(Class c) {
//        return new Set();
//    }
}
