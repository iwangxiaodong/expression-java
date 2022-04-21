# [expression-java](https://github.com/iwangxiaodong/expression-java)

***functional-sql*** is a expression parser of functional lambda to sql.
<br />
Gradle:
<br />
> repositories {
> 
> 
> }
> 
> dependencies {
>
> > implementation 'com.openle.source.expression:lambda-parser:+'
>
> }
<br />
<br />

**Test**: lambda-parser/src/test/java/com/openle/source/expression/LambdaTest.java
```sql

    //  import static com.openle.source.expression.sql.*;
    //  import static com.openle.source.expression.sql.f.*;
    //
    String s = "select * from User";

    @Test
    public void testSelect() {

        select().from(User.class)
                //
                .assertEquals(Assertions::fail, s);

        s = "select null";
        select()
                //
                .assertEquals(Assertions::fail, s);

        s = "select max(*)";
        select(max("*"))
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
        update(User.class).set(pair(User::getName, "abc"))
                //
                .assertEquals(Assertions::fail, s);

        s = "update User set Age = 22 , Name = 'a' where Age >= 18";
        update(User.class).set(pair(User::getAge, 22), pair(User::getName, "a"))
                .where((User t) -> t.getAge() >= 18)
                //
                .assertEquals(Assertions::fail, s);

        s = "update MyTable set Name = 'abc' where Age <> 18";
        update("MyTable").set(pair(User::getName, "abc"))
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


```
