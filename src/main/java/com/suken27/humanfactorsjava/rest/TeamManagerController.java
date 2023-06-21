package com.suken27.humanfactorsjava.rest;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.suken27.humanfactorsjava.model.TeamManager;
import com.suken27.humanfactorsjava.repository.TeamManagerRepository;
import com.suken27.humanfactorsjava.rest.dto.TeamManagerDto;

@RestController
public class TeamManagerController {
    
    @Autowired
    private TeamManagerRepository repository;

    @Autowired
    private ModelMapper modelMapper;

    TeamManagerController() {
        super();
    }

    @GetMapping("/teamManagers")
    public List<TeamManager> all() {
        return repository.findAll();
    }

    @PostMapping("/teamManagers")
    @ResponseStatus(HttpStatus.CREATED)
    public void createTeamManager(TeamManagerDto teamManagerDto) {
        // TODO: Map Dto to entity
        // TODO: Check that email and password are valid
        // TODO: Check that email doesn't exist in database
        // TODO: Add to repository
    }

    private TeamManagerDto convertToDto(TeamManager entity) {
        return modelMapper.map(entity, TeamManagerDto.class);
    }

    private TeamManager convertToEntity(TeamManagerDto dto) {
        return modelMapper.map(dto, TeamManager.class);
    }

}
