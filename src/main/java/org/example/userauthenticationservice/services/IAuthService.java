package org.example.userauthenticationservice.services;

import org.example.userauthenticationservice.exceptions.InvalidCredentialsException;
import org.example.userauthenticationservice.exceptions.UserAlreadyExistsException;
import org.example.userauthenticationservice.models.User;

public interface IAuthService {

    User signup(String email, String password) throws UserAlreadyExistsException;

    User login(String email, String password) throws InvalidCredentialsException;

    User logout(String email);
}
