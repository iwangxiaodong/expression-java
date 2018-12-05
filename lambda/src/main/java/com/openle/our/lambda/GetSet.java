package com.openle.our.lambda;

import java.io.IOException;

/**
 *
 * @author 168
 */
public class GetSet {

    public static void main(String[] args) throws IOException {
        String mName = new MethodParser().getMethodName(GetSet.class, GetSet::getName);
        System.out.println(mName);
    }

    public String getName() {
        return "example name";
    }
}
