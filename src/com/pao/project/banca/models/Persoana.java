package com.pao.project.banca.models;

import java.util.Objects;

public abstract class Persoana implements Comparable<Persoana> {
    protected String id;
    protected String nume;
    protected String prenume;
    protected String cnp;
    protected String email;
    protected String telefon;
    protected Adresa adresa;

    public Persoana(String id, String nume, String prenume, String cnp, String email, String tel, Adresa adresa) {
        this.id = id;
        this.nume = nume;
        this.prenume = prenume;
        this.cnp = cnp;
        this.email = email;
        this.telefon = tel;
        this.adresa = adresa;
    }

    public abstract String getRol();
    public String getNumeComplet(){
        return prenume + " " + nume;
    }

    public String getId()      { return id; }
    public String getNume()    { return nume; }
    public String getPrenume() { return prenume; }
    public String getCnp()     { return cnp; }
    public String getEmail()   { return email; }
    public String getTelefon() { return telefon; }
    public Adresa getAdresa()  { return adresa; }

    public void setNume(String nume)       { this.nume = nume; }
    public void setPrenume(String prenume) { this.prenume = prenume; }
    public void setEmail(String email)     { this.email = email; }
    public void setTelefon(String telefon) { this.telefon = telefon; }
    public void setAdresa(Adresa adresa)   { this.adresa = adresa; }

    @Override
    public int compareTo(Persoana o) {
        /*
        * Sortare alfabetica dupa Nume , apoi prenume
        * */
        int cmp = this.getNume().compareTo(o.getNume());
        if (cmp != 0) return cmp;
        return this.getPrenume().compareTo(o.getPrenume());
    }
    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Persoana persoana = (Persoana) o;
        return Objects.equals(id, persoana.id) && Objects.equals(cnp, persoana.cnp);
    }

    @Override
    public int hashCode(){
        return Objects.hash(id, cnp);
    }
    @Override
    public String toString(){
        return String.format("[%s] %s %s (CNP: %s) | %s | %s", getRol(), getPrenume(), getNume(), getCnp(), getEmail(), getTelefon() );
    }
}
