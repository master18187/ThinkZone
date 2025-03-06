package com.example.demo.fake.spring;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.example.demo.fake.spring.context.AnnotationConfigurableApplicationContext;
import com.example.demo.fake.spring.context.ApplicationContext;
import com.example.demo.fake.spring.test.TestDao;
import com.example.demo.fake.spring.test.TestDaoImpl;
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
        bean.sayHello();
        bean = applicationContext.getBean(TestServiceImpl.class);
        bean.sayHello();

        bean.testDao();
        TestDaoImpl bean1 = applicationContext.getBean(TestDaoImpl.class);
        bean1.testService();
        
        TestDao testDao = (TestDao) Proxy.newProxyInstance(bean1.getClass().getClassLoader(),
                bean1.getClass().getInterfaces(),
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        System.out.println("before invoke method: " + method);
                        return method.invoke(bean1, args);
                    }

                });
        testDao.testDao();
    }
}
