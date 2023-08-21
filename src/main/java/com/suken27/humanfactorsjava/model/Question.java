package com.suken27.humanfactorsjava.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

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
public class Question {

    @Id
    @GeneratedValue
    private Long id;
    // This list should be a stack, but hibernate does not persist stacks. This list
    // should be used as Last Input First Output.
    @OneToMany(cascade = CascadeType.ALL)
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public QuestionType getType() {
        return type;
    }

    public void setType(QuestionType type) {
        this.type = type;
    }

    public LocalDate getLastAnswerDateTime() {
        return lastAnswerDateTime;
    }

    public void setLastAnswerDateTime(LocalDate lastAnswerDateTime) {
        this.lastAnswerDateTime = lastAnswerDateTime;
    }

    public void answer(Answer answer) {
        answer.setCreationTime(LocalDateTime.now());
        answers.add(answer);
        lastAnswerDateTime = LocalDate.now();
    }

    public long daysSinceLastAnswer() {
        return ChronoUnit.DAYS.between(lastAnswerDateTime, LocalDate.now());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Question other = (Question) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Question [id=" + id + "]";
    }

}
