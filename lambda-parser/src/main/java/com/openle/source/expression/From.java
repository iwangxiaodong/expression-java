package com.openle.source.expression;

import java.util.function.Function;

public class From<T> {

    DML dml = DML.SELECT;
    Function[] fs;

    protected From() {
    }

    protected From(DML dml) {
        this.dml = dml;
    }

    protected From(Function[] fs) {
        this.fs = fs;
    }

    public Where from(Class c) {
        return new Where(dml, c, null, fs);
    }

    public Where from(String tableName) {
        return new Where(dml, null, tableName, fs);
    }

}
