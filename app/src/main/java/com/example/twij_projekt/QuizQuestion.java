package com.example.twij_projekt;

import java.util.ArrayList;

public class QuizQuestion {
    private String questionText;
    private ArrayList<String> options;
    private int correctOptionIndex;

    public QuizQuestion(String questionText, ArrayList<String> options, int correctOptionIndex) {
        this.questionText = questionText;
        this.options = options;
        this.correctOptionIndex = correctOptionIndex;
    }

    public String getQuestionText() {
        return questionText;
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    public int getCorrectOptionIndex() {
        return correctOptionIndex;
    }
}
