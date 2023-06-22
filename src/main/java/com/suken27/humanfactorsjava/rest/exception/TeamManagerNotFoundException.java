package com.suken27.humanfactorsjava.rest.exception;

public class TeamManagerNotFoundException extends RuntimeException {

    public TeamManagerNotFoundException(Long id) {
        super("Team Manager with id '" + id + "' not found");
    }
    
}
