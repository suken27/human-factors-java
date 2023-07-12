package com.suken27.humanfactorsjava.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.suken27.humanfactorsjava.model.Team;
import com.suken27.humanfactorsjava.model.TeamManager;

public interface TeamRepository extends JpaRepository<Team, Long> {
    
    public Team findByTeamManager(TeamManager manager);

}
