package com.openle.source.expression;

import java.lang.reflect.ParameterizedType;

/**
 * Created by i on 11/12/15.
 */
public class From<E> {

  public  SQLSelect where(PredicateSerializable<E> lambda){
        Class <E> entityClass = (Class <E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        //System.out.println(entityClass.getSimpleName());
        return new SQLSelect(entityClass.getSimpleName(),lambda);
    }
}
