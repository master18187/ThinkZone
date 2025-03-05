package com.example.demo.fake.spring.core;

import java.util.HashSet;
import java.util.Set;

import aj.org.objectweb.asm.AnnotationVisitor;
import aj.org.objectweb.asm.ClassVisitor;
import aj.org.objectweb.asm.MethodVisitor;
import aj.org.objectweb.asm.Opcodes;

public class AnnotationMetadataReadingVisitor extends ClassVisitor {

    private String CLASS_SUFFIX = ".class";

    private String className = null;

    private Set<String> annotationMap = new HashSet<>();

    public AnnotationMetadataReadingVisitor() {
        super(Opcodes.ASM9);
    }

    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.className = name.replace("/", ".");
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor,
            String signature, String[] exceptions) {
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        String annotationName = Type.getType(descriptor).getClassName();
        this.annotationMap.add(annotationName);
        return super.visitAnnotation(descriptor, visible);
    }

    public boolean hasAnnotation(String annotationName) {
        return annotationMap.contains(annotationName);
    }

    public String getClassName() {
        return this.className;
    }

}
