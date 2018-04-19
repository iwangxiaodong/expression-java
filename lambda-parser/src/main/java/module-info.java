module com.openle.source.expression {
    requires java.sql;
    requires java.naming;
    requires cdi.api;
    requires org.jooq;
    requires jinq.asm.rebased;
    requires com.openle.module.lambda;//requires my.lambda; 
    requires com.openle.module.core;
    requires org.mockito;

    //requires org.junit.jupiter.api;
    //requires javax.persistence;
    //requires jinq.jooq;
    //requires org.mockito;
    // for test
    //requires org.junit.jupiter.api;
//    // 限定 - 前者只能被后者访问
//    exports org.jinq.jooq.transform to com.openle.source.expression;
//    exports org.jinq.jooq.querygen to com.openle.source.expression;
    // 不限定
    exports com.openle.source.expression; 
}
