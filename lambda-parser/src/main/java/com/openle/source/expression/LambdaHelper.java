package com.openle.source.expression;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 *
 * If the lambda is not serializable, the jdk.internal.lambda.dumpProxyClasses
 * system property must be set and point to an existing writable directory to
 * give the parser access to the lambda byte code.
 *
 * @author xiaodong
 */
public class LambdaHelper {

    // 若开启javaagent可直接用库 - https://github.com/ruediste/lambda-inspector
    public static Function getFunctionByName(String methodName) {
        methodName = replaceSymbol(methodName);
        //System.setProperty("jdk.internal.lambda.dumpProxyClasses", "D:\\temp");

//        // 方法引用的getter方法不存在时，临时创建一个类来模拟。
//        Class<?> c = new ByteBuddy()
//                .subclass(Object.class)
//                .defineMethod(methodName, String.class, Visibility.PUBLIC)
//                .intercept(FixedValue.value(methodName))
//                .make()
//                .load(LambdaHelper.class.getClassLoader())
//                .getLoaded();
        try {
            MethodHandles.Lookup caller = MethodHandles.lookup();
            MethodType getter = MethodType.methodType(String.class);

            // 为避免findVirtual检查方法是否存在，so 自行构建。
            MethodHandle target = getMethodHandle(Object.class, methodName, getter);
//            MethodHandle target1 = caller.findVirtual(LambdaHelper.class, methodName, getter);

            CallSite site = LambdaMetafactory.metafactory(caller,
                    "apply",
                    MethodType.methodType(Function.class),
                    target.type().generic(), target, target.type());
//
//            //         LambdaMetafactory.metafactory反射版本：
//            Class cClass = Class.forName("java.lang.invoke.InnerClassLambdaMetafactory");
//            Constructor con = cClass.getConstructor(MethodHandles.Lookup.class, MethodType.class, String.class, MethodType.class, MethodHandle.class, MethodType.class, boolean.class, Class[].class, MethodType[].class);
//            con.setAccessible(true);
//            Object inner = con.newInstance(caller, MethodType.methodType(Function.class), "apply", target.type().generic(), target, target.type(), false, new Class<?>[0], new MethodType[0]);
//            con.setAccessible(false);
//            Method m = cClass.getDeclaredMethod("buildCallSite");
//            m.setAccessible(true);
//            CallSite site = (CallSite) m.invoke(inner);
//            m.setAccessible(false);
//
            MethodHandle factory = site.getTarget();
            Function f = (Function) factory.invoke();

            //System.out.println(new Utils().getSelectName(c, f));
            //System.out.println(f.apply(c.getConstructor().newInstance()));
            return f;
        } catch (Throwable ex) {
            Logger.getGlobal().severe(ex.toString());
        }

        return null;
    }

    private static MethodHandle getMethodHandle(Class<?> c, String methodName, MethodType getter) throws ReflectiveOperationException {
        final byte REF_invokeVirtual = 5, REF_invokeInterface = 9;
        byte refKind = (c.isInterface() ? REF_invokeInterface : REF_invokeVirtual);
        Class memberNameClass = Class.forName("java.lang.invoke.MemberName");
        Constructor con = memberNameClass.getConstructor(Class.class, String.class, MethodType.class, byte.class);
        con.setAccessible(true);
        Object memberName = con.newInstance(c, methodName, getter, refKind);
        con.setAccessible(false);
        //
        Field field = memberNameClass.getDeclaredField("resolution");
        field.setAccessible(true);
        field.set(memberName, null);
        field.setAccessible(false);
        //
        Method m = memberNameClass.getDeclaredMethod("flagsMods", int.class, int.class, byte.class);
        m.setAccessible(true);
        int flags = (int) m.invoke(memberName, 0x00010000, con.getModifiers(), REF_invokeVirtual);
        //System.out.println(flags);
        m.setAccessible(false);
        //
        field = memberNameClass.getDeclaredField("flags");
        field.setAccessible(true);
        field.set(memberName, flags);
        field.setAccessible(false);
        /*
        后续考虑以下方式是否直接可用：
        LambdaForm lform = preparedLambdaForm(member);
        return new DirectMethodHandle(mtype, lform, member);
         */
        Class directMethodHandleClass = Class.forName("java.lang.invoke.DirectMethodHandle");
        m = directMethodHandleClass.getDeclaredMethod("make", byte.class, Class.class, memberNameClass);
        m.setAccessible(true);
        MethodHandle target = (MethodHandle) m.invoke(null, refKind, c, memberName);
        m.setAccessible(false);
        return target;
    }

    // https://www.oschina.net/translate/hacking-lambda-expressions-in-java?lang=chs&page=2
    public static void createSupplier(String[] args) throws Throwable {
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

    // 后续通过Base32或Base36处理
    public static String replaceSymbol(String s) {
        return s.replace("(", "左括号").replace(")", "右括号").replace("*", "星号");
    }

    public static String restoreSymbol(String s) {
        return s.replace("左括号", "(").replace("右括号", ")").replace("星号", "*");
    }

}
