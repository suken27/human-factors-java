package com.suken27.humanfactorsjava.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Flyweight class that registers the intrinsic state of a Human factor.
 * @see HumanFactor
 */
@Entity
@Data
@EqualsAndHashCode(exclude = {"bibliographicSource", "questionTypes", "affectsTo", "actionTypes"})
@ToString(exclude = {"bibliographicSource", "questionTypes", "affectsTo", "actionTypes"})
public class HumanFactorType {
    
    @Id
    private Long id;
    private String title;
    private String description;
    private boolean onlyOnce;
    private Cluster cluster;
    @ManyToMany
    private List<BibliographicSource> bibliographicSource;
    @OneToMany
    private List<QuestionType> questionTypes;
    @ManyToMany
    private List<HumanFactorType> affectsTo;
    @ManyToMany
    private List<ActionType> actionTypes;

    /**
     * Creates an instance of human factor with this type as intrinsic state.
     * It also creates the instances for the Questions and Actions associated.
     * @return Human factor (extrinsic state) with this object as intrinsic state.
     * @see Question
     * @see Answer
     */
    public HumanFactor createInstance() {
        HumanFactor humanFactor = new HumanFactor(this);
        ArrayList<Question> questions = new ArrayList<>();
        for (QuestionType questionType : getQuestionTypes()) {
            questions.add(questionType.createInstance());
        }
        humanFactor.setQuestions(questions);
        return humanFactor;
    }

    public TeamHumanFactor createTeamInstance() {
        TeamHumanFactor humanFactor = new TeamHumanFactor(this);
        ArrayList<Action> actions = new ArrayList<>();
        for (ActionType actionType : getActionTypes()) {
            actions.add(actionType.createInstance());
        }
        humanFactor.setActions(actions);
        return humanFactor;
    }

    public boolean isMemberMeasured() {
        for(QuestionType questionType : getQuestionTypes()) {
            if(!questionType.isOnlyForManagement()) {
                return true;
            }
        }
        return false;
    }

}
