package com.suken27.humanfactorsjava.rest.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.suken27.humanfactorsjava.model.Team;
import com.suken27.humanfactorsjava.model.TeamManager;
import com.suken27.humanfactorsjava.repository.TeamManagerRepository;
import com.suken27.humanfactorsjava.repository.TeamRepository;
import com.suken27.humanfactorsjava.rest.dto.AuthDto;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private AuthController controller;

    @Autowired
    private TeamManagerRepository teamManagerRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void contextLoads() {
        assertNotNull(controller);
    }

    @Test
    public void testRegisterUser() throws Exception {
        AuthDto dto = new AuthDto();
        dto.setEmail("testCreateManager@test.test");
        dto.setPassword("passwordTest");
        this.mockMvc.perform(MockMvcRequestBuilders.post("/signup")
                .content("{\"email\":\"" + dto.getEmail() + "\",\"password\":\"" + dto.getPassword() + "\"}")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
        TeamManager entity = teamManagerRepository.findByEmail(dto.getEmail());
        assertNotNull(entity);
        assertEquals(dto.getEmail(), entity.getEmail());
        assertTrue(passwordEncoder.matches(dto.getPassword(), entity.getPassword()));
        assertNotNull(entity.getTeam());

        Team team = teamRepository.findByTeamManagerEmail(dto.getEmail());
        assertNotNull(team);
        assertNotNull(team.getManager());
        assertEquals(entity, team.getManager());
    }

}
