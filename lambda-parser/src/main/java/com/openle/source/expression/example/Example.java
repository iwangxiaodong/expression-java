package com.openle.source.expression.example;

import com.openle.source.expression.LambdaParser;
import static com.openle.source.expression.sql.*;

public class Example {

    public static void main(String[] args) {
        System.out.println("example main()");

        select(EntityDemo::getName, EntityDemo::getAge).from(EntityDemo.class)
                .where((EntityDemo t) -> t.getAge() > 0)
                .execute(System.out::println);

        select().from(EntityDemo.class).where((EntityDemo t) -> t.getAge() == 8 && true)
                .execute(System.out::println);

        delete().from(EntityDemo.class).where((EntityDemo t) -> t.getName().equals("abc"))
                .execute(System.out::println);

        insert(EntityDemo.class, EntityDemo::getName, EntityDemo::getAge).values("a", "b")
                .execute(System.out::println);

        insert(EntityDemo.class).values("abc", 123, null).execute(System.out::println);

        String sql = LambdaParser.toSQL("SELECT * FROM myTable",
                 (EntityDemo t) -> t.getAge() < (t.getAge() + 1) && t.getFullName().equals("myName") && true);
        System.out.println(sql);

        //for Camel To Underline
        LambdaParser.isCamelToUnderline = true;
        sql = LambdaParser.toSQL("SELECT * FROM myTable",
                 (EntityDemo t) -> t.getAge() < (t.getAge() + 1) && t.getFullName().equals("myName") && true);
        System.out.println();
        System.out.println("Camel To Underline:\r\n" + sql);
    }
}
