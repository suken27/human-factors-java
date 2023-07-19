package com.suken27.humanfactorsjava.model;

import java.util.Collection;

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
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private Collection<TeamMember> members;

    public Team() {
        super();
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

    public Collection<TeamMember> getMembers() {
        return members;
    }

    public void setMembers(Collection<TeamMember> members) {
        this.members = members;
    }

    public void addMember(TeamMember member) {
        members.add(member);
    }

    public void removeMember(TeamMember member) {
        members.remove(member);
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
        return "Team [id=" + id + ", manager=" + manager + ", members=" + members + "]";
    }

}
