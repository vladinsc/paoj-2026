package com.pao.project.banca.utils;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class CreditCardNumberGenerator {

    private static final Random rnd = new Random();
    public static boolean checkLuhn(String cardNumber) {
        int nDigits = cardNumber.length();
        int sum = 0;
        boolean isSecond = false;
        for (int i = nDigits - 1; i >= 0; i--)
        {
            int d = cardNumber.charAt(i) - '0';
            if (isSecond == true)
                d = d * 2;
            sum += d / 10;
            sum += d % 10;
            isSecond = !isSecond;
        }
        return (sum % 10 == 0);
    }

    public static String generator(){
        StringBuilder sb;
        do {
            sb = new StringBuilder();
            for (int i = 0; i < 16; i++) {
                sb.append(rnd.nextInt(10));

            }
        } while (!checkLuhn(sb.toString()));
        return sb.toString();
    }


}
