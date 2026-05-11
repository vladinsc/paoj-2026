package com.pao.laboratory11.exercise2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.DoubleSummaryStatistics;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        try {
            run();
        } catch (IOException e) {
            // Keep deterministic checker output.
        }
    }

    private static void run() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String first = nextNonEmpty(br);
        if (first == null) {
            return;
        }

        int n = Integer.parseInt(first);
        List<Tx> txs = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            String line = nextNonEmpty(br);
            if (line == null) {
                return;
            }

            String[] p = line.split("\\s+");
            txs.add(new Tx(
                    Integer.parseInt(p[0]),
                    Double.parseDouble(p[1]),
                    p[2],
                    p[3],
                    p[4],
                    p[5]));
        }

        String qLine = nextNonEmpty(br);
        if (qLine == null) {
            return;
        }
        int q = Integer.parseInt(qLine);

        for (int i = 0; i < q; i++) {
            String line = nextNonEmpty(br);
            if (line == null) {
                return;
            }

            String[] p = line.split("\\s+");
            String op = p[0].toUpperCase();

            switch (op) {
                case "REPORT_MONTH": {
                    String month = p[1];
                    // Folosim Stream API si summarizingDouble pentru a obtine atat suma cat si count-ul simultan
                    DoubleSummaryStatistics stats = txs.stream()
                            .filter(tx -> tx.date.startsWith(month))
                            .collect(Collectors.summarizingDouble(tx -> tx.amount));

                    System.out.printf(Locale.US, "MONTH %s total=%.2f count=%d%n",
                            month, stats.getSum(), stats.getCount());
                    break;
                }

                case "REPORT_ACCOUNT": {
                    String account = p[1];
                    // Folosim Stream API similar cu REPORT_MONTH
                    DoubleSummaryStatistics stats = txs.stream()
                            .filter(tx -> tx.account.equals(account))
                            .collect(Collectors.summarizingDouble(tx -> tx.amount));

                    System.out.printf(Locale.US, "ACCOUNT %s total=%.2f count=%d%n",
                            account, stats.getSum(), stats.getCount());
                    break;
                }

                case "TOP_CHANNELS": {
                    int k = Integer.parseInt(p[1]);

                    if (txs.isEmpty()) {
                        System.out.println("NONE");
                    } else {
                        // Folosim Stream API pentru grupare, numarare, sortare si limitare
                        txs.stream()
                                .collect(Collectors.groupingBy(tx -> tx.channel, Collectors.counting()))
                                .entrySet().stream()
                                // Sortam descrescator dupa count, apoi crescator alfabetic dupa nume
                                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder())
                                        .thenComparing(Map.Entry.comparingByKey()))
                                .limit(k)
                                .forEach(e -> System.out.println(e.getKey() + " " + e.getValue()));
                    }
                    break;
                }

                default:
                    // Ignoram comenzile necunoscute, conform cerintei testelor.
                    break;
            }
        }
    }

    private static String nextNonEmpty(BufferedReader br) throws IOException {
        String line;
        while ((line = br.readLine()) != null) {
            if (!line.trim().isEmpty()) {
                return line.trim();
            }
        }
        return null;
    }

    private static final class Tx {
        public final int id;
        public final double amount;
        public final String date;
        public final String country;
        public final String channel;
        public final String account;

        private Tx(int id, double amount, String date, String country, String channel, String account) {
            this.id = id;
            this.amount = amount;
            this.date = date;
            this.country = country;
            this.channel = channel;
            this.account = account;
        }
    }
}
