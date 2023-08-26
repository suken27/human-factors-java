package com.suken27.humanfactorsjava.model.dto;

import com.suken27.humanfactorsjava.model.User;

import lombok.Data;

@Data
public abstract class UserDto {
    
    private Long id;
    private String email;
    private String slackId;

    protected UserDto() {
        super();
    }

    protected UserDto(User entity) {
        setId(entity.getId());
        setEmail(entity.getEmail());
    }

}
