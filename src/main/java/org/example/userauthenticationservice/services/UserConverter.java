package org.example.userauthenticationservice.services;

import org.example.userauthenticationservice.dtos.UserDto;
import org.example.userauthenticationservice.models.User;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {

    public UserDto getUserDto(User user){
        UserDto userDto = new UserDto();
        userDto.setEmail(user.getEmail());
        if (user.getRoles() != null) {
            userDto.setRoles(user.getRoles());
        }
        return userDto;
    }
}
