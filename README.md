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
> > compile 'com.openle.source.expression:lambda-parser:1.0.5'
>
> }
<br />
<br />
    https://bintray.com/wangxiaodong/maven/lambda-parser
<br />
<br />

**Test**: lambda-parser/src/test/java/com/openle/source/expression/LambdaTest.java
```sql

    /*
    import static com.openle.source.expression.sql.*; 
     */
    String s = "select * from User";
    select().from(User.class)
            //
            .assertEquals(Assertions::fail, s);

    s = "select Name,Age,FullName from User where Age > 0";
    select(User::getName, User::getAge, User::getFullName)
            .from(User.class).where((User t) -> t.getAge() > 0)
            //
            .assertEquals(Assertions::fail, s);

    s = "delete from User";
    delete().from(User.class)
            //
            .assertEquals(Assertions::fail, s);

    s = "delete from User where Name = 'abc'";
    delete().from(User.class).where((User t) -> t.getName().equals("abc"))
            //
            .assertEquals(Assertions::fail, s);

    s = "update User set Name = 'abc'";
    update(User.class).set(eq(User::getName, "abc"))
            //
            .assertEquals(Assertions::fail, s);

    s = "update User set Age = 22 , Name = 'a' where Name = 'abc'";
    update(User.class).set(eq(User::getAge, 22), eq(User::getName, "a"))
            .where((User t) -> t.getName().equals("abc"))
            //
            .assertEquals(Assertions::fail, s);

    s = "insert User values ('abc',now())";
    insert(User.class).values("abc", k("now()"))
            //
            .assertEquals(Assertions::fail, s);

    s = "insert User (Name,FullName,v) values ('abc',null,1)";
    insert(User.class, User::getName, User::getFullName, kf("v"))
            .values("abc", null, 1)
            //
            .assertEquals(Assertions::fail, s);

```
