package Service.helper;

import Model.Task;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class TaskSearchHelper {

    public static List<Task> searchAndSort(List<Task> cachedTasks, String searchKey) {
        String key = normalize(searchKey);
        List<TaskScore> matched = new ArrayList<>();

        for (Task t : cachedTasks) {
            String title = normalize(t.getTitle());
            String desc = normalize(t.getDescription());

            int score = matchScore(title, key) * 2 + matchScore(desc, key);
            if (!key.isEmpty() && score == 0) continue;

            matched.add(new TaskScore(t, score));
        }

        // Dữ liệu Sort
        matched.sort((a, b) -> {
            if (!key.isEmpty()) {
                int s = Integer.compare(b.score, a.score);
                if (s != 0) return s;
            }

            int p = Integer.compare(getPriorityOrder(a.task), getPriorityOrder(b.task));
            if (p != 0) return p;

            if (a.task.getDeadline() == null && b.task.getDeadline() == null) {
                return Integer.compare(a.task.getId(), b.task.getId());
            }
            if (a.task.getDeadline() == null) return 1;
            if (b.task.getDeadline() == null) return -1;

            int dateCompare = a.task.getDeadline().compareTo(b.task.getDeadline());
            if (dateCompare != 0) return dateCompare;

            return Integer.compare(a.task.getId(), b.task.getId());
        });

        List<Task> result = new ArrayList<>();
        for (TaskScore ts : matched) {
            result.add(ts.task);
        }
        return result;
    }

    public static String normalize(String text) {
        if (text == null) return "";
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        normalized = normalized.replaceAll("[^a-zA-Z0-9 ]", "");
        return normalized.toLowerCase().trim();
    }

    private static int matchScore(String text, String keyword) {
        if (keyword.isEmpty()) return 0;
        int score = 0;
        String[] words = keyword.split("\\s+");

        for (String w : words) {
            if (text.contains(w)) score += 20;
            else if (fuzzyMatch(text, w)) score += 10;
        }
        if (text.startsWith(keyword)) score += 30;
        return score;
    }

    private static boolean fuzzyMatch(String text, String keyword) {
        if (keyword.isEmpty()) return true;
        int t = 0, k = 0;
        while (t < text.length() && k < keyword.length()) {
            if (text.charAt(t) == keyword.charAt(k)) k++;
            t++;
        }
        return k == keyword.length();
    }

    private static int getPriorityOrder(Task t) {
        if (t.getPriority() == null) return 4;
        return switch (t.getPriority()) {
            case HIGH -> 1;
            case MEDIUM -> 2;
            case LOW -> 3;
        };
    }

    private static class TaskScore {
        Task task;
        int score;
        TaskScore(Task t, int s) { this.task = t; this.score = s; }
    }
}