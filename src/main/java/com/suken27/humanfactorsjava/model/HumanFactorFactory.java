package com.suken27.humanfactorsjava.model;

import java.util.ArrayList;
import java.util.List;

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

}
