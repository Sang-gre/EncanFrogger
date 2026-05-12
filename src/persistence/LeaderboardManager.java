package persistence;

import java.io.*;
import java.util.*;

public class LeaderboardManager {

    private static final Object LOCK = new Object();
    private static final String FILE_PATH = "data/data.txt";
    private static final int MAX_ENTRIES = 100;

    public static void saveEntry(ScoreEntry entry) {
        List<ScoreEntry> entries = loadAll();
        entries.add(entry);

        entries.sort((a, b) -> Integer.compare(b.score, a.score));

        if (entries.size() > MAX_ENTRIES)
            entries = entries.subList(0, MAX_ENTRIES);

        writeAll(entries);
    }

    public static void upsertEntry(ScoreEntry newEntry) {
        synchronized (LOCK) {
            List<ScoreEntry> entries = loadAll();
            boolean found = false;

            for (int i = 0; i < entries.size(); i++) {
                if (entries.get(i).initials.trim().equalsIgnoreCase(newEntry.initials.trim())) {
                    ScoreEntry existing = entries.get(i);

                    entries.set(i, new ScoreEntry(
                            existing.initials,
                            newEntry.score,
                            newEntry.level,
                            newEntry.isAlive,
                            newEntry.coins * 50));
                    found = true;
                    break;
                }
            }

            if (!found)
                entries.add(new ScoreEntry(
                        newEntry.initials,
                        newEntry.score,
                        newEntry.level,
                        newEntry.isAlive,
                        newEntry.coins * 50
                ));

            entries.sort((a, b) -> b.score - a.score);
            if (entries.size() > MAX_ENTRIES)
                entries = entries.subList(0, MAX_ENTRIES);
            writeAll(entries);
        }
    }

    public static List<ScoreEntry> loadAll() {
        List<ScoreEntry> entries = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        File f = new File(FILE_PATH);
        if (!f.exists())
            return entries;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length == 5) {
                    try {
                        String initials = parts[0].trim();
                        int score = Integer.parseInt(parts[1].trim());
                        int level = Integer.parseInt(parts[2].trim());
                        boolean isAlive = Boolean.parseBoolean(parts[3].trim());
                        int coins = Integer.parseInt(parts[4].trim());

                        if (!seen.contains(initials.toLowerCase())) {
                            entries.add(new ScoreEntry(initials, score, level, isAlive, coins));
                            seen.add(initials.toLowerCase());
                        }

                    } catch (Exception ex) {
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
        synchronized (LOCK) {
            File f = new File(FILE_PATH);
            f.getParentFile().mkdirs();
            try (PrintWriter pw = new PrintWriter(new FileWriter(f))) {
                for (ScoreEntry e : entries) {
                    pw.printf("%s,%d,%d,%b,%d%n",
                            e.initials,
                            e.score,
                            e.level,
                            e.isAlive,
                            e.coins);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}