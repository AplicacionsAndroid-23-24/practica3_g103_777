package com.tecnocampus.practica3_g103_777;

public class Score {
    private String username;
    private int correctAnswers;

    public Score() {
        // Public no-arg constructor needed
    }

    public Score(String username, int correctAnswers) {
        this.username = username;
        this.correctAnswers = correctAnswers;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }
}
