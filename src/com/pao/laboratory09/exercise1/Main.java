package com.pao.laboratory09.exercise1;

import java.io.*;
import java.util.*;

public class Main {
    private static final String OUTPUT_FILE = "output/lab09_ex1.ser";

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        if (!scanner.hasNextInt()) {
            scanner.close();
            return;
        }

        int n = scanner.nextInt();
        scanner.nextLine();

        List<Tranzactie> tranzactii = new ArrayList<>();


        for (int i = 0; i < n; i++) {
            String line = scanner.nextLine().trim();
            String[] parts = line.split("\\s+");

            int id = Integer.parseInt(parts[0]);
            double suma = Double.parseDouble(parts[1]);
            String data = parts[2];
            String contSursa = parts[3];
            String contDestinatie = parts[4];
            TipTranzactie tip = TipTranzactie.valueOf(parts[5]);

            Tranzactie t = new Tranzactie(id, suma, data, contSursa, contDestinatie, tip);
            t.setNote("procesat");

            tranzactii.add(t);
        }


        File file = new File(OUTPUT_FILE);
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }


        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(tranzactii);
        }


        List<Tranzactie> listaDeserializata;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            listaDeserializata = (List<Tranzactie>) ois.readObject();
        }


        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }

            String[] cmdParts = line.split("\\s+");
            String command = cmdParts[0];

            if (command.equals("LIST")) {
                for (Tranzactie t : listaDeserializata) {
                    System.out.println(t);
                }
            }
            else if (command.equals("FILTER")) {
                String prefixData = cmdParts[1];
                boolean found = false;

                for (Tranzactie t : listaDeserializata) {
                    if (t.getData().startsWith(prefixData)) {
                        System.out.println(t);
                        found = true;
                    }
                }

                if (!found) {
                    System.out.println("Niciun rezultat.");
                }
            }
            else if (command.equals("NOTE")) {
                int searchId = Integer.parseInt(cmdParts[1]);
                boolean found = false;

                for (Tranzactie t : listaDeserializata) {
                    if (t.getId() == searchId) {
                        System.out.println("NOTE[" + searchId + "]: " + t.getNote());
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    System.out.println("NOTE[" + searchId + "]: not found");
                }
            }
        }

        scanner.close();
    }
}