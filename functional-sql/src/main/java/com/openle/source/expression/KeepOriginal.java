/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openle.source.expression;

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
