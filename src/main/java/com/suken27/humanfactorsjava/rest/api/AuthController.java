package com.suken27.humanfactorsjava.rest.api;

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

import com.suken27.humanfactorsjava.model.HumanFactorFactory;
import com.suken27.humanfactorsjava.model.TeamManager;
import com.suken27.humanfactorsjava.repository.TeamManagerRepository;
import com.suken27.humanfactorsjava.rest.dto.AuthDto;
import com.suken27.humanfactorsjava.rest.dto.JwtResponseDto;
import com.suken27.humanfactorsjava.rest.exception.IncorrectEmailFormatException;
import com.suken27.humanfactorsjava.rest.exception.IncorrectPasswordFormatException;
import com.suken27.humanfactorsjava.rest.util.ApiValidator;
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
    private ApiValidator validator;

    @Autowired
    private HumanFactorFactory humanFactorFactory;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Validated @RequestBody AuthDto authDto) {
        if(authDto == null || authDto.getEmail() == null || authDto.getPassword() == null) {
            return ResponseEntity.badRequest().body("Empty email or password.");
        }
        TeamManager user = repository.findByEmail(authDto.getEmail());
        if(user == null || !passwordEncoder.matches(authDto.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("User not found, or password does not match.");
        }

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
            return ResponseEntity.badRequest().body(new IncorrectEmailFormatException(null));
        }
        if(!validator.isValidEmail(authDto.getEmail())) {
            return ResponseEntity.badRequest().body(new IncorrectEmailFormatException(null));
        }
        if(!validator.isValidPassword(authDto.getPassword())) {
            return ResponseEntity.badRequest().body(new IncorrectPasswordFormatException());
        }
        if(repository.findByEmail(authDto.getEmail()) != null) {
            return ResponseEntity.badRequest().body(new IncorrectEmailFormatException(authDto.getEmail()));
        }
        TeamManager entity = new TeamManager(humanFactorFactory.createInstances());
        entity.setEmail(authDto.getEmail());
        entity.setPassword(passwordEncoder.encode(authDto.getPassword()));
        repository.save(entity);
        return ResponseEntity.ok("User registered successfully.");
    }

}
