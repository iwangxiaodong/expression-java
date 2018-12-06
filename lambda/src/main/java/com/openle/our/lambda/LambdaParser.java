package com.openle.our.lambda;

import com.openle.our.core.lambda.SerializedPredicate;
import com.user00.thunk.SerializedLambda;
import org.jinq.jooq.transform.LambdaInfo;
import org.jooq.Condition;
import org.jooq.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.jinq.jooq.transform.MetamodelUtil;
import org.jinq.jooq.transform.WhereTransform;

// 解析Lambda表达式 - SerializedLambda.extractLambda(lambda).implMethodName;
public class LambdaParser {

    // jdk9-jdk11 ByteBuddy 兼容性 - https://mydailyjava.blogspot.com/2018/04/jdk-11-and-proxies-in-world-past.html
    public static void main(String[] args) throws Throwable {
        SerializedPredicate<?> lambda = (LambdaParser u) -> u.hashCode() > 0;
        System.out.println(LambdaParser.parseWhere(lambda));

    }

    public static boolean isCamelToUnderline = false;

    //  注意 - getter返回值必须是基本类型
    public static Condition parseWhere(SerializedPredicate<?> lambda) {
        MetamodelUtil mu = new MyInterceptor().getMetamodelUtil();
        LambdaInfo where = LambdaInfo.analyze(mu, lambda);
        if (where == null) {
            throw new IllegalArgumentException("Could not create convert Lambda into a query");
        }

        WhereTransform whereTransform = new WhereTransform(mu, where);
        List<Table<?>> from = new ArrayList<>();
        Condition cond = whereTransform.apply(from);
        //System.out.println(cond);

        return cond;
    }

    // 优先使用com.openle.module.core.lambda的原生实现。
    // extractLambda(ConsumerSerializable<?> lambda);
    @Deprecated
    static SerializedLambda extractLambda(Object lambda) {
        SerializedLambda s;
        try {
            s = SerializedLambda.extractLambda(lambda);
        } catch (Exception e) {
            Logger.getGlobal().severe(e.toString());
            return null;
        }
        return s;
    }

//    public static String toSelectAllSQL(String fromTable, SerializedPredicate<?> lambda) {
//        String sql = DSL.select().from(fromTable).where(parseWhere(lambda)).getSQL(ParamType.INLINED);
//        //System.out.println(sql);
//        return sql;
//    }
//
//
//    public static String toSQL(Collection selectFields, String fromTable, SerializedPredicate<?> lambda) {
//        String sql = DSL.select(selectFields).from(fromTable).where(parseWhere(lambda)).getSQL(ParamType.INLINED);
//        // String sql = DSL.select(DSL.field("Age"), DSL.field("Name")).from(table).where(where(lambda)).getSQL(ParamType.INLINED);
//        //System.out.println(sql);
//        return sql;
//    }
}
