package com.example.demo.fake.spring.bean;

import java.util.Map;

import lombok.Data;

@Data
public class BeanDefinition {

    private String name;

    private Class<?> clazz;

    private Map<String, Class<?>> injectFields;

    
}
