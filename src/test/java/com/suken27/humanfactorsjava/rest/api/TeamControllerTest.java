package com.suken27.humanfactorsjava.rest.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.suken27.humanfactorsjava.model.Team;
import com.suken27.humanfactorsjava.model.TeamManager;
import com.suken27.humanfactorsjava.model.TeamMember;
import com.suken27.humanfactorsjava.repository.TeamManagerRepository;
import com.suken27.humanfactorsjava.repository.TeamRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
public class TeamControllerTest {

    private final static String TEST_TEAM_MANAGER_EMAIL = "testAddMember@test.test";
    private final static String TEST_TEAM_MANAGER_PASSWORD = "testPassword";
    private final static String TEST_TEAM_MEMBER_EMAIL = "testMember@test.test";

    @Autowired
    TeamController controller;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    TeamManagerRepository teamManagerRepository;

    @Autowired
    MockMvc mockMvc;

    @Test
    @WithMockUser(username=TEST_TEAM_MANAGER_EMAIL, roles={"USER"})
    @Transactional
    public void testAddTeamMember() throws Exception {
        TeamManager teamManager = createTestTeamManager();
        mockMvc.perform(MockMvcRequestBuilders.patch("/teams").content(TEST_TEAM_MEMBER_EMAIL)).andExpect(status().isOk());
        Team team = teamRepository.findByTeamManagerEmail(teamManager.getEmail());
        assertNotNull(team);
        assertNotNull(team.getMembers());
        assertEquals(1, team.getMembers().size());
        assertEquals(TEST_TEAM_MEMBER_EMAIL, team.getMembers().get(0).getEmail());
        deleteTestTeamManager(teamManager);
    }

    @Test
    @WithMockUser(username=TEST_TEAM_MANAGER_EMAIL, roles={"USER"})
    @Transactional
    public void testRemoveTeamMember() throws Exception {
        TeamManager teamManager = createTestTeamManager();
        TeamMember teamMember = new TeamMember();
        teamMember.setEmail(TEST_TEAM_MEMBER_EMAIL);
        teamManager.getTeam().addMember(teamMember);
        teamRepository.save(teamManager.getTeam());
        mockMvc.perform(MockMvcRequestBuilders.delete("/teams/" + TEST_TEAM_MEMBER_EMAIL)).andExpect(status().isOk());
        Team team = teamRepository.findByTeamManagerEmail(teamManager.getEmail());
        assertNotNull(team);
        assertNotNull(team.getMembers());
        assertTrue(team.getMembers().isEmpty());
    }

    private TeamManager createTestTeamManager() {
        TeamManager teamManager = new TeamManager();
        teamManager.setEmail(TEST_TEAM_MANAGER_EMAIL);
        teamManager.setPassword(TEST_TEAM_MANAGER_PASSWORD);
        teamManager.setTeam(new Team());
        teamManager.getTeam().setManager(teamManager);
        return teamManagerRepository.save(teamManager);
    }

    private void deleteTestTeamManager(TeamManager teamManager) {
        teamManagerRepository.delete(teamManager);
    }
}
