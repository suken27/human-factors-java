package com.suken27.humanfactorsjava.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.suken27.humanfactorsjava.model.HumanFactorType;

public interface HumanFactorTypeRepository extends JpaRepository<HumanFactorType, Long> {

    @Query("Select hft from HumanFactorType hft join fetch hft.questionTypes where hft in ?1")
    public List<HumanFactorType> findAllFetchQuestions(List<HumanFactorType> humanFactorTypes);

    @Query("Select hft from HumanFactorType hft join fetch hft.actionTypes where hft in ?1")
    public List<HumanFactorType> findAllFetchActions(List<HumanFactorType> humanFactorTypes);

    @Query("Select hft from HumanFactorType hft join fetch hft.dependsOn where hft in ?1")
    public List<HumanFactorType> findAllFetchDependsOn(List<HumanFactorType> humanFactorTypes);

    @Query("Select hft from HumanFactorType hft join fetch hft.bibliographicSource where hft in ?1")
    public List<HumanFactorType> findAllFetchBibliographicSource(List<HumanFactorType> humanFactorTypes);
    
}
