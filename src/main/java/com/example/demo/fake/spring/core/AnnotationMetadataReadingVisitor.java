package com.example.demo.fake.spring.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import aj.org.objectweb.asm.AnnotationVisitor;
import aj.org.objectweb.asm.ClassVisitor;
import aj.org.objectweb.asm.FieldVisitor;
import aj.org.objectweb.asm.MethodVisitor;
import aj.org.objectweb.asm.Opcodes;

public class AnnotationMetadataReadingVisitor extends ClassVisitor {

    private String CLASS_SUFFIX = ".class";

    private String className = null;

    private Set<String> classAnnotationMap = new HashSet<>();

    private Map<String, Set<String>> fieldAnnotationMap = new HashMap<>();

    public AnnotationMetadataReadingVisitor() {
        super(Opcodes.ASM9);
    }

    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.className = name.replace("/", ".");
        super.visit(version, access, name, signature, superName, interfaces);
    }

    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        String classType = Type.getType(descriptor).getClassName();
        FieldMetadataReadingVisitor fieldMetadataReadingVisitor = new FieldMetadataReadingVisitor(name, classType,
                fieldAnnotationMap);
        return fieldMetadataReadingVisitor;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor,
            String signature, String[] exceptions) {
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        String annotationName = Type.getType(descriptor).getClassName();
        this.classAnnotationMap.add(annotationName);
        return super.visitAnnotation(descriptor, visible);
    }

    public boolean hasAnnotation(String annotationName) {
        return classAnnotationMap.contains(annotationName);
    }

    public boolean hasFiledAnnotation() {
        return !fieldAnnotationMap.isEmpty();
    }

    public Map<String, String> hasAnnotationFields(String annotationName) {
        Map<String, String> fieldMap = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : fieldAnnotationMap.entrySet()) {
            String filedInfo = entry.getKey();
            String[] infoArr = filedInfo.split("_");
            Set<String> fieldAnnotations = entry.getValue();
            if (fieldAnnotations.contains(annotationName)) {
                fieldMap.put(infoArr[0], infoArr[1]);
            }
        }
        return fieldMap;
    }

    public String getClassName() {
        return this.className;
    }

    class FieldMetadataReadingVisitor extends FieldVisitor {

        private String fieldName;

        private String fieldType;

        private Map<String, Set<String>> fieldAnnotationMap;

        public FieldMetadataReadingVisitor(String fieldName, String fieldType,
                Map<String, Set<String>> fieldAnnotationMap) {
            super(Opcodes.ASM9);
            this.fieldName = fieldName;
            this.fieldType = fieldType;
            this.fieldAnnotationMap = fieldAnnotationMap;
        }

        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            Set<String> annotations = fieldAnnotationMap.computeIfAbsent(fieldName + "_" + fieldType,
                    o -> new HashSet<>());
            String annotationName = Type.getType(descriptor).getClassName();
            annotations.add(annotationName);
            return new AnnotationVisitor(Opcodes.ASM9) {
                @Override
                public void visit(String name, Object value) {
                    System.out.println("Annotation element " + name + "=" + value);
                }
            };
        }

    }
}
