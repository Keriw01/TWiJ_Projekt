package com.example.twij_projekt;

public class QuizOption {
    private String text;
    private boolean isCorrect;
    private boolean isChecked;  // Dodane pole do przechowywania informacji o zaznaczeniu opcji

    public QuizOption(String text, boolean isCorrect) {
        this.text = text;
        this.isCorrect = isCorrect;
        this.isChecked = false;  // Domyślnie opcja nie jest zaznaczona
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