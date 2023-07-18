package com.suken27.humanfactorsjava.rest.dto;

public class TeamManagerDto extends UserDto {
    
    private Long team;

    public TeamManagerDto() {
        super();
    }

    public Long getTeam() {
        return team;
    }

    public void setTeam(Long team) {
        this.team = team;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
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
        TeamManagerDto other = (TeamManagerDto) obj;
        if (team == null) {
            if (other.team != null)
                return false;
        } else if (!team.equals(other.team))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "TeamManagerDto [team=" + team + "]";
    }

}
