package com.dainam.D2.annotation.dto.factory;

import com.dainam.D2.annotation.dto.Dto;
import com.dainam.D2.annotation.dto.mapper.DtoMapper;
import com.dainam.D2.annotation.dto.mapper.VoidMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
@Slf4j
@RequiredArgsConstructor
public class MapperContainer<T, E, M extends DtoMapper<T, E>> {

    private final String basePackage;

    private Class<T> dtoClass;

    private final Class<E> doClass;

    private final Class<M> mapper;

    private HashMap<String, Method> getterMap = new HashMap<>();

    private HashMap<String, Method> setterMap = new HashMap<>();

    public MapperContainer(Class<T> dtoClass, Class<E> doClass, String basePackage) {
        this.dtoClass = dtoClass;
        this.doClass = doClass;
        this.basePackage = basePackage;
        this.mapper = findMapper();
        this.getterMap = new HashMap<>();
        this.setterMap = new HashMap<>();
    }


    @SuppressWarnings("unchecked")
    private Class<M> findMapper() {
        Class<M> mapperClass = DtoUtils.getMapperClass(this.dtoClass);
        Class<E> mappedClass = DtoUtils.getMappedClass(this.dtoClass);
        // FILTER: Check if mapperClass is Void.class
        if (mapperClass == VoidMapper.class || mappedClass != this.doClass) {
            // LOOP through each subclasses to seek for the proper mapper
            for (Class<? extends T> subClass: ReflectionUtils.getSubClasses(this.dtoClass, this.basePackage)) {
                // FILTER: Check if annotated with @Dto
                Dto dtoAnnotation = subClass.getDeclaredAnnotation(Dto.class);
                if (dtoAnnotation == null) continue;

                // FILTER: Check if subMappedClass is not Void.class
                Class<E> subMappedClass = DtoUtils.getMappedClass(subClass);
                if (subMappedClass == Void.class) continue;

                // FILTER: Check if subMappedClass match with the doClass
                if (subMappedClass.equals(this.doClass)) {

                    // FILTER: If matched, then get the mapper
                    Class<M> subMapperClass = (Class<M>) DtoUtils.getMapperClass(subClass);
                    if (subMapperClass == VoidMapper.class) continue;

                    // FILTER: If mapper is not Void.class, put to mappingConfig and break loop
                    mapperClass = subMapperClass;
                    this.dtoClass = (Class<T>) subClass;
                    break;
                }
            }
        }
        if (mapperClass == VoidMapper.class) {
            throw new NoSuchElementException("No such mapper is given for converting " + this.doClass + " to " + this.dtoClass);
        }
        try {
            return mapperClass;
        } catch (Exception e) {
            throw new RuntimeException("Error while getting mapper from mapper class " + mapperClass, e);
        }
    }

    public T map(E object) {
        T result = Mappers.getMapper(mapper).toDto(object);
        return forward(result, object);
    }


    private T forward(T backResult, E backDo) {
        if (backResult == null) {
            log.info("Not forward mapping since back-result is null");
            return null;
        }

        List<Field> fields = ReflectionUtils.getAllFields(this.dtoClass);
//        log.info("Number of forward field: "  + fields.size());
        for (Field field: fields) {
            // Filter: check mapped field forward
            if (!DtoUtils.isAnnotatedForward(field)) {
                continue;
            }

            // Filter: Find getter and get mapped value
            Dto.Forward forwardAnnotation = field.getAnnotation(Dto.Forward.class);
            Object forwardDoValue = null;
            try {
                String mappedFieldGetterKey = forwardAnnotation.mappedField();
                String itemGetterKey = forwardAnnotation.mappedField() + "." + forwardAnnotation.mappedBy();
                Method mappedFieldGetter = this.getAndPutGetter(forwardAnnotation.mappedField(), mappedFieldGetterKey, doClass);
                Object forwardDo = DtoUtils.getObject(mappedFieldGetter, backDo);
                Class<?> forwardDoClass = forwardDo.getClass();
                if (Collection.class.isAssignableFrom(forwardDoClass)) {
                    forwardDoValue = ((Collection<?>) forwardDo).stream().map(
                        item -> {
                            try {
                                Class<?> forwardItemClass = DtoUtils.getFirstDataClass(item);
                                Method itemGetter = this.getAndPutGetter(forwardAnnotation.mappedBy(), itemGetterKey, forwardItemClass);
                                return DtoUtils.getObject(itemGetter, item);
                            } catch (Exception e) {
                                log.info(String.format("Cannot get forward DO value from %s", forwardAnnotation.mappedField()));
                                return null;
                            }
                        }
                    );
                    if (Set.class.isAssignableFrom(forwardDoClass)) {
                        forwardDoValue = ((Stream<?>) forwardDoValue).collect(Collectors.toSet());
                    } else {
                        forwardDoValue = ((Stream<?>) forwardDoValue).collect(Collectors.toList());
                    }
                } else {
                    Class<?> forwardDataClass = DtoUtils.getFirstDataClass(forwardDo);
                    Method valueGetter = this.getAndPutGetter(forwardAnnotation.mappedBy(), itemGetterKey, forwardDataClass);
                    forwardDoValue = DtoUtils.getObject(valueGetter, forwardDo);
                }
            } catch (Exception e) {
                log.info("Exception occurs while get mapped value for field " + field.getName(), e);
                continue;
            }

            // Filter: find Setter and set value
            try {
                Method fieldSetter = this.getAndPutSetter(field.getName(), field.getName(), dtoClass, field.getType());
                DtoUtils.setObject(fieldSetter, backResult, forwardDoValue);
            } catch (Exception e) {
                log.info(String.format("Cannot set forward DO value for %s", field.getName()));
            }
        }
        return backResult;
    }


    private Method getAndPutGetter(String name, String key, Class<?> clazz) throws NoSuchMethodException {
        Method getter = this.getterMap.getOrDefault(key, null);
        if (getter == null) {
            getter = DtoUtils.getGetter(name, clazz);
            log.info("Put new getter: " + key);
            this.getterMap.put(key, getter);
        }
        return getter;
    }


    private Method getAndPutSetter(String name, String key, Class<?> clazz, Class<?>... args) throws NoSuchMethodException {
        Method setter = this.setterMap.getOrDefault(key, null);
        if (setter == null) {
            setter = DtoUtils.getSetter(name, clazz, args);
            log.info("Put new setter: " + key);
            this.setterMap.put(key, setter);
        }
        return setter;
    }


    static public <T, E> MapperContainer<T, E, ? extends DtoMapper<T, E>> findContainer(
            Set<MapperContainer<T, E, ? extends DtoMapper<T, E>>> mapperContainers,
            Class<?> doClass)
    {
        for (MapperContainer<T, E, ? extends DtoMapper<T, E>> container: mapperContainers) {
            if (container.getDoClass().equals(doClass)) return container;
        }
        return null;
    }


}

