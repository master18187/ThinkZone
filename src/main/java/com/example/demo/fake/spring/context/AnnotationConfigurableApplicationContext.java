package com.example.demo.fake.spring.context;

public class AnnotationConfigurableApplicationContext implements ApplicationContext {

    @Override
    public <T> T getBean(Class<T> clazz) {
        
        throw new UnsupportedOperationException("Unimplemented method 'getBean'");
    }
    
}
