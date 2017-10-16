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
```sql

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

```
