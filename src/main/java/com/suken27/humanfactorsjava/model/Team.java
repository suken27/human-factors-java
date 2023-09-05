package com.suken27.humanfactorsjava.model;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.slack.api.methods.SlackApiException;
import com.suken27.humanfactorsjava.model.dto.QuestionDto;
import com.suken27.humanfactorsjava.model.dto.UserDto;
import com.suken27.humanfactorsjava.slack.SlackController;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@Entity
@Data
@EqualsAndHashCode(exclude = {"manager", "members", "humanFactors", "scheduledQuestions", "threadPoolTaskScheduler", "modelController"})
@Slf4j
public class Team {

    @Transient
    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Transient
    @Autowired
    private SlackController slackController;
    
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
    @Transient
    private ScheduledFuture<?> scheduledQuestions;
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
        scheduleQuestions();
        questionsPerDay = 10;
        this.humanFactors = humanFactors;
    }

    /**
     * Sets the time of the day when the questions will be sent and schedules the sending of the questions.
     * @param questionSendingTime Time of the day when the questions will be sent (from Monday to Friday).
     */
    public void setQuestionSendingTime(LocalTime questionSendingTime) {
        this.questionSendingTime = questionSendingTime;
        scheduleQuestions();
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

    public void sendQuestionsToSlack() throws SlackApiException, IOException {
        if(slackBotToken == null) {
            return;
        }
        Map<UserDto, List<QuestionDto>> questionsDto = new HashMap<>();
        for(Entry<User, List<Question>> entry : launchQuestions().entrySet()) {
            questionsDto.put(new UserDto(entry.getKey()), QuestionDto.toDto(entry.getValue()));
        }
        slackController.sendQuestionsToSlack(questionsDto, slackBotToken);
    }

    private void scheduleQuestions() {
        if(scheduledQuestions != null) {
            scheduledQuestions.cancel(false);
        }
        CronTrigger cronTrigger = new CronTrigger(questionSendingTime.getMinute() + " " + questionSendingTime.getHour() + " * * * 1-5");
        scheduledQuestions = threadPoolTaskScheduler.schedule(() -> {
            try {
                sendQuestionsToSlack();
            } catch (SlackApiException | IOException e) {
                log.error("Error sending questions to Slack", e);
            }
        }, cronTrigger);
    }

}
