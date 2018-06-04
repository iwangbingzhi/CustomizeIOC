package com.wbz.parseXML;

import com.wbz.config.Bean;
import com.wbz.config.Property;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {
    //读取配置文件并且返回读取结果
    public static Map<String,Bean> getConfig(String path) throws DocumentException {
        //创建一个用于返回的Map对象
        Map<String,Bean> map = new HashMap<String, Bean>();

        //dom4j实现
          //1.创建解析器
        SAXReader saxReader = new SAXReader();
          //2.加载配置文件，得到document对象
        InputStream resourceAsStream = ConfigManager.class.getResourceAsStream(path);
        Document doc = null;

        try{
            doc = saxReader.read(resourceAsStream);
                 }catch (DocumentException e){
            e.printStackTrace();
            throw new RuntimeException("请检查xml配置是否正确");
        }
            //3.定义xpath表达式，取出所有的Bean元素
        String xpath = "//bean";   //双斜杠表示整个元素内查找bean
            //4.将Bean对象进行遍历
        List<Element> list = doc.selectNodes(xpath);

        if (list!=null){
            for (Element beanEle:list) {
                //4.1将bean元素的name,  class属性封装到bean对象中
                Bean bean = new Bean();
                String name = beanEle.attributeValue("name");
                String className = beanEle.attributeValue("class");

                bean.setName(name);
                bean.setClassName(className);

                //4.2获得bean元素下所有property子元素，将属性name、value、ref封装到property中
                List<Element> children = beanEle.elements("property");

                if (children!=null){
                    for (Element child:children) {
                        Property prop = new Property();

                        String pName = child.attributeValue("name");
                        String pValue = child.attributeValue("value");
                        String pRef = child.attributeValue("ref");

                        prop.setName(pName);
                        prop.setValue(pValue);
                        prop.setRef(pRef);

                        //4.3将property封装到bean对象
                        bean.getProperties().add(prop);
                    }
                }
                //4.4将bean对象封装到map中（用于返回的map）
                map.put(name,bean);
            }
        }
        return map;
          //5.返回map结果
    }
}
