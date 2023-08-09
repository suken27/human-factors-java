package com.suken27.humanfactorsjava.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Question {
    
    @Id
    @GeneratedValue
    private Long id;
    // This list should be a stack, but hibernate does not persist stacks. This list should be used as Last Input First Output.
    @OneToMany(cascade = CascadeType.ALL)
    private List<Answer> answers;
    @ManyToOne
    private QuestionType type;

    /**
     * This constructor should never be used. Use Question(QuestionType) instead.
	 * This constructor cannot be removed as hibernate uses the default constructor to instantiate entities.
     */
    public Question() {
        super();
    }

    public Question(QuestionType questionType) {
        super();
        answers = new ArrayList<>();
        type = questionType;
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
