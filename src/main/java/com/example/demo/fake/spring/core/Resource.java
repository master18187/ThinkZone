package com.example.demo.fake.spring.core;

import java.io.File;
import java.io.InputStream;

import lombok.Data;

@Data
public class Resource {
    
    private String name;

    private File file;

    private String filePath;

    private InputStream fileStream;

}
