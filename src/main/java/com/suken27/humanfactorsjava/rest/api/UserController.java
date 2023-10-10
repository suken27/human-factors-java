package com.suken27.humanfactorsjava.rest.api;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.suken27.humanfactorsjava.model.controller.ModelController;
import com.suken27.humanfactorsjava.model.dto.TeamManagerDto;

@RestController
public class UserController {
    
    @Autowired
    private ModelController modelController;

    @PutMapping("/user/password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> json) {
        String teamManagerEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        modelController.checkUser(teamManagerEmail, json.get("oldPassword"));
        TeamManagerDto teamManager = modelController.getTeamManager(teamManagerEmail);
        String newPassword = json.get("newPassword");
        if(newPassword == null) {
            return ResponseEntity.badRequest().body("Empty password.");
        }
        teamManager.setPassword(newPassword);
        return ResponseEntity.ok().body(modelController.updateTeamManager(teamManager));
    }

    @GetMapping("/user/integration")
    public ResponseEntity<?> integrationCompleted() {
        String teamManagerEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        TeamManagerDto teamManager = modelController.getTeamManager(teamManagerEmail);
        return ResponseEntity.ok().body(teamManager.getSlackId() != null);
    }

}
