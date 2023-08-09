package com.suken27.humanfactorsjava.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 * Flyweight class that registers the intrinsic state of a Question.
 * @see Question
 */
@Entity
public class QuestionType {
    
    @Id
    private Long id;
    private String questionText;
    private boolean onlyForManagement;
    private boolean isNegative;
    private TypeOfAnswer typeOfAnswer;

    public QuestionType() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public boolean isOnlyForManagement() {
        return onlyForManagement;
    }

    public void setOnlyForManagement(boolean onlyForManagement) {
        this.onlyForManagement = onlyForManagement;
    }

    public boolean isNegative() {
        return isNegative;
    }

    public void setNegative(boolean isNegative) {
        this.isNegative = isNegative;
    }

    public TypeOfAnswer getTypeOfAnswer() {
        return typeOfAnswer;
    }

    public void setTypeOfAnswer(TypeOfAnswer typeOfAnswer) {
        this.typeOfAnswer = typeOfAnswer;
    }

    /**
     * Creates an instance of question with this type as intrinsic state.
     * @return Question (extrinsic state) with this object as intrinsic state.
     */
    public Question createInstance() {
        return new Question(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((questionText == null) ? 0 : questionText.hashCode());
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
        QuestionType other = (QuestionType) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (questionText == null) {
            if (other.questionText != null)
                return false;
        } else if (!questionText.equals(other.questionText))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "QuestionType [id=" + id + ", questionText=" + questionText + "]";
    }

}
