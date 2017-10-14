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
> > compile 'com.openle.source.expression:lambda-parser:1.0.2'
>
> }
<br />
<br />
    https://bintray.com/wangxiaodong/maven/lambda-parser
<br />
<br />

**Test**: lambda-parser/src/test/java/com/openle/source/expression/LambdaParserTest.java
```java

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

```

**Output**:
```sql
select Name,Age from EntityDemo where Age > 0
```
...
```sql
SELECT * FROM myTable where (
    Age < (Age + 1)
    and FullName = 'myName'
)
```
```sql
Camel To Underline:
SELECT * FROM myTable where (
  age < (age + 1)
  and full_name = 'myName'
)
```
