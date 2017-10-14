package com.openle.source.expression;

import java.util.function.Consumer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Execute {

    String sql;

    protected Execute(String sql) {
        this.sql = sql;
    }

    public void execute() {
        System.out.println("Execute - " + sql);
    }

    public void execute(Consumer<String> f) {
        System.out.println("Execute Consumer - " + sql);
        f.accept(sql);
    }

    public boolean execute(Connection conn) {
        System.out.println("Execute Connection - " + sql);
        boolean r = false;
        try {
            Statement stmt = conn.createStatement();
            r = stmt.execute(sql);
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.err.println(e);
        }
        return r;
    }

    public void execute(Consumer<ResultSet> f, Connection conn) {
        System.out.println("Execute Connection - " + sql);
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
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
        return sql;
    }

}
