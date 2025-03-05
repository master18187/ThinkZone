package com.example.demo.fake.spring.bean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.fake.spring.annotation.MyComponent;
import com.example.demo.fake.spring.core.AnnotationMetadataReadingVisitor;
import com.example.demo.fake.spring.core.Resource;

import aj.org.objectweb.asm.ClassReader;

public class ClassPathBeanDefinitionReader {

    PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    private List<BeanDefinition> beanDefinitions = new ArrayList<>();

    public void doScan(String pkg) throws IOException {
        // 解析获得扫描的资源文件信息
        Resource[] resources = resourcePatternResolver.getResources(pkg);

        for (Resource rs : resources) {
            ClassReader classReader = new ClassReader(rs.getFileStream());
            // 提取类元数据（类名、接口、注解等）
            AnnotationMetadataReadingVisitor visitor = new AnnotationMetadataReadingVisitor();
            classReader.accept(visitor, ClassReader.SKIP_DEBUG);

            // 是否为Component组件
            if (visitor.hasAnnotation(MyComponent.class.getName())) {
                // 加载类到JVM
                String className = visitor.getClassName();
                try {
                    Class<?> clazz = Class.forName(className);

                    BeanDefinition beanDefinition = new BeanDefinition();
                    beanDefinition.setName(className);
                    beanDefinition.setClazz(clazz);
                    beanDefinitions.add(beanDefinition);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public List<BeanDefinition> getBeanDefinitions() {
        return this.beanDefinitions;
    }

    public static void main(String[] args) throws Exception {
        ClassPathBeanDefinitionReader bean = new ClassPathBeanDefinitionReader();
        String locationPattern = "classpath*:com/example/demo";
        bean.doScan(locationPattern);

        bean.beanDefinitions.stream().forEach(System.out::print);
    }

}
