package pers.leonzlhao.mahjongcalculator;

import java.util.ArrayList;
import java.util.List;

public class GameManager implements PlayerObserver {
    private static GameManager instance = new GameManager();
    public List<Player> players;

    private GameManager () {
        players = new ArrayList<>();
        for(int i = 0; i < 4; i++) {
            players.add(new Player(i, this));
        }
    }

    public static GameManager getInstance() {
        return instance;
    }

    public void addPlayer(Player replacedPlayer) {
        replacedPlayer.isOnline = false;
    }

    @Override
    public void onSelfTouch(Player touchPlayer, int score) {
        for(Player player: players) {
            if(player != touchPlayer) {
                if (player.isOnline) {
                    player.points -= score;
                }
            }
        }
    }

    @Override
    public void onFireOff(int score, Player firedPlayer) {
        if(firedPlayer.isOnline) {
            firedPlayer.points -= score;
        }
    }

    @Override
    public void onFollowWind(Player dealer, int score) {
        for(Player player: players) {
            if(player != dealer) {
                if (player.isOnline) {
                    player.points += score;
                }
            }
        }
    }
}
