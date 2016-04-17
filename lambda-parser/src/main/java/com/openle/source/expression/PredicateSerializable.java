package com.openle.source.expression;

import java.io.Serializable;
import java.util.function.Predicate;

public interface PredicateSerializable<T> extends Predicate<T>, Serializable {
}
