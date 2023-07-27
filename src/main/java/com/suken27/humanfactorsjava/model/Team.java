package com.suken27.humanfactorsjava.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class Team {
    
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(mappedBy = "team")
    private TeamManager manager;

    @OneToMany(
        mappedBy = "team",
        cascade = CascadeType.ALL
    )
    private List<TeamMember> members;

    public Team() {
        super();
        members = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TeamManager getManager() {
        return manager;
    }

    public void setManager(TeamManager manager) {
        this.manager = manager;
    }

    public List<TeamMember> getMembers() {
        return members;
    }

    public void setMembers(List<TeamMember> members) {
        this.members = members;
    }

    public void addMember(TeamMember member) {
        members.add(member);
    }

    /**
     * Removes member from the team, marks it as a deleted team member, and sets the time of deletion.
     * @param member Member to remove from the team.
     */
    public void removeMember(TeamMember member) {
        members.remove(member);
        member.setDeleted(true);
        member.setDeletionTime(LocalDateTime.now());
        member.setTeam(null);
    }

    public boolean isMember(TeamMember member) {
        return members.contains(member);
    }

    public boolean isMember(String email) {
        for (TeamMember teamMember : members) {
            if(teamMember.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }

    

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Team other = (Team) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Team [id=" + id + ", manager=" + manager.getEmail() + ", members=" + members + "]";
    }

}
