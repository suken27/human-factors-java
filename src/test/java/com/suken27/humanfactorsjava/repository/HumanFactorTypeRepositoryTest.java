package com.suken27.humanfactorsjava.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.suken27.humanfactorsjava.model.HumanFactorType;

@SpringBootTest
public class HumanFactorTypeRepositoryTest {

    @Autowired
    private HumanFactorTypeRepository repository;

    private static final Logger logger = LoggerFactory.getLogger(HumanFactorTypeRepositoryTest.class);

    @Test
    public void testContent() {
        List<HumanFactorType> entities = repository.findAll();
        assertNotNull(entities);
        List<HumanFactorType> dependencies;
        for (HumanFactorType entity : entities) {
            assertNotNull(entity);
            dependencies = entity.getDependsOn();
            assertNotNull(dependencies);
            if(dependencies.size() > 0) {
                for (HumanFactorType dependency : dependencies) {
                    logger.debug("{} -> {}", entity.getTitle(), dependency.getTitle());
                }
            }
        }
        assertNotEquals(0, entities.size());
    }

}
