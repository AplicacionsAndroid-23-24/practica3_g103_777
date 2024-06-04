package com.tecnocampus.practica3_g103_777;

public class Score {
    private String email;
    private int correctAnswers;

    public Score() {
        // Public no-arg constructor needed
    }

    public Score(String email, int correctAnswers) {
        this.email = email;
        this.correctAnswers = correctAnswers;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }
}
