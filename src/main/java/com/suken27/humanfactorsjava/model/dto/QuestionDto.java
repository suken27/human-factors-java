package com.suken27.humanfactorsjava.model.dto;

import java.util.List;

import com.suken27.humanfactorsjava.model.Question;
import com.suken27.humanfactorsjava.model.TypeOfAnswer;

import lombok.Data;

@Data
public class QuestionDto {
    
    private Long id;
    private String questionText;
    private List<String> options;

    public QuestionDto(Question entity) {
        super();
        this.id = entity.getId();
        this.questionText = entity.getType().getQuestionText();
        this.options = getOptions(entity.getType().getTypeOfAnswer());
    }

    public static List<QuestionDto> toDto(List<Question> entities) {
        return entities.stream().map(QuestionDto::new).toList();
    }

    private List<String> getOptions(TypeOfAnswer typeOfAnswer) {
        switch (typeOfAnswer) {
            case VALUE_RANGE:
                return List.of("Completely", "Mostly", "Somewhat", "A Little", "Not At All");
            case FREQUENCY_RANGE:
                return List.of("Always", "Frequently", "Sometimes", "Rarely", "Never");
            case BINARY:
                return List.of("Yes", "No");
            default:
                throw new IllegalArgumentException("Type of answer " + typeOfAnswer + " not supported");
        }
    }

}
