package core.logic;

/* Tracks score, time, and lane progress for every run */
public class ScoreManager {

    // --- Constants ---
    private static final int LANES = 10;
    private static final int TIME_LIMIT_SECONDS = 30;
    private static final int POINTS_FORWARD_HOP = 10;
    private static final int POINTS_COIN = 50;
    private static final int POINTS_REACH_TOP = 500;
    private static final int POINTS_TIME_BONUS = 20;
    private static final int POINTS_LEVEL_BONUS = 100;

    // --- State ---
    private int levelScore;
    private int previousScore;
    private int totalScore;
    private int highestLaneReached;
    private float timeRemaining;
    private boolean timerRunning;

    public ScoreManager() {
        levelScore = 0;
        previousScore = 0;
        totalScore = 0;
        highestLaneReached = LANES - 1;
        timeRemaining = TIME_LIMIT_SECONDS;
        timerRunning = true;
    }

    // -------------------------------------------------------------------------
    // Reset
    // -------------------------------------------------------------------------
    public void reset() {
        levelScore = 0;
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

    // -------------------------------------------------------------------------
    // Game events
    // -------------------------------------------------------------------------
    public void onPlayerMovedToLane(int laneIndex) {
        if (laneIndex < highestLaneReached) {
            levelScore += POINTS_FORWARD_HOP;
            highestLaneReached = laneIndex;
        }
    }

    public void onReachedTop(int currentLevel) {
        previousScore = totalScore;

        int bonus = POINTS_REACH_TOP
                + currentLevel * POINTS_LEVEL_BONUS
                + Math.round(timeRemaining) * POINTS_TIME_BONUS;

        totalScore = previousScore + levelScore + bonus;
        levelScore = 0;
        resetCrossing();
    }

    public void onCoinCollected() {
        levelScore += POINTS_COIN;
    }

    public void onPlayerDied() {
        resetCrossing();
    }

    // -------------------------------------------------------------------------
    // Timer
    // -------------------------------------------------------------------------
    public void updateTimer(float deltaSeconds) {
        if (!timerRunning)
            return;
        timeRemaining = Math.max(0, timeRemaining - deltaSeconds);
    }

    // -------------------------------------------------------------------------
    // Getters / Setters
    // -------------------------------------------------------------------------
    public int getScore() {
        return levelScore;
    }

    public void setScore(int score) {
        this.levelScore = score;
    }

    public int getPreviousScore() {
        return previousScore;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int score) {
        this.totalScore = score;
    }

    public float getTimeRemaining() {
        return timeRemaining;
    }

    public boolean isTimeUp() {
        return timeRemaining <= 0;
    }
}