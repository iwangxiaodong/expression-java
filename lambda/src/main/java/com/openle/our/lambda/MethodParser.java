package com.openle.our.lambda;

import java.lang.reflect.Method;
import java.util.function.Function;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;

/**
 *
 * @author 168
 */
public class MethodParser {

    //  有明确Class类型的getter; 不要求实现Serializable
    //  new MethodParser().getMethodName(GetSet.class, GetSet::getName);
    @SuppressWarnings("unchecked")
    public <T> String getMethodName(final Class<?> clazz, final Function<T, ?> getter) {
        final Method[] method = new Method[1];
        getter.apply((T) Mockito.mock(clazz, Mockito.withSettings().invocationListeners(methodInvocationReport -> {
            method[0] = ((InvocationOnMock) methodInvocationReport.getInvocation()).getMethod();
        })));
        return method[0].getName();
    }
}
