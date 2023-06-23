package com.suken27.humanfactorsjava.rest.api;

import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.suken27.humanfactorsjava.model.TeamManager;
import com.suken27.humanfactorsjava.repository.TeamManagerRepository;
import com.suken27.humanfactorsjava.rest.dto.AuthDto;
import com.suken27.humanfactorsjava.rest.dto.JwtResponseDto;
import com.suken27.humanfactorsjava.rest.exception.IncorrectEmailFormatException;
import com.suken27.humanfactorsjava.rest.exception.IncorrectPasswordFormatException;
import com.suken27.humanfactorsjava.security.JwtUtils;

@RestController
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TeamManagerRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private static final String EMAIL_REGEX = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
    
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Validated @RequestBody AuthDto authDto) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDto.getEmail(), authDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String role = userDetails.getAuthorities().stream().map(item -> item.getAuthority()).collect(Collectors.toList()).get(0);

        return ResponseEntity.ok(new JwtResponseDto(jwt, userDetails.getUsername(), role));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Validated @RequestBody AuthDto authDto) {
        if(authDto == null) {
            throw new IncorrectEmailFormatException(null);
        }
        logger.debug("Request to create a new TeamManager with email: {}, password {}", authDto.getEmail(), authDto.getPassword());
        if(!validateEmail(authDto.getEmail())) {
            return ResponseEntity.badRequest().body(new IncorrectEmailFormatException(null));
        }
        if(!validatePassword(authDto.getPassword())) {
            return ResponseEntity.badRequest().body(new IncorrectPasswordFormatException());
        }
        if(repository.findByEmail(authDto.getEmail()) != null) {
            return ResponseEntity.badRequest().body(new IncorrectEmailFormatException(authDto.getEmail()));
        }
        TeamManager entity = new TeamManager();
        entity.setEmail(authDto.getEmail());
        entity.setPassword(passwordEncoder.encode(authDto.getPassword()));
        repository.save(entity);
        return ResponseEntity.ok("User registered successfully.");
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
