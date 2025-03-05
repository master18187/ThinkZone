package com.example.demo.fake.spring.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.example.demo.fake.spring.core.Resource;

public class PathMatchingResourcePatternResolver {

    private static final String CLASSPATH_ALL_URL_PREFIX = "classpath*";

    public Resource[] getResources(String locationPattern) throws IOException {
        // 1. 解析路径前缀（如 classpath*:）
        String prefix = getResourcePrefix(locationPattern);

        // 2. 根据前缀选择资源加载策略
        if (prefix.equals(CLASSPATH_ALL_URL_PREFIX)) {
            return findClassPathResources(locationPattern);
        } else {
            // 其他前缀（如 file:）
            return findPathMatchingResources(locationPattern);
        }

    }

    private String getResourcePrefix(String locationPattern) {
        if (StringUtils.contains(locationPattern, ":")) {
            return StringUtils.substringBefore(locationPattern, ":");
        }
        return locationPattern;
    }

    private Resource[] findClassPathResources(String locationPattern) throws IOException {
        // 地址处理
        locationPattern.replace(locationPattern, CLASSPATH_ALL_URL_PREFIX);
        locationPattern = StringUtils.substringAfter(locationPattern, ":");


        Enumeration<URL> resources = getClass().getClassLoader().getResources(locationPattern);
        Set<String> resultFiles = new HashSet<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            String rootDirAbsolutePath = resource.getPath();
            resolverPathFiles(rootDirAbsolutePath, resultFiles);
        }
        if (resultFiles.isEmpty()) {
            return new Resource[0];
        }

        // 1.路径Ant匹配

        // 2.转为Resource类型
        List<Resource> resourceList = new ArrayList<>();
        for (String fileAbsolutePath : resultFiles) {
            Resource resource = new Resource();
            File file = new File(fileAbsolutePath);
            resource.setName(file.getName());
            resource.setFile(file);
            resource.setFilePath(file.getAbsolutePath());
            resource.setFileStream(new FileInputStream(file));
            resourceList.add(resource);
        }
        return resourceList.toArray(new Resource[0]);
    }

    private void resolverPathFiles(String absolutePath, Set<String> resultFiles) {
        File rootDirFile = new File(absolutePath);
        if (!rootDirFile.isDirectory()) {
            resultFiles.add(rootDirFile.getAbsolutePath());
            return;
        }
        File[] listFiles = rootDirFile.listFiles();
        for (File file : listFiles) {
            if (file.isDirectory()) {
                resolverPathFiles(file.getAbsolutePath(), resultFiles);
                continue;
            }
            resultFiles.add(file.getAbsolutePath());
        }
    }

    private Resource[] findPathMatchingResources(String locationPattern) {
        throw new UnsupportedOperationException("Unimplemented method 'getResourcePrefix'");
    }

    public static void main(String[] args) throws Exception {
        PathMatchingResourcePatternResolver bean = new PathMatchingResourcePatternResolver();
        String locationPattern = "classpath*:com/example/demo";
        Resource[] classPathResources = bean.findClassPathResources(locationPattern);
        Arrays.stream(classPathResources).forEach(o -> System.out.println(o.getFilePath()));

    }
}
