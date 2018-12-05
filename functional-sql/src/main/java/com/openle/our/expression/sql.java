package com.openle.our.expression;

import com.openle.our.lambda.LambdaFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Logger;
import javax.enterprise.util.TypeLiteral;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.jooq.Log;
import org.jooq.tools.JooqLogger;

/**
 * java模拟sql链式操作类
 *
 * @author xiaodong
 */
// 库.查(Object::toString).表(Object.class).筛(t -> t.equals(null));//删、改、插
public class sql {

    static String connString = null;
    static DataSource dataSource = null;
    static CONNTYPE connType = null;

    public enum CONNTYPE {
        STRING, DATASOURCE
    }

    public static Connection getConnection() {
        try {
            if (connType.equals(CONNTYPE.STRING)) {
                return DriverManager.getConnection(connString);

            } else if (connType.equals(CONNTYPE.DATASOURCE)) {
                return dataSource.getConnection();
            }
        } catch (SQLException ex) {
            Logger.getGlobal().severe(ex.toString());
        }
        return null;
    }

    /**
     * 尽量靠前初始化;若简单使用可忽略该方法
     */
    public static void initialize() {
        initializeOther();
    }

    // 数据库Driver均已采用SPI技术，不再需要加载Class.forName(driverClass);
    public static void initialize(String connString, boolean isDebug) throws ClassNotFoundException {
        connType = CONNTYPE.STRING;
        sql.connString = connString;
        initializeOther();
    }

    public static void initialize(String jndiName) throws SQLException, NamingException {
        if (!jndiName.startsWith("java:comp/")) {
            throw new IllegalArgumentException("你输入的JNDI名格式有误，应该以java:comp/...开头！");
        }

        Context ctx = new InitialContext();
        DataSource dsJNDI = (DataSource) ctx
                .lookup(jndiName);

        initialize(dsJNDI);
    }

    /**
     * JNDI DataSource
     *
     * @param dataSource `
     */
    public static void initialize(DataSource dataSource) {
        connType = CONNTYPE.DATASOURCE;
        sql.dataSource = dataSource;
        initializeOther();
    }

    private static void initializeOther() {
        // remove jooq logo 
        // or -Dorg.jooq.no-logo=true  
        JooqLogger.globalThreshold(Log.Level.WARN);
    }

    /**
     * @param <T> `
     * @param getter 实体方法引用 User::getName,User::getAge
     * @return SQL链式对象
     */
    @SafeVarargs
    public static <T> FromOfSelect select(final Function<T, ?>... getter) {
        FromOfSelect from = new FromOfSelect(getter);
        return from;
    }

    /**
     * @param <T> `
     * @param c 实体Class User.class
     * @return SQL链式对象
     */
    public static <T> Values insert(Class c) {
        return new Values(c, false);
    }

    /**
     * insert数据已存在时忽略不抛异常
     *
     * @param <T> `
     * @param c 实体Class User.class
     * @return SQL链式对象
     */
    public static <T> Values insertIgnore(Class c) {
        return new Values(c, true);
    }

    /**
     * @param <T> `
     * @param c 实体Class User.class
     * @param getter `
     * @return SQL链式对象
     */
    @SafeVarargs
    public static <T> Values insert(Class c, final Function<T, ?>... getter) {
        return new Values(c, getter, false);
    }

    /**
     * @param <T> `
     * @param c 实体Class User.class
     * @param getter `
     * @return SQL链式对象
     * @since 1.0.7
     */
    @SafeVarargs
    public static <T> Values insertIgnore(Class c, final Function<T, ?>... getter) {
        return new Values(c, getter, true);
    }

    /**
     * @param <T> `
     * @param c 实体Class User.class
     * @return SQL链式对象
     * @since 1.0.7
     */
    public static <T> Set update(Class c) {
        return new Set(c);
    }

    /**
     * @param <T> `
     * @param tableName 表名
     * @return SQL链式对象
     */
    public static <T> Set update(String tableName) {
        return new Set(tableName);
    }

    /**
     * @param <T> `
     * @return SQL链式对象
     */
    public static <T> FromOfDelete delete() {
        return new FromOfDelete();
    }

    //用于UPDATE XXX SET K=V更新项
    /**
     * @param <T> `
     * @param getter `
     * @param obj `
     * @return `
     */
    public static <T> Map<Function, Object> eq(final Function<T, ?> getter, Object obj) {
        //注意of方法构造的对象是只读的！
        return Map.<Function, Object>of(getter, obj);
    }

//    /**
//     * 字符型数值
//     *
//     * @param s `
//     * @return `
//     */
//    public static KeepOriginal v(String s) {
//        return new KeepOriginal(s);
//    }
    /**
     * 字符型字段
     *
     * @param <T> `
     * @param s `
     * @return `
     */
    public static <T> Function<T, ?> s(String s) {
        Class<Function<T, ?>> raw = new TypeLiteral<Function<T, ?>>() {
        }.getRawType();
        Function<T, ?> f = raw.cast(LambdaFactory.newSerializedMethodReferences(s));
        return f;
    }

    public static class f {

        public static <T> Function<T, ?> now() {
            return s("now()");
        }

        public static <T> Function<T, ?> uuid() {
            return s("uuid()");
        }

        public static <T> Function<T, ?> rand() {
            return s("rand()");
        }

        public static <T> Function<T, ?> lastInsertId() {
            return s("last_insert_id()");
        }

        public static <T> Function<T, ?> count() {
            return s("count('*')");
        }

        public static <T> Function<T, ?> count(String s) {
            if (s != null && s.trim().length() > 0) {
                return s("count(" + s.trim() + ")");
            }
            return s("count(*)");
        }

        public static <T> Function<T, ?> len(String s) {
            Objects.requireNonNull(s);
            return s("len(" + s.trim() + ")");
        }

        public static <T> Function<T, ?> avg(String s) {
            Objects.requireNonNull(s);
            return s("avg(" + s.trim() + ")");
        }

        public static <T> Function<T, ?> max(String s) {
            Objects.requireNonNull(s);
            return s("max(" + s.trim() + ")");
        }

        public static <T> Function<T, ?> min(String s) {
            Objects.requireNonNull(s);
            return s("min(" + s.trim() + ")");
        }

        public static <T> Function<T, ?> sum(String s) {
            Objects.requireNonNull(s);
            return s("sum(" + s.trim() + ")");
        }

//        //  无法引入java.sql.JDBCType？？？
//        public static <T> Function<T, ?> cast(String s, SQLType asType) {
//            Objects.requireNonNull(s);
//            throw new java.lang.UnsupportedOperationException("请使用cast(String s, String asType)");
//        }
        public static <T> Function<T, ?> cast(String s, String asType) {
            Objects.requireNonNull(s);

            return s("cast(" + s.trim() + " as " + asType.trim() + ")");
        }

    }

}
