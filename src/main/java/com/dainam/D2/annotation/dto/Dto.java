package com.dainam.D2.annotation.dto;

import com.dainam.D2.annotation.dto.mapper.DtoMapper;
import com.dainam.D2.annotation.dto.mapper.VoidMapper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Dto {
    Class<?> mappedClass() default Void.class;
    Class<? extends DtoMapper<?, ?>> mapper() default VoidMapper.class;

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface Forward{
        String mappedField();

        String mappedBy() default "id";
    }

}
