package com.example.demo.fake.spring.context;

import com.example.demo.fake.spring.factory.BeanFactory;

public interface ApplicationContext extends BeanFactory {

    BeanFactory getBeanFactory();

    default <T> T getBean(Class<T> clazz) {
        return getBeanFactory().getBean(clazz);
    }

    void refresh();
    
}
