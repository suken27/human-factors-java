package com.suken27.humanfactorsjava.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

/**
 * Flyweight class that registers the intrinsic state of a human factor.
 */
@Entity
public class HumanFactorType {
    
    @Id
    @GeneratedValue
    private Long id;
    private String title;
    private String description;
    private boolean onlyOnce;
    private BibliographicSource bibliographicSource;
    private List<QuestionType> questionTypes;

    public HumanFactorType() {
        super();
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isOnlyOnce() {
        return onlyOnce;
    }

    public void setOnlyOnce(boolean onlyOnce) {
        this.onlyOnce = onlyOnce;
    }

    public BibliographicSource getBibliographicSource() {
        return bibliographicSource;
    }

    public void setBibliographicSource(BibliographicSource bibliographicSource) {
        this.bibliographicSource = bibliographicSource;
    }

    public List<QuestionType> getQuestionTypes() {
        return questionTypes;
    }

    public void setQuestionTypes(List<QuestionType> questionTypes) {
        this.questionTypes = questionTypes;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + (onlyOnce ? 1231 : 1237);
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
        HumanFactorType other = (HumanFactorType) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (onlyOnce != other.onlyOnce)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "HumanFactorType [id=" + id + ", title=" + title + ", description=" + description + ", onlyOnce="
                + onlyOnce + "]";
    }

}
