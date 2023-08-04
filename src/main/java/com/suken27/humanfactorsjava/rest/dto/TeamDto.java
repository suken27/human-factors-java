package com.suken27.humanfactorsjava.rest.dto;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.suken27.humanfactorsjava.model.Team;
import com.suken27.humanfactorsjava.model.TeamMember;

public class TeamDto {
    
    private Long id;
    private Long manager;
    private List<Long> members;
    private String questionSendingTime;

    public TeamDto() {
        super();
    }

    public TeamDto(Team team) {
        id = team.getId();
        manager = team.getManager().getId();
        members = new ArrayList<Long>();
        for (TeamMember member : team.getMembers()) {
            members.add(member.getId());
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        questionSendingTime = team.getQuestionSendingTime().format(dateTimeFormatter);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getManager() {
        return manager;
    }

    public void setManager(Long manager) {
        this.manager = manager;
    }

    public List<Long> getMembers() {
        return members;
    }

    public void setMembers(List<Long> members) {
        this.members = members;
    }

    public String getQuestionSendingTime() {
        return questionSendingTime;
    }

    public void setQuestionSendingTime(String questionSendingTime) {
        this.questionSendingTime = questionSendingTime;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((manager == null) ? 0 : manager.hashCode());
        result = prime * result + ((members == null) ? 0 : members.hashCode());
        result = prime * result + ((questionSendingTime == null) ? 0 : questionSendingTime.hashCode());
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
        TeamDto other = (TeamDto) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (manager == null) {
            if (other.manager != null)
                return false;
        } else if (!manager.equals(other.manager))
            return false;
        if (members == null) {
            if (other.members != null)
                return false;
        } else if (!members.equals(other.members))
            return false;
        if (questionSendingTime == null) {
            if (other.questionSendingTime != null)
                return false;
        } else if (!questionSendingTime.equals(other.questionSendingTime))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "TeamDto [id=" + id + ", manager=" + manager + ", members=" + members + ", questionSendingTime="
                + questionSendingTime + "]";
    }

}
