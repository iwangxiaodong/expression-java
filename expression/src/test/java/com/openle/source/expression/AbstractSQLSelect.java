package com.openle.source.expression;

/**
 * Created by i on 11/13/15.
 */
public abstract class AbstractSQLSelect<E> {
    String tableName;
    PredicateSerializable<E> lambda;
    public  AbstractSQLSelect(String tableName,PredicateSerializable<E> lambda){
        this.tableName=tableName;
        this.lambda=lambda;
    }
//    public abstract List<E> select() throws Exception;
//    public abstract List<E> select(String selectFields) throws Exception;

    public String select(){
        return LambdaParser.toSelectAllSQL(tableName,lambda);
    }

    public String select(String selectFields){
        return  LambdaParser.toSQL(selectFields, tableName, lambda);
    }
}
