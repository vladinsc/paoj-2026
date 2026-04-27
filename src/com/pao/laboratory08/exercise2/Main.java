package com.pao.laboratory08.exercise2;

import com.pao.laboratory08.exercise1.Student;
import com.pao.laboratory08.exercise1.Adresa;

import java.io.*;
import java.util.*;

public class Main {
    // Calea catre fisierul cu date — relativa la radacina proiectului
    private static final String FILE_PATH = "src/com/pao/laboratory08/tests/studenti.txt";

    public static void main(String[] args) throws Exception {
        List<Student> totiStudentii = new ArrayList<>();


        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String linie;
            while ((linie = br.readLine()) != null) {
                String[] parti = linie.split(",");
                if (parti.length == 4) {
                    String nume = parti[0].trim();
                    int varsta = Integer.parseInt(parti[1].trim());
                    String oras = parti[2].trim();
                    String strada = parti[3].trim();

                    Adresa adresa = new Adresa(oras, strada);
                    Student student = new Student(nume, varsta, adresa);
                    totiStudentii.add(student);
                }
            }
        } catch (IOException e) {
            System.out.println("Eroare la citirea fisierului de intrare: " + e.getMessage());
            return;
        }


        Scanner scanner = new Scanner(System.in);
        if (!scanner.hasNextInt()) {
            scanner.close();
            return;
        }
        int pragVarsta = scanner.nextInt();


        List<Student> studentiFiltrati = new ArrayList<>();
        for (Student s : totiStudentii) {
            if (s.getVarsta() >= pragVarsta) {
                studentiFiltrati.add(s);
            }
        }

        try (BufferedWriter fout = new BufferedWriter(new FileWriter("rezultate.txt"))) {
            for (Student s : studentiFiltrati) {
                fout.write(s.toString());
                fout.newLine();
            }
        } catch (IOException e) {
            System.out.println("Eroare la scrierea in fisier: " + e.getMessage());
        }


        System.out.println("Filtru: varsta >= " + pragVarsta);
        System.out.println("Rezultate: " + studentiFiltrati.size() + " studenti");
        System.out.println();

        for (Student s : studentiFiltrati) {
            System.out.println(s);
        }

        System.out.println();
        System.out.println("Scris in: rezultate.txt");

        scanner.close();
    }
}