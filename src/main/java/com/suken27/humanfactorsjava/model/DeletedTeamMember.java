package com.suken27.humanfactorsjava.model;

import java.time.LocalDateTime;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

/**
 * Class that registers deleted team members.
 */
@Entity
// The email is no longer unique since a team member may be created and removed more than once.
@AttributeOverride(name = "email", column = @Column(unique = false))
public class DeletedTeamMember extends TeamMember {
    
    @Column(nullable = false)
    private LocalDateTime deletionTime;

    public DeletedTeamMember() {
        super();
        deletionTime = LocalDateTime.now();
    }

    public DeletedTeamMember(TeamMember teamMember) {
        super();
        this.setEmail(teamMember.getEmail());
        this.setTeam(teamMember.getTeam());
        deletionTime = LocalDateTime.now();
    }

    public LocalDateTime getDeletionTime() {
        return deletionTime;
    }

    public void setDeletionTime(LocalDateTime deletionTime) {
        this.deletionTime = deletionTime;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((deletionTime == null) ? 0 : deletionTime.hashCode());
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
        DeletedTeamMember other = (DeletedTeamMember) obj;
        if (deletionTime == null) {
            if (other.deletionTime != null)
                return false;
        } else if (!deletionTime.equals(other.deletionTime))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "DeletedTeamMember [deletionTime=" + deletionTime + "]";
    }

}
