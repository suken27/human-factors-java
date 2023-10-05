package com.suken27.humanfactorsjava.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.suken27.humanfactorsjava.model.HumanFactor;

@SpringBootTest
public class HumanFactorTest {

    private HumanFactorType humanFactorType1;
    private HumanFactorType humanFactorType2;
    private HumanFactor humanFactor1;
    private HumanFactor humanFactor2;
    private List<BibliographicSource> bibliographicSources;

    @BeforeEach
    public void setup() {
        // Create new instances of HumanFactorType
        humanFactorType1 = new HumanFactorType();
        humanFactorType2 = new HumanFactorType();
        bibliographicSources = new ArrayList<BibliographicSource>();
    
        // Create new instances of BibliographicSource
        BibliographicSource bibliographicSource = new BibliographicSource();

        // Set specific values for the properties
        bibliographicSource.setId(1L);
        bibliographicSource.setAuthors("BibliographicSourceAuthors");
        bibliographicSource.setPublicationDate(LocalDate.parse("2020-01-08"));
        bibliographicSource.setTitle("BibliographicSourceTitle");
    
        // Add Bibliographic instances to list
        bibliographicSources.add(bibliographicSource);
        humanFactorType1.setBibliographicSource(bibliographicSources);
    
        // Create new QuestionType instances
        QuestionType questionType1 = new QuestionType();
        QuestionType questionType2 = new QuestionType();

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

        // Create new lists of QuestionType instances
        List<QuestionType> questionTypes1 = new ArrayList<QuestionType>();
        List<QuestionType> questionTypes2 = new ArrayList<QuestionType>();
        questionTypes1.add(questionType1);
        questionTypes1.add(questionType2);

        humanFactorType1.setQuestionTypes(questionTypes1);
        humanFactorType2.setQuestionTypes(questionTypes2);
        
        // Initalize the HumanFactor instances
        humanFactor1 = new HumanFactor(humanFactorType1);
        humanFactor2 = new HumanFactor(humanFactorType2);
    }

    @Test
    public void testHumanFactor() {
        assertEquals(humanFactorType1, humanFactor1.getType());
        assertEquals(2, humanFactor1.getQuestions().size());
        assertEquals(false, humanFactor1.isFullyMeasured());
    }

    @Test
    public void testOldestQuestionDaysSinceLastAnswer() {
        assertEquals(0, humanFactor1.oldestQuestionDaysSinceLastAnswer());
        assertEquals(0, humanFactor2.oldestQuestionDaysSinceLastAnswer());
    }

    @Test
    public void testGetOldestQuestion() {
        assertEquals(humanFactor1.getQuestions().get(0), humanFactor1.getOldestQuestion());
        assertEquals(null, humanFactor2.getOldestQuestion());
    }





    @Test
    public void testCanBeMEasured(){
        assertEquals(true, humanFactor1.canBeMeasured());
        assertEquals(false, humanFactor2.canBeMeasured());
    }
    
}
