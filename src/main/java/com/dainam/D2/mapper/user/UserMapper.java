package com.dainam.D2.mapper.user;

import com.dainam.D2.annotation.dto.mapper.DtoMapper;
import com.dainam.D2.dto.user.UserDto;
import com.dainam.D2.models.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper extends DtoMapper<UserDto, User> {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

}
