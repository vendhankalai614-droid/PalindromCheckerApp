import java.util.*;
#problem 1
public class UsernameChecker {

    private HashMap<String, Integer> users = new HashMap<>();
    private HashMap<String, Integer> attempts = new HashMap<>();

    public void addUser(String username, int userId) {
        users.put(username, userId);
    }

    public boolean checkAvailability(String username) {
        attempts.put(username, attempts.getOrDefault(username, 0) + 1);
        return !users.containsKey(username);
    }

    public List<String> suggestAlternatives(String username) {
        List<String> suggestions = new ArrayList<>();

        if (!users.containsKey(username + "1"))
            suggestions.add(username + "1");

        if (!users.containsKey(username + "2"))
            suggestions.add(username + "2");

        if (!users.containsKey(username.replace("_", ".")))
            suggestions.add(username.replace("_", "."));

        return suggestions;
    }

    public String getMostAttempted() {
        String most = "";
        int max = 0;

        for (Map.Entry<String, Integer> entry : attempts.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                most = entry.getKey();
            }
        }

        return most;
    }

    public static void main(String[] args) {

        UsernameChecker checker = new UsernameChecker();

        checker.addUser("john_doe", 1);
        checker.addUser("admin", 2);

        System.out.println(checker.checkAvailability("john_doe"));
        System.out.println(checker.checkAvailability("jane_smith"));

        System.out.println(checker.suggestAlternatives("john_doe"));

        checker.checkAvailability("admin");
        checker.checkAvailability("admin");

        System.out.println(checker.getMostAttempted());
    }
}
