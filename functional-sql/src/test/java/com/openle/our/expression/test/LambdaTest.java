package com.openle.our.expression.test;

import com.openle.our.expression.Utils;
import com.openle.our.expression.sql;
import static com.openle.our.expression.sql.*;
import static com.openle.our.expression.sql.f.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInfo;

@SuppressWarnings("unchecked") 
public class LambdaTest {

    @BeforeAll
    public static void init() {
        System.out.println("Start testing");
        sql.initialize();
    }

    //  import static com.openle.source.expression.sql.*;
    //  import static com.openle.source.expression.sql.f.*;
    //
    String s = "select * from User";

    @Test
    public void testSelect() {
        select().from(User.class)
                //
                .assertEquals(Assertions::fail, s);

        s = "select Name,Age,FullName from User where Age > 0";
        select(User::getName, User::getAge, User::getFullName)
                .from(User.class).where((User t) -> t.getAge() > 0)
                //
                .assertEquals(Assertions::fail, s);

        s = "select max(id),count(*),now(),len(name) from User";
        select(max("id"), count("*"), now(), len("name"))
                .from(User.class)
                //
                .assertEquals(Assertions::fail, s);
    }

    @Test
    public void testDelete() {
        s = "delete from User";
        delete().from(User.class)
                //
                .assertEquals(Assertions::fail, s);

        s = "delete from User where id = 18";
        delete().from(User.class).where((User t) -> t.id() == 18)
                //
                .assertEquals(Assertions::fail, s);

        s = "delete from MyUser";
        delete().from("MyUser")
                //
                .assertEquals(Assertions::fail, s);

        s = "delete from MyUser where Name = 'abc'";
        delete().from("MyUser").where((User t) -> t.getName().equals("abc"))
                //
                .assertEquals(Assertions::fail, s);
    }

    //@Disabled
    @Test
    public void testUpdate() {
        s = "update User set Name = 'abc'";
        update(User.class).set(eq(User::getName, "abc"))
                //
                .assertEquals(Assertions::fail, s);

        s = "update User set Age = 22 , Name = 'a' where Age >= 18";
        update(User.class).set(eq(User::getAge, 22), eq(User::getName, "a"))
                .where((User t) -> t.getAge() >= 18)
                //
                .assertEquals(Assertions::fail, s);

        s = "update MyTable set Name = 'abc' where Age <> 18";
        update("MyTable").set(eq(User::getName, "abc"))
                .where((User t) -> t.getAge() != 18)
                //
                .assertEquals(Assertions::fail, s);
    }

    //@Disabled
    @Test
    public void testInsert() {
        s = "insert User values ('abc',now())";
        insert(User.class).values("abc", now())
                //
                .assertEquals(Assertions::fail, s);

        s = "insert User (Name,FullName,f) values ('abc',null,1)";
        insert(User.class, User::getName, User::getFullName, s("f"))
                .values("abc", null, 1)
                //
                .assertEquals(Assertions::fail, s);

        s = "insert ignore User values ('abc')";
        insertIgnore(User.class).values("abc")
                //
                .assertEquals(Assertions::fail, s);
    }

    @Disabled
    @Test
    public void testAssert() {
        delete().from(User.class).test(t -> assertEquals(t, "xyz"));
        delete().from(User.class).assertEquals(Assertions::fail, "abc");
    }

    // where条件值未进行SQL注入防护，后续补上。
    @Test
    public void testSqlInjection() {
        s = "insert User values ('''injection')";
        insert(User.class).values("'injection")
                //
                .assertEquals(Assertions::fail, s);
    }

    @Test
    public void testOther() {
        System.out.println("testOther");
        int i = 18; // where条件支持外部变量值
        s = "select Name,Age,FullName from User where (\n"
                + "  userId > 18\n"
                + "  and 100 > Age\n"
                + ")";
        select(User::getName, User::getAge, User::getFullName)
                .from(User.class).where((User t) -> t.userId().id() > i && 100 > t.getAge())
                //
                .assertEquals(Assertions::fail, s);
    }

    //    @Disabled
//    @Test
//    public void testLambdaParser() {
//        String sql = LambdaParser.toSQL("SELECT * FROM myTable",
//                (User t) -> t.getAge() < (t.getAge() + 1) && t.getFullName().equals("myName") && true);
//        System.out.println(sql);
//
//        //for Camel To Underline
//        LambdaParser.isCamelToUnderline = true;
//        sql = LambdaParser.toSQL("SELECT * FROM myTable", (User t) -> t.getFullName().equals("xyz"));
//        LambdaParser.isCamelToUnderline = false;
//        System.out.println();
//
//        System.out.println("Camel To Underline:\r\n" + sql);
//    }
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
