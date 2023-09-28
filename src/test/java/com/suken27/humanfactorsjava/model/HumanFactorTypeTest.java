package com.suken27.humanfactorsjava.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class HumanFactorTypeTest {

    private HumanFactorType humanFactorType1;
    private HumanFactorType humanFactorType2;

    @BeforeEach
    public void setUp() {
        humanFactorType1 = new HumanFactorType();
        humanFactorType2 = new HumanFactorType();

        // Create a new QuestionType instance
        QuestionType questionType1 = new QuestionType();
        QuestionType questionType2 = new QuestionType();
        QuestionType questionType3 = new QuestionType();

        // Set specific values for the properties
        questionType1.setId(1L); // Set the ID to 1L
        questionType1.setQuestionText("Do you enjoy your work most of the time?"); // Set the question text
        questionType1.setOnlyForManagement(false); // Set onlyForManagement to false
        questionType1.setNegative(false); // Set isNegative to false

        questionType2.setId(2L); // Set the ID to 1L
        questionType2.setQuestionText("Do you hate the work with your team?"); // Set the question text
        questionType2.setOnlyForManagement(true); // Set onlyForManagement to true
        questionType2.setNegative(true); // Set isNegative to true

        questionType3.setId(3L); // Set the ID to 1L
        questionType3.setQuestionText("Do you love the work with your team?"); // Set the question text
        questionType3.setOnlyForManagement(true); // Set onlyForManagement to true
        questionType3.setNegative(false); // Set isNegative to true

        // Create a new list of QuestionType instances
        List<QuestionType> questionTypes1 = new ArrayList<QuestionType>();
        questionTypes1.add(questionType1);
        questionTypes1.add(questionType2);

        List<QuestionType> questionTypes2 = new ArrayList<QuestionType>();
        questionTypes2.add(questionType2);
        questionTypes2.add(questionType3);

        humanFactorType1.setQuestionTypes(questionTypes1);
        humanFactorType2.setQuestionTypes(questionTypes2);
    }

    @Test
    public void testCreateInstance() {
   
        HumanFactor instance = humanFactorType1.createInstance();
        assertNotNull(instance);

        assertEquals(2, instance.getQuestions().size());

        assertEquals(1L, instance.getQuestions().get(0).getType().getId());
        assertEquals("Do you enjoy your work most of the time?", instance.getQuestions().get(0).getType().getQuestionText());
        assertEquals(false, instance.getQuestions().get(0).getType().isOnlyForManagement());
        assertEquals(false, instance.getQuestions().get(0).getType().isNegative());

        assertEquals(2L, instance.getQuestions().get(1).getType().getId());
        assertEquals("Do you hate the work with your team?", instance.getQuestions().get(1).getType().getQuestionText());
        assertEquals(true, instance.getQuestions().get(1).getType().isOnlyForManagement());
        assertEquals(true, instance.getQuestions().get(1).getType().isNegative());

    }

    @Test
    public void testIsMemberMeasured() {
        assertEquals(true, humanFactorType1.isMemberMeasured());
        assertEquals(false, humanFactorType2.isMemberMeasured());
        
    
    }


}