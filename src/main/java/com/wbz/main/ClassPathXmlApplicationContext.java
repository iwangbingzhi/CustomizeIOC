package com.wbz.main;

import com.wbz.BeanUtils.BeanUtils;
import com.wbz.config.Bean;
import com.wbz.config.Property;
import com.wbz.parseXML.ConfigManager;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
/*   ClassPathXmlApplicationContext创建的时候，
就初始化spring容器（装载bean实例的）*/
public class ClassPathXmlApplicationContext implements BeanFactory {

    @Override
    //根据bean名称获得bean实例
    public Object getBean(String beanName) {
        Object bean = context.get(beanName);

        return bean;
    }

    //使用map做spring容器,放置spring所管理的对象
    private Map<String,Object> context = new HashMap<String, Object>();
    //配置信息
    private Map<String, Bean> config;


    public ClassPathXmlApplicationContext(String path) throws Exception {
        //读取配置文件获得需要初始化的bean信息
        config = ConfigManager.getConfig(path);
        //遍历配置初始化bean
        if (config!=null){
            for (Map.Entry<String,Bean> entry : config.entrySet()) {
                //获取配置中的bean信息
                String beanName = entry.getKey();
                Bean bean = entry.getValue();
                Object existBean = context.get(beanName);

                /*createBean也会向容器放置bean，
                在初始化之前要判断容器中是否存在了这个bean，再去完成初始化工作*/
                if (existBean==null) {
                    //根据bean配置，创建bean对象
                    Object beanObj = createBean(bean);
                    //将初始化好的bean放入容器中
                    context.put(beanName, beanObj);
                }
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
            clazz = Class.forName(className);
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
        //2.获得bean属性，将其注入 2.1 value属性 2.2其他Bean注入 ref
        if (bean.getProperties()!=null){
            for (Property prop : bean.getProperties()){
                //获得要注入的属性名称
                String name = prop.getName();
                //根据属性名称获得注入属性对应的set方法
                Method setMethod = BeanUtils.getWriteMethod(beanObj,name);
                //创建一个需要注入到bean中的属性
                Object param = null;

                if (prop.getValue()!=null){
                    //获得要注入的属性值
                    String value = prop.getValue();
                    param = value;
                }

                if (prop.getRef()!=null){
                //因为要注入其他bean到当前bean中，
                    // 先从容器中查找当前要注入的bean是否创建，并且放入容器中
                    Object existBean = context.get(prop.getRef());

                    if (existBean==null){
                        //为空则说明容器中不存在我们要注入的Bean,则创建bean
                        existBean = createBean(config.get(prop.getRef()));
                        //将创建好的bean放入到容器中
                        context.put(prop.getRef(),existBean);
                    }
                    param = existBean;
                }
                //调用set方法注入即可
                try {
                    setMethod.invoke(beanObj,param);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new Exception(name+"bean的属性没有对应的set方法或方法参数不正确"+className);
                }
            }
        }
        return beanObj;
    }
}
