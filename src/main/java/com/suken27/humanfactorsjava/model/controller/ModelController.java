package com.suken27.humanfactorsjava.model.controller;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;

import com.suken27.humanfactorsjava.model.HumanFactorFactory;
import com.suken27.humanfactorsjava.model.Question;
import com.suken27.humanfactorsjava.model.Team;
import com.suken27.humanfactorsjava.model.TeamManager;
import com.suken27.humanfactorsjava.model.TeamMember;
import com.suken27.humanfactorsjava.model.User;
import com.suken27.humanfactorsjava.model.dto.QuestionDto;
import com.suken27.humanfactorsjava.model.dto.TeamDto;
import com.suken27.humanfactorsjava.model.dto.TeamManagerDto;
import com.suken27.humanfactorsjava.model.dto.UserDto;
import com.suken27.humanfactorsjava.model.exception.EmailInUseException;
import com.suken27.humanfactorsjava.model.exception.IncorrectLoginException;
import com.suken27.humanfactorsjava.model.exception.MemberAlreadyInTeamException;
import com.suken27.humanfactorsjava.model.exception.TeamManagerNotFoundException;
import com.suken27.humanfactorsjava.model.exception.TeamMemberNotFoundException;
import com.suken27.humanfactorsjava.repository.TeamManagerRepository;
import com.suken27.humanfactorsjava.repository.TeamMemberRepository;
import com.suken27.humanfactorsjava.repository.TeamRepository;
import com.suken27.humanfactorsjava.rest.exception.MemberInAnotherTeamException;

@Controller
public class ModelController {

    // TODO: Refactor model to decouple the messaging logic from the model
    // TODO: Cache users to avoid fetching them every time

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

    public void checkUser(String email, String password) {
        TeamManager user = teamManagerRepository.findByEmail(email);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new IncorrectLoginException();
        }
    }

    public TeamManagerDto getTeamManager(String email) {
        TeamManager teamManager = teamManagerRepository.findByEmail(email);
        if(teamManager == null) {
            throw new TeamManagerNotFoundException(email);
        }
        return new TeamManagerDto(teamManager);
    }
    
    public TeamManagerDto registerTeamManager(String email, String password) {
        if (teamManagerRepository.findByEmail(email) != null) {
            throw new EmailInUseException(email);
        }
        TeamManager entity = new TeamManager(humanFactorFactory);
        entity.setEmail(email);
        entity.setPassword(passwordEncoder.encode(password));
        return new TeamManagerDto(teamManagerRepository.save(entity));
    }

    public TeamManagerDto updateTeamManager(TeamManagerDto teamManagerDto) {
        TeamManager teamManager = teamManagerRepository.findByEmail(teamManagerDto.getEmail());
        if(teamManager == null) {
            throw new TeamManagerNotFoundException(teamManagerDto.getEmail());
        }
        teamManager.setSlackId(teamManagerDto.getSlackId());
        return new TeamManagerDto(teamManagerRepository.save(teamManager));
    }

    public TeamDto getTeam(String teamManagerEmail) {
        Team team = teamRepository.findByTeamManagerEmail(teamManagerEmail);
        if(team  == null) {
            return null;
        }
        return new TeamDto(team);
    }

    public TeamDto updateTeam(TeamDto teamDto) {
        Team team = teamRepository.findByTeamManagerId(teamDto.getManager());
        if(team == null) {
            throw new TeamManagerNotFoundException(teamDto.getManager());
        }
        // parse questionSendingTime to LocalTime
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        team.setQuestionSendingTime(LocalTime.parse(teamDto.getQuestionSendingTime(), dateTimeFormatter));
        team.setQuestionsPerDay(teamDto.getQuestionsPerDay());
        team.setSlackBotToken(teamDto.getSlackBotToken());
        return new TeamDto(teamRepository.save(team));
    }

    public TeamDto addTeamMember(String teamManagerEmail, String email, String slackId) throws MemberAlreadyInTeamException, MemberInAnotherTeamException {
        Team team = teamRepository.findByTeamManagerEmail(teamManagerEmail);
        // Team should not be null as every team manager is created with an empty team, so no check should be required
        if(teamManagerEmail.equals(email) || team.isMember(email)) {
            throw new MemberAlreadyInTeamException(email);
        }
        TeamMember teamMember = teamMemberRepository.findByEmail(email);
        if(teamMember != null) {
            throw new MemberInAnotherTeamException(email);
        }
        teamMember = new TeamMember(humanFactorFactory);
        teamMember.setEmail(email);
        teamMember.setTeam(team);
        teamMember.setSlackId(slackId);
        team.addMember(teamMember);
        return new TeamDto(teamRepository.save(team));
    }

    public TeamDto removeTeamMember(String teamManagerEmail, String email) {
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
        return new TeamDto(teamRepository.save(team));
    }

    public TeamDto modifyQuestionSendingTime(String teamManagerEmail, LocalTime questionSendingTime) {
        Team team = teamRepository.findByTeamManagerEmail(teamManagerEmail);
        team.setQuestionSendingTime(questionSendingTime);
        return new TeamDto(teamRepository.save(team));
    }

    public Map<UserDto, List<QuestionDto>> launchQuestions(String teamManagerEmail) {
        Team team = teamRepository.findByTeamManagerEmail(teamManagerEmail);
        Map<User, List<Question>> questions = team.launchQuestions();
        Map<UserDto, List<QuestionDto>> questionsDto = new HashMap<>();
        for(User user : questions.keySet()) {
            questionsDto.put(new UserDto(user), QuestionDto.toDto(questions.get(user)));
        }
        return questionsDto;
    }

}
