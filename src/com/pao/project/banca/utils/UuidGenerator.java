package com.pao.project.banca.utils;
import java.util.UUID;
public class UuidGenerator {
    private UuidGenerator() {}
    public static String generateIDClient() {
        return "CLT-" + UUID.randomUUID().toString();
    }
    public static String generateTranzactieID() {
        return "TRX-" + UUID.randomUUID().toString();
    }
}
