package com.openle.module.lambda;

import org.jinq.jooq.transform.*;
import ch.epfl.labos.iu.orm.queryll2.path.TransformationClassAnalyzer;
import ch.epfl.labos.iu.orm.queryll2.symbolic.MethodCallValue;
import ch.epfl.labos.iu.orm.queryll2.symbolic.MethodSignature;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import static net.bytebuddy.matcher.ElementMatchers.named;
import org.jinq.jooq.querygen.ColumnExpressions;
import org.jooq.Schema;
import org.jooq.impl.DSL;
import org.jooq.impl.SchemaImpl;

// 将WhereTransform.java中new SymbExToColumns修改为new MyInterceptor().getSymbExToColumns即可
// 考虑通过--patch-module替换WhereTransform.java类
public class MyInterceptor {

    public static boolean isCamelToUnderline = false;

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

    public MetamodelUtil getMetamodelUtil() {
        MetamodelUtil metamodel = null;
        try {

            Constructor<?> c = new ByteBuddy()
                    .subclass(MetamodelUtil.class)
                    .method(named("isFieldGetterMethod")).intercept(FixedValue.value(false))
                    .method(named("isSafeMethod")).intercept(FixedValue.value(true))
                    .make()
                    .load(getClass().getClassLoader())
                    .getLoaded()
                    .getDeclaredConstructor(Schema.class);
            c.setAccessible(true);

            metamodel = (MetamodelUtil) c.newInstance(new SchemaImpl(""));
            c.setAccessible(false);
        } catch (ReflectiveOperationException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        return metamodel;
    }

    public SymbExToColumns getSymbExToColumns(MetamodelUtil metamodel, SymbExArgumentHandler seah) {
        SymbExToColumns translator = null;

        Constructor<?> c;
        try {
            c = new ByteBuddy()
                    .subclass(SymbExToColumns.class)
                    .method(named("virtualMethodCallValue")).intercept(MethodDelegation.to(MyInterceptor.Target.class))
                    .make()
                    //.load(getClass().getClassLoader())    // jdk9 must use UsingLookup
                    .load(getClass().getClassLoader(), ClassLoadingStrategy.UsingLookup.of(MethodHandles
                            .privateLookupIn(SymbExToColumns.class, MethodHandles.lookup())))
                    .getLoaded()
                    .getDeclaredConstructor(MetamodelUtil.class, SymbExArgumentHandler.class);

            c.setAccessible(true);

            translator = (SymbExToColumns) c.newInstance(metamodel, seah);
            c.setAccessible(false);

        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException ex) {
            Logger.getLogger(MyInterceptor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException e) {
            System.out.println("此处接收被调用方法内部未被捕获的异常");
            //Throwable t = e.getTargetException();// 获取目标异常  
            //t.printStackTrace();
        }
        return translator;
    }

    public static class Target {

        @RuntimeType
        public static Object intercept(@SuperCall Callable<?> callable, @AllArguments Object... args) throws Exception {

            MethodCallValue.VirtualMethodCallValue val = (MethodCallValue.VirtualMethodCallValue) args[0];

            MethodSignature sig = val.getSignature();
            //System.out.println("sig - " + sig.toString() + " | " + "val - " + val.base.toString());

            String rName = null;
            if (sig.name.startsWith("get")) {
                rName = sig.name;
            }

            //Integer和Double类型允许多级链式调用
            if (!sig.equals(TransformationClassAnalyzer.integerIntValue)
                    && !sig.equals(TransformationClassAnalyzer.doubleDoubleValue)) {
                if ("java/lang/Object:toString()Ljava/lang/String;".equals(sig.toString()) || "java/lang/Object:hashCode()I".equals(sig.toString())) {
                    System.out.println(sig.name + "方法不适合作为字段名！");
                    rName = sig.name;
                } else if (val.base.toString().endsWith("()")) {
                    String[] rNames = val.base.toString().split("\\.");
                    System.out.println("length - " + rNames.length);
                    if (rNames.length > 1) {
                        rName = rNames[1].replace("()", "");
                        System.out.println("实体方法名链式调用无效，只取第一级方法名！" + rName);
                    }
                } else {
                    //其他非数值型字段名
                    rName = sig.name;
                }
            }

            if (rName != null) {
                rName = rName.replace("get", "");
                if (isCamelToUnderline) {
                    rName = camelToUnderline(rName);
                }
                ColumnExpressions<?> newColumns = new ColumnExpressions<>(null);
                newColumns.columns.add(DSL.field(rName));
                return newColumns;
            }

            try {
                return callable.call();
            } catch (Exception ex) {
                Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }
    }

}
