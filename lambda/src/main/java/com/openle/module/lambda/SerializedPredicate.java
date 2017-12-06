package com.openle.module.lambda;

import java.io.Serializable;
import java.util.function.Predicate;

public interface SerializedPredicate<T> extends Predicate<T>, Serializable {
}
