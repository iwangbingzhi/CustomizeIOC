package com.wbz.domain;

public class B {
    private A a;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public A getA() {
        return a;
    }

    public void setA(A a) {
        this.a = a;
    }

    public B() {
        System.out.println("B被创建了");
    }
}
