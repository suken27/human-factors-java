package com.suken27.humanfactorsjava.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;

@Entity
public class TeamMember extends User {

    @ManyToOne(fetch = FetchType.LAZY)
    private Team team;
    
    public TeamMember() {
        super();
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

}
