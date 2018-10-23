package com.openle.our.expression;

// 已被s("now()")取代，后续若有性能问题可通过该类优化
@Deprecated
public class KeepOriginal {

    private String original;

    protected KeepOriginal(String originalText) {
        this.original = originalText;
    }

    @Override
    public String toString() {
        return original;
    }

}
