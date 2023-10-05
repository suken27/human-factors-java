package com.suken27.humanfactorsjava.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class HumanFactorTypeTest {

    private HumanFactorType humanFactorType1;
    private HumanFactorType humanFactorType2;
    private Map<ActionType, Action> sharedActions;
    private List<ActionType> actionTypes;
    private List<BibliographicSource> bibliographicSources;
    private HumanFactor humanFactor;
    private TeamHumanFactor teamHumanFactor;

    @BeforeEach
    public void setUp() {
        humanFactorType1 = new HumanFactorType();
        humanFactorType2 = new HumanFactorType();
        sharedActions = new java.util.HashMap<ActionType,Action>();
        actionTypes = new ArrayList<ActionType>();
        bibliographicSources = new ArrayList<BibliographicSource>();

        // Create new instances of BibliographicSource
        BibliographicSource bibliographicSource1 = new BibliographicSource();
        BibliographicSource bibliographicSource2 = new BibliographicSource();

        // Set specific values for the properties
        bibliographicSource1.setId(1L);
        bibliographicSource1.setAuthors("BibliographicSourceAuthors1");
        bibliographicSource1.setPublicationDate(LocalDate.parse("2020-01-08"));
        bibliographicSource1.setTitle("BibliographicSourceTitle1");

        bibliographicSource2.setId(2L);
        bibliographicSource2.setAuthors("BibliographicSourceAuthors2");
        bibliographicSource2.setPublicationDate(LocalDate.parse("2000-05-29"));
        bibliographicSource2.setTitle("BibliographicSourceTitle2");

        // Add Bibliographic instances to list
        bibliographicSources.add(bibliographicSource1);
        bibliographicSources.add(bibliographicSource2);
        humanFactorType1.setBibliographicSource(bibliographicSources);

        // Create new QuestionType instances
        QuestionType questionType1 = new QuestionType();
        QuestionType questionType2 = new QuestionType();
        QuestionType questionType3 = new QuestionType();

        // Set specific values for the properties
        questionType1.setId(1L);
        questionType1.setQuestionText("Do you enjoy your work most of the time?");
        questionType1.setOnlyForManagement(false);
        questionType1.setNegative(false);
        questionType1.setTypeOfAnswer(TypeOfAnswer.FREQUENCY_RANGE);

        questionType2.setId(2L);
        questionType2.setQuestionText("Do you hate the work with your team?");
        questionType2.setOnlyForManagement(true);
        questionType2.setNegative(true);
        questionType2.setTypeOfAnswer(TypeOfAnswer.BINARY);

        questionType3.setId(3L);
        questionType3.setQuestionText("Do you love the work with your team?");
        questionType3.setOnlyForManagement(true);
        questionType3.setNegative(false);
        questionType3.setTypeOfAnswer(TypeOfAnswer.BINARY);

        // Create new lists of QuestionType instances
        List<QuestionType> questionTypes1 = new ArrayList<QuestionType>();
        questionTypes1.add(questionType1);
        questionTypes1.add(questionType2);

        List<QuestionType> questionTypes2 = new ArrayList<QuestionType>();
        questionTypes2.add(questionType2);
        questionTypes2.add(questionType3);

        humanFactorType1.setQuestionTypes(questionTypes1);
        humanFactorType2.setQuestionTypes(questionTypes2);

        // Create new ActionType instances
        ActionType actionType1 = new ActionType();
        ActionType actionType2 = new ActionType();
        ActionType actionType3 = new ActionType();

        // Set specific values for the properties
        actionType1.setId(1L);
        actionType1.setTitle("ActionTypeTitle1");
        actionType1.setDescription("ActionTypeDescription1");
        actionType1.setBibliographicSource(bibliographicSource1);

        actionType2.setId(2L);
        actionType2.setTitle("ActionTypeTitle2");
        actionType2.setDescription("ActionTypeDescription2");
        actionType2.setBibliographicSource(bibliographicSource2);

        actionType3.setId(3L);
        actionType3.setTitle("ActionTypeTitle3");
        actionType3.setDescription("ActionTypeDescription3");
        actionType3.setBibliographicSource(bibliographicSource2);

        actionTypes.add(actionType1);
        actionTypes.add(actionType2);
        actionTypes.add(actionType3);
        humanFactorType1.setActionTypes(actionTypes);

        // Create new Action instances
        Action action1 = new Action();
        Action action2 = new Action();
        Action action3 = new Action();

        // Set specific values for the properties
        action1.setId(4L);
        action1.setType(actionType1);

        action2.setId(5L);
        action2.setType(actionType2);

        action3.setId(6L);
        action3.setType(actionType3);

        // Create new map of ActionType Action pairs
        sharedActions.put(actionType1, action1);
        sharedActions.put(actionType2, action2);
        sharedActions.put(actionType3, action3);

        // Create new Team instance and humanFactorType instance
        humanFactor = humanFactorType1.createInstance();
        teamHumanFactor = humanFactorType1.createTeamInstance(sharedActions);
        

    }

    @Test
    public void testCreateInstance() {
        assertNotNull(humanFactor);
        assertEquals(2, humanFactor.getQuestions().size());
    }

    @Test
     public void testQuestionsTypeInstances() {
        assertEquals(1L, humanFactor.getQuestions().get(0).getType().getId());
        assertEquals("Do you enjoy your work most of the time?",
                humanFactor.getQuestions().get(0).getType().getQuestionText());
        assertEquals(false, humanFactor.getQuestions().get(0).getType().isOnlyForManagement());
        assertEquals(false, humanFactor.getQuestions().get(0).getType().isNegative());
        assertEquals(TypeOfAnswer.FREQUENCY_RANGE, humanFactor.getQuestions().get(0).getType().getTypeOfAnswer());

        assertEquals(2L, humanFactor.getQuestions().get(1).getType().getId());
        assertEquals("Do you hate the work with your team?",
                humanFactor.getQuestions().get(1).getType().getQuestionText());
        assertEquals(true, humanFactor.getQuestions().get(1).getType().isOnlyForManagement());
        assertEquals(true, humanFactor.getQuestions().get(1).getType().isNegative());
        assertEquals(TypeOfAnswer.BINARY, humanFactor.getQuestions().get(1).getType().getTypeOfAnswer()); 

    }


    @Test
    public void testCreateTeamInstance() {
       assertNotNull(teamHumanFactor);
       assertEquals(3, teamHumanFactor.getActions().size());
    }

    @Test
    public void testActionTypeInstances() {
        assertEquals(1L, humanFactorType1.getActionTypes().get(0).getId());
        assertEquals("ActionTypeTitle1", humanFactorType1.getActionTypes().get(0).getTitle());
        assertEquals("ActionTypeDescription1", humanFactorType1.getActionTypes().get(0).getDescription());

        assertEquals(2L, humanFactorType1.getActionTypes().get(1).getId());
        assertEquals("ActionTypeTitle2", humanFactorType1.getActionTypes().get(1).getTitle());
        assertEquals("ActionTypeDescription2", humanFactorType1.getActionTypes().get(1).getDescription());

        assertEquals(3L, humanFactorType1.getActionTypes().get(2).getId());
        assertEquals("ActionTypeTitle3", humanFactorType1.getActionTypes().get(2).getTitle());
        assertEquals("ActionTypeDescription3", humanFactorType1.getActionTypes().get(2).getDescription());
    }

    @Test
    public void testActionInstances() {
        assertEquals(4L, teamHumanFactor.getActions().get(0).getId());
        assertEquals(1L, teamHumanFactor.getActions().get(0).getType().getId());

        assertEquals(5L, teamHumanFactor.getActions().get(1).getId());
        assertEquals(2L, teamHumanFactor.getActions().get(1).getType().getId());

        assertEquals(6L, teamHumanFactor.getActions().get(2).getId());
        assertEquals(3L, teamHumanFactor.getActions().get(2).getType().getId()); 
    } 

    @Test
    public void testBibliogrpahicSourceInstances() {
        assertEquals(1L, humanFactorType1.getBibliographicSource().get(0).getId());
        assertEquals("BibliographicSourceAuthors1", humanFactorType1.getBibliographicSource().get(0).getAuthors());
        assertEquals(LocalDate.parse("2020-01-08"),
                humanFactorType1.getBibliographicSource().get(0).getPublicationDate());
        assertEquals("BibliographicSourceTitle1", humanFactorType1.getBibliographicSource().get(0).getTitle());

        assertEquals(2L, humanFactorType1.getBibliographicSource().get(1).getId());
        assertEquals("BibliographicSourceAuthors2", humanFactorType1.getBibliographicSource().get(1).getAuthors());
        assertEquals(LocalDate.parse("2000-05-29"),
                humanFactorType1.getBibliographicSource().get(1).getPublicationDate());
        assertEquals("BibliographicSourceTitle2", humanFactorType1.getBibliographicSource().get(1).getTitle());
 
    }

    @Test
    public void testIsMemberMeasured() {
        assertEquals(true, humanFactorType1.isMemberMeasured());
        assertEquals(false, humanFactorType2.isMemberMeasured());

    }

}