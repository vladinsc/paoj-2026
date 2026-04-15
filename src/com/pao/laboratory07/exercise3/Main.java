package com.pao.laboratory07.exercise3;

import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        sc.useLocale(Locale.US);

        if (!sc.hasNextInt()) return;

        int n = sc.nextInt();
        List<Comanda> comenzi = new ArrayList<>();


        for (int i = 0; i < n; i++) {
            String tip = sc.next();
            if (tip.equals("STANDARD")) {
                String nume = sc.next();
                double pret = sc.nextDouble();
                String client = sc.next();
                comenzi.add(new ComandaStandard(nume, pret, client));
            } else if (tip.equals("DISCOUNTED")) {
                String nume = sc.next();
                double pret = sc.nextDouble();
                int discount = sc.nextInt();
                String client = sc.next();
                comenzi.add(new ComandaRedusa(nume, pret, discount, client));
            } else if (tip.equals("GIFT")) {
                String nume = sc.next();
                String client = sc.next();
                comenzi.add(new ComandaGratuita(nume, client));
            }
        }


        comenzi.forEach(c -> System.out.println(c.descriere()));


        while (sc.hasNext()) {
            String cmd = sc.next();

            try {
                switch (cmd) {
                    case "STATS" -> {
                        System.out.println("--- STATS ---");

                        Map<String, Double> medii = comenzi.stream().collect(
                                Collectors.groupingBy(
                                        c -> switch (c) {
                                            case ComandaStandard cs -> "STANDARD";
                                            case ComandaRedusa cr -> "DISCOUNTED";
                                            case ComandaGratuita cg -> "GIFT";
                                        },
                                        Collectors.averagingDouble(Comanda::pretFinal)
                                )
                        );


                        if (medii.containsKey("STANDARD"))
                            System.out.printf(Locale.US, "STANDARD: medie = %.2f lei\n", medii.get("STANDARD"));
                        if (medii.containsKey("DISCOUNTED"))
                            System.out.printf(Locale.US, "DISCOUNTED: medie = %.2f lei\n", medii.get("DISCOUNTED"));
                        if (medii.containsKey("GIFT"))
                            System.out.printf(Locale.US, "GIFT: medie = %.2f lei\n", medii.get("GIFT"));
                    }
                    case "FILTER" -> {
                        double threshold = sc.nextDouble();

                        int tInt = (int) threshold;
                        System.out.printf(Locale.US, "--- FILTER (>= %d) ---\n", tInt);
                        comenzi.stream()
                                .filter(c -> c.pretFinal() >= threshold)
                                .forEach(c -> System.out.println(c.descriereFaraStare()));
                    }
                    case "SORT" -> {
                        System.out.println("--- SORT (by client, then by pret) ---");
                        comenzi.stream()
                                .sorted(Comparator.comparing(Comanda::getClient)
                                        .thenComparing(Comanda::pretFinal))
                                .forEach(c -> System.out.println(c.descriereFaraStare()));
                    }
                    case "SPECIAL" -> {
                        System.out.println("--- SPECIAL (discount > 15%) ---");
                        comenzi.stream()

                                .filter(c -> c instanceof ComandaRedusa cr && cr.getDiscountProcent() > 15)
                                .forEach(c -> System.out.println(c.descriereFaraStare()));
                    }
                    case "QUIT" -> {
                        sc.close();
                        return;
                    }
                    default -> throw new InvalidCommandException("Comanda necunoscuta: " + cmd);
                }
            } catch (InvalidCommandException e) {
                System.out.println(e.getMessage());
            }
        }
        sc.close();
    }
}