package com.wbz.main;

import com.wbz.beanutils.BeanUtils;
import com.wbz.config.Bean;
import com.wbz.config.Property;
import com.wbz.parseXML.ConfigManager;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
/* ClassPathXmlApplicationContext创建的时候，就初始化spring容器（装载bean实例的）*/
public class ClassPathXmlApplicationContext implements BeanFactory {
    //使用map做spring容器,放置spring所创建出的对象
    private Map<String,Object> context = new HashMap<String, Object>();
    //放置配置信息
    private Map<String, Bean> config;

    public ClassPathXmlApplicationContext(String path) throws Exception {
        //1读取配置文件获得需要初始化的bean信息
        config = ConfigManager.getConfig(path);
        //2遍历配置初始化bean
        if (config != null){
            for (Map.Entry<String,Bean> entry : config.entrySet()) {
                //获取配置中的bean信息
                String beanName = entry.getKey();
                Bean bean = entry.getValue();
                Object existBean = context.get(beanName);

                /*createBean也会向容器放置bean，
                在初始化之前要判断容器中是否存在了这个bean，再去完成初始化工作
                scope属性设置为singleton的时候才会放进入容器中
                */
                if (existBean==null && bean.getScope().equals("singleton")) {
                    //根据bean配置，创建bean对象
                    Object beanObj = createBean(bean);
                    //3将初始化好的bean放入容器中
                    context.put(beanName, beanObj);
                }
            }
        }
    }

    @Override
    //根据bean名称获得bean实例
    public Object getBean(String beanName) {
        Object bean = context.get(beanName);
        //如果scope设置为prototype，context中不会包含bean对象
        if (bean == null){
            //如果不存在该bean对象，则创建它
            try {
                bean = createBean(config.get(beanName));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return bean;
    }

    //根据bean配置，创建bean对象
   /*  <bean name="A" class="com.wbz.domain.A">
        <property name="name" value="aname"/>
    </bean>*/
   //相当于该方法中含有上面的bean配置
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
        //2.获得bean属性，将其注入 2.1 value属性  2.2其他Bean注入 ref
        if (bean.getProperties() != null){
            for (Property prop : bean.getProperties()){
                //获得要注入的属性名称
                String name = prop.getName();
                String value = prop.getValue();
                String ref = prop.getRef();

                if (value!=null) {
                    Map<String, String[]> paramMap = new HashMap<>();
                    paramMap.put(name, new String[]{value});

                    //使用apache beanutils类库注入属性并且自动类型转换
                    try {
                        org.apache.commons.beanutils.BeanUtils.populate(beanObj, paramMap);
                    } catch (Exception e) {
                        throw new RuntimeException("请检查" + name + "的属性！");
                    }
                }
                if (prop.getRef()!=null){
                    Method setMethod = BeanUtils.getWriteMethod(beanObj,name);
                    //因为要注入其他bean到当前bean中，
                    // 先从容器中查找当前要注入的bean是否创建，并且放入容器中
                    Object existBean = context.get(prop.getRef());

                    if (existBean==null){
                        //为空则说明容器中不存在我们要注入的Bean,则创建bean
                        existBean = createBean(config.get(prop.getRef()));
                        //将创建好的bean放入到容器中,scope属性为singleton才会放入容器中
                        if (config.get(prop.getRef()).getScope().equals("singleton")) {
                            context.put(prop.getRef(), existBean);
                        }
                    }
                    //调用set方法注入即可
                    try {
                        setMethod.invoke(beanObj,existBean);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new Exception(name+"bean的属性没有对应的set方法或方法参数不正确"+className);
                    }
                }

            }

             /*   注入属性方法1
             //根据属性名称获得注入属性对应的set方法
                Method setMethod = BeanUtils.getWriteMethod(beanObj,name);
                //创建一个需要注入到bean中的属性
                Object param = null;

                if (prop.getValue() != null){
                    //获得要注入的属性值
                    String value = prop.getValue();
                    param = value;
                }*/


        }
        return beanObj;
    }
}
