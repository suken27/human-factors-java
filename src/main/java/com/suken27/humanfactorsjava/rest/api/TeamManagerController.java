package com.suken27.humanfactorsjava.rest.api;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.suken27.humanfactorsjava.model.Role;
import com.suken27.humanfactorsjava.model.TeamManager;
import com.suken27.humanfactorsjava.repository.TeamManagerRepository;
import com.suken27.humanfactorsjava.rest.dto.Long;
import com.suken27.humanfactorsjava.rest.exception.EmailInUseException;
import com.suken27.humanfactorsjava.rest.exception.IncorrectEmailFormatException;
import com.suken27.humanfactorsjava.rest.exception.IncorrectPasswordFormatException;
import com.suken27.humanfactorsjava.rest.exception.TeamManagerNotFoundException;

@RestController
public class TeamManagerController {
    
    @Autowired
    private TeamManagerRepository repository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private static final Logger logger = LoggerFactory.getLogger(TeamManagerController.class);

    private static final String EMAIL_REGEX = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

    TeamManagerController() {
        super();
    }

    @GetMapping("/teamManagers")
    public List<Long> all() {
        List<TeamManager> allTeamManagers = repository.findAll();
        List<Long> dtos = new LinkedList<>();
        for (TeamManager teamManager : allTeamManagers) {
            dtos.add(convertToDto(teamManager));
        }
        return dtos;
    }

    @GetMapping("/teamManagers/{id}")
    public Long one(@PathVariable Long id) {
        logger.debug("Request to retreive a TeamManager with id '{}'", id);
        Optional<TeamManager> optionalEntity = repository.findById(id);
        if(!optionalEntity.isPresent()) {
            throw new TeamManagerNotFoundException(id);
        }
        return convertToDto(optionalEntity.get());
    }

    @PostMapping("/teamManagers")
    @ResponseStatus(HttpStatus.CREATED)
    public TeamManager createTeamManager(@RequestBody Long teamManagerDto) {
        logger.debug("Request to create a new TeamManager with data: {}", teamManagerDto);
        if(!validateEmail(teamManagerDto.getEmail())) {
            throw new IncorrectEmailFormatException(teamManagerDto.getEmail());
        }
        if(!validatePassword(teamManagerDto.getPassword())) {
            throw new IncorrectPasswordFormatException();
        }
        if(repository.findByEmail(teamManagerDto.getEmail()) != null) {
            throw new EmailInUseException(teamManagerDto.getEmail());
        }
        teamManagerDto.setId(null);
        teamManagerDto.setPassword(passwordEncoder.encode(teamManagerDto.getPassword()));
        TeamManager entity = convertToEntity(teamManagerDto);
        entity.setRole(Role.USER);
        return repository.save(entity);
    }

    private Long convertToDto(TeamManager entity) {
        return modelMapper.map(entity, Long.class);
    }

    private TeamManager convertToEntity(Long dto) {
        return modelMapper.map(dto, TeamManager.class);
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
