package com.example.demo.fake.spring.test;

import com.example.demo.fake.spring.annotation.MyAutowired;
import com.example.demo.fake.spring.annotation.MyComponent;
import com.example.demo.fake.spring.annotation.MyLazy;

@MyComponent
public class TestDaoImpl implements TestDao {

    @MyLazy
    @MyAutowired
    private TestServiceImpl testServiceImpl;

    public void testDao() {
        System.out.println("hello dao");
    }

    public void testService() {
        testServiceImpl.sayHello();
    }
}
