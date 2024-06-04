package com.dainam.D2.annotation.dto.factory;

import com.dainam.D2.annotation.dto.Dto;
import com.dainam.D2.annotation.dto.mapper.DtoMapper;
import com.dainam.D2.utils.StringUtils;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AbstractTypeHierarchyTraversingFilter;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Component
@Slf4j
public class DtoUtils {

    public static boolean isAnnotatedDto(Class<?> clazz) {
        Dto dtoAnnotation = clazz.getAnnotation(Dto.class);
        return dtoAnnotation != null;
    }

    public static boolean isAnnotatedForward(Field field) {
        Dto.Forward dtoForwardAnnotation = field.getAnnotation(Dto.Forward.class);
        return dtoForwardAnnotation != null;
    }

    @SuppressWarnings("unchecked")
    public static <T, E> Class<E> getMappedClass(Class<T> dtoClass) {
        Dto dtoAnnotation = dtoClass.getAnnotation(Dto.class);
        if (dtoAnnotation == null) {
            throw new NoSuchElementException("Class +" + dtoClass + " is not annotated with @Dto");
        }
        return (Class<E>) dtoAnnotation.mappedClass();
    }

    @SuppressWarnings("unchecked")
    public static <T, E, M extends DtoMapper<T, E>> Class<M> getMapperClass(Class<T> dtoClass) {
        Dto dtoAnnotation = dtoClass.getAnnotation(Dto.class);
        if (dtoAnnotation == null) {
            throw new NoSuchElementException("Class +" + dtoClass + " is not annotated with @Dto");
        }
        return (Class<M>) dtoAnnotation.mapper();
    }

    @SuppressWarnings("unchecked")
    public static  <T> T getDtoBySimpleName(String simpleName, String basePackage) {
        try {
            log.info("Get dto by simple name with base package: " + basePackage);
            if (basePackage.isEmpty())
                throw new ValidationException("Base package cannot be empty");
            String className = StringUtils.toPascalCase(simpleName, "_") + DtoConverter.DtoSuffix;
            ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false) {
                    @Override
                    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                        boolean isCandidate = false;
                        if (beanDefinition.getMetadata().isIndependent()) {
                            if (!beanDefinition.getMetadata().isAnnotation() &&
                                    !beanDefinition.getMetadata().isInterface()) {
                                isCandidate = true;
                            } else if (beanDefinition.getMetadata().isAbstract()) {
                                isCandidate = true;
                            }
                        }
                        return isCandidate;
                    }
                };
            scanner.addIncludeFilter(new AnnotationTypeFilter(Dto.class));
            scanner.addIncludeFilter(new AbstractTypeHierarchyTraversingFilter(false, false) {
                @Override
                protected boolean matchClassName(@NotNull String className) {
                    return true;
                }
            });
            for (BeanDefinition bd : scanner.findCandidateComponents(basePackage)) {
                String beanClassName = bd.getBeanClassName();
                log.info("Bean class name check: " + beanClassName);
                if (beanClassName == null) continue;
                String beanSimpleName = beanClassName.substring(beanClassName.lastIndexOf(".") + 1);
                if (beanSimpleName.equals(className)) {
                    return (T) Class.forName(beanClassName);
                }
            }
            return null;
        }
        catch (Exception e) {
            log.info("Error while get class from simpleName: " + simpleName);
            return null;
        }
    }


    static public Class<?> getFirstDataClass(Object DO) {
        if (Collection.class.isAssignableFrom(DO.getClass())) {
            if (((Collection<?>) DO).isEmpty()) {
                log.info("No such data");
                return null;
            } else {
                for (Object item : (Collection<?>) DO) {
                    return item.getClass();
                }
            }
        }
        return DO.getClass();
    }

    static public boolean isContainsDataClass(Object DO) {
        return getFirstDataClass(DO) != null;
    }

    static public boolean isEmptyDataClass(Object DO) {
        return getFirstDataClass(DO) == null;
    }

    static public Set<Class<?>> getDataClasses(Collection<Object> DO) {
        if (DO == null) return null;
        if (getFirstDataClass(DO) == null) return null;
        Set<Class<?>> setDataClasses = new HashSet<>();
        for (Object object: DO) {
            setDataClasses.add(getFirstDataClass(object));
        }
        return setDataClasses;
    }

    static public boolean isMapperMethod(Method method, Object DO) {
        return (
                method.getParameterCount() == 1 &&
                method.getParameterTypes()[0].isAssignableFrom(DO.getClass()) &&
                method.getGenericParameterTypes()[0].toString().contains(getFirstDataClass(DO).getName())
                );
    }

    static public boolean isUnitMapperMethod(Method method, Object DO) {
        return (
                method.getParameterCount() == 1 &&
                method.getGenericParameterTypes()[0].equals(getFirstDataClass(DO))
                );
    }
    static public boolean isUnitMapperMethod(Method method, Class<?> genericType) {
        return (
                method.getParameterCount() == 1 &&
                        method.getGenericParameterTypes()[0].equals(genericType)
        );
    }

    static public Method getGetter(String field, Class<?> clazz) throws NoSuchMethodException, SecurityException {
        // Find Getter if exist
        String getterName = "get" + StringUtils.toPascalCase(field, "");
        Method getterMethod = null;
        getterMethod = ReflectionUtils.getMethod(clazz, getterName);
        getterMethod.setAccessible(true);
//        log.info("Getter name: " + getterName);
        return getterMethod;
    }

    static public Method getSetter(String field, Class<?> clazz, Class<?>... args) throws NoSuchMethodException, SecurityException {
        // Find Getter if exist
        String setterName = "set" + StringUtils.toPascalCase(field, "");
        Method setterMethod;
        setterMethod = ReflectionUtils.getMethod(clazz, setterName, args);
        setterMethod.setAccessible(true);
//        log.info("Setter name: " + setterName);
        return setterMethod;
    }

    static public Object getObject(Method getterMethod, Object backDo) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        // Get value by invoking getter
        Object resultObj = null;
        resultObj = getterMethod.invoke(backDo);
        return resultObj;
    }

    static public void setObject(Method setterMethod, Object object, Object value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        // Set value by invoking setter
        setterMethod.invoke(object, value);
    }


}
