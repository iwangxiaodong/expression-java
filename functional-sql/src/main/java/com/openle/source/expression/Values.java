package com.openle.source.expression;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Values {

    Class c;
    Function[] fs;
    String sName = "";

    protected Values(Class c, Function[] fs, boolean isIgnore) {
        this.c = c;
        this.fs = fs;
        sName = "insert " + (isIgnore ? "ignore " : "") + Utils.getTableName(c) + " ";

        if (fs.length > 0) {
            List<String> list = new ArrayList<>();
            for (Function f : fs) {
                String name = "";
//                if (f instanceof KeepField) {
//                    System.out.println("keepField");
//                    name = ((KeepField) f).getFieldName();
//                } else {
//                    name = new Utils().getSelectName(c, f);
//                }

                name = new Utils().getSelectName(c, f);
                if (name.startsWith("get")) {
                    name = name.replaceFirst("get", "");
                }

                list.add(name);
            }
            sName = sName + "(" + String.join(",", list) + ") ";
        }

    }

    protected Values(Class c, boolean isIgnore) {
        sName = "insert " + (isIgnore ? "ignore " : "") + Utils.getTableName(c) + " ";
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
            String s =Utils.getValueString(c, value);
            list.add(s);
        }
        String values = String.join(",", list);
        sName = sName + "values (" + values + ")";

        return new Execute(sName);
    }

}
