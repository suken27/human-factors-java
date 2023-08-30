package com.suken27.humanfactorsjava.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

/**
 * Class that represents a question that addresses a human factor.
 * This class represents the extrinsic state of a question, while QuestionType
 * represents the intrinsic state (flyweight design pattern).
 * There should be only one Question object per model's textual question, while
 * the same question may have multiple Answers if the user has answered it in
 * different times.
 * 
 * @see QuestionType
 * @see HumanFactor
 * @see Answer
 */
@Entity
@Data
public class Question {

    @Id
    @GeneratedValue
    private Long id;
    // This list should be a stack, but hibernate does not persist stacks. This list
    // should be used as Last Input First Output.
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Answer> answers;
    @ManyToOne
    private QuestionType type;
    private LocalDate lastAnswerDateTime;

    /**
     * This constructor should never be used. Use Question(QuestionType) instead.
     * This constructor cannot be removed as hibernate uses the default constructor
     * to instantiate entities.
     */
    public Question() {
        super();
    }

    public Question(QuestionType questionType) {
        super();
        answers = new ArrayList<>();
        type = questionType;
        // Even if no answer has been given to this question, a date is saved so it can be sorted.
        lastAnswerDateTime = LocalDate.now();
    }

    public void answer(Answer answer) {
        answer.setCreationTime(LocalDateTime.now());
        answers.add(answer);
        lastAnswerDateTime = LocalDate.now();
    }

    public long daysSinceLastAnswer() {
        return ChronoUnit.DAYS.between(lastAnswerDateTime, LocalDate.now());
    }

}
