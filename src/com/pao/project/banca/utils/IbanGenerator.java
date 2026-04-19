package com.pao.project.banca.utils;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public final class IbanGenerator {
    private static final AtomicLong contor = new AtomicLong(1000_0000_0000_0000L);
    private static final Random rnd = new Random();

    private IbanGenerator() {}

    public static String genereazaIban(String prefixBanca){
        String prefix = (prefixBanca != null && prefixBanca.length() >= 4) ? prefixBanca.substring(0, 4).toUpperCase() : "BNCA";
        int digits = 10 + rnd.nextInt(90);
        long nrCont =  contor.getAndIncrement();
        return String.format("RO%02d%s%016d",digits,prefix,nrCont);

    }
}
