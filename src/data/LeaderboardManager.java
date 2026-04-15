package data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class LeaderboardManager {
    private List<Integer> scores = new ArrayList<>();
    //private String filePath;

    public void addScore(int pts){
        scores.add(pts);
        Collections.sort(scores, Collections.reverseOrder());
    }

    public List<Integer> getTopScores() {
        return scores;
    }
}
