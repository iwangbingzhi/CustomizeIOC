package com.wbz.domain;

public class A {
    private String name;

    public A() {
        System.out.println("A被创建了");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
