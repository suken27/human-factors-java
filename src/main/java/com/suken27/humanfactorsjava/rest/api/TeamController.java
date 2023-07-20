package com.suken27.humanfactorsjava.rest.api;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.suken27.humanfactorsjava.model.DeletedTeamMember;
import com.suken27.humanfactorsjava.model.Team;
import com.suken27.humanfactorsjava.model.TeamMember;
import com.suken27.humanfactorsjava.repository.DeletedTeamMemberRepository;
import com.suken27.humanfactorsjava.repository.TeamMemberRepository;
import com.suken27.humanfactorsjava.repository.TeamRepository;
import com.suken27.humanfactorsjava.rest.dto.TeamMemberDto;
import com.suken27.humanfactorsjava.rest.exception.IncorrectEmailFormatException;
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
    private DeletedTeamMemberRepository deletedTeamMemberRepository;

    @Autowired
    private ApiValidator validator;

    @Autowired
    private static final Logger logger = LoggerFactory.getLogger(TeamController.class);

    @GetMapping("/teams")
    public ResponseEntity<?> getTeamMembers() {
        String teamManagerEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Team team = teamRepository.findByTeamManagerEmail(teamManagerEmail);
        List<TeamMemberDto> teamMembers = toDto(team.getMembers());
        return ResponseEntity.ok().body(teamMembers);
    }

    @PatchMapping("/teams")
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

    @DeleteMapping("/teams")
    public ResponseEntity<?> removeTeamMember(@RequestBody String email) {
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
        teamRepository.save(team);
        DeletedTeamMember deletedTeamMember = new DeletedTeamMember(teamMember);
        deletedTeamMemberRepository.save(deletedTeamMember);
        // Suggestion: Maybe the users should not be removed from database to avoid data loss.
        teamMemberRepository.delete(teamMember);
        return ResponseEntity.ok().body("Team member with email '" + email + "' removed successfully.");
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
