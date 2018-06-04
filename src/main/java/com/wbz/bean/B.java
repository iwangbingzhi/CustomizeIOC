package com.wbz.bean;

public class B {
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "B{" +
                "name='" + name + '\'' +
                '}';
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;
}
