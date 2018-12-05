package com.openle.our.lambda;

/**
 *
 * @author 168
 */
public class GetSet {

    public static void main(String[] args) {

        String mName = MethodParser.getMethodRefName(GetSet::getName);
        System.out.println(mName);

//        String mName = new MethodParser().getMethodName(GetSet.class, GetSet::getName);
//        System.out.println(mName);
    }

    public String getName() {
        return "example name";
    }
}
