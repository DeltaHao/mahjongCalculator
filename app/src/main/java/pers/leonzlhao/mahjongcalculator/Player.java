package pers.leonzlhao.mahjongcalculator;

public class Player {
    public int index;
    public String nickName;
    public int points = 0;
    public boolean isOnline = true;
    private static PlayerObserver mObserver;

    public Player(int i, PlayerObserver observer) {
        index = i;
        nickName = "player" + i;
        mObserver = observer;
    }

    public void selfTouch(int score) {
        this.points += score * 3;
        mObserver.onSelfTouch(this, score);
    }

    public void fireOff(int score, Player firedPlayer) {
        this.points += score;
        mObserver.onFireOff(score, firedPlayer);
    }

    public void followWind(int score) {
        this.points -= score * 3;
        mObserver.onFollowWind(this, score);
    }
}
