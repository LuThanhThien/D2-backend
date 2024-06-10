package com.dainam.D2.dto.user;

import com.dainam.D2.annotation.dto.Dto;
import com.dainam.D2.mapper.user.UserMapper;
import com.dainam.D2.models.auth.Role;
import com.dainam.D2.models.global.DataStatus;
import com.dainam.D2.models.user.User;
import com.dainam.D2.models.user.UserProfile;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;


@RequiredArgsConstructor
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@Dto(mapper = UserMapper.class, mappedClass = User.class)
public class UserDto {

    @NotNull
    private Long id;

    @NotEmpty
    private String username;

    private LocalDate dob;

    private String phoneNumber;

    private String firstName;

    private String lastName;

    private UserProfile profile;

}
