package com.suken27.humanfactorsjava.model;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@Entity
@Data
@EqualsAndHashCode(exclude = {"manager", "members", "humanFactors", "scheduledQuestions"})
@Slf4j
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
    @OneToMany(cascade = CascadeType.ALL)
    private List<TeamHumanFactor> humanFactors;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime questionSendingTime;
    private int questionsPerDay;
    // TODO: Refactor to avoid coupling between the model and a specific messaging interface
    private String slackBotToken;

    /**
     * This constructor should never be used. Use Team(TeamManager) instead.
     */
    public Team() {
        super();
    }

    public Team(TeamManager teamManager, List<TeamHumanFactor> humanFactors) {
        super();
        manager = teamManager;
        members = new ArrayList<>();
        questionSendingTime = LocalTime.of(9, 0);
        questionsPerDay = 10;
        this.humanFactors = humanFactors;
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
        return members.stream().anyMatch(member -> member.getEmail().equals(email));
    }

    public Map<User, List<Question>> launchQuestions() {
        Map<User, List<Question>> questionMap = new HashMap<>();
        List<User> teamUsers = new ArrayList<>(members);
        teamUsers.add(manager);
        for(User user : teamUsers) {
            questionMap.put(user, user.launchQuestions(questionsPerDay));
        }
        return questionMap;
    }

}
