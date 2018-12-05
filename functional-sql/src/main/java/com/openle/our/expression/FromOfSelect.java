package com.openle.our.expression;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class FromOfSelect<T> extends Execute {

    Function[] fs;

    protected FromOfSelect(Function[] fs) {
        this.fs = fs;
        String rrr = "select null";
        Optional<List<String>> oList = Utils.functionsToList(fs, null);
        if (oList.isPresent()) {
            rrr = "select " + String.join(",", oList.get());
        }
        super.sqlString = rrr;
    }

    public Where from(Class c) {
        Where where = new Where(DML.SELECT, c, null, fs);
        return where;
    }

    public Where from(String tableName) {
        return new Where(DML.SELECT, null, tableName, fs);
    }

}
