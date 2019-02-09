package com.wbz.domain;

public class C {
    private B b;

    public B getB() {
        return b;
    }

    public C() {
        System.out.println("C被创建了");
    }

    public void setB(B b) {
        this.b = b;
    }
}
