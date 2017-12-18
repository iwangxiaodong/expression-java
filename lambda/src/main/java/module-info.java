// javac.modulepath全局通用 - 随便建立一个Java Modular Project项目通过IDE添加模块库（\nbproject\project.properties）
module com.openle.module.lambda {
    requires java.sql;
    requires java.naming;
    requires org.jooq;
    requires jinq.asm.rebased;
    requires analysis;
    requires transitive com.openle.module.core;
    requires net.bytebuddy;

    //requires org.mockito;
//    // 限定 - 前者只能被后者访问
//    exports org.jinq.jooq.transform to com.openle.source.expression;
//    exports org.jinq.jooq.querygen to com.openle.source.expression;
    // 不限定
    exports com.openle.module.lambda;
}
