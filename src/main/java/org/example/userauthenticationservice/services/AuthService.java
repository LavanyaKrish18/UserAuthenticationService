package org.example.userauthenticationservice.services;

import org.example.userauthenticationservice.exceptions.UserAlreadyExistsException;
import org.example.userauthenticationservice.models.State;
import org.example.userauthenticationservice.models.User;
import org.example.userauthenticationservice.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService implements IAuthService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public User signup(String email, String password) throws UserAlreadyExistsException {
        Optional<User> userOptional = userRepo.findUserByEmail(email);
        if (userOptional.isPresent()) {
            throw new UserAlreadyExistsException("Email already Registered !!");
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        user.setState(State.ACTIVE);
        userRepo.save(user);
        return user;
    }

    @Override
    public User login(String email, String password) {
        return null;
    }

    @Override
    public User logout(String email) {
        return null;
    }
}
