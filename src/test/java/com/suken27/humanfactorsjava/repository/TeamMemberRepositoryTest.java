package com.suken27.humanfactorsjava.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.suken27.humanfactorsjava.model.TeamMember;

@SpringBootTest
public class TeamMemberRepositoryTest {

    private final static String TEST_TEAM_MEMBER_EMAIL = "testMember@test.test";

    @Autowired
    private TeamMemberRepository repository;

    @Test
    void testFindByEmail() {
        createTeamMember(TEST_TEAM_MEMBER_EMAIL);
        TeamMember teamMember = repository.findByEmail(TEST_TEAM_MEMBER_EMAIL);
        assertNotNull(teamMember);
        assertEquals(TEST_TEAM_MEMBER_EMAIL, teamMember.getEmail());
        markTeamMemberAsRemoved(teamMember);
        TeamMember teamMember2 = repository.findByEmail(TEST_TEAM_MEMBER_EMAIL);
        assertNull(teamMember2);
        removeTeamMember(teamMember);
    }

    @Test
    void testFindByEmailIncludingDeleted() {
        createTeamMember(TEST_TEAM_MEMBER_EMAIL);
        TeamMember teamMember = repository.findByEmail(TEST_TEAM_MEMBER_EMAIL);
        assertNotNull(teamMember);
        markTeamMemberAsRemoved(teamMember);
        List<TeamMember> results = repository.findByEmailIncludingDeleted(TEST_TEAM_MEMBER_EMAIL);
        assertNotNull(results);
        assertEquals(1, results.size());
        removeTeamMember(teamMember);
    }

    private void createTeamMember(String email) {
        TeamMember teamMember = new TeamMember();
        teamMember.setEmail(email);
        teamMember = repository.save(teamMember);
        assertNotNull(teamMember);
    }

    private void removeTeamMember(TeamMember teamMember) {
        repository.delete(teamMember);
        Optional<TeamMember> result = repository.findById(teamMember.getId());
        assertFalse(result.isPresent());
    }

    private void markTeamMemberAsRemoved(TeamMember teamMember) {
        teamMember.setDeleted(true);
        teamMember.setDeletionTime(LocalDateTime.now());
        teamMember = repository.save(teamMember);
        assertNotNull(teamMember);
    }
}
