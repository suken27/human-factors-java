package com.suken27.humanfactorsjava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.suken27.humanfactorsjava.model.Team;
import com.suken27.humanfactorsjava.model.TeamManager;

public interface TeamRepository extends JpaRepository<Team, Long> {
    
    public Team findByTeamManager(TeamManager manager);

    @Query("Select t FFROM Team t JOIN t.manager m WHERE m.email = ?1")
    public Team findByTeamManagerEmail(String email);

}
