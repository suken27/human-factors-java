package com.suken27.humanfactorsjava.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToOne;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class TeamMember extends User {

    @Column(nullable = false)
    private boolean deleted;

    private LocalDateTime deletionTime;

    @ManyToOne(fetch = FetchType.LAZY)
    private Team team;
    
    /**
     * This constructor should never be used. Use TeamMember(List<HumanFactor>) instead.
     * This constructor cannot be removed as hibernate uses the default constructor to instantiate entities.
     */
    public TeamMember() {
        super();
    }

    public TeamMember(List<HumanFactor> humanFactors) {
        super(humanFactors);
        deleted = false;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public LocalDateTime getDeletionTime() {
        return deletionTime;
    }

    public void setDeletionTime(LocalDateTime deletionTime) {
        this.deletionTime = deletionTime;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (deleted ? 1231 : 1237);
        result = prime * result + ((deletionTime == null) ? 0 : deletionTime.hashCode());
        result = prime * result + ((team == null) ? 0 : team.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        TeamMember other = (TeamMember) obj;
        if (deleted != other.deleted)
            return false;
        if (deletionTime == null) {
            if (other.deletionTime != null)
                return false;
        } else if (!deletionTime.equals(other.deletionTime))
            return false;
        if (team == null) {
            if (other.team != null)
                return false;
        } else if (!team.equals(other.team))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "TeamMember [deleted=" + deleted + ", deletionTime=" + deletionTime + ", team=" + team + "]";
    }
    
}
