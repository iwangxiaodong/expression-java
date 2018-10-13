package com.openle.source.expression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class Set {

    DML dml = DML.UPDATE;
    Class c;
    String tableName;

    protected Set(Class c) {
        this.c = c;
    }

    protected Set(String tableName) {
        this.tableName = tableName;
    }

    //首个参数使其至少存在1个更新项参数！
    @SafeVarargs
    public final Where set(Map<Function, ?> firstSetMap, Map<Function, ?>... setMap) {
        Objects.requireNonNull(firstSetMap);

        List<Map<Function, ?>> list = new ArrayList<>();
        list.add(firstSetMap);
        list.addAll(Arrays.asList(setMap));
        Where where = new Where(dml, c, tableName, list);
        return where;
    }
}
