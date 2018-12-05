package com.openle.our.expression.test;

import com.openle.our.expression.sql;
import static com.openle.our.expression.sql.*;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;

public class TempTest {

    @BeforeAll
    public static void init() {
        System.out.println("Start testing");
        sql.initialize();
    }
    String s = "select * from User";

    @Test
    public void testSelect() {

        select().from(User.class)
                //
                .assertEquals(Assertions::fail, s);

        s = "select cast('2010-10-10' as Date)";
        select(f.cast("'2010-10-10'", "Date"))
                //
                .assertEquals(Assertions::fail, s);

        s = "update MyTable set Name = 'abc'";
        update("MyTable").set(eq(User::getName, "abc"))
                //.where((User t) -> t.getAge() != 18)
                //
                .assertEquals(Assertions::fail, s);
    }
}
