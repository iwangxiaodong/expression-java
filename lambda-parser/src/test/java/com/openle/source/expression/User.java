package com.openle.source.expression;

import javax.persistence.Table;

@Table(name = "User")
public class User {

    private String name;
    private String fullName;
    private Integer age;
    private int id;
    private UserId userId;

    public User() {
        System.out.println("User Init!");
    }

    public UserId userId() {
        return userId;
    }

    public int id() {
        return id;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

}
