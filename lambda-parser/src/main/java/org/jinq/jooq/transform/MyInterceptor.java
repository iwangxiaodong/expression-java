/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jinq.jooq.transform;

import ch.epfl.labos.iu.orm.queryll2.path.TransformationClassAnalyzer;
import ch.epfl.labos.iu.orm.queryll2.symbolic.MethodCallValue;
import ch.epfl.labos.iu.orm.queryll2.symbolic.MethodSignature;
import com.openle.source.expression.LambdaParser;
import com.openle.source.expression.Utils;
import java.lang.reflect.Constructor;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import static net.bytebuddy.matcher.ElementMatchers.named;
import org.jinq.jooq.querygen.ColumnExpressions;
import org.jooq.Schema;
import org.jooq.impl.DSL;
import org.jooq.impl.SchemaImpl;

/**
 *
 * @author xiaodong
 */
public class MyInterceptor {

    public MetamodelUtil getMetamodelUtil() {
        MetamodelUtil metamodel = null;
        try {

            Constructor<?> c = new ByteBuddy()
                    .subclass(MetamodelUtil.class)
                    .method(named("isFieldGetterMethod")).intercept(MethodDelegation.to(MyInterceptor.Target1.class))
                    .method(named("isSafeMethod")).intercept(MethodDelegation.to(MyInterceptor.Target2.class))
                    .make()
                    .load(getClass().getClassLoader())
                    .getLoaded()
                    .getDeclaredConstructor(Schema.class);
            c.setAccessible(true);

            metamodel = (MetamodelUtil) c.newInstance(new SchemaImpl(""));
            c.setAccessible(false);
        } catch (ReflectiveOperationException ex) {
            Logger.getLogger(MyWhereTransform.class.getName()).log(Level.SEVERE, null, ex);
        }
        return metamodel;
    }

    public SymbExToColumns getSymbExToColumns(MetamodelUtil metamodel, SymbExArgumentHandler seah) {
        SymbExToColumns translator = null;
        try {

            Constructor<?> c = new ByteBuddy()
                    .subclass(SymbExToColumns.class)
                    .method(named("virtualMethodCallValue")).intercept(MethodDelegation.to(MyInterceptor.Target.class))
                    .make()
                    .load(getClass().getClassLoader())
                    .getLoaded()
                    .getDeclaredConstructor(MetamodelUtil.class, SymbExArgumentHandler.class);
            c.setAccessible(true);

            translator = (SymbExToColumns) c.newInstance(metamodel, seah);
            c.setAccessible(false);
        } catch (ReflectiveOperationException ex) {
            Logger.getLogger(MyWhereTransform.class.getName()).log(Level.SEVERE, null, ex);
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
                if (LambdaParser.isCamelToUnderline) {
                    rName = Utils.camelToUnderline(rName);
                }
                ColumnExpressions<?> newColumns = new ColumnExpressions<>(null);
                newColumns.columns.add(DSL.field(rName));
                return newColumns;
            }

            try {
                return callable.call();
            } catch (Exception ex) {
                Logger.getLogger(MyWhereTransform.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }
    }

    public static class Target1 {

        public static boolean isFieldGetterMethod(MethodSignature sig) {
            return false;
        }
    }

    public static class Target2 {

        public static boolean isSafeMethod(MethodSignature sig) {
            return true;
        }
    }

}
