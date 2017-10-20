package com.openle.source.expression;

import java.util.Map;
import java.util.function.Function;
import org.jooq.Log;
import org.jooq.tools.JooqLogger;

/**
 *
 * java模拟sql链式操作类
 *
 * @author xiaodong
 */
public class sql {

    /**
     * 尽量靠前初始化;若简单使用可忽略该方法
     */
    public static void initialize() {
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
