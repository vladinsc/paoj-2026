package com.pao.laboratory11.exercise1;

// Imports for command parsing and in-memory ranking/lookup structures.
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
public class Main {


        private static final Set<String> HIGH_RISK_COUNTRIES =
                new HashSet<>(Arrays.asList("RU", "NG", "IR", "KP", "SY"));

        private static final Map<String, Integer> CHANNEL_SCORE = new HashMap<>();

        static {
            CHANNEL_SCORE.put("WEB", 15);
            CHANNEL_SCORE.put("APP", 10);
            CHANNEL_SCORE.put("CRYPTO", 30);
            CHANNEL_SCORE.put("POS", 5);
            CHANNEL_SCORE.put("ATM", 0);
        }

        private static final int FLAG_THRESHOLD = 60;

        // Partea A: Predicate elementare cerute pentru evaluarea tranzactiilor
        public static final Predicate<Transaction> amountOverThreshold = tx -> tx.amount >= 1000;
        public static final Predicate<Transaction> countryInRisk = tx -> HIGH_RISK_COUNTRIES.contains(tx.country);
        public static final Predicate<Transaction> channelSuspicious = tx ->
                Arrays.asList("WEB", "APP", "CRYPTO").contains(tx.channel);

        // Partea B: Exemplu de compozitie (cerut de enunt), desi in acest caz
        // decizia corecta/stricta conform cerintei numerice se face pe baza scorului total
        public static final Predicate<Transaction> isFlaggedRule = tx -> riskScore(tx) >= FLAG_THRESHOLD;

        // Partea C: Comparator compus
        private static final Comparator<Transaction> BY_RISK_DESC_THEN_ID_ASC =
                Comparator.comparingInt(Main::riskScore).reversed().thenComparingInt(t -> t.id);

        public static void main(String[] args) {
            try {
                run();
            } catch (IOException e) {
                System.out.println("ERR IO");
            }
        }

        private static void run() throws IOException {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String first = readNonEmptyLine(br);
            if (first == null) {
                return;
            }

            int n = Integer.parseInt(first);
            Map<Integer, Transaction> byId = new HashMap<>();
            List<Transaction> all = new ArrayList<>();

            for (int i = 0; i < n; i++) {
                String line = readNonEmptyLine(br);
                if (line == null) {
                    return;
                }

                String[] tok = line.split("\\s+");
                if (tok.length < 5) {
                    continue;
                }

                Transaction tx = new Transaction(
                        Integer.parseInt(tok[0]),
                        Double.parseDouble(tok[1]),
                        tok[2],
                        tok[3].toUpperCase(),
                        tok[4].toUpperCase());

                byId.put(tx.id, tx);
                all.add(tx);
            }

            String qLine = readNonEmptyLine(br);
            if (qLine == null) {
                return;
            }
            int q = Integer.parseInt(qLine);

            for (int i = 0; i < q; i++) {
                String cmdLine = readNonEmptyLine(br);
                if (cmdLine == null) {
                    return;
                }

                String[] cmd = cmdLine.split("\\s+");
                String op = cmd[0].toUpperCase();

                switch (op) {
                    case "CHECK":
                        if (cmd.length < 2) {
                            System.out.println("ERR BAD_COMMAND");
                            break;
                        }
                        int id = Integer.parseInt(cmd[1]);
                        Transaction tx = byId.get(id);
                        if (tx == null) {
                            System.out.println("CHECK " + id + " => NOT_FOUND");
                        } else {
                            int score = riskScore(tx);
                            System.out.println("CHECK " + id + " => " + verdict(score) + " score=" + score);
                        }
                        break;

                    case "LIST_FLAGGED":
                        List<Transaction> flagged = new ArrayList<>();
                        // Folosim predicatul compus definit anterior
                        for (Transaction t : all) {
                            if (isFlaggedRule.test(t)) {
                                flagged.add(t);
                            }
                        }
                        flagged.sort(BY_RISK_DESC_THEN_ID_ASC);

                        if (flagged.isEmpty()) {
                            System.out.println("NONE");
                        } else {
                            for (Transaction t : flagged) {
                                System.out.println(formatRiskLine(t));
                            }
                        }
                        break;

                    case "TOP_RISK":
                        if (cmd.length < 2) {
                            System.out.println("ERR BAD_COMMAND");
                            break;
                        }
                        int k = Integer.parseInt(cmd[1]);
                        List<Transaction> ranked = new ArrayList<>(all);
                        ranked.sort(BY_RISK_DESC_THEN_ID_ASC);

                        int limit = Math.max(0, Math.min(k, ranked.size()));
                        for (int idx = 0; idx < limit; idx++) {
                            System.out.println(formatRiskLine(ranked.get(idx)));
                        }
                        break;

                    default:
                        System.out.println("ERR UNKNOWN_COMMAND");
                        break;
                }
            }
        }

        private static String readNonEmptyLine(BufferedReader br) throws IOException {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    return line.trim();
                }
            }
            return null;
        }

        public static int riskScore(Transaction tx) {
            int score = 0;

            // Treptele sunt exclusive top-down, cu exceptia sumei <= 100
            if (tx.amount >= 5000.0) {
                score += 70;
            } else if (tx.amount >= 1000.0) {
                score += 40;
            } else if (tx.amount >= 500.0) {
                score += 20;
            }

            if (tx.amount <= 100.0) {
                score += 5;
            }

            if (HIGH_RISK_COUNTRIES.contains(tx.country)) {
                score += 25;
            }

            score += CHANNEL_SCORE.getOrDefault(tx.channel, 0);
            return score;
        }

        private static String verdict(int score) {
            return score >= FLAG_THRESHOLD ? "FLAG" : "ALLOW";
        }

        private static String formatRiskLine(Transaction tx) {
            int score = riskScore(tx);
            return "[" + tx.id + "] " + verdict(score) + " score=" + score;
        }
}
