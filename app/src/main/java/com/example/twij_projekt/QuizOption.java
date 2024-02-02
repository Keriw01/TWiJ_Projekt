package com.example.twij_projekt;

public class QuizOption {
    private String text;
    private boolean isCorrect;
    private boolean isChecked;

    public QuizOption(String text, boolean isCorrect) {
        this.text = text;
        this.isCorrect = isCorrect;
        this.isChecked = false;
    }

    public String getText() {
        return text;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}