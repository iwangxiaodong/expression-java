package com.openle.source.expression;

import static com.openle.source.expression.LambdaParser.parseWhere;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.jooq.Condition;

public class Where {

    Class c;
    String selectName;
    DML dml;

    protected Where(DML dml, Class c, Function[] fs) {
        this.c = c;
        if (dml.equals(DML.DELETE)) {
            selectName = "delete ";
        }

        if (dml.equals(DML.SELECT)) {
            if (fs != null) {
                List<String> list = new ArrayList<>();
                for (Function f : fs) {
                    String name = new Utils().getSelectName(c, f);
                    if (name.startsWith("get")) {
                        name = name.replaceFirst("get", "");
                    }
                    list.add(name);
                }
                selectName = "select " + String.join(",", list) + " ";
            } else {
                selectName = "select * ";
            }
        }

    }

    // 注意：lambda表达式左侧为getXxx()方法，右侧输入最好将方法转换为变量再代入。
    @SuppressWarnings("unchecked")
    public Execute where(PredicateSerializable<?> lambda) {
        Condition con = parseWhere(lambda);
        String where = "";
        if (con != null) {
            where = " where " + con;
        }

        String sql = selectName + "from " + Utils.getTableName(c) + where;
        return new Execute(sql);
    }

}
