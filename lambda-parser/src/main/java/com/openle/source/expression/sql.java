package com.openle.source.expression;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.jooq.Log;
import org.jooq.tools.JooqLogger;

/**
 *
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
            Logger.getLogger(sql.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * 尽量靠前初始化;若简单使用可忽略该方法
     */
    public static void initialize() {
        initializeOther();
    }

    public static void initialize(String connString, String driverClassName) throws ClassNotFoundException {
        connType = CONNTYPE.STRING;
        Class.forName(driverClassName);
        sql.connString = connString;
        initializeOther();
    }

    public static void initialize(String jndiName) throws SQLException, NamingException {
        if (!jndiName.startsWith("java:comp/")) {
            throw new IllegalArgumentException("你输入的JNDI名格式有误，应该以java:comp/...开头！");
        }

        Context ctx;
        DataSource dsJNDI = null;

        ctx = new InitialContext();
        dsJNDI = (DataSource) ctx
                .lookup(jndiName);

        initialize(dsJNDI);
    }

    /**
     * JNDI DataSource
     *
     * @param dataSource
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
     *
     * @param <T>
     * @param getter 实体方法引用 User::getName,User::getAge
     * @return SQL链式对象
     */
    @SafeVarargs
    public static <T> From select(final Function<T, ?>... getter) {
        return new From(getter);
    }

    /**
     *
     * @param <T>
     * @param c 实体Class User.class
     * @return SQL链式对象
     */
    public static <T> Values insert(Class c) {
        return new Values(c);
    }

    /**
     *
     * @param <T>
     * @param c 实体Class User.class
     * @param getter
     * @return SQL链式对象
     */
    @SafeVarargs
    public static <T> Values insert(Class c, final Function<T, ?>... getter) {
        return new Values(c, getter);
    }

    /**
     *
     * @param <T>
     * @param c 实体Class User.class
     * @return SQL链式对象
     */
    public static <T> Set update(Class c) {
        return new Set(c);
    }

    /**
     *
     * @param <T>
     * @return SQL链式对象
     */
    public static <T> From delete() {
        return new From(DML.DELETE);
    }

    //用于UPDATE XXX SET K=V更新项
    /**
     *
     * @param <T>
     * @param getter
     * @param obj
     * @return
     */
    public static <T> Map<Function, Object> eq(final Function<T, ?> getter, Object obj) {
        //注意of方法构造的对象是只读的！
        return Map.<Function, Object>of(getter, obj);
    }

    /**
     * 保持字符
     *
     * @param s
     * @return
     */
    public static KeepOriginal k(String s) {
        return new KeepOriginal(s);
    }

    /**
     * 保持字段
     *
     * @param s
     * @return
     */
    public static Function kf(String s) {
        //return new KeepField(s);
        return LambdaHelper.getFunctionByName(s);
    }
}
