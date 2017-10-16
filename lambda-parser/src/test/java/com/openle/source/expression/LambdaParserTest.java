package com.openle.source.expression;

import static com.openle.source.expression.sql.*;
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

        //  import static com.openle.source.expression.sql.*;
        //
        select().from(User.class)
                .assertMe(t -> assertEquals(t, "select * from User"));

        select(User::getName, User::getAge, User::getFullName)
                .from(User.class).where((User t) -> t.getAge() > 0)
                .assertMe(t -> assertEquals(t, "select Name,Age,FullName from User where Age > 0"));

        delete().from(User.class)
                .assertMe(t -> assertEquals(t, "delete from User"));

        delete().from(User.class).where((User t) -> t.getName().equals("abc"))
                .assertMe(t -> assertEquals(t, "delete from User where Name = 'abc'"));

        update(User.class).set(kv(User::getName, "abc"))
                .assertMe(t -> assertEquals(t, "update User set Name = 'abc'"));

        update(User.class).set(kv(User::getAge, 22), kv(User::getName, "a"))
                .where((User t) -> t.getName().equals("abc"))
                .assertMe(t -> assertEquals(t, "update User set Age = 22 , Name = 'a' where Name = 'abc'"));

        insert(User.class).values("abc")
                .assertMe(t -> assertEquals(t, "insert User values ('abc')"));

        insert(User.class, User::getName, User::getAge, User::getFullName).values("abc", 22, null)
                .assertMe(t -> assertEquals(t, "insert User (Name,Age,FullName) values ('abc',22,null)"));

    }

    @Disabled
    @Test
    public void testAssert() {
        delete().from(User.class).assertMe(t -> assertEquals(t, "xyz"));
        delete().from(User.class).assertEquals(Assertions::fail, "abc");
    }

    @Disabled
    @Test
    public void testOther() {

        String sqlString = LambdaParser.toSQL("SELECT * FROM myTable",
                (User t) -> t.getAge() < (t.getAge() + 1) && t.getFullName().equals("myName") && true);
        System.out.println(sqlString);

        //for Camel To Underline
        LambdaParser.isCamelToUnderline = true;
        sqlString = LambdaParser.toSQL("SELECT * FROM myTable",
                (User t) -> t.getAge() < (t.getAge() + 1) && t.getFullName().equals("myName") && true);
        System.out.println();
        System.out.println("Camel To Underline:\r\n" + sqlString);
    }

    @Disabled
    @Test
    public void testLambdaParser() {
        String sql = LambdaParser.toSQL("SELECT * FROM myTable", (User t) -> t.getAge() < (t.getAge() + 1) && t.getFullName().equals("myName") && true);
        System.out.println(sql);

        //for Camel To Underline
        LambdaParser.isCamelToUnderline = true;
        sql = LambdaParser.toSQL("SELECT * FROM myTable", (User t) -> t.getFullName().equals("xyz"));
        LambdaParser.isCamelToUnderline = false;
        System.out.println();

        System.out.println("Camel To Underline:\r\n" + sql);

    }

    @Disabled
    @Test
    public void testStream() {
        List<User> list = new ArrayList<>();

        User edemo = new User();
        edemo.setFullName("abc");
        edemo.setAge(13);
        list.add(edemo);

        User b = new User();
        b.setFullName("xyz");
        b.setAge(12);
        list.add(b);

        User c = new User();
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
