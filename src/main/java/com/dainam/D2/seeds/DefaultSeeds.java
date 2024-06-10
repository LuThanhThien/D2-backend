package com.dainam.D2.seeds;

import com.dainam.D2.models.warehouse.Item;
import com.dainam.D2.repository.warehouse.IITemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.dainam.D2.models.auth.Role;
import com.dainam.D2.models.user.User;
import com.dainam.D2.repository.user.IUserRepository;
import com.dainam.D2.service.user.UserService;

import java.util.Optional;

@Component
@Slf4j
public class DefaultSeeds {

    @Autowired
    private UserService userService;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IITemRepository iTemRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public DefaultSeeds(
            UserService userService,
            IUserRepository userRepository,
            IITemRepository iTemRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.iTemRepository = iTemRepository;
        this.passwordEncoder = passwordEncoder;
        createUserIfNotExist("admin@dainam.vn", Role.ADMIN);
        createUserIfNotExist("engineer@dainam.com", Role.ENGR);
        createItemIfNameNotExist("Cola", 1000, 20);
        createItemIfNameNotExist("Pepsi", 2400, 20);
        createItemIfNameNotExist("Wine", 500, 100);
    }

    private void createUserIfNotExist(String username, Role role) {
        Optional<User> existingAdmin = userRepository.findByUsername(username);
        if (existingAdmin.isEmpty()) {
            log.info("Create username " + username);
            String DEFAULT_PASSWORD = "password@123";
            User newAdmin = User.builder()
                    .username(username)
                    .password(passwordEncoder.encode(DEFAULT_PASSWORD))
                    .firstName("Firstname")
                    .lastName("Lastname")
                    .role(role)
                    .build();
            userRepository.save(newAdmin);
        } else {
            // change the role if role is different
            User user = existingAdmin.get();
            if (role.equals(user.getRole())) return;
            log.info("Change user {} role from {} to {}", username, user.getRole(), role);
            user.setRole(role);
            userRepository.save(user);
        }
    }

    private void createItemIfNameNotExist(String name, int quantity, long price) {
        Optional<Item> foundItem = iTemRepository.findByName(name);
        if (foundItem.isEmpty()) {
            log.info("Create item with name {}", name);
            Item newItem = Item.builder()
                    .name(name)
                    .quantity(quantity)
                    .price(price)
                    .build();
            iTemRepository.save(newItem);
        }
    }
}
