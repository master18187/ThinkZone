package com.example.demo.fake.spring.context;

import java.util.Collection;

import com.example.demo.fake.spring.bean.BeanDefinition;
import com.example.demo.fake.spring.factory.BeanFactory;
import com.example.demo.fake.spring.factory.SimpleBeanFactory;

public class AnnotationConfigurableApplicationContext implements ApplicationContext {

    private SimpleBeanFactory beanFactory = new SimpleBeanFactory();

    @Override
    public void refresh() {
        // 初始化类信息
        initBeanDefination();

        // 预实例化所有非懒加载的单例
        finishBeanFactoryInitialization();
    }

    @Override
    public BeanFactory getBeanFactory() {
        return this.beanFactory;
    }

    private void finishBeanFactoryInitialization() {
        Collection<BeanDefinition> beanDefinitions = beanFactory.getBeanDefinitionMap().values();
        for (BeanDefinition beanDefinition : beanDefinitions) {
            beanFactory.getBean(beanDefinition.getClazz());
        }
    }

    private void initBeanDefination() {
        beanFactory.componentScan("classpath*:com/example/demo");
    }
}
