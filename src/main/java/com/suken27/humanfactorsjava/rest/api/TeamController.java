package com.suken27.humanfactorsjava.rest.api;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.suken27.humanfactorsjava.model.Team;
import com.suken27.humanfactorsjava.model.TeamMember;
import com.suken27.humanfactorsjava.repository.TeamMemberRepository;
import com.suken27.humanfactorsjava.repository.TeamRepository;
import com.suken27.humanfactorsjava.rest.dto.TeamDto;
import com.suken27.humanfactorsjava.rest.dto.TeamMemberDto;
import com.suken27.humanfactorsjava.rest.exception.IncorrectEmailFormatException;
import com.suken27.humanfactorsjava.rest.exception.IncorrectTimeFormatException;
import com.suken27.humanfactorsjava.rest.exception.MemberAlreadyInTeamException;
import com.suken27.humanfactorsjava.rest.exception.MemberInAnotherTeamException;
import com.suken27.humanfactorsjava.rest.exception.TeamMemberNotFoundException;
import com.suken27.humanfactorsjava.rest.exception.UserNotInTeamException;
import com.suken27.humanfactorsjava.rest.util.ApiValidator;

@RestController
public class TeamController {
    
    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private ApiValidator validator;

    @Autowired
    private static final Logger logger = LoggerFactory.getLogger(TeamController.class);

    @GetMapping("/teams")
    public ResponseEntity<?> getTeam() {
        String teamManagerEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Team team = teamRepository.findByTeamManagerEmail(teamManagerEmail);
        return ResponseEntity.ok().body(new TeamDto(team));
    }

    @PostMapping("/teams")
    public ResponseEntity<?> addTeamMember(@RequestBody String email) {
        if(email == null || !validator.isValidEmail(email)) {
            return ResponseEntity.badRequest().body(new IncorrectEmailFormatException(email));
        }
        String teamManagerEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Team team = teamRepository.findByTeamManagerEmail(teamManagerEmail);
        // Team should not be null as every team manager is created with an empty team, so no check should be required
        if(team.isMember(email)) {
            return ResponseEntity.badRequest().body(new MemberAlreadyInTeamException(email));
        }
        TeamMember teamMember = teamMemberRepository.findByEmail(email);
        if(teamMember != null) {
            return ResponseEntity.badRequest().body(new MemberInAnotherTeamException(email));
        }
        teamMember = new TeamMember();
        teamMember.setEmail(email);
        teamMember.setTeam(team);
        team.addMember(teamMember);
        team = teamRepository.save(team);
        return ResponseEntity.ok().body(toDto(team.getMembers()));
    }

    @DeleteMapping("/teams/{email}")
    public ResponseEntity<?> removeTeamMember(@PathVariable String email) {
        if(email == null || !validator.isValidEmail(email)) {
            return ResponseEntity.badRequest().body(new IncorrectEmailFormatException(email));
        }
        String teamManagerEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Team team = teamRepository.findByTeamManagerEmail(teamManagerEmail);
        // Team should not be null as every team manager is created with an empty team, so no check should be required
        if(!team.isMember(email)) {
            return ResponseEntity.badRequest().body(new UserNotInTeamException(email));
        }
        // At this point the user should exist, this is a double check just in case
        TeamMember teamMember = teamMemberRepository.findByEmail(email);
        if(teamMember == null) {
            return ResponseEntity.badRequest().body(new TeamMemberNotFoundException(email));
        }
        team.removeMember(teamMember);
        team = teamRepository.save(team);
        teamMemberRepository.save(teamMember);
        return ResponseEntity.ok().body(toDto(team.getMembers()));
    }

    @PutMapping("/teams/time")
    public ResponseEntity<?> modifyQuestionSendingTime(@RequestBody String questionSendingTime) {
        LocalTime localTime = validator.parseTimeString(questionSendingTime);
        if(localTime == null) {
            return ResponseEntity.badRequest().body(new IncorrectTimeFormatException(questionSendingTime));
        }
        String teamManagerEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Team team = teamRepository.findByTeamManagerEmail(teamManagerEmail);
        team.setQuestionSendingTime(localTime);
        team = teamRepository.save(team);
        return ResponseEntity.ok().body(new TeamDto(team));
    }

    private TeamMemberDto toDto(TeamMember teamMember) {
        TeamMemberDto dto = new TeamMemberDto();
        dto.setEmail(teamMember.getEmail());
        dto.setId(teamMember.getId());
        dto.setTeam(teamMember.getTeam().getId());
        return dto;
    }

    private List<TeamMemberDto> toDto(List<TeamMember> teamMembers) {
        List<TeamMemberDto> dtos = new ArrayList<>();
        for (TeamMember teamMember : teamMembers) {
            dtos.add(toDto(teamMember));
        }
        return dtos;
    }

}
