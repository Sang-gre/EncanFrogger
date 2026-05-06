package persistence;

import java.io.*;
import java.util.*;

public class LeaderboardManager {

    private static final String FILE_PATH = "data/scores.txt";
    private static final int MAX_ENTRIES = 100;

    public static void saveEntry(ScoreEntry entry) {
        List<ScoreEntry> entries = loadAll();
        entries.add(entry);
        entries.sort((a, b) -> b.score - a.score);
        if (entries.size() > MAX_ENTRIES)
            entries = entries.subList(0, MAX_ENTRIES);
        writeAll(entries);
    }

    public static List<ScoreEntry> loadAll() {
        List<ScoreEntry> entries = new ArrayList<>();
        File f = new File(FILE_PATH);
        if (!f.exists())
            return entries;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    try {
                        entries.add(new ScoreEntry(parts[0], Integer.parseInt(parts[1].trim())));
                    } catch (NumberFormatException ex) {
                        System.err.println("Skipping corrupted line: " + line);
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return entries;
    }

    private static void writeAll(List<ScoreEntry> entries) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (ScoreEntry e : entries) {
                pw.printf("%s,%d%n", e.initials, e.score);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}