package com.example.demo.fake.spring.factory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.example.demo.fake.spring.bean.BeanDefinition;
import com.example.demo.fake.spring.bean.ClassPathBeanDefinitionReader;

public class SimpleBeanFactory implements BeanFactory {

    private ClassPathBeanDefinitionReader beanDefinitionReader = new ClassPathBeanDefinitionReader();

    private Map<Class<?>, BeanDefinition> beanDefinitionMap = new HashMap<>();

    private Map<Class<?>, Object> beanMap = new ConcurrentHashMap<>(256);

    public Map<Class<?>, BeanDefinition> getBeanDefinitionMap() {
        return this.beanDefinitionMap;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> clazz) {
        BeanDefinition beanDefinition = beanDefinitionMap.get(clazz);
        if (beanDefinition == null) {
            throw new IllegalArgumentException(clazz.getName() + " unDefinition");
        }
        Object bean = beanMap.get(clazz);
        if (bean == null) {
            return doCreateBean(clazz, beanDefinition);
        }
        return (T) beanMap.get(clazz);
    }

    public void componentScan(String location) {
        try {
            beanDefinitionReader.doScan(location);

            beanDefinitionMap = beanDefinitionReader.getBeanDefinitions()
                    .stream().collect(Collectors.toMap(BeanDefinition::getClazz, o -> o));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public <T> T doCreateBean(Class<T> clazz, BeanDefinition beanDefinition) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
