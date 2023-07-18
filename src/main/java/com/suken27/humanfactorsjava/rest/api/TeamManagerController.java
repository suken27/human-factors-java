package com.suken27.humanfactorsjava.rest.api;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

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
import com.suken27.humanfactorsjava.model.Team;
import com.suken27.humanfactorsjava.model.TeamManager;
import com.suken27.humanfactorsjava.repository.TeamManagerRepository;
import com.suken27.humanfactorsjava.rest.dto.TeamManagerDto;
import com.suken27.humanfactorsjava.rest.exception.EmailInUseException;
import com.suken27.humanfactorsjava.rest.exception.IncorrectEmailFormatException;
import com.suken27.humanfactorsjava.rest.exception.IncorrectPasswordFormatException;
import com.suken27.humanfactorsjava.rest.exception.TeamManagerNotFoundException;
import com.suken27.humanfactorsjava.rest.util.ApiValidator;

@RestController
public class TeamManagerController {
    
    @Autowired
    private TeamManagerRepository repository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ApiValidator validator;

    @Autowired
    private static final Logger logger = LoggerFactory.getLogger(TeamManagerController.class);

    TeamManagerController() {
        super();
    }

    @GetMapping("/teamManagers")
    public List<TeamManagerDto> all() {
        List<TeamManager> allTeamManagers = repository.findAll();
        List<TeamManagerDto> dtos = new LinkedList<>();
        for (TeamManager teamManager : allTeamManagers) {
            dtos.add(convertToDto(teamManager));
        }
        return dtos;
    }

    @GetMapping("/teamManagers/{id}")
    public TeamManagerDto one(@PathVariable Long id) {
        logger.debug("Request to retreive a TeamManager with id '{}'", id);
        Optional<TeamManager> optionalEntity = repository.findById(id);
        if(!optionalEntity.isPresent()) {
            throw new TeamManagerNotFoundException(id);
        }
        return convertToDto(optionalEntity.get());
    }

    @PostMapping("/teamManagers")
    @ResponseStatus(HttpStatus.CREATED)
    public TeamManager createTeamManager(@RequestBody TeamManagerDto teamManagerDto) {
        logger.debug("Request to create a new TeamManager with data: {}", teamManagerDto);
        if(!validator.isValidEmail(teamManagerDto.getEmail())) {
            throw new IncorrectEmailFormatException(teamManagerDto.getEmail());
        }
        if(!validator.isValidPassword(teamManagerDto.getPassword())) {
            throw new IncorrectPasswordFormatException();
        }
        if(repository.findByEmail(teamManagerDto.getEmail()) != null) {
            throw new EmailInUseException(teamManagerDto.getEmail());
        }
        teamManagerDto.setId(null);
        teamManagerDto.setPassword(passwordEncoder.encode(teamManagerDto.getPassword()));
        TeamManager entity = convertToEntity(teamManagerDto);
        entity.setTeam(new Team());
        entity.setRole(Role.USER);
        return repository.save(entity);
    }

    private TeamManagerDto convertToDto(TeamManager entity) {
        return modelMapper.map(entity, TeamManagerDto.class);
    }

    private TeamManager convertToEntity(TeamManagerDto dto) {
        return modelMapper.map(dto, TeamManager.class);
    }

    

    

}
