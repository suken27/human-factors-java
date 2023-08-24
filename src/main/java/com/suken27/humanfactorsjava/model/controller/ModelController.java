package com.suken27.humanfactorsjava.model.controller;

import java.io.IOException;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.slack.api.methods.SlackApiException;
import com.suken27.humanfactorsjava.model.HumanFactorFactory;
import com.suken27.humanfactorsjava.model.Team;
import com.suken27.humanfactorsjava.model.TeamManager;
import com.suken27.humanfactorsjava.model.TeamMember;
import com.suken27.humanfactorsjava.model.exception.EmailInUseException;
import com.suken27.humanfactorsjava.model.exception.IncorrectLoginException;
import com.suken27.humanfactorsjava.model.exception.MemberAlreadyInTeamException;
import com.suken27.humanfactorsjava.model.exception.TeamManagerNotFoundException;
import com.suken27.humanfactorsjava.model.exception.TeamMemberNotFoundException;
import com.suken27.humanfactorsjava.repository.TeamManagerRepository;
import com.suken27.humanfactorsjava.repository.TeamMemberRepository;
import com.suken27.humanfactorsjava.repository.TeamRepository;
import com.suken27.humanfactorsjava.rest.exception.MemberInAnotherTeamException;
import com.suken27.humanfactorsjava.slack.SlackMethodHandler;
import com.suken27.humanfactorsjava.slack.exception.UserNotFoundInWorkspaceException;

@Component
public class ModelController {

    // TODO: Refactor class to return DTOs instead of entities

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TeamManagerRepository teamManagerRepository;

    @Autowired
    private HumanFactorFactory humanFactorFactory;

    @Autowired 
    private TeamRepository teamRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    // TODO: Refactor to use a 'messagingInterface' instead of using Slack directly

    @Autowired
    private SlackMethodHandler slackMethodHandler;

    public void checkUser(String email, String password) {
        TeamManager user = teamManagerRepository.findByEmail(email);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new IncorrectLoginException();
        }
    }

    public Team checkTeamManager(String id, String slackBotToken) throws TeamManagerNotFoundException, UserNotFoundInWorkspaceException, SlackApiException, IOException {
        String email = slackMethodHandler.retrieveUserEmail(id, slackBotToken);
        Team team = teamRepository.findByTeamManagerEmail(email);
        if (team == null) {
            throw new TeamManagerNotFoundException(email);
        }
        team.getManager().setSlackId(id);
        team.setSlackBotToken(slackBotToken);
        return team;
    }
    
    public TeamManager registerTeamManager(String email, String password) {
        if (teamManagerRepository.findByEmail(email) != null) {
            throw new EmailInUseException(email);
        }
        TeamManager entity = new TeamManager(humanFactorFactory);
        entity.setEmail(email);
        entity.setPassword(passwordEncoder.encode(password));
        return teamManagerRepository.save(entity);
    }

    public Team getTeam(String teamManagerEmail) {
        return teamRepository.findByTeamManagerEmail(teamManagerEmail);
    }

    public Team addTeamMember(String teamManagerEmail, String email) throws UserNotFoundInWorkspaceException, SlackApiException, IOException {
        //TODO: Add checks for no integration with Slack before adding team members
        //TODO: Add checks for adding a team member with an email not included in Slack
        Team team = teamRepository.findByTeamManagerEmail(teamManagerEmail);
        // Team should not be null as every team manager is created with an empty team, so no check should be required
        if(team.isMember(email)) {
            throw new MemberAlreadyInTeamException(email);
        }
        TeamMember teamMember = teamMemberRepository.findByEmail(email);
        if(teamMember != null) {
            throw new MemberInAnotherTeamException(email);
        }
        teamMember = new TeamMember(humanFactorFactory.createInstances());
        teamMember.setEmail(email);
        teamMember.setTeam(team);
        team.addMember(teamMember);
        String slackId = slackMethodHandler.retrieveUserId(email, team.getSlackBotToken());
        teamMember.setSlackId(slackId);
        return teamRepository.save(team);
    }

    public Team removeTeamMember(String teamManagerEmail, String email) {
        Team team = teamRepository.findByTeamManagerEmail(teamManagerEmail);
        // Team should not be null as every team manager is created with an empty team, so no check should be required
        if(!team.isMember(email)) {
            throw new TeamMemberNotFoundException(email);
        }
        // At this point the user should exist, this is a double check just in case
        TeamMember teamMember = teamMemberRepository.findByEmail(email);
        if(teamMember == null) {
            throw new TeamMemberNotFoundException(email);
        }
        team.removeMember(teamMember);
        return teamRepository.save(team);
    }

    public Team modifyQuestionSendingTime(String teamManagerEmail, LocalTime questionSendingTime) {
        Team team = teamRepository.findByTeamManagerEmail(teamManagerEmail);
        team.setQuestionSendingTime(questionSendingTime);
        return teamRepository.save(team);
    }

}
