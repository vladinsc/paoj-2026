package com.pao.laboratory09.exercise2;

import com.pao.laboratory09.exercise1.TipTranzactie;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;

public class Main {
    private static final String OUTPUT_FILE = "output/lab09_ex2.bin";
    private static final int RECORD_SIZE = 32;

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        if (!scanner.hasNextInt()) {
            scanner.close();
            return;
        }

        int n = scanner.nextInt();
        scanner.nextLine();

        File file = new File(OUTPUT_FILE);
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }


        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(file))) {
            for (int i = 0; i < n; i++) {
                String line = scanner.nextLine().trim();
                String[] parts = line.split("\\s+");

                int id = Integer.parseInt(parts[0]);
                double suma = Double.parseDouble(parts[1]);
                String data = parts[2];
                TipTranzactie tip = TipTranzactie.valueOf(parts[3]);

                //id
                byte[] idBytes = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(id).array();
                dos.write(idBytes);

                //suma
                byte[] sumaBytes = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putDouble(suma).array();
                dos.write(sumaBytes);

                // data
                String paddedData = String.format("%-10s", data);
                dos.write(paddedData.substring(0, 10).getBytes("US-ASCII"));

                // tip
                dos.writeByte(tip == TipTranzactie.CREDIT ? 0 : 1);

                //status 0 == Pending
                dos.writeByte(0);

                // padding
                dos.write(new byte[8]);
            }
        }


        try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] cmdParts = line.split("\\s+");
                String command = cmdParts[0];

                if (command.equals("READ")) {
                    int idx = Integer.parseInt(cmdParts[1]);
                    printRecord(raf, idx);
                }
                else if (command.equals("UPDATE")) {
                    int idx = Integer.parseInt(cmdParts[1]);
                    String statusStr = cmdParts[2];

                    byte statusByte = 0;
                    if (statusStr.equals("PROCESSED")) {
                        statusByte = 1;
                    } else if (statusStr.equals("REJECTED")) {
                        statusByte = 2;
                    }

                    // Sarim exact la byte-ul de status (offset 23)
                    raf.seek((long) idx * RECORD_SIZE + 23);
                    raf.write(statusByte);

                    System.out.println("Updated [" + idx + "]: " + statusStr);
                }
                else if (command.equals("PRINT_ALL")) {
                    int numarInregistrari = (int) (raf.length() / RECORD_SIZE);
                    for (int i = 0; i < numarInregistrari; i++) {
                        printRecord(raf, i);
                    }
                }
            }
        }

        scanner.close();
    }


    private static void printRecord(RandomAccessFile raf, int idx) throws IOException {
        raf.seek((long) idx * RECORD_SIZE);
        byte[] record = new byte[RECORD_SIZE];
        raf.readFully(record);

        ByteBuffer buffer = ByteBuffer.wrap(record).order(ByteOrder.LITTLE_ENDIAN);

        int id = buffer.getInt();
        double suma = buffer.getDouble();

        byte[] dataBytes = new byte[10];
        buffer.get(dataBytes);
        String data = new String(dataBytes, "US-ASCII").trim();

        byte tipByte = buffer.get();
        String tip = (tipByte == 0) ? "CREDIT" : "DEBIT";

        byte statusByte = buffer.get();
        String status = "PENDING";
        if (statusByte == 1) status = "PROCESSED";
        else if (statusByte == 2) status = "REJECTED";


        System.out.println(String.format(Locale.US, "[%d] id=%d data=%s tip=%s suma=%.2f RON status=%s",
                idx, id, data, tip, suma, status));
    }
}