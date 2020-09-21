package com.thegalos.mathkef.objects;

public class Room {
    private String roomName;
    private String numOfPlayers;
    private String status;




    public Room() {
    }

    public String getRoomName() {
        return roomName;
    }
    public String getNumOfPlayers() {
        return numOfPlayers;
    }
    public String getStatus() {
        return status;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
    public void setNumOfPlayers(String numOfPlayers) {
        this.numOfPlayers = numOfPlayers;
    }
    public void setStatus(String status) {
        this.status = status;
    }

}
