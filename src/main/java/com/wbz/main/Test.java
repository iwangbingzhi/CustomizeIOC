package com.wbz.main;

import com.wbz.domain.A;
import com.wbz.domain.B;
import com.wbz.domain.C;

public class Test {
    @org.junit.Test
    public void fun1() throws Exception {
        BeanFactory beanFactory = new ClassPathXmlApplicationContext("/applicationContext.xml");
        A a = (A) beanFactory.getBean("A");
        A a2 = (A) beanFactory.getBean("A");
        A a3 = (A) beanFactory.getBean("A");
        A a4 = (A) beanFactory.getBean("A");
        System.out.println(a.getName());
    }

    @org.junit.Test
    public void fun2() throws Exception {
        BeanFactory beanFactory = new ClassPathXmlApplicationContext("/applicationContext.xml");
        B b = (B) beanFactory.getBean("B");
        System.out.println(b.getA().getName());
    }
    @org.junit.Test
    public void fun3() throws Exception {
        BeanFactory beanFactory = new ClassPathXmlApplicationContext("/applicationContext.xml");
        C c = (C) beanFactory.getBean("C");
        System.out.println("a的名字="+c.getB().getA().getName());
        System.out.println("b的名字="+c.getB().getName());
    }
}
