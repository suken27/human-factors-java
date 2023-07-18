package com.suken27.humanfactorsjava.rest.exception;

public class MemberAlreadyInTeamException extends RuntimeException {
    
    public MemberAlreadyInTeamException(String email) {
        super("The user with email '" + email + "' is already a member of the team.");
    }

}
