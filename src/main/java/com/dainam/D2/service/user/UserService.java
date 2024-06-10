package com.dainam.D2.service.user;

import com.dainam.D2.dto.user.UserDto;
import com.dainam.D2.mapper.user.UserMapper;
import com.dainam.D2.models.auth.Role;
import com.dainam.D2.models.user.User;
import com.dainam.D2.models.user.UserProfile;
import com.dainam.D2.repository.user.IUserRepository;
import com.dainam.D2.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.security.sasl.AuthenticationException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService implements IUserService {


    @Autowired
    private final IUserRepository userRepository;


    // CREATE
    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User uploadDesignation(MultipartFile designation, String username) {
        User user = this.findByUsername(username);
        log.info("Current user is: {}", username);
        UserProfile userProfile = user.getProfile();
        if (userProfile == null) {
            user.setProfile(new UserProfile());
        }
        log.info("Current user profile: {}", user.getProfile());
        String currentDesignation = user.getProfile().getDesignation();
        try {
            user.getProfile().setDesignation(FileUtils.upload(designation));
            user = userRepository.save(user);
            if(currentDesignation != null && FileUtils.fileExist(currentDesignation)){
                FileUtils.delete(currentDesignation);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    // READ
    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User does not exist!"));
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return findByUsername(username);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User does not exist!"));
    }

    @Override
    public List<User> findByRole(Role role) {
        return userRepository.findByRole(role);
    }


    @Override
    public Object getCurrentPrinciple() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            log.info("Get current user authentication principle: " + authentication);
            return authentication.getPrincipal();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User getCurrentUser() {
        try {
            Object principle = this.getCurrentPrinciple();
            if (principle instanceof User) {
                return (User) principle;
            } else {
                throw new AuthenticationException("Authentication exception!");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Role getCurrentRole() {
        Object principle = this.getCurrentPrinciple();
        if (principle instanceof User) {
            return ((User) principle).getRole();
        } else {
            return Role.ANONYMOUS;
        }
    }

    @Override
    public boolean isAuthenticated() {
        Object principle = this.getCurrentPrinciple();
        return principle instanceof User;
    }

    // UPDATE
    @Override
    public User updateUserInformation(UserDto updateDto) {
        User user = this.findByUsername(updateDto.getUsername());
        UserMapper.INSTANCE.updateEntity(updateDto, user);
        log.info("Updated user data: " + user);
        return userRepository.save(user);
    }

    @Override
    public void checkExistByUsername(String username) {
        Boolean result = userRepository.existsByUsername(username);
        if (result) {
            throw new UsernameNotFoundException("User does not exist!");
        }
    }


}


