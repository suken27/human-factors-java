package com.suken27.humanfactorsjava.rest.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.suken27.humanfactorsjava.model.Team;
import com.suken27.humanfactorsjava.repository.TeamRepository;
import com.suken27.humanfactorsjava.rest.dto.TeamDto;

@RestController
public class TeamController {
    
    @Autowired
    private TeamRepository repository;

    @Autowired
    private static final Logger logger = LoggerFactory.getLogger(TeamController.class);

    @GetMapping("/teams")
    public List<Team> getTeams() {
        return repository.findAll();
    }

    @PostMapping("/teams")
    public void createTeam(TeamDto team) {
        repository.findByTeamManager(null)
    }

}
