package com.suken27.humanfactorsjava.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.suken27.humanfactorsjava.model.Team;

public interface TeamRepository extends JpaRepository<Team, Long> {

    @Query("Select t FROM Team t, TeamManager m WHERE t.manager = m AND m.email = ?1")
    public Team findByTeamManagerEmail(String email);

}
