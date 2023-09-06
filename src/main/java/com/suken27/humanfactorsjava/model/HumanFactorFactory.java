package com.suken27.humanfactorsjava.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.suken27.humanfactorsjava.repository.HumanFactorTypeRepository;

@Component
public class HumanFactorFactory {

    @Autowired
    private HumanFactorTypeRepository humanFactorTypeRepository;
    
    private List<HumanFactorType> all;

    public List<HumanFactorType> getAll() {
        if(all == null) {
            all = humanFactorTypeRepository.findAll();
            List<HumanFactorType> actions = humanFactorTypeRepository.findAllFetchActions(all);
            List<HumanFactorType> questions = humanFactorTypeRepository.findAllFetchQuestions(all);
            List<HumanFactorType> dependsOn = humanFactorTypeRepository.findAllFetchDependsOn(all);
            List<HumanFactorType> bibliographicSource = humanFactorTypeRepository.findAllFetchBibliographicSource(all);
            for(int i = 0; i < all.size(); i++) {
                all.get(i).setActionTypes(actions.get(i).getActionTypes());
                all.get(i).setQuestionTypes(questions.get(i).getQuestionTypes());
                all.get(i).setDependsOn(dependsOn.get(i).getDependsOn());
                all.get(i).setBibliographicSource(bibliographicSource.get(i).getBibliographicSource());
            }
        }
        
        return all;
    }

    public List<HumanFactor> createInstances() {
        List<HumanFactor> humanFactors = new ArrayList<>();
        for (HumanFactorType humanFactorType : getAll()) {
            humanFactors.add(humanFactorType.createInstance());
        }
        return humanFactors;
    }

    public List<TeamHumanFactor> createTeamInstances() {
        Map<HumanFactorType, TeamHumanFactor> map = new HashMap<>();
        List<TeamHumanFactor> humanFactors = new ArrayList<>();
        for (HumanFactorType humanFactorType : getAll()) {
            TeamHumanFactor humanFactor = humanFactorType.createTeamInstance();
            map.put(humanFactorType, humanFactor);
            humanFactors.add(humanFactor);
        }
        // Adds the depending factors objects with aliasing
        for (TeamHumanFactor humanFactor : humanFactors) {
            for (HumanFactorType humanFactorType : humanFactor.getType().getDependsOn()) {
                humanFactor.getDependingFactors().add(map.get(humanFactorType));
            }
        }
        return humanFactors;
    }

}
