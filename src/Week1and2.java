// Problem 1:
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UsernameAvailabilityChecker {

    // username -> userId
    private ConcurrentHashMap<String, Integer> usernameMap;

    // username -> attempt count
    private ConcurrentHashMap<String, Integer> attemptFrequency;

    public UsernameAvailabilityChecker() {
        usernameMap = new ConcurrentHashMap<>();
        attemptFrequency = new ConcurrentHashMap<>();
    }

    // Check if username is available
    public boolean checkAvailability(String username) {

        // Track attempt count
        attemptFrequency.put(username,
                attemptFrequency.getOrDefault(username, 0) + 1);

        // O(1) lookup
        return !usernameMap.containsKey(username);
    }

    // Register new username
    public boolean registerUsername(String username, int userId) {

        if (usernameMap.containsKey(username)) {
            return false;
        }

        usernameMap.put(username, userId);
        return true;
    }

    // Suggest alternative usernames
    public List<String> suggestAlternatives(String username) {

        List<String> suggestions = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            String candidate = username + i;

            if (!usernameMap.containsKey(candidate)) {
                suggestions.add(candidate);
            }
        }

        String dotVersion = username.replace("_", ".");
        if (!usernameMap.containsKey(dotVersion)) {
            suggestions.add(dotVersion);
        }

        return suggestions;
    }

    // Get most attempted username
    public String getMostAttempted() {

        String result = "";
        int max = 0;

        for (Map.Entry<String, Integer> entry : attemptFrequency.entrySet()) {

            if (entry.getValue() > max) {
                max = entry.getValue();
                result = entry.getKey();
            }
        }

        return result + " (" + max + " attempts)";
    }

    // Demo
    public static void main(String[] args) {

        UsernameAvailabilityChecker system = new UsernameAvailabilityChecker();

        // Pre-existing users
        system.registerUsername("john_doe", 1);
        system.registerUsername("admin", 2);

        // Availability checks
        System.out.println("Check john_doe: " + system.checkAvailability("john_doe"));
        System.out.println("Check jane_smith: " + system.checkAvailability("jane_smith"));

        // Suggestions
        System.out.println("Suggestions for john_doe: "
                + system.suggestAlternatives("john_doe"));

        // Simulate attempts
        for (int i = 0; i < 5; i++)
            system.checkAvailability("admin");

        for (int i = 0; i < 3; i++)
            system.checkAvailability("guest");

        // Most attempted username
        System.out.println("Most attempted: " + system.getMostAttempted());
    }
}

// Problem 2:
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FlashSaleInventoryManager {

    // productId -> stockCount
    private ConcurrentHashMap<String, Integer> inventory;

    // productId -> waiting list (FIFO)
    private ConcurrentHashMap<String, Queue<Integer>> waitingList;

    public FlashSaleInventoryManager() {
        inventory = new ConcurrentHashMap<>();
        waitingList = new ConcurrentHashMap<>();
    }

    // Add product to inventory
    public void addProduct(String productId, int stock) {
        inventory.put(productId, stock);
        waitingList.put(productId, new LinkedList<>());
    }

    // Check stock availability
    public int checkStock(String productId) {
        return inventory.getOrDefault(productId, 0);
    }

    // Purchase item
    public synchronized String purchaseItem(String productId, int userId) {

        int stock = inventory.getOrDefault(productId, 0);

        if (stock > 0) {
            inventory.put(productId, stock - 1);

            return "Success! User " + userId +
                    " purchased item. Remaining stock: " + (stock - 1);
        }

        // Add to waiting list
        Queue<Integer> queue = waitingList.get(productId);
        queue.add(userId);

        return "Out of stock. User " + userId +
                " added to waiting list. Position #" + queue.size();
    }

    // View waiting list
    public void printWaitingList(String productId) {

        Queue<Integer> queue = waitingList.get(productId);

        if (queue.isEmpty()) {
            System.out.println("No users in waiting list.");
            return;
        }

        System.out.println("Waiting List:");
        int position = 1;

        for (Integer user : queue) {
            System.out.println("Position " + position + " : User " + user);
            position++;
        }
    }

    // Demo simulation
    public static void main(String[] args) {

        FlashSaleInventoryManager system = new FlashSaleInventoryManager();

        system.addProduct("IPHONE15_256GB", 3);

        System.out.println("Stock Available: "
                + system.checkStock("IPHONE15_256GB"));

        System.out.println(system.purchaseItem("IPHONE15_256GB", 101));
        System.out.println(system.purchaseItem("IPHONE15_256GB", 102));
        System.out.println(system.purchaseItem("IPHONE15_256GB", 103));

        // Stock finished
        System.out.println(system.purchaseItem("IPHONE15_256GB", 104));
        System.out.println(system.purchaseItem("IPHONE15_256GB", 105));

        system.printWaitingList("IPHONE15_256GB");
    }
}

