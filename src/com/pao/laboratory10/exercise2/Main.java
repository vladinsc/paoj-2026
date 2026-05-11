package com.pao.laboratory10.exercise2;

import com.pao.laboratory10.exercise1.Tranzactie;
import com.pao.laboratory10.exercise1.TipTranzactie;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        // TODO: Implementează conform Readme.md
        Scanner scanner = new Scanner(System.in);

        // Citim N, daca nu exista ne oprim
        if (!scanner.hasNextInt()) {
            return;
        }

        int n = scanner.nextInt();
        List<Tranzactie> lista = new ArrayList<>();

        // Citim cele N tranzactii
        for (int i = 0; i < n; i++) {
            int id = Integer.parseInt(scanner.next());
            double suma = Double.parseDouble(scanner.next());
            String data = scanner.next();
            TipTranzactie tip = TipTranzactie.valueOf(scanner.next());

            lista.add(new Tranzactie(id, suma, data, tip));
        }

        // Definim un comparator in functie de suma pentru a-l refolosi
        Comparator<Tranzactie> sumComparator = Comparator.comparingDouble(Tranzactie::getSuma);

        // Procesam comenzile
        while (scanner.hasNext()) {
            String comanda = scanner.next();

            switch (comanda) {
                case "UNIQUE_IDS": {
                    LinkedHashSet<Integer> uniqueIds = new LinkedHashSet<>();
                    for (Tranzactie t : lista) {
                        uniqueIds.add(t.getId());
                    }
                    System.out.println("IDs unice (" + uniqueIds.size() + "): " + uniqueIds.toString());
                    break;
                }

                case "MONTHLY_REPORT": {
                    // TreeMap sorteaza automat cheile (lunile de tip yyyy-MM) in ordine lexicografica
                    TreeMap<String, double[]> report = new TreeMap<>();
                    for (Tranzactie t : lista) {
                        String luna = t.getData().substring(0, 7);
                        report.putIfAbsent(luna, new double[]{0.0, 0.0}); // index 0 pentru CREDIT, 1 pentru DEBIT

                        if (t.getTip() == TipTranzactie.CREDIT) {
                            report.get(luna)[0] += t.getSuma();
                        } else {
                            report.get(luna)[1] += t.getSuma();
                        }
                    }

                    for (Map.Entry<String, double[]> entry : report.entrySet()) {
                        System.out.println(String.format(Locale.US, "%s: CREDIT %.2f RON, DEBIT %.2f RON",
                                entry.getKey(), entry.getValue()[0], entry.getValue()[1]));
                    }
                    break;
                }

                case "TOP": {
                    int nrTop = Integer.parseInt(scanner.next());
                    List<Tranzactie> copyList = new ArrayList<>(lista);
                    copyList.sort(sumComparator.reversed());

                    System.out.println("Top " + nrTop + ":");
                    int limita = Math.min(nrTop, copyList.size());
                    for (int i = 0; i < limita; i++) {
                        System.out.println(copyList.get(i).toString());
                    }
                    break;
                }

                case "SORT_ASC": {
                    lista.sort(sumComparator);
                    for (Tranzactie t : lista) {
                        System.out.println(t.toString());
                    }
                    break;
                }

                case "SORT_DESC": {
                    lista.sort(sumComparator.reversed());
                    for (Tranzactie t : lista) {
                        System.out.println(t.toString());
                    }
                    break;
                }

                case "REVERSE": {
                    Collections.reverse(lista);
                    for (Tranzactie t : lista) {
                        System.out.println(t.toString());
                    }
                    break;
                }

                case "MIN_MAX": {
                    if (!lista.isEmpty()) {
                        Tranzactie min = Collections.min(lista, sumComparator);
                        Tranzactie max = Collections.max(lista, sumComparator);

                        System.out.println("MIN: " + min.toString());
                        System.out.println("MAX: " + max.toString());
                    }
                    break;
                }

                case "CME_DEMO": {
                    try {
                        // Iterarea directa cu enhanced-for si modificarea listei va arunca ConcurrentModificationException
                        for (Tranzactie t : lista) {
                            lista.remove(t);
                        }
                    } catch (ConcurrentModificationException e) {
                        System.out.println("ConcurrentModificationException prins: modificare in iteratie detectata.");
                    }
                    break;
                }

                default:
                    break;
            }
        }

        scanner.close();
    }
}
