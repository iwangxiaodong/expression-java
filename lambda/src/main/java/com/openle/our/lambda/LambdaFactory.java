package com.openle.our.lambda;

import com.openle.our.core.converter.HexConverter;
import com.openle.our.core.lambda.Lambda;
import com.openle.our.core.lambda.SerializedFunction;
import java.io.Serializable;
import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Function;
import java.util.function.Supplier;

//  由于使用了MethodHandle，对android系统版本要求太高！
public class LambdaFactory {

    // https://stackoverflow.com/questions/23861619/how-to-read-lambda-expression-bytecode-using-asm#answers
    public static void main(String[] args) {
        //System.setProperty("jdk.internal.lambda.dumpProxyClasses", "D:\\temp");

        Function f = newSerializedMethodReferences("abc");//
        System.out.println(f);
        System.out.println(f instanceof SerializedFunction);
        SerializedLambda sl = Lambda.getSerializedLambda((Serializable) f).get();
        System.out.println(sl.getImplMethodName());

        String s = getMethodReferencesName(f);
        System.out.println(s);

//        Function f = getFunctionByName("abc");//
//        System.out.println(f);
//        System.out.println(f instanceof SerializedFunction);
//        f.apply(null);
//
//        Function<Lambda, ?> s = Lambda::toString;
//        System.out.println(s instanceof SerializedFunction);
//        Runnable anotherWay = (Serializable & Runnable) () -> System.out.println("I am a serializable lambda too!");
//        SerializedLambda sl = Lambda.getSerializedLambda((Serializable) anotherWay).get();
//        System.out.println(sl.getImplMethodName());
//
//        Function<Lambda, ?> s = Lambda::toString;
//        System.out.println(s);
//        List.of("a").forEach(System.out::println);
//
//        SerializedFunction<Lambda, ?> f = Lambda::toString;
//        SerializedLambda sl1 = Lambda.extractFunction(f).get();
//        System.out.println(sl1.getImplMethodName());
//            SerializedFunction<?, ?> sf = (SerializedFunction<?, ?>) obj;
//            System.out.println(sf);
//            SerializedLambda sl = (SerializedLambda) Lambda.extractFunction(sf).get();
//            System.out.println(sl.getImplMethodName());
    }

    public static Function newMethodReferences(String methodName) {
        return newMethodReferences(methodName, false);
    }

    public static Function newSerializedMethodReferences(String methodName) {
        return newMethodReferences(methodName, true);
    }

    //  若开启javaagent可直接用库 - https://github.com/ruediste/lambda-inspector
    //  可考虑将Object.class更换为可序列化的String和Integer等
    private static Function newMethodReferences(String methodName, boolean isSerializable) {
        methodName = HexConverter.bytesToHexString(methodName.getBytes());// 支持数字开头method。

        //System.setProperty("jdk.internal.lambda.dumpProxyClasses", "D:\\temp");
        try {
            MethodHandles.Lookup caller = MethodHandles.lookup();
            MethodType getter = MethodType.methodType(String.class);

            // 跳过findVirtual方法是否存在检查，Object.class为方法引用依托的类型，支持动态类。
            MethodHandle target = getMethodHandle(Object.class, methodName, getter);
//            MethodHandle target1 = caller.findVirtual(Object.class, methodName, getter);

            MethodType invokedType = MethodType.methodType(Function.class);//MethodType.methodType(SerializedFunction.class);
            CallSite site = isSerializable
                    ? LambdaMetafactory.altMetafactory(caller,
                            "apply",
                            invokedType,
                            target.type().generic(), target, target.type(), 1)
                    : LambdaMetafactory.metafactory(caller,
                            "apply",
                            invokedType,
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
            Object obj = site.getTarget().invoke();
            Function f = (Function) obj;
            //System.out.println(f.apply(c.getConstructor().newInstance()));
            return f;
        } catch (Throwable ex) {
            System.err.println(ex);
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
        m.setAccessible(false);
        //
        field = memberNameClass.getDeclaredField("flags");
        field.setAccessible(true);
        field.set(memberName, flags);
        field.setAccessible(false);

        //  jdk11起改为了4个参数
        Class directMethodHandleClass = Class.forName("java.lang.invoke.DirectMethodHandle");
        MethodHandle target = null;
        //  为兼容jdk9，暂用过时方法major()取代jdk10的feature()
        if (Runtime.version().major() < 11) {
            m = directMethodHandleClass.getDeclaredMethod("make", byte.class, Class.class, memberNameClass);
            m.setAccessible(true);
            target = (MethodHandle) m.invoke(null, refKind, c, memberName);
        } else {
            m = directMethodHandleClass.getDeclaredMethod("make", byte.class, Class.class, memberNameClass, Class.class);
            m.setAccessible(true);
            target = (MethodHandle) m.invoke(null, refKind, c, memberName, null);
        }

        /*
        后续考虑以下方式是否直接可用：
            LambdaForm lform = preparedLambdaForm(member);
            return new DirectMethodHandle(mtype, lform, member);
         */
//        m = directMethodHandleClass.getDeclaredMethod("preparedLambdaForm", memberNameClass);
//        m.setAccessible(true);
//        Object lambdaFormObj = m.invoke(null, memberName);
//
//        Method m1 = memberNameClass.getDeclaredMethod("getMethodOrFieldType");
//        m1.setAccessible(true);
//        MethodType mt = (MethodType) m1.invoke(memberName);
//        m1.setAccessible(false);
//
//        Class lambdaFormClass = Class.forName("java.lang.invoke.LambdaForm");
//        Constructor con1 = directMethodHandleClass.getConstructor(Class.class, MethodType.class, lambdaFormClass, memberNameClass);
//        con1.setAccessible(true);
//        target = (MethodHandle) con1.newInstance(c, mt, lambdaFormObj, memberName);
//        con1.setAccessible(false);
        m.setAccessible(false);
        return target;
    }

    public static <T> String getMethodReferencesName(final Function<T, ?> getter) {
        SerializedLambda sl = Lambda.extractFunction(getter).get();
        String name = new String(HexConverter.hexStringToBytes(sl.getImplMethodName()));
        return name;
    }

    // https://www.oschina.net/translate/hacking-lambda-expressions-in-java?lang=chs&page=2
    private static void createSupplier() throws Throwable {
        MethodHandles.Lookup caller = MethodHandles.lookup();
        MethodType methodType = MethodType.methodType(Object.class);
        MethodType actualMethodType = MethodType.methodType(String.class);
        MethodType invokedType = MethodType.methodType(Supplier.class);
        CallSite site = LambdaMetafactory.metafactory(caller,
                "get",
                invokedType,
                methodType,
                caller.findStatic(LambdaFactory.class, "print", actualMethodType),
                methodType);
        MethodHandle factory = site.getTarget();
        Supplier<String> r = (Supplier<String>) factory.invoke();
        System.out.println(r.get());
    }
}