// Problem 3:
import java.util.*;

public class DNSCache {

    // Entry class
    class DNSEntry {
        String domain;
        String ipAddress;
        long expiryTime;

        DNSEntry(String domain, String ipAddress, long ttlSeconds) {
            this.domain = domain;
            this.ipAddress = ipAddress;
            this.expiryTime = System.currentTimeMillis() + ttlSeconds * 1000;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }

    // LRU Cache using LinkedHashMap
    private LinkedHashMap<String, DNSEntry> cache;

    private int capacity;
    private int hits = 0;
    private int misses = 0;

    public DNSCache(int capacity) {
        this.capacity = capacity;

        cache = new LinkedHashMap<String, DNSEntry>(capacity, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                return size() > DNSCache.this.capacity;
            }
        };

        startCleanupThread();
    }

    // Resolve domain
    public synchronized String resolve(String domain) {

        DNSEntry entry = cache.get(domain);

        if (entry != null && !entry.isExpired()) {
            hits++;
            return "Cache HIT → " + entry.ipAddress;
        }

        if (entry != null && entry.isExpired()) {
            cache.remove(domain);
        }

        misses++;

        String ip = queryUpstreamDNS(domain);

        cache.put(domain, new DNSEntry(domain, ip, 5));

        return "Cache MISS → Upstream DNS → " + ip;
    }

    // Simulated upstream DNS lookup
    private String queryUpstreamDNS(String domain) {
        Random rand = new Random();
        return "172.217." + rand.nextInt(255) + "." + rand.nextInt(255);
    }

