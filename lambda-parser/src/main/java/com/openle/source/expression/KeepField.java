package com.openle.source.expression;

import java.util.function.Function;

/**
 *
 * @author xiaodong
 */
// 由于getFunctionByName通过抛异常方式性能可能会低，so 保留该方案
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
