package com.suken27.humanfactorsjava.rest.api;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.suken27.humanfactorsjava.repository.TeamRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class TeamControllerTest {

    @Autowired
    TeamController controller;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void testAddTeamMember() throws Exception {
        String teamMember = "testMember@test.test";
        mockMvc.perform(MockMvcRequestBuilders.patch("/teams").content(teamMember)).andExpect(status().isOk());
        // TODO: What is the authenticated user? Mock authenticated user?
        // TODO: Get authenticated user team, check not null
        // TODO: Check authenticated user team includes new member
    }

    @Test
    public void testRemoveTeamMember() {

    }
}
