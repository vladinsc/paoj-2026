package com.pao.laboratory05.audit;
import java.time.LocalDateTime;
import java.util.Arrays;

public class AngajatService {
    private Angajat[] angajati;
    private AuditEntry[] auditLog;
    private AngajatService() {
        this.angajati = new Angajat[0];
        this.auditLog = new AuditEntry[0];
    }

    private static class Holder {
        private static final AngajatService INSTANCE = new AngajatService();
    }

    public static AngajatService getInstance() {
        return Holder.INSTANCE;
    }
    private void logAction(String action, String target) {
        AuditEntry entry = new AuditEntry(action, target, LocalDateTime.now().toString());
        AuditEntry[] newLog = new AuditEntry[this.auditLog.length + 1];
        System.arraycopy(this.auditLog, 0, newLog, 0, this.auditLog.length);
        newLog[newLog.length - 1] = entry;
        this.auditLog = newLog;
    }

    public void addAngajat(Angajat a) {
        logAction("ADD",a.getNume());
        Angajat[] noiAngajati = new Angajat[this.angajati.length + 1];
        System.arraycopy(this.angajati, 0, noiAngajati, 0, this.angajati.length);
        noiAngajati[noiAngajati.length - 1] = a;
        this.angajati = noiAngajati;

        System.out.println("Confirmare: Angajatul '" + a.getNume() + "' a fost adaugat.");
    }

    public void printAll() {
        for (Angajat a : this.angajati) {
            System.out.println(a);
        }
    }

    public void listBySalary() {
        Angajat[] copie = this.angajati.clone();
        Arrays.sort(copie);

        for (Angajat a : copie) {
            System.out.println(a);
        }
    }

    public void findByDepartament(String numeDept) {
        boolean gasit = false;
        logAction("FIND_BY_DEPT",  numeDept);
        for (Angajat a : this.angajati) {
            if (a.getDepartament().nume().equalsIgnoreCase(numeDept)) {
                System.out.println(a);
                gasit = true;
            }
        }

        if (!gasit) {
            System.out.println("Niciun angajat in departamentul: " + numeDept);
        }
    }
    public void printAuditLog() {
        if (this.auditLog.length == 0) {
            System.out.println("Audit log-ul este gol.");
            return;
        }

        for (AuditEntry entry : this.auditLog) {
            System.out.println(entry);
        }
    }


}