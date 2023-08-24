package com.suken27.humanfactorsjava.model.exception;

public class TeamManagerNotFoundException extends RuntimeException {
    
    public TeamManagerNotFoundException(String email) {
        super("The email '" + email + "' does not belong to a registered team manager.");
    }

}
