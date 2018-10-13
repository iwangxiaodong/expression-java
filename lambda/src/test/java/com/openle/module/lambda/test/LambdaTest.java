package com.openle.module.lambda.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInfo;

@SuppressWarnings("unchecked")
public class LambdaTest {

    @BeforeAll
    public static void init() {
        System.out.println("Start testing");
    }

    String s = "select * from User";

    @Test
    public void testSelect() {
        Assertions.assertEquals("abc", "abc");
    }

    // 显示测试相关信息
    @Test
    @DisplayName("test info")
    public void testInfo(final TestInfo testInfo) {
        System.out.println(testInfo.getDisplayName());
    }
}
