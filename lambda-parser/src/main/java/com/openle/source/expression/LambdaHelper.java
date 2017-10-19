package com.openle.source.expression;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaConversionException;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.implementation.FixedValue;

/**
 *
 * @author xiaodong
 */
public class LambdaHelper {

    // https://www.oschina.net/translate/hacking-lambda-expressions-in-java?lang=chs&page=2
    public static void main(String[] args) throws Throwable {
        MethodHandles.Lookup caller = MethodHandles.lookup();
        MethodType methodType = MethodType.methodType(Object.class);
        MethodType actualMethodType = MethodType.methodType(String.class);
        MethodType invokedType = MethodType.methodType(Supplier.class);
        CallSite site = LambdaMetafactory.metafactory(caller,
                "get",
                invokedType,
                methodType,
                caller.findStatic(LambdaHelper.class, "print", actualMethodType),
                methodType);
        MethodHandle factory = site.getTarget();
        Supplier<String> r = (Supplier<String>) factory.invoke();
        System.out.println(r.get());
    }

    private static String print() {
        return "hello world";
    }

    // 若开启javaagent可直接用库 - https://github.com/ruediste/lambda-inspector
    public static void main1(String[] args) {
        Map<Function, Class> m = getFunctionByName("abc");

    }

    // InnerClassLambdaMetafactory.buildCallSite or 通过库创建类和方法 - http://bytebuddy.net/#/#helloworld
    public static Map<Function, Class> getFunctionByName(String methodName) {
        Function f = null;

        Class<?> c = new ByteBuddy()
                .subclass(Object.class)
                //.annotateType(AnnotationDescription.Builder.ofType(FunctionAnnotation.class).build())                
                .defineMethod(methodName, String.class, Visibility.PUBLIC)
                .intercept(FixedValue.value(methodName))
                .make()
                .load(LambdaHelper.class.getClassLoader())
                .getLoaded();

        try {
            MethodHandles.Lookup caller = MethodHandles.lookup();
            MethodType getter = MethodType.methodType(String.class);
            MethodHandle target = caller.findVirtual(c, methodName, getter);
            MethodType func = target.type();
            CallSite site = LambdaMetafactory.metafactory(caller,
                    "apply",
                    MethodType.methodType(Function.class),
                    func.generic(), target, func);
            MethodHandle factory = site.getTarget();
            f = (Function) factory.invoke();

            //System.out.println(new Utils().getSelectName(c, f));
            //System.out.println(f.apply(c.getConstructor().newInstance()));
            return Map.of(f, c);

        } catch (NoSuchMethodException ex) {
            Logger.getLogger(LambdaHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException | LambdaConversionException ex) {
            Logger.getLogger(LambdaHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Throwable ex) {
            Logger.getLogger(LambdaHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
