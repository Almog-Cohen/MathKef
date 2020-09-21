package com.thegalos.mathkef.objects;

public class Score {
    private String playerNames;
    private int score;
    private int questionAnswered;



    public Score() {
    }

    public String getPlayerNames() {
        return playerNames;
    }
    public int getScore() {
        return score;
    }

    public void setPlayerNames(String playerNames) {
        this.playerNames = playerNames;
    }
    public void setScore(int score) {
        this.score = score;
    }
    public void setQuestionAnswered(int questionAnswered) {
        this.questionAnswered = questionAnswered;
    }

}
