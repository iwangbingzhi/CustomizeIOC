package com.wbz.main;

public interface BeanFactory {
    //根据bean的name获得bean对象的方法
    Object getBean(String beanName);
}
