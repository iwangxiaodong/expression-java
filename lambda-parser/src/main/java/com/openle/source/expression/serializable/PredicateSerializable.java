package com.openle.source.expression.serializable;

import java.io.Serializable;
import java.util.function.Predicate;

public interface PredicateSerializable<T> extends Predicate<T>, Serializable {
}
