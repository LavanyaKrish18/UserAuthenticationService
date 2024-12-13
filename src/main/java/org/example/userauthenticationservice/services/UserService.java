package org.example.userauthenticationservice.services;

import org.example.userauthenticationservice.models.User;
import org.example.userauthenticationservice.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {

    @Autowired
    private UserRepo userRepo;

    public User getUser(Long id){
        return userRepo.findById(id).get();
    }
}
