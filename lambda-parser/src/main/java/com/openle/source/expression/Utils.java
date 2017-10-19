package com.openle.source.expression;

import java.lang.annotation.Annotation;
import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaConversionException;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;

/**
 * 公用类
 */
public class Utils {

    public static void main(String[] args) throws Throwable {
        xxx();

//        MethodHandles.Lookup caller = MethodHandles.lookup();
//        MethodType methodType = MethodType.methodType(Object.class);
//        MethodType actualMethodType = MethodType.methodType(String.class);
//        MethodType invokedType = MethodType.methodType(Supplier.class);
//        CallSite site = LambdaMetafactory.metafactory(caller,
//                "get",
//                invokedType,
//                methodType,
//                caller.findStatic(Utils.class, "print", actualMethodType),
//                methodType);
//        MethodHandle factory = site.getTarget();
//        Supplier<String> r = (Supplier<String>) factory.invoke();
//        System.out.println(r.get());
    }

    private static String print() {
        return "hello world";
    }

    public String getVersion() {
        return "hello world";
    }

    private static void xxx() throws NoSuchMethodException, IllegalAccessException, LambdaConversionException, Throwable {

        MethodHandles.Lookup caller = MethodHandles.lookup();
        MethodType getter = MethodType.methodType(String.class);
        MethodHandle target = caller.findVirtual(Utils.class, "getVersion", getter);
        MethodType func = target.type();
        CallSite site = LambdaMetafactory.metafactory(caller,
                "apply",
                MethodType.methodType(Function.class),
                func.generic(), target, func);

        MethodHandle factory = site.getTarget();
        Function r = (Function) factory.invoke();
        System.out.println(r.apply(new Utils()));
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
        //System.out.println("Entity " + c.getName());
        String tableName = c.getSimpleName();
        for (Annotation a : c.getAnnotations()) {
            //System.out.println(a.annotationType().getName());
            if (a.annotationType().getName().equals("javax.persistence.Table")) {
                try {
                    Method m = a.annotationType().getMethod("name", new Class[]{});
                    Object obj = m.invoke(a, new Object[]{});
                    System.out.println("Table Name = " + obj);
                    tableName = obj.toString();
                } catch (java.lang.ReflectiveOperationException ex) {
                    Logger.getLogger(From.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        return tableName;
    }

    @SuppressWarnings("unchecked")
    protected <T> String getSelectName(Class c, final Function<T, ?> getter) {
        final Method[] method = new Method[1];
        //System.out.println(getter);
        getter.apply((T) Mockito.mock(c, Mockito.withSettings().invocationListeners(methodInvocationReport -> {
            method[0] = ((InvocationOnMock) methodInvocationReport.getInvocation()).getMethod();
        })));
        String name = method[0].getName();
        //System.out.println(" - " + name);
        return name;
    }
}
