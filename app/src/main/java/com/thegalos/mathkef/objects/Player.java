package com.thegalos.mathkef.objects;

import java.io.Serializable;

public class Player implements Serializable {
    private String PlayerUID;
    private String PlayerName;
    private String PlayerFigure;

    public Player() {
    }

    public String getPlayerUID() {
        return PlayerUID;
    }
    public String getPlayerName() {
        return PlayerName;
    }
    public String getPlayerFigure() {
        return PlayerFigure;
    }

    public void setPlayerUID(String playerUID) {
        this.PlayerUID = playerUID;
    }
    public void setPlayerName(String playerName) {
        this.PlayerName = playerName;
    }
    public void setPlayerFigure(String playerFigure) { PlayerFigure = playerFigure; }

}
