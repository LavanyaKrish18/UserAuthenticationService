package org.example.userauthenticationservice.controllers;

import org.antlr.v4.runtime.misc.Pair;
import org.example.userauthenticationservice.dtos.*;
import org.example.userauthenticationservice.exceptions.InvalidCredentialsException;
import org.example.userauthenticationservice.exceptions.UserAlreadyExistsException;
import org.example.userauthenticationservice.models.User;
import org.example.userauthenticationservice.services.IAuthService;
import org.example.userauthenticationservice.services.UserConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private IAuthService authService;

    @Autowired
    private UserConverter userConverter;

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signup(@RequestBody SignupRequestDto signupRequestDto) {
        if (signupRequestDto.getEmail() == null || signupRequestDto.getPassword() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        User user = null;
        try {
            user = authService.signup(signupRequestDto.getEmail(), signupRequestDto.getPassword());
        } catch (UserAlreadyExistsException e) {
            throw new RuntimeException(e);
        }
        UserDto userDto = userConverter.getUserDto(user);
        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        if (loginRequestDto.getEmail() == null || loginRequestDto.getPassword() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            Pair<User, MultiValueMap<String, String>> userWithHeaders = authService.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());
            if (userWithHeaders.a == null){
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            UserDto userDto = userConverter.getUserDto(userWithHeaders.a);
            return new ResponseEntity<>(userDto, userWithHeaders.b, HttpStatus.OK);
        } catch (InvalidCredentialsException e) {
            throw new RuntimeException(e);
        }

    }

    @PostMapping("/logout")
    public ResponseEntity<UserDto> logout(@RequestBody LogoutRequestDto logoutRequestDto) {
        //TODO : Implement after Backend Projects: Authentication - 4: Implementng OAuth 2 class
        return null;
    }

    @PostMapping("/validateToken")
    public Boolean validateToken(@RequestBody ValidateTokenDto validateTokenDto) {
        Boolean result = authService.validateToken(validateTokenDto.getToken(), validateTokenDto.getUserId());
        if (!result) {
            throw new RuntimeException("Invalid token, Please login again");
        }
        return result;
    }

}
