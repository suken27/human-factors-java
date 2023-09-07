package com.suken27.humanfactorsjava.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
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
@EqualsAndHashCode(exclude = {"manager", "members", "humanFactors"})
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
    @ElementCollection
    private Map<HumanFactorType, TeamHumanFactor> humanFactors;
    //TODO: Y si un factor humano no tiene preguntas para los miembros, solo para el lider?
    // Añadir una variable para registrar si el factor humano tiene puntuacion para todos los miembros y calcular esa variable en inicializacion
    /**
     * This map is a flattened nested map that represents a Map<HumanFactorType, Map<User, Double>>.
     * The String key is built using HumanFactorType.getId() + "." + User.hashCode().
     */
    @ElementCollection
    private Map<String, Double> humanFactorUserScores;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime questionSendingTime;
    private ZoneId timeZone;
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
        // This sets the question sending time to 9:00 spanish time.
        questionSendingTime = LocalTime.of(9, 0);
        timeZone = ZoneId.of("Europe/Madrid");
        setZonedQuestionSendingTime(questionSendingTime);
        questionsPerDay = 10;
        this.humanFactors = new HashMap<>();
        for(TeamHumanFactor teamHumanFactor : humanFactors) {
            this.humanFactors.put(teamHumanFactor.getType(), teamHumanFactor);
        }
        humanFactorUserScores = new HashMap<>();
        for(TeamHumanFactor teamHumanFactor : humanFactors) {
            String teamHumanFactorId = teamHumanFactor.getType().getId().toString();
            for(User user : getAllMembers()) {
                humanFactorUserScores.put(teamHumanFactorId + "." + user.hashCode(), null);
            }
        }
    }

    /**
     * Gets the question sending time in the team's time zone. The time returned will be in the team's time zone.
     * @return Time of the day to send the questions. This time is according the team's time zone.
     */
    public LocalTime getZonedQuestionSendingTime() {
        return LocalDateTime.of(LocalDate.now(), questionSendingTime).atZone(ZoneId.systemDefault()).withZoneSameInstant(timeZone).toLocalTime();
    }

    /**
     * Sets the question sending time in the team's time zone. The time received should be in the team's time zone and the time stored will be in the system's time zone.
     * @param questionSendingTime Time of the day to send the questions. This time should be according the team's time zone.
     */
    public void setZonedQuestionSendingTime(LocalTime questionSendingTime) {
        ZonedDateTime zonedTime = LocalDateTime.of(LocalDate.now(), questionSendingTime).atZone(timeZone);
        this.questionSendingTime = zonedTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalTime();
    }

    public void addMember(TeamMember member) {
        members.add(member);
        for(Entry<HumanFactorType, TeamHumanFactor> entry : humanFactors.entrySet()) {
            humanFactorUserScores.put(entry.getKey() + "." + member.hashCode(), null);
            updateHumanFactorScores(entry.getKey());
        }
    }

    /**
     * Removes member from the team, marks it as a deleted team member, and sets the time of deletion.
     * @param member Member to remove from the team.
     */
    public void removeMember(TeamMember member) {
        members.remove(member);
        for(Entry<HumanFactorType, TeamHumanFactor> entry : humanFactors.entrySet()) {
            humanFactorUserScores.remove(entry.getKey() + "." + member.hashCode(), null);
            updateHumanFactorScores(entry.getKey());
        }
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

    public void setTimeZone(ZoneId timeZone) {
        this.timeZone = timeZone;
        ZonedDateTime zonedTime = LocalDateTime.of(LocalDate.now(), questionSendingTime).atZone(timeZone);
        questionSendingTime = zonedTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalTime();
    }

    public Map<User, List<Question>> launchQuestions() {
        Map<User, List<Question>> questionMap = new HashMap<>();
        for(User user : getAllMembers()) {
            questionMap.put(user, user.launchQuestions(questionsPerDay));
        }
        return questionMap;
    }

    public List<Action> getRecommendedActions() {
        Map<ActionType, Double> actionScores = new HashMap<>();
        List<Action> recommendedActions = new ArrayList<>();
        for(Entry<HumanFactorType, TeamHumanFactor> entry : humanFactors.entrySet()) {
            TeamHumanFactor humanFactor = entry.getValue();
            if(humanFactor.getScore() == null) {
                return recommendedActions;
            }
            List<ActionType> actions = entry.getKey().getActionTypes();
            for(ActionType actionType : actions) {
                actionScores.put(actionType, humanFactor.getScore());
                // TODO: Consider the dependences between human factors to calculate the action score
            }
        }
        return recommendedActions;
    }

    public String answerQuestion(String userEmail, Question question, Double answer) {
        User user = getMember(userEmail);
        HumanFactor humanFactor = user.answerQuestion(question, answer);
        if(humanFactor != null) {
            String key = humanFactor.getType().getId() + "." + user.hashCode();
            humanFactorUserScores.put(key, humanFactor.getScore());
            updateHumanFactorScores(humanFactor.getType());
        }
        return question.answerValueToText(answer);
    }

    private User getMember(String memberEmail) {
        for (User user : getAllMembers()) {
            if (user.getEmail().equals(memberEmail)) {
                return user;
            }
        }
        return null;
    }

    private List<User> getAllMembers() {
        List<User> allMembers = new ArrayList<>(this.members);
        allMembers.add(manager);
        return allMembers;
    }

    private Double updateHumanFactorScores(HumanFactorType humanFactorType) {
        Double average = 0.0;
        List<User> allMembers = getAllMembers();
        for(User user : allMembers) {
            String key = humanFactorType.getId() + "." + user.hashCode();
            Double userScore = humanFactorUserScores.get(key);
            if(userScore == null) {
                humanFactorUserScores.put(key, null);
                return null;
            }
            average += userScore;
        }
        average /= allMembers.size();
        TeamHumanFactor teamHumanFactor = humanFactors.get(humanFactorType);
        int humanFactorScoresCounter = 1;
        for(TeamHumanFactor dependingFactor : teamHumanFactor.getDependantFactors()) {
            // Precondition: No circular dependencies between human factors
            Double dependingFactorScore = updateHumanFactorScores(dependingFactor.getType());
            // If not all the depending factors are available, the human factor score is still calculated
            if(dependingFactorScore != null) {
                average += dependingFactorScore;
                humanFactorScoresCounter++;
            }
        }
        // Every dependant human factor has the same weight in the average than the human factor being calculated
        average /= humanFactorScoresCounter;
        humanFactors.get(humanFactorType).setScore(average);
        return average;
    }

}
