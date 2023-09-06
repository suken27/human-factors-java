package com.suken27.humanfactorsjava.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class TeamHumanFactor {
    
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private HumanFactorType type;
    @ManyToMany(cascade = CascadeType.ALL)
    private List<TeamHumanFactor> dependingFactors;
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Action> actions;
    private boolean isFullyMeasured;
    private double score;

    /**
     * This constructor should never be used. Use TeamHumanFactor(HumanFactorType) instead.
     */
    public TeamHumanFactor() {
        super();
    }

    public TeamHumanFactor(HumanFactorType type) {
        this.type = type;
        dependingFactors = new ArrayList<>();
    }

}
