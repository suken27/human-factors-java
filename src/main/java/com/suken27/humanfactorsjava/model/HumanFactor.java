package com.suken27.humanfactorsjava.model;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class HumanFactor {
    
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private HumanFactorType type;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Question> questions;
    private LocalDate lastQuestionAnswered;

    /**
     * This constructor should never be used. Use HumanFactor(HumanFactorType) instead.
	 * This constructor cannot be removed as hibernate uses the default constructor to instantiate entities.
     */
    public HumanFactor() {
        super();
    }

    public HumanFactor(HumanFactorType humanFactorType) {
        super();
        type = humanFactorType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HumanFactorType getType() {
        return type;
    }

    public void setType(HumanFactorType type) {
        this.type = type;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public LocalDate getLastQuestionAnswered() {
        return lastQuestionAnswered;
    }

    public void setLastQuestionAnswered(LocalDate lastQuestionAnswered) {
        this.lastQuestionAnswered = lastQuestionAnswered;
    }

    public long oldestQuestionDaysSinceLastAnswer() {
        Question question = getOldestQuestion();
        if(question == null) {
            return 0;
        }
        return getOldestQuestion().daysSinceLastAnswer();
    }

    public Question getOldestQuestion() {
        if(questions == null || questions.isEmpty()) {
            return null;
        }
        Question oldestQuestion = questions.get(0);
        for (Question question : questions) {
            if(question.getLastAnswerDateTime() == null) {
                return question;
            }
            if(question.getLastAnswerDateTime().isBefore(oldestQuestion.getLastAnswerDateTime())) {
                oldestQuestion = question;
            }
        }
        return oldestQuestion;
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
        HumanFactor other = (HumanFactor) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "HumanFactor [id=" + id + "]";
    }

        

}
