package com.pao.laboratory13.exercise1;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        if (!scanner.hasNextInt()) {
            return;
        }
        int q = scanner.nextInt();
        scanner.nextLine(); // consume newline

        ProtocolEngine engine = new ProtocolEngine();
        for (int i = 0; i < q; i++) {
            if (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.trim().isEmpty()) {
                    i--; 
                    continue;
                }
                String result = engine.processCommand(line);
                if (result != null) {
                    System.out.println(result);
                }
            }
        }
    }
}
