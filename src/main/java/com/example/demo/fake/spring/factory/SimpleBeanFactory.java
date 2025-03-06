package com.example.demo.fake.spring.factory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.lang3.reflect.FieldUtils;

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
        Object bean = getSingleton(clazz, true);
        if (bean == null) {
            T createdBean = doCreateBean(clazz, beanDefinition);

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

    protected <T> T getSingleton(Class<T> clazz, boolean allowEarlyReference) {
        // 1. 从一级缓存获取（完全初始化的 Bean）
        Object singletonObject = this.completeBeanMap.get(clazz);
        if (singletonObject == null) {
            synchronized (this.completeBeanMap) {
                // 2. 从二级缓存获取（未初始化的 Bean）
                singletonObject = this.proxyBeanMap.get(clazz);
                if (singletonObject == null && allowEarlyReference) {
                    // 3. 从三级缓存获取 ObjectFactory，生成早期引用
                    Supplier<?> singletonFactory = this.unInitMap.get(clazz);
                    if (singletonFactory != null) {
                        // 调用 ObjectFactory.getObject() 生成 Bean 的早期引用
                        singletonObject = singletonFactory.get();
                        // 将早期引用存入二级缓存，并删除三级缓存的记录
                        this.proxyBeanMap.put(clazz, singletonObject);
                        this.unInitMap.remove(clazz);
                    }
                }
            }
        }
        return (T) singletonObject;
    }

    public <T> T doCreateBean(Class<T> clazz, BeanDefinition beanDefinition) {
        try {
            // 1. 实例化Bean（此时属性尚未填充）
            T newInstanceBean = newInstanceBean(clazz);

            // 2. 将Bean工厂放入三级缓存（解决循环依赖）
            unInitMap.put(clazz, () -> newInstanceBean);

            // 3. 属性注入（可能触发依赖Bean的创建）
            populateBean(newInstanceBean, beanDefinition);

            // 4. 初始化Bean（调用@PostConstruct方法）
            initializeBean(newInstanceBean, beanDefinition);
           
            return newInstanceBean;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> T newInstanceBean(Class<T> clazz) {
        T instance = null;
        try {
            instance = clazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }

    public <T> void populateBean(T bean, BeanDefinition beanDefinition) {

        // 注入属性
        autowireByType(bean, beanDefinition);

        // 应用属性值到Bean实例上
        applyPropertyValues(bean, beanDefinition);
    }

    public <T> void autowireByType(T bean, BeanDefinition beanDefinition) {
        Map<String, Class<?>> injectFields = beanDefinition.getInjectFields();
        if (injectFields == null || injectFields.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Class<?>> entry : injectFields.entrySet()) {
            Object val = getBean(entry.getValue());
            try {
                FieldUtils.writeDeclaredField(bean, entry.getKey(), val, true);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

    }

    public <T> void applyPropertyValues(T bean, BeanDefinition beanDefinition) {

    }

    public <T> void initializeBean(T bean, BeanDefinition beanDefinition) {

    }
}
