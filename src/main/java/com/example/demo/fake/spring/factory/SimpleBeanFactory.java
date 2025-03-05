package com.example.demo.fake.spring.factory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleBeanFactory implements BeanFactory {
    
    private Map<Class<?>, Object> beanMap = new ConcurrentHashMap<>(256);

    
}
