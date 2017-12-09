package com.openle.source.expression;

import com.openle.module.core.lambda.LambdaFactory;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.enterprise.util.TypeLiteral;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;

/**
 * 公用类
 */
public class Utils implements Serializable {

    public static void main(String[] args) throws Throwable {
//        sql.initialize();
//        Integer ii = 100;
//        PredicateSerializable<?> lambda = (Utils u) -> u.print() == "bbb" && u.id() == 345;
//        System.out.println(LambdaParser.parseWhere(lambda));

//        lambda = (Utils u) -> u.getAge() > 18 && u.getClass().getName().toString().equals("abc");
//        System.out.println(LambdaParser.parseWhere(lambda));
        //System.out.println(Lambda2Sql.toSql(lambda));
        //parseLambda(Utils::getVersion);
        //parseLambda((Utils u) -> u.getAge());
        System.out.println(LambdaFactory.getMethodReferencesName(sql.f("abcdefg")));
    }

    public String print() {
        return "hello world";
    }

    public int id() {
        return 123;
    }

    public Integer getAge() {
        return 123;
    }

    public String getVersion() {
        return "hello world";
    }

    public static String camelToUnderline(String param) {
        Pattern p = Pattern.compile("[A-Z]");
        if (param == null || param.equals("")) {
            return "";
        }
        StringBuilder builder = new StringBuilder(param);
        Matcher mc = p.matcher(param);
        int i = 0;
        while (mc.find()) {
            builder.replace(mc.start() + i, mc.end() + i, "_" + mc.group().toLowerCase());
            i++;
        }

        if ('_' == builder.charAt(0)) {
            builder.deleteCharAt(0);
        }
        return builder.toString();
    }

    public static String getTableName(Class c) {

        String tableName = c.getSimpleName();

        for (Annotation a : c.getAnnotations()) {
            //System.out.println(a.annotationType().getName());
            if (a.annotationType().getName().equals("javax.persistence.Table")) {
                try {
                    Method m = a.annotationType().getMethod("name", new Class[]{});
                    Object obj = m.invoke(a, new Object[]{});
                    System.out.println("getTableName Entity " + c.getName() + "|TableName - " + obj);
                    //System.out.println("Table Name = " + obj);
                    tableName = obj.toString();
                } catch (java.lang.ReflectiveOperationException ex) {
                    Logger.getGlobal().severe(ex.toString());
                }
            }
        }

        return tableName;
    }

    protected String getSelectName(Class c, final Function getter) {
        Objects.requireNonNull(c);

        Class<Function<Object, ?>> raw = new TypeLiteral<Function<Object, ?>>() {
        }.getRawType();

        // 字符串方法名，若getter不支持序列化则可通过NoSuchMethodError来获取。
        if (getter instanceof Serializable) {
            return LambdaFactory.getMethodReferencesName(raw.cast(getter));
        }

        Class<Class<Object>> rawClass = new TypeLiteral<Class<Object>>() {
        }.getRawType();

        final Method[] method = new Method[1];
        Object t = Mockito.mock(rawClass.cast(c), Mockito.withSettings().invocationListeners(methodInvocationReport -> {
            method[0] = ((InvocationOnMock) methodInvocationReport.getInvocation()).getMethod();
        }));
        // T t = (T) Mockito.mock(c, ...);
        try {
            raw.cast(getter).apply(t);
            return method[0].getName();
        } catch (ClassCastException ex) {
            // 字符串表名则通过异常ClassCastException来获取。
            String msg = ex.getMessage();
            msg = msg.substring(msg.lastIndexOf("cast to") + 8);
            Class clazz = null;
            try {
                clazz = Class.forName(msg);
            } catch (ClassNotFoundException ex1) {
                throw new RuntimeException(ex1);
            }
            System.out.println("msg - " + clazz);
            return getSelectName(clazz, getter);
        }
    }
}
