package com.openle.source.expression.serializable;

import java.io.Serializable;
import java.util.function.Function;

public interface FunctionSerializable<T, R> extends Function<T, R>, Serializable {
}
