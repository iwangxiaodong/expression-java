/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openle.source.expression;

/**
 *
 * @author xiaodong
 */
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
