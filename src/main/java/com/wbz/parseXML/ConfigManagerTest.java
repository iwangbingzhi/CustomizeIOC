package com.wbz.parseXML;

import com.wbz.config.Bean;
import org.dom4j.DocumentException;


import java.util.Map;


public class ConfigManagerTest {

    @org.junit.Before
    public void setUp() throws Exception {
    }

    @org.junit.After
    public void tearDown() throws Exception {
    }

    @org.junit.Test
    public void getConfig() throws DocumentException {
        Map<String, Bean> map = ConfigManager.getConfig("/applicationContext.xml");
        System.out.println(map);
    }
}
