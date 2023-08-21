package com.suken27.humanfactorsjava.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
// The word 'user' is a reserved keyword for H2 databases, so it needs to be
// replaced.
@Table(name = "Users")
public abstract class User {

    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false)
    private String email;
    @OneToMany(cascade = CascadeType.ALL)
    private List<HumanFactor> humanFactors;

    /**
     * This constructor should never be used. Use User(List<HumanFactor) instead.
     * This constructor cannot be removed as hibernate uses the default constructor
     * to instantiate entities.
     */
    protected User() {
        super();
    }

    protected User(List<HumanFactor> humanFactors) {
        super();
        this.humanFactors = humanFactors;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<HumanFactor> getHumanFactors() {
        return humanFactors;
    }

    public void setHumanFactors(List<HumanFactor> humanFactors) {
        this.humanFactors = humanFactors;
    }

    /**
     * Returns a random set of questions addressing some the less frequently
     * measured human factors. The questions are selected through a weighted random
     * selection (the less frequently measured human factors have a higher chance of
     * being selected).
     * 
     * @param numberOfQuestions Amount of questions to be returned.
     * @return A list of questions.
     * @see Question
     * @see HumanFactor
     */
    public List<Question> launchQuestions(int numberOfQuestions) {
        List<Question> launchQuestions = new ArrayList<>();
        List<Question> questions = new ArrayList<>();
        long totalWeight = 0;
        for (HumanFactor humanFactor : humanFactors) {
            questions.add(humanFactor.getOldestQuestion());
            // The +1 is to avoid 0 total weight if the questions are launched the same day
            // they are created.
            totalWeight += humanFactor.oldestQuestionDaysSinceLastAnswer() + 1;
        }
        double random;
        for (int i = 0; i < numberOfQuestions; i++) {
            random = Math.random() * totalWeight;
            for (Question question : questions) {
                random -= question.daysSinceLastAnswer() + 1;
                if (random <= 0.0d) {
                    launchQuestions.add(question);
                    questions.remove(question);
                    break;
                }
            }
        }
        return launchQuestions;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((email == null) ? 0 : email.hashCode());
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
        User other = (User) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (email == null) {
            if (other.email != null)
                return false;
        } else if (!email.equals(other.email))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", email=" + email + "]";
    }

}
