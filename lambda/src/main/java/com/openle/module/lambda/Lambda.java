/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openle.module.lambda;

import com.user00.thunk.SerializedLambda;
import java.util.logging.Logger;

// 优先使用module.base.lambda的原生实现。
public class Lambda {

    // extractLambda(ConsumerSerializable<?> lambda);
    public static SerializedLambda extractLambda(Object lambda) {
        SerializedLambda s;
        try {
            s = SerializedLambda.extractLambda(lambda);
        } catch (Exception e) {
            Logger.getGlobal().severe(e.toString());
            return null;
        }
        return s;
    }
}
