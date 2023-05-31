package com.bezkoder.spring.thymeleaf;

public class Question {


    private String title;
    private String place;
    private String question;
    private String[] correctAnswers;
    private String hint;


    public Question(String title, String place, String question, String[] correctAnswers, String hint) {
        this.title = title;
        this.place = place;
        this.question = question;
        this.correctAnswers = correctAnswers;
        this.hint = hint;
    }

    public String getQuestion() {
        return question;
    }

    public String[] getCorrectAnswers() {
        return correctAnswers;
    }

    public String getHint() {
        return hint;
    }

    public String getPlace() {
        return place;
    }

    public String getTitle() {
        return title;
    }
}
