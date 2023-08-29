package com.suken27.humanfactorsjava.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Data
public class TeamMember extends User {

    @Column(nullable = false)
    private boolean deleted;

    private LocalDateTime deletionTime;

    @ManyToOne(fetch = FetchType.LAZY)
    private Team team;
    
    /**
     * This constructor should never be used. Use TeamMember(List<HumanFactor>) instead.
     * This constructor cannot be removed as hibernate uses the default constructor to instantiate entities.
     */
    public TeamMember() {
        super();
    }

    public TeamMember(List<HumanFactor> humanFactors) {
        super(humanFactors);
        deleted = false;
    }
    
}
