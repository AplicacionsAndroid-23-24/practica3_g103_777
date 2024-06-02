package com.tecnocampus.practica3_g103_777;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Question {
    private String questionText;
    private String correctAnswer;
    private List<String> incorrectAnswers;

    public Question(String questionText, String correctAnswer, List<String> incorrectAnswers) {
        this.questionText = questionText;
        this.correctAnswer = correctAnswer;
        this.incorrectAnswers = incorrectAnswers;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public List<String> getAllOptions() {
        List<String> allOptions = new ArrayList<>(incorrectAnswers);
        allOptions.add(correctAnswer);
        Collections.shuffle(allOptions);
        return allOptions;
    }
}

