package com.openle.our.expression;

import com.openle.our.core.CoreData;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class Values {

    Class c;
    Function[] fs;
    String sName = "";

    protected Values(Class c, Function[] fs, boolean isIgnore) {
        this.c = c;
        this.fs = fs;
        sName = "insert " + (isIgnore ? "ignore " : "") + CoreData.getTableName(c) + " ";

        Optional<List<String>> oList = Utils.functionsToList(fs, null);
        if (oList.isPresent()) {
            sName = sName + "(" + String.join(",", oList.get()) + ") ";
        }
    }

    protected Values(Class c, boolean isIgnore) {
        sName = "insert " + (isIgnore ? "ignore " : "") + CoreData.getTableName(c) + " ";
    }

    public Execute values(Object... objArray) {

        if (objArray.length == 0) {
            throw new IllegalArgumentException("insert语句 - 输入值数目不能为0！");
        }

        if (fs != null && fs.length > 0) {
            if (objArray.length != fs.length) {
                throw new IllegalArgumentException("insert语句 - 列和输入值数目不对！");
            }
        }

        List<String> list = new ArrayList<>();
        for (Object value : objArray) {
            String s = Utils.getValueString(c, value);
            list.add(s);
        }
        String values = String.join(",", list);
        sName = sName + "values (" + values + ")";

        return new Execute(sName);
    }

}
