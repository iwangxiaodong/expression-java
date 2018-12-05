package com.openle.our.lambda;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.jodah.typetools.TypeResolver;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;

/**
 *
 * @author 168
 */
public class MethodParser {

    //  无需传入方法引用的Class类型; 不要求实现Serializable
    //  https://github.com/jhalterman/typetools
    public static <T> String getMethodRefName(final Function<T, ?> getter) {
        String name = null;
        try {
            Method m = TypeResolver.class.getDeclaredMethod("getMemberRef", Class.class);
            m.setAccessible(true);
            MethodHandle mh = MethodHandles.lookup().unreflect(m);
            Member member = (Member) mh.invoke(getter.getClass());
            name = member.getName();
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(MethodParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException | IllegalAccessException ex) {
            Logger.getLogger(MethodParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Throwable ex) {
            Logger.getLogger(MethodParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return name;
    }

    //  需要传入方法引用的Class类型; 不要求实现Serializable
    //  MethodParser.getMethodName(GetSet.class, GetSet::getName);
    public static <T> String getMethodName(final Class<?> clazz, final Function<T, ?> getter) {
        final Method[] method = new Method[1];
        getter.apply((T) Mockito.mock(clazz, Mockito.withSettings().invocationListeners(methodInvocationReport -> {
            method[0] = ((InvocationOnMock) methodInvocationReport.getInvocation()).getMethod();
        })));
        return method[0].getName();
    }
}
