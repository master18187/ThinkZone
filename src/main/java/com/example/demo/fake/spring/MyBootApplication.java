package com.example.demo.fake.spring;

import com.example.demo.fake.spring.context.AnnotationConfigurableApplicationContext;
import com.example.demo.fake.spring.context.ApplicationContext;
import com.example.demo.fake.spring.test.TestServiceImpl;

public class MyBootApplication {

    public static void run(Class<?> primarySource, String[] args) {
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        ApplicationContext applicationContext = new AnnotationConfigurableApplicationContext();
        applicationContext.refresh();

        TestServiceImpl bean = applicationContext.getBean(TestServiceImpl.class);
        System.out.println(bean);
        bean.sayHello();
        bean = applicationContext.getBean(TestServiceImpl.class);
        System.out.println(bean);
        bean.sayHello();
    }
}
