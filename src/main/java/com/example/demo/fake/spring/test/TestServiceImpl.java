package com.example.demo.fake.spring.test;

import com.example.demo.fake.spring.annotation.MyAutowired;
import com.example.demo.fake.spring.annotation.MyComponent;
import com.example.demo.fake.spring.annotation.MyLazy;

@MyComponent
public class TestServiceImpl {

    @MyLazy
    @MyAutowired
    private TestDao testDao;

    public void sayHello() {
        System.out.println("hello world");
    }

    public void testDao() {
        testDao.testDao();
    }
    
}
