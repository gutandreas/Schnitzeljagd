package com.bezkoder.spring.thymeleaf;

public class Question {


    private String title;
    private String place;
    private String question;
    private String[] correctAnswers;
    private String hint;
    private String nextStep;


    public Question(String title, String place, String question, String[] correctAnswers, String hint, String nextStep) {
        this.title = title;
        this.place = place;
        this.question = question;
        this.correctAnswers = correctAnswers;
        this.hint = hint;
        this.nextStep = nextStep;
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

    public String getNextStep() {
        return nextStep;
    }
}
