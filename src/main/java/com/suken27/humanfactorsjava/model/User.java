package com.suken27.humanfactorsjava.model;

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
     * This constructor cannot be removed as hibernate uses the default constructor to instantiate entities.
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
