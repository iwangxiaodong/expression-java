package org.jinq.jooq.transform;

public class MySchema extends org.jooq.impl.SchemaImpl {

    private static final long serialVersionUID = 785710849;

    public static final MySchema APP = new MySchema();

    private MySchema() {
        super("APP");
    }

    @Override
    public final java.util.List<org.jooq.Table<?>> getTables() {
        java.util.List result = new java.util.ArrayList();
        //result.addAll(getTables0());
        return result;
    }

}
