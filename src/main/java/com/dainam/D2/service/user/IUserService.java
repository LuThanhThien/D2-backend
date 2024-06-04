package com.dainam.D2.service.user;

import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.dainam.D2.dto.user.UserDto;
import com.dainam.D2.models.auth.Role;
import com.dainam.D2.models.user.User;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public interface IUserService extends UserDetailsService {

    // CREATE
    User save(User user);

    // READ
    List<User> findAll();

    User findById(long id);

    User loadUserByUsername(String username) throws UsernameNotFoundException;

    List<User> findByRole(Role role);

    Object getCurrentPrinciple();

    User getCurrentUser();

    Role getCurrentRole();

    boolean isAuthenticated();

    // UPDATE
    User updateUserInformation(UserDto updateDto);

    // CHECK
    void checkExistByUsername(String username);

}
