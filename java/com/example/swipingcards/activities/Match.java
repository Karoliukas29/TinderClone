package com.example.swipingcards.activities;

public class Match {
    public String userOne, userTwo, matchId, matchTime;


    public Match(String userOne, String userTwo, String matchId, String matchTime) {
        this.userOne = userOne;
        this.userTwo = userTwo;
        this.matchId = matchId;
        this.matchTime = matchTime;
    }

    public Match() {

    }
}
