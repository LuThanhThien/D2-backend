package com.dainam.D2.annotation.dto.factory;

import com.dainam.D2.annotation.dto.Dto;
import com.dainam.D2.annotation.dto.mapper.DtoMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Slf4j
@Getter
public class DtoConverter<T, E> {

    private final Class<T> dtoClass;

    private final String basePackage;

    private Object DO;

    private Method mappingMethod;

    private Set<MapperContainer<T, E, ? extends DtoMapper<T, E>>> mapperContainers;

    public static final String DtoSuffix = "Dto";

    private static final HashMap<Class<?>, Method> mappingConfig = new HashMap<>();
    {
        try {
            // Populate the map with fixed class-method mappings
            mappingConfig.put(Class.class, DtoConverter.class.getDeclaredMethod("toDto", Object.class));
            mappingConfig.put(List.class, DtoConverter.class.getDeclaredMethod("toListDto", List.class));
            mappingConfig.put(Set.class, DtoConverter.class.getDeclaredMethod("toSetDto", Set.class));
        } catch (NoSuchMethodException e) {
            throw new InternalError("Method not found", e);
        }
    }

    public DtoConverter(Class<T> dtoClass, String basePackage) {
        if (dtoClass.getDeclaredAnnotation(Dto.class) == null)
            throw new NoSuchElementException("No such @Dto for " + dtoClass);
        this.basePackage = basePackage;
        this.dtoClass = dtoClass;
    }

    public DtoConverter(Class<T> dtoClass) {
        if (dtoClass.getDeclaredAnnotation(Dto.class) == null)
            throw new NoSuchElementException("No such @Dto for " + dtoClass);
        this.basePackage = "";
        this.dtoClass = dtoClass;
    }


    static public <T, E> DtoConverter<T, E> build(Class<T> dtoClass, String basePackage) {
        return new DtoConverter<>(dtoClass, basePackage);
    }

    @SuppressWarnings("unchecked")
    public <O, D> O map(D Do) {
        try {
            log.info("Start DTO mapping process...");
            if (Do == null) return null;
            if (DtoUtils.isEmptyDataClass(Do)) return (O) Do;

            // FILTER: Assign DO to dynamic parameter and get Data Class
            this.DO = Do;
            log.info("this.dtoClass: " + this.dtoClass);
            log.info("this.DO.getClass(): " + this.DO.getClass());

            // FILTER: Get correct mapping method
            this.mappingMethod = getMappingMethod();
            log.info("this.mappingMethod: " + this.mappingMethod);

            // Init mapper containers
            this.mapperContainers = new HashSet<>();

            // Invoke the mapping method
            O result = (O) this.mappingMethod.invoke(this, this.DO);


            return result;
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Method getMappingMethod() {
        Class<?> doObjectClass = this.DO.getClass();
        if (doObjectClass == DtoUtils.getFirstDataClass(this.DO)) return mappingConfig.get(Class.class);
        for (Class<?> mappingClazz: mappingConfig.keySet()) {
            if (mappingClazz.isAssignableFrom(doObjectClass)) {
                return mappingConfig.get(mappingClazz);
            }
        }
        throw new IllegalArgumentException("No such dto mapping service for converting " +
                DtoUtils.getFirstDataClass(this.DO) + " into " + this.dtoClass.getSimpleName());
    }

    @SuppressWarnings("unchecked")
    private T doMapping(E doItem) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        MapperContainer<T, E, ?> container = MapperContainer.findContainer(this.mapperContainers, doItem.getClass());
        if (container == null) {
            log.info("Mapper has not been initiated for " + doItem.getClass());
            container = (MapperContainer<T, E, ?>) new MapperContainer<>(this.dtoClass, doItem.getClass(), this.basePackage);
            this.mapperContainers.add(container);
        }
        return container.map(doItem);
    }

    @SuppressWarnings("unchecked")
    private T toDto(E DO) {
        try {
            return this.doMapping(DO);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.info("Invocation exception in toDto", e);
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private List<T> toListDto(List<E> listDo) {
        List<T> listDto = new ArrayList<T>();
        try {
            for (E doItem: listDo) listDto.add(this.doMapping(doItem));
            return listDto;
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.info("Invocation exception in toListDto", e);
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public Set<T> toSetDto(Set<E> setDo) throws NoSuchMethodException {
        Set<T> setDto = new LinkedHashSet<T>( Math.max( (int) ( setDo.size() / .75f ) + 1, 16 ) );
        try {
            for (E doItem: setDo) setDto.add(this.doMapping(doItem));
            return setDto;
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.info("Invocation exception in toSetDto", e);
            throw new RuntimeException(e);
        }
    }


}
