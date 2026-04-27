package com.pao.laboratory08.exercise1;

import java.io.*;
import java.util.*;

public class Main {
    // Calea catre fisierul cu date — relativa la radacina proiectului
    private static final String FILE_PATH = "src/com/pao/laboratory08/tests/studenti.txt";

    public static void main(String[] args) throws Exception {
        List<Student> studenti = new ArrayList<>();


        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String nume = parts[0].trim();
                    int varsta = Integer.parseInt(parts[1].trim());
                    String oras = parts[2].trim();
                    String strada = parts[3].trim();

                    Adresa adresa = new Adresa(oras, strada);
                    Student student = new Student(nume, varsta, adresa);
                    studenti.add(student);
                }
            }
        } catch (IOException e) {
            System.out.println("Eroare la citirea fisierului: " + e.getMessage());
            return;
        }

        Scanner scanner = new Scanner(System.in);
        if (!scanner.hasNextLine()) {
            scanner.close();
            return;
        }

        String input = scanner.nextLine().trim();
        String[] comandaParts = input.split(" ", 2);
        String comanda = comandaParts[0];

        if (comanda.equals("PRINT")) {
            for (Student s : studenti) {
                System.out.println(s);
            }
        } else if (comanda.equals("SHALLOW") || comanda.equals("DEEP")) {
            if (comandaParts.length < 2) {
                System.out.println("Comanda invalida. Lipseste numele studentului.");
                scanner.close();
                return;
            }

            String numeCautat = comandaParts[1];
            Student studentOriginal = null;

            for (Student s : studenti) {
                if (s.getNume().equals(numeCautat)) {
                    studentOriginal = s;
                    break;
                }
            }

            if (studentOriginal != null) {
                Student studentClona;


                if (comanda.equals("SHALLOW")) {
                    studentClona = studentOriginal.shallowClone();
                } else {
                    studentClona = studentOriginal.deepClone();
                }


                studentClona.getAdresa().setOras("MODIFICAT");

                System.out.println("Original: " + studentOriginal);
                System.out.println("Clona: " + studentClona);
            } else {
                System.out.println("Studentul cu numele " + numeCautat + " nu a fost gasit.");
            }
        } else {
            System.out.println("Comanda necunoscuta.");
        }

        scanner.close();
    }
}