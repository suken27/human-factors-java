package com.suken27.humanfactorsjava.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.suken27.humanfactorsjava.model.HumanFactorType;

public interface HumanFactorTypeRepository extends JpaRepository<HumanFactorType, Long> {
    
}
