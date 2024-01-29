package com.example.twij_projekt;

import java.util.ArrayList;

public class QuizQuestion {
    private String questionText;
    private ArrayList<QuizOption> options;

    public QuizQuestion(String questionText, ArrayList<QuizOption> options) {
        this.questionText = questionText;
        this.options = options;
    }

    public String getQuestionText() {
        return questionText;
    }

    public ArrayList<QuizOption> getOptions() {
        return options;
    }
}

