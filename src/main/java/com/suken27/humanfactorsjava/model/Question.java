package com.suken27.humanfactorsjava.model;

import java.util.ArrayDeque;
import java.util.Deque;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Question {
    
    @Id
    @GeneratedValue
    private Long id;
    private Deque<Answer> answers;

    public Question() {
        super();
        answers = new ArrayDeque<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Deque<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(Deque<Answer> answers) {
        this.answers = answers;
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
