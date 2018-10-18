module com.openle.module.lambda {
    requires java.sql;
    requires java.naming;
    requires org.jooq;
    requires jinq.asm.rebased;
    requires analysis;
    requires net.bytebuddy;
//    requires transitive com.openle.module.core;

    //requires org.mockito;
//    // 限定 - 前者只能被后者访问
//    exports org.jinq.jooq.transform to com.openle.module.lambda;
//    exports org.jinq.jooq.querygen to com.openle.module.lambda;
//    exports org.jinq.jooq.transform;
//    opens org.jinq.jooq.transform;
//    exports org.jinq.jooq.querygen;
//    opens org.jinq.jooq.querygen;
// 不限定
    exports com.openle.module.lambda;
}
