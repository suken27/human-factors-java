package com.suken27.humanfactorsjava.rest.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.suken27.humanfactorsjava.model.Team;
import com.suken27.humanfactorsjava.model.TeamMember;
import com.suken27.humanfactorsjava.repository.TeamMemberRepository;
import com.suken27.humanfactorsjava.repository.TeamRepository;
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
    private ApiValidator validator;

    @Autowired
    private static final Logger logger = LoggerFactory.getLogger(TeamController.class);

    @PatchMapping("/teams")
    public ResponseEntity<?> addTeamMember(@RequestBody String email) {
        if(email == null || validator.isValidEmail(email)) {
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
        teamMember = teamMemberRepository.save(teamMember);
        team.addMember(teamMember);
        teamRepository.save(team);
        return ResponseEntity.ok().body("Team member with email '" + email + "'' added successfully.");
    }

    @DeleteMapping("/teams")
    public ResponseEntity<?> removeTeamMember(@RequestBody String email) {
        if(email == null || validator.isValidEmail(email)) {
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
        // Suggestion: Maybe the users should not be removed from database to avoid data loss.
        teamMemberRepository.delete(teamMember);
        return ResponseEntity.ok().body("Team member with email '" + email + "' removed successfully.");
    }

}
