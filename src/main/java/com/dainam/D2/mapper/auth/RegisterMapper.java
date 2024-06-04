package com.dainam.D2.mapper.auth;

import com.dainam.D2.annotation.dto.mapper.DtoMapper;
import com.dainam.D2.dto.auth.RegisterRequest;
import com.dainam.D2.models.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface RegisterMapper extends DtoMapper<RegisterRequest, User> {
    RegisterMapper INSTANCE = Mappers.getMapper(RegisterMapper.class);
}
