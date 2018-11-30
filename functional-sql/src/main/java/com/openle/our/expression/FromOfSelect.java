package com.openle.our.expression;

import java.util.function.Function;

public class FromOfSelect<T> extends Execute {

    Function[] fs;

    protected FromOfSelect(Function[] fs) {
        this.fs = fs;

        String[] arr = new String[]{"select null"};
        Utils.functionsToList(fs, null).ifPresent(list -> {
            arr[0] = "select " + String.join(",", list);
        });
        super.sqlString = arr[0];
    }

    public Where from(Class c) {
        Where where=new Where(DML.SELECT, c, null, fs);
        return where;
    }

    public Where from(String tableName) {
        return new Where(DML.SELECT, null, tableName, fs);
    }

}
