package com.openle.source.expression;

import com.openle.source.expression.serializable.PredicateSerializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import org.jooq.Condition;

public class Where extends Execute {

    //Class c;
    String beforeWhere;
    DML dml;

    //for Update
    protected Where(DML dml, Class c, String tableName, List<Map<Function, ?>> setMap) {
        tableName = c != null ? Utils.getTableName(c) : tableName;
        List<String> list = new ArrayList<>();
        for (Map<Function, ?> m : setMap) {
            for (Map.Entry<Function, ?> entry : m.entrySet()) {
                Object value = entry.getValue();
                Function f = entry.getKey();
                String name = "";
//                if (f instanceof KeepField) {
//                    System.out.println("keepField");
//                    name = ((KeepField) f).getFieldName();
//                } else {
//                    name = new Utils().getSelectName(c, f);
//                }

                name = new Utils().getSelectName(c != null ? c : Object.class, f);
                if (name.startsWith("get")) {
                    name = name.replaceFirst("get", "");
                }

                String s = Objects.isNull(value) ? "null" : String.valueOf(value);
                if (Objects.nonNull(value) && value.getClass().equals(String.class)) {
                    s = "'" + value + "'";
                }
                list.add(name + " = " + s);
            }
        }
        beforeWhere = "update " + tableName + " set " + String.join(" , ", list);
        super.sqlString = beforeWhere;
    }

    //for select delete
    protected Where(DML dml, Class c, String tableName, Function[] fs) {
        //this.c = c;
        tableName = c != null ? Utils.getTableName(c) : tableName;

        if (dml.equals(DML.DELETE)) {
            beforeWhere = "delete from " + tableName;
        }

        if (dml.equals(DML.SELECT)) {
            if (fs != null && fs.length > 0) {
                List<String> list = new ArrayList<>();
                for (Function f : fs) {
                    String name = new Utils().getSelectName(c != null ? c : Object.class, f);
                    if (name.startsWith("get")) {
                        name = name.replaceFirst("get", "");
                    }
                    list.add(name);
                }
                beforeWhere = "select " + String.join(",", list) + " from " + tableName;
            } else {
                beforeWhere = "select * from " + tableName;
            }
        }

        super.sqlString = beforeWhere;
    }

    // 注意：where表达式两侧若是方法，则取方法名，若是变量则直接代入值而不进入方法名判断。
    public Execute where(PredicateSerializable<?> lambda) {

        String where = "";
        if (lambda != null) {
            Condition cond = LambdaParser.parseWhere(lambda);

            if (cond != null) {
                where = " where " + cond;
            }

        }
        return new Execute(beforeWhere + where);

    }

//    public Execute where(Predicate<?> lambda) {
//        System.out.println("where Predicate");
//        return null;
//    }
}
