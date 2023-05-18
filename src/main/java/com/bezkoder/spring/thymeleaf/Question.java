package com.bezkoder.spring.thymeleaf;

public class Question {


    private String place;
    private String question;
    private String[] correctAnswers;
    private String hint;
    private String nextStep;


    public Question(String place, String question, String[] correctAnswers, String hint, String nextStep) {
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

    public String getNextStep() {
        return nextStep;
    }
}
