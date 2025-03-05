package com.example.demo.fake.spring.test;

import com.example.demo.fake.spring.annotation.MyAutowired;
import com.example.demo.fake.spring.annotation.MyComponent;

@MyComponent
public class TestServiceImpl {

    @MyAutowired
    private TestDao testDao;

    public void sayHello() {
        System.out.println("hello world");
    }
    
}
