package com.example.demo.fake.spring.factory;

public interface BeanFactory {

    <T> T getBean(Class<T> clazz);
    
}
