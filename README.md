# [expression-java](https://github.com/iwangxiaodong/expression-java) [![Build Status](https://travis-ci.org/iwangxiaodong/expression-java.svg?branch=master)](https://travis-ci.org/iwangxiaodong/expression-java)

***expression-java*** is a expression parser of lambda to sql.
<br />
<br />

**Test**: expression/src/test/java/com/openle/source/expression/LambdaParserTest.java
```java
PredicateSerializable<EntityDemo> whereLambda = null;

whereLambda = t -> t.getAge() < (t.getAge() + 1) && t.getName().equals("myName") && true;

String sql = LambdaParser.toSQL("SELECT * FROM myTable",whereLambda);

System.out.println(sql);
```

**Output**:
```sql
SELECT * FROM myTable where (
    Age < (Age + 1)
    and Name = 'myName'
)
```
