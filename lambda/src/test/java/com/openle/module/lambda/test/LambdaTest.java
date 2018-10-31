package com.openle.module.lambda.test;

import com.openle.our.core.lambda.LambdaFactory;
import java.util.function.Function;
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
    
        @Test
    //@Disabled
    public void testLambdaGetter() {
        Function f = LambdaFactory.newSerializedMethodReferences("fieldName");
        String s = LambdaFactory.getMethodReferencesName(f);
        System.out.println(s);
        Assertions.assertEquals(s, "fieldName");
    }
}
