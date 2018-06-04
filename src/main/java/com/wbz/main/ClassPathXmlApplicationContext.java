package com.wbz.main;

import com.wbz.BeanUtils.BeanUtils;
import com.wbz.config.Bean;
import com.wbz.config.Property;
import com.wbz.parseXML.ConfigManager;
import org.dom4j.DocumentException;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
/*   ClassPathXmlApplicationContext创建的时候，
就初始化spring容器（装载bean实例的）*/
public class ClassPathXmlApplicationContext implements BeanFactory {
    //使用map做spring容器,放置spring所管理的对象
    private Map<String,Object> context = new HashMap<String, Object>();
    //配置信息
    private Map<String, Bean> config;
    
    @Override
    public Object getBean(String beanName) {
        return null;
    }

    public ClassPathXmlApplicationContext(String path) throws Exception {
        //读取配置文件获得需要初始化的bean信息
        config = ConfigManager.getConfig(path);
        //遍历配置初始化bean
        if (config!=null){
            for (Map.Entry<String,Bean> entry :config.entrySet()) {
                //获取配置中的bean信息
                String beanName = entry.getKey();
                Bean bean = entry.getValue();
                //根据bean配置，创建bean对象
                Object beanObj = createBean(bean);

                //将初始化好的bean放入容器中
                context.put(beanName,beanObj);
            }

        }
    }
    //根据bean配置，创建bean对象
   /*  <bean name="A" class="com.wbz.bean.A">
        <property name="name" value="aname"/>
    </bean>*/
    private Object createBean(Bean bean) throws Exception {
        //1.获得要创建的bean的class
        String className = bean.getClassName();
        Class clazz = null;

        try {
            clazz = java.lang.Class.forName(className);
        }catch (ClassNotFoundException e){
            e.printStackTrace();
            throw new RuntimeException("请检查"+className+"bean的class配置是否正确");
        }

        //获得class后，将class对应的对象创建出来
        Object beanObj = null;
        try {
            beanObj = clazz.newInstance();
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(className+"bean中没有空参构造");
        }
        //2.获得bean属性，将其注入  2.1 value属性 2.2其他Bean注入 ref
        if (bean.getProperties()!=null){
            for (Property prop : bean.getProperties()){
                if (prop.getValue()!=null){
                    //获得要注入的属性值
                    String value = prop.getValue();
                    //获得要注入的属性名称
                    String name = prop.getName();
                    //根据属性名称获得注入属性对应的set方法
                    Method setMethod = BeanUtils.getWriteMethod(beanObj,name);
                    //调用set方法注入即可
                    try {
                        setMethod.invoke(beanObj,value);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new Exception(name+"bean的属性没有对应的set方法或方法参数不正确");
                    }
                }

                if (prop.getRef()!=null){

                }
            }
        }
        return null;
    }
}
