package com.wbz.main;

import com.wbz.bean.A;
import com.wbz.bean.B;
import com.wbz.bean.C;

public class Test {
    @org.junit.Test
    public void fun1() throws Exception {
        BeanFactory beanFactory = new ClassPathXmlApplicationContext("/applicationContext.xml");
        A a = (A) beanFactory.getBean("A");
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
        System.out.println(c.getB().getA().getName());
    }
}
