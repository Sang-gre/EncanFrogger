package core;

public class ScoreManager {

    private int score;

    // forward hop tracking
    private int highestLaneReached; // lowest lane index = furthest forward
    private static final int LANES = 10;

    // scoring constants
    private static final int POINTS_FORWARD_HOP = 10;
    private static final int POINTS_COIN = 50;
    private static final int POINTS_REACH_TOP = 500;
    private static final int POINTS_TIME_BONUS = 20; // per second remaining
    private static final int POINTS_LEVEL_BONUS = 100; // multiplied by level

    // countdown timer (in seconds)
    private static final int TIME_LIMIT_SECONDS = 30;
    private float timeRemaining;
    private boolean timerRunning;

    public ScoreManager() {
        reset();
    }

    // full reset for a new game run
    public void reset() {
        score = 0;
        highestLaneReached = LANES - 1;
        timeRemaining = TIME_LIMIT_SECONDS;
        timerRunning = true;
    }

    // only resets the crossing state, keeps the score
    public void resetCrossing() {
        highestLaneReached = LANES - 1;
        timeRemaining = TIME_LIMIT_SECONDS;
        timerRunning = true;
    }

    // call once per game loop tick, passing delta time in seconds
    public void updateTimer(float deltaSeconds) {
        if (!timerRunning)
            return;
        timeRemaining -= deltaSeconds;
        if (timeRemaining < 0)
            timeRemaining = 0;
    }

    // call every time the player moves to a new lane
    // laneIndex: 0 = top (goal), LANES-1 = bottom (start)
    public void onPlayerMovedToLane(int laneIndex) {
        if (laneIndex < highestLaneReached) {
            // player moved forward (toward top)
            score += POINTS_FORWARD_HOP;
            highestLaneReached = laneIndex;
        }
    }

    // call when player reaches lane 0
    public void onReachedTop(int currentLevel) {
        score += POINTS_REACH_TOP;
        score += currentLevel * POINTS_LEVEL_BONUS;
        score += (int) timeRemaining * POINTS_TIME_BONUS;
        resetCrossing(); // keep score, just reset timer and hop tracking
    }

    // call when a coin is collected
    public void onCoinCollected() {
        score += POINTS_COIN;
    }

    // call when player dies — reset their hop progress and timer
    public void onPlayerDied() {
        resetCrossing();
    }

    public int getScore() {
        return score;
    }

    public float getTimeRemaining() {
        return timeRemaining;
    }

    public boolean isTimeUp() {
        return timeRemaining <= 0;
    }
}