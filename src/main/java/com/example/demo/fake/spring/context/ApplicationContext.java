package com.example.demo.fake.spring.context;

import com.example.demo.fake.spring.factory.BeanFactory;

public interface ApplicationContext extends BeanFactory {

    public <T> T getBean(Class<T> clazz);
    
}
