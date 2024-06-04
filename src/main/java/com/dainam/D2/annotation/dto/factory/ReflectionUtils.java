package com.dainam.D2.annotation.dto.factory;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Table;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Slf4j
@Component
public class ReflectionUtils {

    static public <T> Set<Class<? extends T>> getSubClasses(Class<T> superClass, String basePackage) {
        Reflections reflections = new Reflections(basePackage, new SubTypesScanner(false));
        return reflections.getSubTypesOf(superClass);
    }

    static public <T> Set<Class<? extends T>> getSubClasses(Class<T> superClass) {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forJavaClassPath())
                .setScanners(new SubTypesScanner(false))
        );
        return reflections.getSubTypesOf(superClass);
    }

    public static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null && clazz != Object.class) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    public static Method getMethod(Class<?> clazz, String name, Class<?>... args) throws NoSuchMethodException, NoClassDefFoundError {
        if (clazz == null)
            throw new NoClassDefFoundError("Cannot find method since class is null");
        Class<?> initClass = clazz;
        while(clazz != null && clazz != Object.class) {
            try {
                return clazz.getMethod(name, args);
            } catch (NoSuchMethodException e) {
                log.info("Method " + name + " is not in sub class: " + clazz + ". Find in super class " + clazz.getSuperclass());
                clazz = clazz.getSuperclass();
            }
        }
        throw new NoSuchMethodException("No such method " + name + " find in class " + initClass.getName() + " and its super classes");
    }

}
