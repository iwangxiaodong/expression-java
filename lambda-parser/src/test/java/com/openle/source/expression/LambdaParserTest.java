package com.openle.source.expression;

import com.openle.source.expression.example.EntityDemo;
import static com.openle.source.expression.sql.delete;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import static com.openle.source.expression.sql.select;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;

public class LambdaParserTest {

    @BeforeAll
    public static void init() {
        System.out.println("Start testing");
    }

    @Test
    public void testMain() {
        Execute e = select(EntityDemo::getName, EntityDemo::getAge)
                .from(EntityDemo.class)
                .where((EntityDemo t) -> t.getAge() > 0);
        System.out.println(e);
        assertEquals(e.toString(), "select Name,Age from EntityDemo where Age > 0");

        Execute e1 = delete().from(EntityDemo.class).where((EntityDemo t) -> t.getName().equals("abc"));
        System.out.println(e1);
        assertEquals(e1.toString(), "delete from EntityDemo where Name = 'abc'");
    }

    @Test
    public void testLambdaParser() {
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

        String sql = LambdaParser.toSQL("SELECT * FROM myTable", (EntityDemo t) -> t.getAge() < (t.getAge() + 1) && t.getFullName().equals("myName") && true);

        System.out.println(sql);

        //for Camel To Underline
        LambdaParser.isCamelToUnderline = true;

        sql = LambdaParser.toSQL("SELECT * FROM myTable", (EntityDemo t) -> t.getFullName().equals("xyz"));

        System.out.println();

        System.out.println("Camel To Underline:\r\n" + sql);

        long i = list.stream().filter(t -> t.getAge() == 40).count();
        System.out.println(i);
    }

    @Test
    public void testCamelToUnderline() {
        assertEquals(Utils.camelToUnderline("FullName"), "full_name");
    }
}
