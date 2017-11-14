package com.openle.source.expression.serializable;

import java.io.Serializable;
import java.util.function.Supplier;

public interface SupplierSerializable<T> extends Supplier<T>, Serializable {
}
