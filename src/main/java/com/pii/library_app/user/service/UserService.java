package com.pii.library_app.user.service;

import com.pii.library_app.auth.exception.UsernameAlreadyExistsException;
import com.pii.library_app.user.exception.UserNotFoundException;
import com.pii.library_app.user.model.User;
import com.pii.library_app.user.repo.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService(
            UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
    }

    public User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }

    @Transactional
    public void createUser(User user) {
        if (existsByUsername(user.getUsername())) {
            throw new UsernameAlreadyExistsException("Имя пользователя '" + user.getUsername() + "' занято");
        }
        userRepository.save(user);
        LOG.info("Пользователь {} успешно зарегистрирован", user.getUsername());
    }
}
