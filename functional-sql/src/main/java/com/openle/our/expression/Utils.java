package com.openle.our.expression;

import com.openle.our.core.DataCommon;
import com.openle.our.lambda.LambdaFactory;
import com.openle.our.lambda.MethodParser;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.enterprise.util.TypeLiteral;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;

public class Utils implements Serializable {

    public static void main(String[] args) throws Throwable {
//        sql.initialize();
//        Integer ii = 100;
//        SerializedPredicate<?> lambda = (Utils u) -> u.print() == "bbb" && u.id() == 345;
//        System.out.println(LambdaParser.parseWhere(lambda));

//        lambda = (Utils u) -> u.getAge() > 18 && u.getClass().getName().toString().equals("abc");
//        System.out.println(LambdaParser.parseWhere(lambda));
        //System.out.println(Lambda2Sql.toSql(lambda));
        //parseLambda(Utils::getVersion);
        //parseLambda((Utils u) -> u.getAge());
        //System.out.println(LambdaFactory.getMethodReferencesName(sql.f("abcdefg")));
        //insert(KeepOriginal.class).values("abc", sql.f.now());
        //System.out.println(select(NewClass::getName, sql.f.max("id"), sql.f.count("*"), sql.f.now(), sql.f.len("name")).from(NewClass.class).sql());
        //System.out.println(sql.f.now());
        //select(sql.f.now()).from(User.class);
//       
//String r= sql.update("abcdef").set(sql.eq(Utils::getVersion, "aaaa")).where((Utils u) -> true).sql();
        String r = sql.insert(Utils.class, sql.s("abc"), Utils::getVersion).values(1, 23).sql();
        System.out.println("r = " + r);
    }

    public String print() {
        return "hello world123";
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

    public static Optional<List<String>> functionsToList(Function[] fs, Class c) {
        List<String> list = null;
        if (fs != null && fs.length > 0) {
            list = new ArrayList<>();
            for (Function f : fs) {
                String name = new Utils().getSelectName(c != null ? c : Object.class, f);
                if (name.startsWith("get")) {
                    name = name.replaceFirst("get", "");
                }
                list.add(name);
            }
        }
        return Optional.ofNullable(list);
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

    protected static String getValueString(Class c, Object value) {
        if (Objects.isNull(value)) {
            return "null";
        }

        String s = String.valueOf(value);
        if (value.getClass().equals(String.class)) {
            //System.out.println("String value - " + value.toString());
            s = "'" + DataCommon.escapeSql(value.toString()) + "'";
        }
        if (value instanceof Function) {
            //Logger.getGlobal().info("function===================");
            System.out.println("Function value");
            String v = new Utils().getSelectName(c != null ? c : Object.class, (Function) value);
            s = DataCommon.escapeSql(v);
        }
        return s;
    }

    protected String getSelectName(Class c, final Function getter) {
        Objects.requireNonNull(c);

        if (getter == null) {
            throw new NullPointerException("getter is null!!!");
        }

        Class<Function<Object, ?>> raw = new TypeLiteral<Function<Object, ?>>() {
        }.getRawType();

        //  字符串方法名，若getter不支持序列化则可通过NoSuchMethodError来获取。
        //  后续考虑自建getter时基类使用可序列化的SecretKeySpec
        if (getter instanceof Serializable) {
            return LambdaFactory.getMethodReferencesName(raw.cast(getter));
        }
//        else if (!c.getClass().equals(Object.class)) {
//            System.out.println("MethodParser");
//            return new MethodParser().getMethodName(c, getter);
//        }

        Class<Class<Object>> rawClass = new TypeLiteral<Class<Object>>() {
        }.getRawType();

        final Method[] method = new Method[1];
        Object t = Mockito.mock(rawClass.cast(c), Mockito.withSettings().invocationListeners(methodInvocationReport -> {
            method[0] = ((InvocationOnMock) methodInvocationReport.getInvocation()).getMethod();
        }));
        // T t = (T) Mockito.mock(c, ...);

        try {
            raw.cast(getter).apply(t);
            //System.err.println("apply(t)");
            return method[0].getName();
        } catch (ClassCastException ex) {
            // 字符串表名则通过异常ClassCastException来获取。
            String msg = ex.getMessage();
            //System.err.println(msg);

            if (msg.contains("cannot be cast to class")) {
                msg = msg.split(" ")[7];
                //msg = msg.substring(msg.lastIndexOf("cast to") + 8);
            }

            System.out.println("x::getter存在类型信息，但未指定x.class - " + msg);

            Class clazz = null;
            try {
                clazz = Class.forName(msg);
            } catch (ClassNotFoundException ex1) {
                throw new RuntimeException(ex1);
            }

            //System.out.println("Again getSelectName - " + clazz);
            return getSelectName(clazz, getter);
        }
    }
}
