package com.openle.source.expression;

import static com.openle.source.expression.LambdaParser.parseWhere;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import org.jooq.Condition;

public class Where extends Execute {

    Class c;
    String beforeWhere;
    DML dml;

    //for Update
    protected Where(DML dml, Class c, List<Map<Function, ?>> setMap) {
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
                name = new Utils().getSelectName(c, f);
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
        beforeWhere = "update " + Utils.getTableName(c) + " set " + String.join(" , ", list);
        super.sqlString = beforeWhere;
    }

    //for select delete
    protected Where(DML dml, Class c, Function[] fs) {
        this.c = c;
        if (dml.equals(DML.DELETE)) {
            beforeWhere = "delete from " + Utils.getTableName(c);
        }

        if (dml.equals(DML.SELECT)) {
            if (fs != null && fs.length > 0) {
                List<String> list = new ArrayList<>();
                for (Function f : fs) {
                    String name = new Utils().getSelectName(c, f);
                    if (name.startsWith("get")) {
                        name = name.replaceFirst("get", "");
                    }
                    list.add(name);
                }
                beforeWhere = "select " + String.join(",", list) + " from " + Utils.getTableName(c);
            } else {
                beforeWhere = "select * from " + Utils.getTableName(c);
            }
        }

        super.sqlString = beforeWhere;
    }

    // 注意：lambda表达式左侧为getXxx()方法，右侧输入最好将方法转换为变量再代入。
    public Execute where(PredicateSerializable<?> lambda) {
        String where = "";
        if (lambda != null) {
            Condition con = parseWhere(lambda);

            if (con != null) {
                where = " where " + con;
            }

        }
        return new Execute(beforeWhere + where);

    }

}