    // Background cleanup thread
    private void startCleanupThread() {

        Thread cleaner = new Thread(() -> {

            while (true) {
                try {
                    Thread.sleep(2000);

                    Iterator<Map.Entry<String, DNSEntry>> it = cache.entrySet().iterator();

                    while (it.hasNext()) {
                        Map.Entry<String, DNSEntry> entry = it.next();

                        if (entry.getValue().isExpired()) {
                            it.remove();
                        }
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        cleaner.setDaemon(true);
        cleaner.start();
    }

    // Cache statistics
    public void getCacheStats() {

        int total = hits + misses;

        double hitRate = total == 0 ? 0 : ((double) hits / total) * 100;

        System.out.println("Cache Hits: " + hits);
        System.out.println("Cache Misses: " + misses);
        System.out.println("Hit Rate: " + String.format("%.2f", hitRate) + "%");
    }

    // Demo
    public static void main(String[] args) throws InterruptedException {

        DNSCache dnsCache = new DNSCache(3);

        System.out.println(dnsCache.resolve("google.com"));
        System.out.println(dnsCache.resolve("google.com"));

        Thread.sleep(6000);

        System.out.println(dnsCache.resolve("google.com"));

        dnsCache.getCacheStats();
    }
}

// Problem 4:
import java.util.*;

public class PlagiarismDetector {

    // nGram -> set of document IDs containing it
    private HashMap<String, Set<String>> nGramIndex = new HashMap<>();

    // documentId -> list of nGrams
    private HashMap<String, List<String>> documentNGrams = new HashMap<>();

    private int N = 5; // 5-gram

    // Break text into n-grams
    private List<String> generateNGrams(String text) {

        List<String> result = new ArrayList<>();

        String[] words = text.toLowerCase().split("\\s+");

        for (int i = 0; i <= words.length - N; i++) {

            StringBuilder gram = new StringBuilder();

            for (int j = 0; j < N; j++) {
                gram.append(words[i + j]).append(" ");
            }

            result.add(gram.toString().trim());
        }

        return result;
    }

    // Add document to database
    public void addDocument(String documentId, String text) {

        List<String> ngrams = generateNGrams(text);
        documentNGrams.put(documentId, ngrams);

        for (String gram : ngrams) {

            nGramIndex.putIfAbsent(gram, new HashSet<>());
            nGramIndex.get(gram).add(documentId);
        }
    }

    // Analyze new document
    public void analyzeDocument(String documentId, String text) {

        List<String> newNgrams = generateNGrams(text);

        System.out.println("Extracted " + newNgrams.size() + " n-grams");

        HashMap<String, Integer> matchCounts = new HashMap<>();

        for (String gram : newNgrams) {

            if (nGramIndex.containsKey(gram)) {

                for (String doc : nGramIndex.get(gram)) {

                    matchCounts.put(doc,
                            matchCounts.getOrDefault(doc, 0) + 1);
                }
            }
        }

        for (String doc : matchCounts.keySet()) {

            int matches = matchCounts.get(doc);

            double similarity =
                    (matches * 100.0) / newNgrams.size();

            System.out.println(
                    "Found " + matches +
                            " matching n-grams with " + doc);

            System.out.println(
                    "Similarity: " +
                            String.format("%.2f", similarity) + "%");

            if (similarity > 50) {
                System.out.println("PLAGIARISM DETECTED");
            }

            System.out.println();
        }
    }

    // Demo
    public static void main(String[] args) {

        PlagiarismDetector detector = new PlagiarismDetector();

        detector.addDocument(
                "essay_089.txt",
                "Artificial intelligence is transforming the world and creating new opportunities for innovation"
        );

        detector.addDocument(
                "essay_092.txt",
                "Machine learning and artificial intelligence are transforming the world and creating powerful technologies"
        );

        String newEssay =
                "Artificial intelligence is transforming the world and creating powerful technologies for innovation";

        detector.analyzeDocument("essay_123.txt", newEssay);
    }
}

// Problem 5:
import java.util.*;
import java.util.concurrent.*;

public class RealTimeAnalytics {

    // pageUrl -> visit count
    private ConcurrentHashMap<String, Integer> pageViews = new ConcurrentHashMap<>();

    // pageUrl -> unique users
    private ConcurrentHashMap<String, Set<String>> uniqueVisitors = new ConcurrentHashMap<>();

    // source -> count
    private ConcurrentHashMap<String, Integer> trafficSources = new ConcurrentHashMap<>();

    // Event structure
    static class Event {
        String url;
        String userId;
        String source;

        Event(String url, String userId, String source) {
            this.url = url;
            this.userId = userId;
            this.source = source;
        }
    }

    // Process incoming page view event
    public void processEvent(Event event) {

        // Count page views
        pageViews.put(event.url,
                pageViews.getOrDefault(event.url, 0) + 1);

        // Track unique visitors
        uniqueVisitors.putIfAbsent(event.url, ConcurrentHashMap.newKeySet());
        uniqueVisitors.get(event.url).add(event.userId);

        // Track traffic source
        trafficSources.put(event.source,
                trafficSources.getOrDefault(event.source, 0) + 1);
    }

    // Get Top 10 pages
    private List<Map.Entry<String, Integer>> getTopPages() {

        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>(Map.Entry.comparingByValue());

        for (Map.Entry<String, Integer> entry : pageViews.entrySet()) {

            pq.offer(entry);

            if (pq.size() > 10) {
                pq.poll();
            }
        }

        List<Map.Entry<String, Integer>> result = new ArrayList<>(pq);
        result.sort((a, b) -> b.getValue() - a.getValue());

        return result;
    }

    // Display dashboard
    public void getDashboard() {

        System.out.println("\n===== REAL-TIME DASHBOARD =====");

        System.out.println("\nTop Pages:");

        List<Map.Entry<String, Integer>> topPages = getTopPages();

        int rank = 1;

        for (Map.Entry<String, Integer> entry : topPages) {

            String page = entry.getKey();
            int views = entry.getValue();
            int unique = uniqueVisitors.get(page).size();

            System.out.println(rank + ". " + page +
                    " - " + views + " views (" +
                    unique + " unique)");

            rank++;
        }

        System.out.println("\nTraffic Sources:");

        int total = trafficSources.values().stream().mapToInt(i -> i).sum();

        for (Map.Entry<String, Integer> entry : trafficSources.entrySet()) {

            double percent =
                    (entry.getValue() * 100.0) / total;

            System.out.println(entry.getKey() + ": "
                    + String.format("%.1f", percent) + "%");
        }
    }

    // Dashboard updater (every 5 seconds)
    public void startDashboardUpdater() {

        ScheduledExecutorService scheduler =
                Executors.newScheduledThreadPool(1);

        scheduler.scheduleAtFixedRate(() -> {
            getDashboard();
        }, 5, 5, TimeUnit.SECONDS);
    }

    // Demo simulation
    public static void main(String[] args) throws InterruptedException {

        RealTimeAnalytics analytics = new RealTimeAnalytics();

        analytics.startDashboardUpdater();

        String[] pages = {
                "/article/breaking-news",
                "/sports/championship",
                "/tech/ai-news",
                "/politics/election"
        };

        String[] sources = {
                "google", "facebook", "direct", "twitter"
        };

        Random rand = new Random();

        // Simulate incoming events
        for (int i = 1; i <= 100; i++) {

            String page = pages[rand.nextInt(pages.length)];
            String user = "user_" + rand.nextInt(50);
            String source = sources[rand.nextInt(sources.length)];

            analytics.processEvent(new Event(page, user, source));

            Thread.sleep(100);
        }
    }
}

// Problem 6:
import java.util.concurrent.ConcurrentHashMap;

public class DistributedRateLimiter {

    // Token Bucket structure
    static class TokenBucket {

        int maxTokens;
        double refillRate; // tokens per second
        double tokens;
        long lastRefillTime;

        TokenBucket(int maxTokens, double refillRate) {
            this.maxTokens = maxTokens;
            this.refillRate = refillRate;
            this.tokens = maxTokens;
            this.lastRefillTime = System.currentTimeMillis();
        }

        // Refill tokens based on elapsed time
        synchronized void refill() {

            long now = System.currentTimeMillis();
            double seconds = (now - lastRefillTime) / 1000.0;

            double newTokens = seconds * refillRate;

            tokens = Math.min(maxTokens, tokens + newTokens);

            lastRefillTime = now;
        }

        // Try consuming token
        synchronized boolean allowRequest() {

            refill();

            if (tokens >= 1) {
                tokens--;
                return true;
            }

            return false;
        }

        int remainingTokens() {
            return (int) tokens;
        }
    }

    // clientId -> TokenBucket
    private ConcurrentHashMap<String, TokenBucket> clientBuckets =
            new ConcurrentHashMap<>();

    private int maxRequests;
    private int windowSeconds;

    public DistributedRateLimiter(int maxRequests, int windowSeconds) {

        this.maxRequests = maxRequests;
        this.windowSeconds = windowSeconds;
    }

    // Check rate limit
    public String checkRateLimit(String clientId) {

        clientBuckets.putIfAbsent(
                clientId,
                new TokenBucket(maxRequests,
                        (double) maxRequests / windowSeconds)
        );

        TokenBucket bucket = clientBuckets.get(clientId);

        if (bucket.allowRequest()) {

            return "Allowed (" +
                    bucket.remainingTokens() +
                    " requests remaining)";
        }

        int retryAfter = windowSeconds;

        return "Denied (0 requests remaining, retry after "
                + retryAfter + "s)";
    }

    // Rate limit status
    public void getRateLimitStatus(String clientId) {

        TokenBucket bucket = clientBuckets.get(clientId);

        if (bucket == null) {
            System.out.println("No usage yet");
            return;
        }

        int used = maxRequests - bucket.remainingTokens();

        long resetTime =
                System.currentTimeMillis() / 1000 + windowSeconds;

        System.out.println("{used: " + used +
                ", limit: " + maxRequests +
                ", reset: " + resetTime + "}");
    }

    // Demo simulation
    public static void main(String[] args) {

        DistributedRateLimiter limiter =
                new DistributedRateLimiter(5, 60); // 5 requests per minute

        String client = "abc123";

        for (int i = 0; i < 7; i++) {

            System.out.println(
                    limiter.checkRateLimit(client));
        }

        limiter.getRateLimitStatus(client);
    }
}

// Problem 7:
import java.util.*;

public class AutocompleteSystem {

    // Trie Node
    static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        boolean isWord = false;
    }

    private TrieNode root = new TrieNode();

    // query -> frequency
    private HashMap<String, Integer> frequencyMap = new HashMap<>();

    // Insert query into Trie
    public void insertQuery(String query) {

        TrieNode node = root;

        for (char c : query.toCharArray()) {

            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);
        }

        node.isWord = true;

        frequencyMap.put(query,
                frequencyMap.getOrDefault(query, 0) + 1);
    }

    // Search node for prefix
    private TrieNode getPrefixNode(String prefix) {

        TrieNode node = root;

        for (char c : prefix.toCharArray()) {

            if (!node.children.containsKey(c))
                return null;

            node = node.children.get(c);
        }

        return node;
    }

    // DFS to collect queries
    private void collectQueries(TrieNode node,
                                String current,
                                List<String> results) {

        if (node.isWord)
            results.add(current);

        for (char c : node.children.keySet()) {

            collectQueries(
                    node.children.get(c),
                    current + c,
                    results);
        }
    }

    // Get Top 10 suggestions
    public List<String> search(String prefix) {

        TrieNode node = getPrefixNode(prefix);

        List<String> queries = new ArrayList<>();

        if (node == null)
            return queries;

        collectQueries(node, prefix, queries);

        // Sort by frequency
        queries.sort((a, b) ->
                frequencyMap.get(b) - frequencyMap.get(a));

        if (queries.size() > 10)
            return queries.subList(0, 10);

        return queries;
    }

    // Update frequency after search
    public void updateFrequency(String query) {

        frequencyMap.put(query,
                frequencyMap.getOrDefault(query, 0) + 1);

        insertQuery(query);
    }

    // Demo
    public static void main(String[] args) {

        AutocompleteSystem system =
                new AutocompleteSystem();

        system.insertQuery("java tutorial");
        system.insertQuery("javascript");
        system.insertQuery("java download");
        system.insertQuery("java tutorial");
        system.insertQuery("java tutorial");
        system.insertQuery("java 21 features");

        List<String> results = system.search("jav");

        System.out.println("Suggestions:");

        for (String r : results) {

            System.out.println(r + " (" +
                    system.frequencyMap.get(r) +
                    " searches)");
        }

        system.updateFrequency("java 21 features");

        System.out.println("\nAfter update:");

        results = system.search("java");

        for (String r : results) {

            System.out.println(r + " (" +
                    system.frequencyMap.get(r) +
                    " searches)");
        }
    }
}

// Problem 8 :
import java.util.*;

public class ParkingLotSystem {

    enum Status {
        EMPTY, OCCUPIED, DELETED
    }

    static class ParkingSpot {
        String licensePlate;
        long entryTime;
        Status status;

        ParkingSpot() {
            status = Status.EMPTY;
        }
    }

    private ParkingSpot[] table;
    private int capacity;
    private int occupiedSpots = 0;
    private int totalProbes = 0;
    private int totalParks = 0;

    public ParkingLotSystem(int capacity) {
        this.capacity = capacity;
        table = new ParkingSpot[capacity];

        for (int i = 0; i < capacity; i++)
            table[i] = new ParkingSpot();
    }

    // Hash function
    private int hash(String licensePlate) {
        return Math.abs(licensePlate.hashCode()) % capacity;
    }

    // Park vehicle
    public void parkVehicle(String plate) {

        int index = hash(plate);
        int probes = 0;

        while (table[index].status == Status.OCCUPIED) {
            index = (index + 1) % capacity;
            probes++;
        }

        table[index].licensePlate = plate;
        table[index].entryTime = System.currentTimeMillis();
        table[index].status = Status.OCCUPIED;

        occupiedSpots++;
        totalProbes += probes;
        totalParks++;

        System.out.println("parkVehicle(\"" + plate + "\") → Assigned spot #"
                + index + " (" + probes + " probes)");
    }

    // Exit vehicle
    public void exitVehicle(String plate) {

        int index = hash(plate);

        while (table[index].status != Status.EMPTY) {

            if (table[index].status == Status.OCCUPIED &&
                    table[index].licensePlate.equals(plate)) {

                long durationMillis =
                        System.currentTimeMillis() - table[index].entryTime;

                double hours = durationMillis / (1000.0 * 60 * 60);

                double fee = hours * 5; // $5 per hour

                table[index].status = Status.DELETED;
                occupiedSpots--;

                System.out.println("exitVehicle(\"" + plate + "\") → Spot #" + index
                        + " freed, Duration: "
                        + String.format("%.2f", hours)
                        + "h, Fee: $" + String.format("%.2f", fee));

                return;
            }

            index = (index + 1) % capacity;
        }

        System.out.println("Vehicle not found");
    }

    // Find nearest available spot from entrance (spot 0)
    public void findNearestSpot() {

        for (int i = 0; i < capacity; i++) {

            if (table[i].status != Status.OCCUPIED) {

                System.out.println("Nearest available spot: #" + i);
                return;
            }
        }

        System.out.println("Parking lot full");
    }

    // Statistics
    public void getStatistics() {

        double occupancy =
                (occupiedSpots * 100.0) / capacity;

        double avgProbes =
                totalParks == 0 ? 0 : (double) totalProbes / totalParks;

        System.out.println("\nParking Statistics:");

        System.out.println("Occupancy: "
                + String.format("%.2f", occupancy) + "%");

        System.out.println("Average Probes: "
                + String.format("%.2f", avgProbes));
    }

    // Demo
    public static void main(String[] args) throws InterruptedException {

        ParkingLotSystem lot = new ParkingLotSystem(10);

        lot.parkVehicle("ABC-1234");
        lot.parkVehicle("ABC-1235");
        lot.parkVehicle("XYZ-9999");

        Thread.sleep(2000);

        lot.exitVehicle("ABC-1234");

        lot.findNearestSpot();

        lot.getStatistics();
    }
}

//Problem 9:
import java.util.*;
import java.text.*;

public class TransactionAnalyzer {

    static class Transaction {
        int id;
        double amount;
        String merchant;
        String account;
        long timestamp; // epoch millis

        public Transaction(int id, double amount, String merchant, String account, String timeStr) {
            this.id = id;
            this.amount = amount;
            this.merchant = merchant;
            this.account = account;

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                Date date = sdf.parse(timeStr);
                this.timestamp = date.getTime();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public String toString() {
            return "{id:" + id + ", amount:" + amount + ", merchant:" + merchant + ", account:" + account + "}";
        }
    }

    List<Transaction> transactions = new ArrayList<>();

    public void addTransaction(Transaction t) {
        transactions.add(t);
    }

    // Classic Two-Sum
    public List<List<Transaction>> findTwoSum(double target) {
        Map<Double, Transaction> map = new HashMap<>();
        List<List<Transaction>> result = new ArrayList<>();

        for (Transaction t : transactions) {
            double complement = target - t.amount;
            if (map.containsKey(complement)) {
                result.add(Arrays.asList(map.get(complement), t));
            }
            map.put(t.amount, t);
        }
        return result;
    }

    // Two-Sum within time window (in minutes)
    public List<List<Transaction>> findTwoSumTimeWindow(double target, long windowMinutes) {
        Map<Double, List<Transaction>> map = new HashMap<>();
        List<List<Transaction>> result = new ArrayList<>();
        long windowMillis = windowMinutes * 60 * 1000;

        transactions.sort(Comparator.comparingLong(t -> t.timestamp));

        for (Transaction t : transactions) {
            double complement = target - t.amount;
            if (map.containsKey(complement)) {
                for (Transaction other : map.get(complement)) {
                    if (Math.abs(t.timestamp - other.timestamp) <= windowMillis) {
                        result.add(Arrays.asList(other, t));
                    }
                }
            }
            map.putIfAbsent(t.amount, new ArrayList<>());
            map.get(t.amount).add(t);
        }
        return result;
    }

    // K-Sum
    public List<List<Transaction>> findKSum(int k, double target) {
        List<List<Transaction>> result = new ArrayList<>();
        transactions.sort(Comparator.comparingDouble(t -> t.amount));
        kSumHelper(transactions, 0, k, target, new ArrayList<>(), result);
        return result;
    }

    private void kSumHelper(List<Transaction> list, int start, int k, double target,
                            List<Transaction> path, List<List<Transaction>> res) {
        if (k == 2) {
            int left = start, right = list.size() - 1;
            while (left < right) {
                double sum = list.get(left).amount + list.get(right).amount;
                if (Math.abs(sum - target) < 1e-6) {
                    List<Transaction> temp = new ArrayList<>(path);
                    temp.add(list.get(left));
                    temp.add(list.get(right));
                    res.add(temp);
                    left++;
                    right--;
                } else if (sum < target) left++;
                else right--;
            }
            return;
        }

        for (int i = start; i < list.size() - k + 1; i++) {
            path.add(list.get(i));
            kSumHelper(list, i + 1, k - 1, target - list.get(i).amount, path, res);
            path.remove(path.size() - 1);
        }
    }

    // Detect duplicates (same amount, same merchant, different accounts)
    public List<Map<String, Object>> detectDuplicates() {
        Map<String, Map<Double, Set<String>>> map = new HashMap<>();
        List<Map<String, Object>> duplicates = new ArrayList<>();

        for (Transaction t : transactions) {
            map.putIfAbsent(t.merchant, new HashMap<>());
            Map<Double, Set<String>> amtMap = map.get(t.merchant);
            amtMap.putIfAbsent(t.amount, new HashSet<>());
            Set<String> accounts = amtMap.get(t.amount);
            accounts.add(t.account);
        }

        for (String merchant : map.keySet()) {
            for (double amt : map.get(merchant).keySet()) {
                Set<String> accounts = map.get(merchant).get(amt);
                if (accounts.size() > 1) {
                    Map<String, Object> entry = new HashMap<>();
                    entry.put("merchant", merchant);
                    entry.put("amount", amt);
                    entry.put("accounts", accounts);
                    duplicates.add(entry);
                }
            }
        }
        return duplicates;
    }

    // Demo
    public static void main(String[] args) {
        TransactionAnalyzer analyzer = new TransactionAnalyzer();

        analyzer.addTransaction(new Transaction(1, 500, "Store A", "acc1", "10:00"));
        analyzer.addTransaction(new Transaction(2, 300, "Store B", "acc2", "10:15"));
        analyzer.addTransaction(new Transaction(3, 200, "Store C", "acc3", "10:30"));
        analyzer.addTransaction(new Transaction(4, 500, "Store A", "acc2", "11:00"));

        System.out.println("Classic Two-Sum (target=500):");
        List<List<Transaction>> twoSum = analyzer.findTwoSum(500);
        for (List<Transaction> pair : twoSum) System.out.println(pair);

        System.out.println("\nTwo-Sum with 60min window (target=500):");
        List<List<Transaction>> twoSumWindow = analyzer.findTwoSumTimeWindow(500, 60);
        for (List<Transaction> pair : twoSumWindow) System.out.println(pair);

        System.out.println("\nK-Sum (k=3, target=1000):");
        List<List<Transaction>> kSum = analyzer.findKSum(3, 1000);
        for (List<Transaction> combo : kSum) System.out.println(combo);

        System.out.println("\nDuplicate detection:");
        List<Map<String,Object>> duplicates = analyzer.detectDuplicates();
        for (Map<String,Object> dup : duplicates) System.out.println(dup);
    }
}

// Problem 10:
import java.util.*;

class MultiLevelCache {

    private LinkedHashMap<String, String> L1;
    private HashMap<String, String> L2;
    private HashMap<String, Integer> accessCount;

    private int L1_SIZE = 10000;
    private int PROMOTION_THRESHOLD = 5;

    public MultiLevelCache() {

        L1 = new LinkedHashMap<String, String>(L1_SIZE, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry eldest) {
                return size() > L1_SIZE;
            }
        };

        L2 = new HashMap<>();
        accessCount = new HashMap<>();
    }

    public String getVideo(String videoId) {

        if (L1.containsKey(videoId)) {
            System.out.println("L1 HIT");
            return L1.get(videoId);
        }

        if (L2.containsKey(videoId)) {
            System.out.println("L2 HIT");

            int count = accessCount.getOrDefault(videoId,0)+1;
            accessCount.put(videoId,count);

            if(count > PROMOTION_THRESHOLD){
                L1.put(videoId,L2.get(videoId));
            }

            return L2.get(videoId);
        }

        System.out.println("Database HIT");

        String data = "VideoData_"+videoId;

        L2.put(videoId,data);
        accessCount.put(videoId,1);

        return data;
    }
}
