package com.openle.source.expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class Values {

    Class c;
    Function[] fs;
    String sName = "";

    protected Values(Class c, Function[] fs) {
        this.c = c;
        this.fs = fs;
        sName = "insert " + Utils.getTableName(c) + " ";

        if (fs.length > 0) {
            List<String> list = new ArrayList<>();
            for (Function f : fs) {
                String name = new Utils().getSelectName(c, f);
                if (name.startsWith("get")) {
                    name = name.replaceFirst("get", "");
                }
                list.add(name);
            }
            sName = sName + "(" + String.join(",", list) + ")";
        }

    }

    public Values(Class c) {
        sName = "insert " + Utils.getTableName(c) + " ";

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
        for (Object obj : objArray) {
            String s = Objects.isNull(obj) ? "null" : String.valueOf(obj);
            if (Objects.nonNull(obj) && obj.getClass().equals(String.class)) {
                s = "'" + obj + "'";
                System.out.println("is String.class");
            }

//                if (obj.getClass().equals(Integer.class)) {
//                    s = String.valueOf(obj);
//                    System.out.println("is Integer.class");
//                }
            list.add(s);
        }
        String values = String.join(",", list);
        sName = sName + "values(" + values + ")";

        return new Execute(sName);
    }

}
