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

    protected Set(Class c) {
        this.c = c;
    }

    //首个参数使其至少存在1个更新项参数！
    public Where set(Map<Function, Object> firstSetMap, Map<Function, Object>... setMap) {
        Objects.requireNonNull(firstSetMap);

        List<Map<Function, ?>> list = new ArrayList<>();
        list.add(firstSetMap);
        list.addAll(Arrays.asList(setMap));
        Where where = new Where(dml, c, list);
        return where;
    }
}
