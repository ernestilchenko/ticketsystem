package com.example.ticketmaster.mapper;

import com.example.ticketmaster.dto.UserDto;
import com.example.ticketmaster.dto.UserRegistrationDto;
import com.example.ticketmaster.entity.User;

import java.util.Set;
import java.util.stream.Collectors;

public class UserMapper {

    public static User toEntity(UserRegistrationDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        return user;
    }

    public static UserDto toDto(User user) {
        if (user == null) {
            return null;
        }

        Set<String> roleNames = user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toSet());

        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                roleNames,
                user.isEnabled(),
                user.getCreatedAt()
        );
    }

    public static User toEntity(UserDto dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEnabled(dto.isEnabled());
        user.setCreatedAt(dto.getCreatedAt());
        return user;
    }
}