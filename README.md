# [expression-java](https://github.com/iwangxiaodong/expression-java) [![Build Status](https://travis-ci.org/iwangxiaodong/expression-java.svg?branch=master)](https://travis-ci.org/iwangxiaodong/expression-java)

***lambda-parser*** is a expression parser of lambda to sql.
<br />
Gradle:
<br />
> repositories {
> 
> > jcenter()
> 
> }
> 
> dependencies {
>
> > compile 'com.openle.source.expression:lambda-parser:1.0.3'
>
> }
<br />
<br />
    https://bintray.com/wangxiaodong/maven/lambda-parser
<br />
<br />

**Test**: lambda-parser/src/test/java/com/openle/source/expression/LambdaParserTest.java
```java

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

```
