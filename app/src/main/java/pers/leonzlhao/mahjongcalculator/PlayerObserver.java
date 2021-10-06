package pers.leonzlhao.mahjongcalculator;

public interface PlayerObserver {
    void onSelfTouch(Player touchPlayer, int score);
    void onFireOff(int score, Player firedPlayer);
    void onFollowWind(Player dealer, int score);
    void onBackOnline(Player replacedPlayer);
}
