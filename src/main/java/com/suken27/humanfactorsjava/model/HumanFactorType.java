package com.suken27.humanfactorsjava.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

/**
 * Flyweight class that registers the intrinsic state of a Human factor.
 * @see HumanFactor
 */
@Entity
public class HumanFactorType {
    
    @Id
    private Long id;
    private String title;
    private String description;
    private boolean onlyOnce;
    private Cluster cluster;
    @ManyToMany
    private List<BibliographicSource> bibliographicSource;
    @OneToMany
    private List<QuestionType> questionTypes;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<HumanFactorType> dependsOn;

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

    public Cluster getCluster() {
        return cluster;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    public List<BibliographicSource> getBibliographicSource() {
        return bibliographicSource;
    }

    public void setBibliographicSource(List<BibliographicSource> bibliographicSource) {
        this.bibliographicSource = bibliographicSource;
    }

    public List<QuestionType> getQuestionTypes() {
        return questionTypes;
    }

    public void setQuestionTypes(List<QuestionType> questionTypes) {
        this.questionTypes = questionTypes;
    }

    public List<HumanFactorType> getDependsOn() {
        return dependsOn;
    }

    public void setDependsOn(List<HumanFactorType> dependsOn) {
        this.dependsOn = dependsOn;
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
