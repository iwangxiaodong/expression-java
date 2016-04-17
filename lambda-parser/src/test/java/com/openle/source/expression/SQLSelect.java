package com.openle.source.expression;

/**
 * Created by i on 11/12/15.
 */
public class SQLSelect<E> extends AbstractSQLSelect<E> {
    String tableName;
    PredicateSerializable<E> lambda;

   public SQLSelect(String tableName, PredicateSerializable<E> lambda) {
        super(tableName, lambda);

        this.tableName=tableName;
        this.lambda=lambda;
    }

//  public  List<E> select() throws Exception {
//        throw new Exception("not implement!");
//    }
//  public  List<E> select(String selectFields) throws Exception {
//        throw new Exception("not implement!");
//    }
}
