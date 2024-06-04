package com.dainam.D2.dto;

import com.dainam.D2.D2Application;
import com.dainam.D2.annotation.dto.factory.DtoConverter;

public class DtoProvider {

    static public <T> DtoConverter<T, ?> build(Class<T> dtoClass) {
        return DtoConverter.build(dtoClass, D2Application.BASE_PACKAGE_NAME);
    }
}
