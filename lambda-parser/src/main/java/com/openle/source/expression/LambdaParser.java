package com.openle.source.expression;

import ch.epfl.labos.iu.orm.queryll2.symbolic.MethodSignature;
import org.jinq.jooq.transform.LambdaInfo;
import org.jooq.Condition;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.conf.ParamType;
import org.jooq.impl.DSL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.jinq.jooq.transform.MetamodelUtil;
import org.jinq.jooq.transform.MyInterceptor;
import org.jinq.jooq.transform.MyWhereTransform;

/**
 * 类似开源项目 - https://github.com/ajermakovics/lambda2sql
 */
public class LambdaParser {

    public static boolean isCamelToUnderline = false;

    protected static Condition parseWhere(PredicateSerializable<?> lambda) {
        MetamodelUtil mu = new MyInterceptor().getMetamodelUtil();
        LambdaInfo where = LambdaInfo.analyze(mu, lambda);
        if (where == null) {
            throw new IllegalArgumentException("Could not create convert Lambda into a query");
        }

        MyWhereTransform whereTransform = new MyWhereTransform(mu, where);
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
