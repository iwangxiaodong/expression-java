package com.openle.our.expression;

import java.util.function.Function;

public class FromOfDelete<T> {

    Function[] fs;

    protected FromOfDelete() {
    }

    protected FromOfDelete(Function[] fs) {
        this.fs = fs;
    }

    public Where from(Class c) {
        return new Where(DML.DELETE, c, null, fs);
    }

    public Where from(String tableName) {
        return new Where(DML.DELETE, null, tableName, fs);
    }

}
