package com.openle.source.expression;

import static com.openle.source.expression.sql.delete;
import static com.openle.source.expression.sql.insert;
import static com.openle.source.expression.sql.kv;
import static com.openle.source.expression.sql.select;
import static com.openle.source.expression.sql.update;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInfo;

public class LambdaParserTest {

    @BeforeAll
    public static void init() {
        System.out.println("Start testing");
    }

    @Test
    public void testMain() {

        select().from(EntityDemo.class).assertMe(t -> assertEquals(t, "select * from EntityDemo"));

        select(EntityDemo::getName, EntityDemo::getAge, EntityDemo::getFullName)
                .from(EntityDemo.class).where((EntityDemo t) -> t.getAge() > 0)
                .assertMe(t -> assertEquals(t, "select Name,Age,FullName from EntityDemo where Age > 0"));

        delete().from(EntityDemo.class).assertMe(t -> assertEquals(t, "delete from EntityDemo"));

        delete().from(EntityDemo.class).where((EntityDemo t) -> t.getName().equals("abc"))
                .assertMe(t -> assertEquals(t, "delete from EntityDemo where Name = 'abc'"));

        insert(EntityDemo.class).values("abc").assertMe(t -> assertEquals(t, "insert EntityDemo values ('abc')"));

        insert(EntityDemo.class, EntityDemo::getName, EntityDemo::getAge, EntityDemo::getFullName).values("abc", 123, null)
                .assertMe(t -> assertEquals(t, "insert EntityDemo (Name,Age,FullName) values ('abc',123,null)"));

        update(EntityDemo.class).set(kv(EntityDemo::getName, "abc"))
                .assertMe(t -> assertEquals(t, "update EntityDemo set Name = 'abc'"));

        update(EntityDemo.class).set(kv(EntityDemo::getAge, 123), kv(EntityDemo::getFullName, "abcd"))
                .where((EntityDemo t) -> t.getName().equals("abc"))
                .assertMe(t -> assertEquals(t, "update EntityDemo set Age = 123 , FullName = 'abcd' where Name = 'abc'"));

    }

    @Disabled
    @Test
    public void testAssert() {
        delete().from(EntityDemo.class).assertMe(t -> assertEquals(t, "xyz"));
        delete().from(EntityDemo.class).assertEquals(Assertions::fail, "abc");
    }

    @Disabled
    @Test
    public void testOther() {

        String sqlString = LambdaParser.toSQL("SELECT * FROM myTable",
                (EntityDemo t) -> t.getAge() < (t.getAge() + 1) && t.getFullName().equals("myName") && true);
        System.out.println(sqlString);

        //for Camel To Underline
        LambdaParser.isCamelToUnderline = true;
        sqlString = LambdaParser.toSQL("SELECT * FROM myTable",
                (EntityDemo t) -> t.getAge() < (t.getAge() + 1) && t.getFullName().equals("myName") && true);
        System.out.println();
        System.out.println("Camel To Underline:\r\n" + sqlString);
    }

    @Disabled
    @Test
    public void testLambdaParser() {
        String sql = LambdaParser.toSQL("SELECT * FROM myTable", (EntityDemo t) -> t.getAge() < (t.getAge() + 1) && t.getFullName().equals("myName") && true);
        System.out.println(sql);

        //for Camel To Underline
        LambdaParser.isCamelToUnderline = true;
        sql = LambdaParser.toSQL("SELECT * FROM myTable", (EntityDemo t) -> t.getFullName().equals("xyz"));
        LambdaParser.isCamelToUnderline = false;
        System.out.println();

        System.out.println("Camel To Underline:\r\n" + sql);

    }

    @Disabled
    @Test
    public void testStream() {
        List<EntityDemo> list = new ArrayList<>();

        EntityDemo edemo = new EntityDemo();
        edemo.setFullName("abc");
        edemo.setAge(13);
        list.add(edemo);

        EntityDemo b = new EntityDemo();
        b.setFullName("xyz");
        b.setAge(12);
        list.add(b);

        EntityDemo c = new EntityDemo();
        c.setFullName("myName");
        c.setAge(40);
        list.add(c);

        long i = list.stream().filter(t -> t.getAge() == 40).count();
        System.out.println(i);

    }

    @Test
    public void testCamelToUnderline() {
        assertEquals(Utils.camelToUnderline("FullName"), "full_name");
    }

    // 显示测试相关信息
    @Test
    @DisplayName("test info")
    public void testInfo(final TestInfo testInfo) {
        System.out.println(testInfo.getDisplayName());
    }
}
