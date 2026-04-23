import java.io.*;
import java.util.*;

public class Main {

  public static void main(String[] args) throws Exception {
    long start = System.currentTimeMillis();

    String path = args[0];

    List<String[]> rows = new ArrayList<>();
    List<String> rawLines = new ArrayList<>();
    Set<String> unique = new HashSet<>();

    try (BufferedReader br = new BufferedReader(new FileReader(path))) {
      String line;

      while ((line = br.readLine()) != null) {
        if (!isValid(line)) {
          continue;
        }
        if (!unique.add(line)) {
          continue;
        }

        rows.add(parse(line));
        rawLines.add(line);
      }
    }

    int n = rows.size();
    DSU dsu = new DSU(n);

    Map<String, Integer> seen = new HashMap<>();

    for (int i = 0; i < n; i++) {
      String[] cols = rows.get(i);

      for (int j = 0; j < cols.length; j++) {
        String val = cols[j];
        if (val.isEmpty()) {
          continue;
        }

        String key = j + "#" + val;

        Integer prev = seen.get(key);
        if (prev != null) {
          dsu.union(i, prev);
        } else {
          seen.put(key, i);
        }
      }
    }

    Map<Integer, List<Integer>> groups = new HashMap<>();

    for (int i = 0; i < n; i++) {
      int root = dsu.find(i);
      groups.computeIfAbsent(root, k -> new ArrayList<>()).add(i);
    }

    List<List<Integer>> result = new ArrayList<>();

    for (List<Integer> g : groups.values()) {
      if (g.size() > 1) {
        result.add(g);
      }
    }

    result.sort((a, b) -> Integer.compare(b.size(), a.size()));

    try (BufferedWriter bw = new BufferedWriter(new FileWriter("output.txt"))) {

      bw.write(String.valueOf(result.size()));
      bw.newLine();

      int groupNum = 1;

      for (List<Integer> group : result) {
        bw.write("Группа " + groupNum++);
        bw.newLine();

        for (int idx : group) {
          bw.write(rawLines.get(idx));
          bw.newLine();
        }

        bw.newLine();
      }
    }

    long end = System.currentTimeMillis();

    System.out.println("Группы: " + result.size());
    System.out.println("Время: " + (end - start) + " ms");
  }

  private static boolean isValid(String line) {
    long quotes = line.chars().filter(c -> c == '"').count();
    return quotes % 2 == 0;
  }

  private static String[] parse(String line) {
    String[] parts = line.split(";", -1);

    for (int i = 0; i < parts.length; i++) {
      String p = parts[i].trim();

      if (p.length() >= 2 && p.startsWith("\"") && p.endsWith("\"")) {
        p = p.substring(1, p.length() - 1);
      }

      parts[i] = p;
    }

    return parts;
  }

  private static class DSU {

    private int[] parent;
    private int[] rank;

    public DSU(int n) {
      parent = new int[n];
      rank = new int[n];
      for (int i = 0; i < n; i++) {
        parent[i] = i;
      }
    }

    public int find(int x) {
      int root = x;

      while (root != parent[root]) {
        root = parent[root];
      }

      while (x != root) {
        int next = parent[x];
        parent[x] = root;
        x = next;
      }

      return root;
    }

    public void union(int a, int b) {
      int ra = find(a);
      int rb = find(b);

      if (ra == rb) return;

      if (rank[ra] < rank[rb]) {
        parent[ra] = rb;
      } else if (rank[ra] > rank[rb]) {
        parent[rb] = ra;
      } else {
        parent[rb] = ra;
        rank[ra]++;
      }
    }
  }
}