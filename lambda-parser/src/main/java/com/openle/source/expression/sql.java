package com.openle.source.expression;

import java.util.Map;
import java.util.function.Function;

public class sql {

    public static <T> From select(final Function<T, ?>... getter) {
        return new From(getter);
    }

    public static <T> Values insert(Class c) {
        return new Values(c);
    }

    public static <T> Values insert(Class c, final Function<T, ?>... getter) {
        return new Values(c, getter);
    }

    public static <T> Set update(Class c) {
        return new Set(c);
    }

    public static <T> From delete() {
        return new From(DML.DELETE);
    }

    //用于UPDATE XXX SET K=V更新项
    public static <T> Map<Function, Object> kv(final Function<T, ?> getter, Object obj) {
        //注意of方法构造的对象是只读的！
        return Map.<Function, Object>of(getter, obj);
    }
}
