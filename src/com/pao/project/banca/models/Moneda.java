package com.pao.project.banca.models;

public enum Moneda {
    RON( "Romanian Leu"),
    EUR( "Euro"),
    USD( "US Dollar"),
    GBP( "British Pound"),
    JPY( "Japanese Yen"),
    CHF( "Swiss Franc"),
    CAD( "Canadian Dollar"),
    AUD( "Australian Dollar"),
    BGN( "Bulgarian Lev"),
    HUF( "Hungarian Forint"),
    PLN( "Polish Zloty"),
    CZK( "Czech Koruna");
    private final String descriere;

    Moneda(String exp){
        this.descriere = exp;
    }
    public String getDescriere() {
        return descriere;
    }
    @Override
    public String toString() {
        return descriere;
    }

}
