package persistence;

public class ScoreEntry {
    public String initials;
    public int score;

    public ScoreEntry(String initials, int score) {
        this.initials = initials.toUpperCase();
        this.score = score;
    }
}