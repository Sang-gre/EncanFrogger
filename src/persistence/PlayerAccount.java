package persistence;

import java.util.ArrayList;
import gameobjects.Player;

public class PlayerAccount {
    private String initials;
    private int coins;
    private int highScore;
    private ArrayList<Player> unlockedChar;

    private static int counter = 0000;

    static {
        counter++;
    }

    public PlayerAccount(String initials) {
        this.initials = initials;
        this.coins = 0;
        this.highScore = 0;
        unlockedChar = new ArrayList<> ();
    }

    // getters & setters
    public String getInitials() { return initials; }
    public int getCoins() { return coins; }
    public int getHighScore() { return highScore; }

    public void addCoins(int amount) {
        coins += amount;
    }

    public void updateHighScore(int score) {
        if (score > highScore) {
            highScore = score;
        }
    }
}
