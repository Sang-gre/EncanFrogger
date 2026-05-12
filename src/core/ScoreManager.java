package core;

public class ScoreManager {

    private int score;
    private int previousScore;
    private int totalScore;

    private int highestLaneReached;
    private static final int LANES = 10;

    private static final int POINTS_FORWARD_HOP = 10;
    private static final int POINTS_COIN = 50;
    private static final int POINTS_REACH_TOP = 500;
    private static final int POINTS_TIME_BONUS = 20;
    private static final int POINTS_LEVEL_BONUS = 100;

    private static final int TIME_LIMIT_SECONDS = 30;
    private float timeRemaining;
    private boolean timerRunning;

    public ScoreManager() {
        reset();
    }

    public void reset() {
        score = 0;
        previousScore = 0;
        totalScore = 0;
        highestLaneReached = LANES - 1;
        timeRemaining = TIME_LIMIT_SECONDS;
        timerRunning = true;
    }

    public void resetCrossing() {
        highestLaneReached = LANES - 1;
        timeRemaining = TIME_LIMIT_SECONDS;
        timerRunning = true;
    }

    public void updateTimer(float deltaSeconds) {
        if (!timerRunning) return;
        timeRemaining -= deltaSeconds;
        if (timeRemaining < 0) timeRemaining = 0;
    }

    public void onPlayerMovedToLane(int laneIndex) {
        if (laneIndex < highestLaneReached) {
            score += POINTS_FORWARD_HOP;
            highestLaneReached = laneIndex;
        }
    }

    public void onReachedTop(int currentLevel) {
        previousScore = score;
        int bonus = POINTS_REACH_TOP
                  + currentLevel * POINTS_LEVEL_BONUS
                  + Math.round(timeRemaining) * POINTS_TIME_BONUS;
        totalScore += previousScore + bonus;
        resetCrossing();
    }

    public void onCoinCollected() {
        score += POINTS_COIN;
    }

    public void onPlayerDied() {
        resetCrossing();
    }

    public int getScore() { 
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getPreviousScore() {
        return previousScore;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public float getTimeRemaining() {
        return timeRemaining;
    }

    public boolean isTimeUp() {
        return timeRemaining <= 0;
    }
}
