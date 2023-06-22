package com.suken27.humanfactorsjava.rest.api;

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.suken27.humanfactorsjava.model.TeamManager;
import com.suken27.humanfactorsjava.repository.TeamManagerRepository;
import com.suken27.humanfactorsjava.rest.dto.AuthDto;
import com.suken27.humanfactorsjava.rest.exception.EmailInUseException;
import com.suken27.humanfactorsjava.rest.exception.IncorrectEmailFormatException;
import com.suken27.humanfactorsjava.rest.exception.IncorrectPasswordFormatException;

@RestController
public class AuthController {

    @Autowired
    private TeamManagerRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private static final String EMAIL_REGEX = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
    
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public TeamManager authenticateUser(@RequestBody AuthDto authDto) {
        return null;
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public TeamManager registerUser(@RequestBody AuthDto authDto) {
        if(authDto == null) {
            throw new IncorrectEmailFormatException(null);
        }
        logger.debug("Request to create a new TeamManager with email: {}, password {}", authDto.getEmail(), authDto.getPassword());
        if(!validateEmail(authDto.getEmail())) {
            throw new IncorrectEmailFormatException(authDto.getEmail());
        }
        if(!validatePassword(authDto.getPassword())) {
            throw new IncorrectPasswordFormatException();
        }
        if(repository.findByEmail(authDto.getEmail()) != null) {
            throw new EmailInUseException(authDto.getEmail());
        }
        TeamManager entity = new TeamManager();
        entity.setEmail(authDto.getEmail());
        entity.setPassword(passwordEncoder.encode(authDto.getPassword()));
        return repository.save(entity);
    }

    private boolean validateEmail(String email) {
        if(email == null) {
            return false;
        }
        return Pattern.compile(EMAIL_REGEX).matcher(email).matches();
    }

    private boolean validatePassword(String password) {
        if(password == null) {
            return false;
        }
        return password.length() > 5;
    }

}
