package persistence;

public class Score {
    private int currentScore;

    public void addScore(int score){
        currentScore += score;
    }

    public int getScore() {
        return currentScore;
    }

    public void reset() {
        currentScore = 0;
    }
}
