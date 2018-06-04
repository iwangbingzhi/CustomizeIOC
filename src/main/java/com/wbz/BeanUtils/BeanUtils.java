package com.wbz.BeanUtils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

public class BeanUtils {
    //参数1 bean对象  参数2 要获得的bean对象对应的属性名称
    public static Method getWriteMethod(Object beanObj, String name) {
        Method method = null;
        //内省技术实现该方法
        try {
            //1.分析bean对象 获得beaninfo
            BeanInfo beanInfo = Introspector.getBeanInfo(beanObj.getClass());

            //2.根据beaninfo 获得所有属性的描述器
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

            //3.遍历这些属性描述器
            if (propertyDescriptors!=null){
                for (PropertyDescriptor pd: propertyDescriptors) {
                    //3.1 判断当前遍历的描述器描述的属性是否是要找的属性
                    //获得当前描述器描述的属性名称，
                    String pdName = pd.getName();
                    // 使用要找的属性名称与当前描述器描述的属性名称比对
                    if(pdName.equals(name)){
                        //比如一致则找到了
                       method = pd.getWriteMethod();

                    }
                }
            }
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
        //4.返回找到的set方法
           //4.1如果没找到，抛出异常提示用户检查是否创建属性对应的set方法
        if (method==null){
            throw new RuntimeException("请检查"+name+"属性的set方法是否创建");
        }
        return method;
    }
}

