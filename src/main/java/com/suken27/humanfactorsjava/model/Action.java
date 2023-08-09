package com.suken27.humanfactorsjava.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Action {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private ActionType type;

    /**
     * This constructor should never be used. Use Action(ActionType) instead.
	 * This constructor cannot be removed as hibernate uses the default constructor to instantiate entities.
     */
    public Action() {
        super();
    }

    public Action(ActionType actionType) {
        super();
        type = actionType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ActionType getType() {
        return type;
    }

    public void setType(ActionType type) {
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
        Action other = (Action) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Action [id=" + id + "]";
    }

    
    
}
