package com.openle.source.expression;

import java.util.function.Consumer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Execute {

    protected String sqlString;
    private String debugSqlString;

    public String sql() {
        return sqlString;
    }

    protected Execute() {
    }

    protected Execute(String sql) {

        this.sqlString = sql;
        this.debugSqlString = sqlString.length() > 500 ? sqlString.substring(0, 500) + " ... 超出字符已省略！" : sqlString;
    }

    public void execute() {
        execute(sql.getConnection());
    }

    public void execute(Consumer<String> f) {
        f.accept(sqlString);
    }

    public boolean execute(Connection conn) {

        System.out.println("Execute Connection - " + debugSqlString);
        boolean r = false;
        if (conn != null) {
            try {
                Statement stmt = conn.createStatement();
                r = stmt.execute(sqlString);
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                System.err.println(e);
            }
        }
        return r;
    }

    public ResultSet executeQuery() {
        ResultSet rs = null;
        try {
            Statement stmt = sql.getConnection().createStatement();
            rs = stmt.executeQuery(sqlString);
            stmt.close();
            sql.getConnection().close();
        } catch (SQLException e) {
            System.err.println(e);
        }
        return rs;
    }

    public void executeQuery(Consumer<ResultSet> f) {
        executeQuery(f, sql.getConnection());
    }

    public void executeQuery(Consumer<ResultSet> f, Connection conn) {
        System.out.println("Execute Consumer,Connection - " + debugSqlString);
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sqlString);
            f.accept(rs);
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println(e);
        }
    }

    @Override
    public String toString() {
        return sqlString;
    }

    // this.assertMe(t -> assertEquals(t, "xyz"))
    public void test(java.util.function.Consumer<String> c) {
        c.accept(sqlString);
    }

    // this.assertEquals(Assertions::fail, "abc")
    public void assertEquals(java.util.function.Consumer<String> c, String sql) {
        System.out.println("Execute - " + sqlString);
        if (!sql.equalsIgnoreCase(sqlString)) {
            String msg = "expected: <" + sqlString + "> but was: <" + sql + ">";
            c.accept(msg);
        }
    }

    //未实现；后续将全部转换改为当前实例转换。
    private Execute camelToUnderline() {
        return this;
    }

    private Execute underlineToCamel() {
        return this;
    }

}
