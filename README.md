# [expression-java](https://github.com/iwangxiaodong/expression-java) [![Build Status](https://travis-ci.org/iwangxiaodong/expression-java.svg?branch=master)](https://travis-ci.org/iwangxiaodong/expression-java)

***lambda-parser*** is a expression parser of lambda to sql.
<br />
Gradle:
<br />
> dependencies {
>
> compile 'com.openle.source.expression:lambda-parser:1.0.1'
>
> }
<br />
<br />

**Test**: lambda-parser/src/test/java/com/openle/source/expression/LambdaParserTest.java
```java
PredicateSerializable<EntityDemo> whereLambda = null;

whereLambda = t -> t.getAge() < (t.getAge() + 1) && t.getFullName().equals("myName") && true;

String sql = LambdaParser.toSQL("SELECT * FROM myTable",whereLambda);

System.out.println(sql);


//for Camel To Underline
LambdaParser.isCamelToUnderline=true;

sql = LambdaParser.toSQL("SELECT * FROM myTable",whereLambda);

System.out.println();

System.out.println("Camel To Underline:\r\n"+sql);

```

**Output**:
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
