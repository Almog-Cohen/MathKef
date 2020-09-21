package com.thegalos.mathkef.objects;

public class MultiChoiceQuestion {
    private String question;
    private String correct;
    private String wrong1;
    private String wrong2;
    private String wrong3;

    public MultiChoiceQuestion() {
    }

    public String getQuestion() {
        return question;
    }
    public String getCorrect() {
        return correct;
    }
    public String getWrong1() {
        return wrong1;
    }
    public String getWrong2() {
        return wrong2;
    }
    public String getWrong3() {
        return wrong3;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
    public void setCorrect(String correct) {
        this.correct = correct;
    }
    public void setWrong1(String wrong1) {
        this.wrong1 = wrong1;
    }
    public void setWrong2(String wrong2) {
        this.wrong2 = wrong2;
    }
    public void setWrong3(String wrong3) {
        this.wrong3 = wrong3;
    }

}
