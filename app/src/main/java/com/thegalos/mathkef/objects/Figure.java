package com.thegalos.mathkef.objects;

public class Figure {

    String figureName;
    boolean isPlaying;

    public Figure() {
    }

    public String getFigureName() {
        return figureName;
    }

    public void setFigureName(String figureName) {
        this.figureName = figureName;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }
}
