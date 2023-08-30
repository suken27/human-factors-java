package com.suken27.humanfactorsjava.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TeamTest {

    private static Team team;

    @BeforeAll
    static void setUp(@Autowired HumanFactorFactory humanFactorFactory) {
        team = new Team(new TeamManager(humanFactorFactory), humanFactorFactory.createTeamInstances());
        TeamMember teamMember = new TeamMember(humanFactorFactory);
        teamMember.setId(1L);
        team.addMember(teamMember);
        teamMember = new TeamMember(humanFactorFactory);
        teamMember.setId(2L);
        team.addMember(teamMember);
        teamMember = new TeamMember(humanFactorFactory);
        teamMember.setId(3L);
        team.addMember(teamMember);
    }

    @Test
    void testLaunchQuestions() {
        assertNotNull(team);
        team.setQuestionsPerDay(13);
        Map<User, List<Question>> questions = team.launchQuestions();
        assertNotNull(questions);
        assertEquals(4, questions.keySet().size());
        for(Entry<User, List<Question>> entry : questions.entrySet()) {
            assertEquals(13, entry.getValue().size());
        }
    }
}
