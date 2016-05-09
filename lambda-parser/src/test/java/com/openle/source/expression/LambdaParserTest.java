package com.openle.source.expression;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class LambdaParserTest {

    @Test
    public void testLambdaParser() {
        List<EntityDemo> list = new ArrayList<EntityDemo>();

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

        PredicateSerializable<EntityDemo> whereLambda = null;

        whereLambda = t -> t.getAge() < (t.getAge() + 1) && t.getFullName().equals("myName") && true;

        String sql = LambdaParser.toSQL("SELECT * FROM myTable",whereLambda);

        System.out.println(sql);

        //for Camel To Underline
        LambdaParser.isCamelToUnderline=true;

        sql = LambdaParser.toSQL("SELECT * FROM myTable",whereLambda);

        System.out.println();

        System.out.println("Camel To Underline:\r\n"+sql);



//        whereLambdaExpression = t -> t.getName().equals(name);
//        long i = list.stream().filter(whereLambdaExpression).count();
//        System.out.println(i);
//        toSQL("select name,age from tableName", whereLambdaExpression);

//        String sql = new From<EntityDemo>() {
//        }.where(whereLambdaExpression).select("name,age");
//        System.out.print(sql);

//        Supplier<EntityDemo> obj=EntityDemo::new;
//        Object o=obj.get();
//        System.out.print(o);


    }
}
