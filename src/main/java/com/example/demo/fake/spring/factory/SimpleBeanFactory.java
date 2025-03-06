package com.example.demo.fake.spring.factory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.example.demo.fake.spring.bean.BeanDefinition;
import com.example.demo.fake.spring.bean.ClassPathBeanDefinitionReader;

public class SimpleBeanFactory implements BeanFactory {

    private ClassPathBeanDefinitionReader beanDefinitionReader = new ClassPathBeanDefinitionReader();

    private Map<Class<?>, BeanDefinition> beanDefinitionMap = new HashMap<>();

    private Map<Class<?>, Object> completeBeanMap = new ConcurrentHashMap<>(256);

    private Map<Class<?>, Object> proxyBeanMap = new ConcurrentHashMap<>(256);

    private Map<Class<?>, Supplier<Object>> unInitMap = new ConcurrentHashMap<>(256);

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
        Object bean = completeBeanMap.get(clazz);
        if (bean == null) {
            T createdBean = doCreateBean(clazz, beanDefinition);

            populateBean(createdBean, beanDefinition);
            
            completeBeanMap.put(clazz, createdBean);
            proxyBeanMap.remove(clazz);
            unInitMap.remove(clazz);
            return createdBean;
        }
        return (T) completeBeanMap.get(clazz);
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
            Object proxyBean = proxyBeanMap.get(clazz);
            if (proxyBean == null) {

            }
            // 原始Bean，未代理
            T instance = clazz.newInstance();
            return instance;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> void populateBean(T bean, BeanDefinition beanDefinition) {

        // 注入属性
        autowireByType(bean, beanDefinition);

        // 应用属性值到Bean实例上
        applyPropertyValues(bean, beanDefinition);
    }

    public <T> void autowireByType(T bean, BeanDefinition beanDefinition) {

    }

    public <T> void applyPropertyValues(T bean, BeanDefinition beanDefinition) {

    }
}
