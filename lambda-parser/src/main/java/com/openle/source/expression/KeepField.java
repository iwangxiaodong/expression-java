package com.openle.source.expression;

import java.util.function.Function;

/**
 *
 * @author xiaodong
 */
public class KeepField implements Function {

    private String fieldName;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public KeepField(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public Object apply(Object t) {
        return null;
    }
}
