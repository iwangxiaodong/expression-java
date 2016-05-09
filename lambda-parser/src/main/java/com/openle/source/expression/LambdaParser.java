package com.openle.source.expression;

import org.jinq.jooq.transform.LambdaInfo;
import org.jinq.jooq.transform.WhereTransform;
import org.jooq.Condition;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.conf.ParamType;
import org.jooq.impl.DSL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LambdaParser {

        /* Test - expression/src/test/java/com/openle/source/expression/LambdaParserTest.java

        PredicateSerializable<EntityDemo> whereLambda = null;

        whereLambda = t -> t.getAge() < (t.getAge() + 1) && t.getName().equals("myName") && true;

        String sql = LambdaParser.toSQL("SELECT * FROM myTable",whereLambda);

        System.out.println(sql);

        */

        /* Output:

        SELECT * FROM myTable where (
            Age < (Age + 1)
            and Name = 'myName'
        )

        */

    public static boolean isCamelToUnderline=false;

    private static Condition parseWhere(PredicateSerializable<?> lambda) {

        LambdaInfo where = LambdaInfo.analyze(null, lambda);
        if (where == null)
            throw new IllegalArgumentException("Could not create convert Lambda into a query");
        WhereTransform whereTransform = new WhereTransform(null, where);
        List<Table<?>> from = new ArrayList<>();
        Condition cond = whereTransform.apply(from);
        //System.out.println(cond);

        return cond;
    }

    public static String toSelectAllSQL(String fromTable, PredicateSerializable<?> lambda) {
        String sql = DSL.select().from(fromTable).where(parseWhere(lambda)).getSQL(ParamType.INLINED);
        //System.out.println(sql);
        return sql;
    }

    public static String toSQL(String selectFromSQL, PredicateSerializable<?> lambda) {
        Condition condition = parseWhere(lambda);
        if (condition == null) {
            return selectFromSQL;
        }

        String sql = selectFromSQL + " where " + condition;
        //System.out.println(sql);
        return sql;
    }

    public static String toSQL(String selectFields, String fromTable, PredicateSerializable<?> lambda) {
        Collection<SelectField> fields = new ArrayList<>();
        for (String s : selectFields.split(",")) {
            fields.add(DSL.field(s.trim()));
        }
        return toSQL(fields, fromTable, lambda);
    }

    public static String toSQL(Collection selectFields, String fromTable, PredicateSerializable<?> lambda) {
        String sql = DSL.select(selectFields).from(fromTable).where(parseWhere(lambda)).getSQL(ParamType.INLINED);
        // String sql = DSL.select(DSL.field("Age"), DSL.field("Name")).from(table).where(where(lambda)).getSQL(ParamType.INLINED);
        //System.out.println(sql);
        return sql;
    }
}
