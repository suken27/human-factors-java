package com.suken27.humanfactorsjava.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.suken27.humanfactorsjava.model.HumanFactorType;

public interface HumanFactorTypeRepository extends JpaRepository<HumanFactorType, Long> {

    @Query("Select hft from HumanFactorType hft join fetch hft.bibliographicSource join fetch hft.questionTypes join fetch hft.dependsOn join fetch hft.actionTypes")
    public List<HumanFactorType> findAllFetchEverything();
    
}
