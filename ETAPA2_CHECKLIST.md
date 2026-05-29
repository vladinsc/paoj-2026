# Audit Proiect Etapa II — Aplicație Bancară

Acest fișier documentează îndeplinirea cerințelor pentru **Etapa II** a proiectului PAO.

---

## 1. Persistență JDBC (1p + 1p + 4p + 2p)

### 1.1 — Schema bazei de date & Conexiune
- [x] **`schema.sql`**: [src/com/pao/project/banca/resources/schema.sql](src/com/pao/project/banca/resources/schema.sql)
  - Conține `CREATE TABLE IF NOT EXISTS` pentru: `adrese`, `clienti`, `conturi`, `carduri`, `tranzactii`.
  - Include chei primare (`PRIMARY KEY`) și relații (`FOREIGN KEY`).
- [x] **`DatabaseConnection`**: [src/com/pao/project/banca/utils/DatabaseConnection.java](src/com/pao/project/banca/utils/DatabaseConnection.java)
  - Implementat ca **Singleton**.
  - Folosește SQLite pentru portabilitate (`jdbc:sqlite:paoj_proiect.db`).

### 1.2 — Repository Generic & Implementări
- [x] **`Repository<T, ID>`**: [src/com/pao/project/banca/repository/Repository.java](src/com/pao/project/banca/repository/Repository.java)
- [x] **Implementări concrete (4 entități)**:
  1. **`ClientRepository`**: [src/com/pao/project/banca/repository/ClientRepository.java](src/com/pao/project/banca/repository/ClientRepository.java)
  2. **`ContRepository`**: [src/com/pao/project/banca/repository/ContRepository.java](src/com/pao/project/banca/repository/ContRepository.java)
  3. **`CardRepository`**: [src/com/pao/project/banca/repository/CardRepository.java](src/com/pao/project/banca/repository/CardRepository.java)
  4. **`TranzactieRepository`**: [src/com/pao/project/banca/repository/TranzactieRepository.java](src/com/pao/project/banca/repository/TranzactieRepository.java)
- [x] **Calitate SQL**:
  - Se folosește **`PreparedStatement`** peste tot.
  - Se folosește **`try-with-resources`** pentru închiderea automată a resurselor.

---

## 2. Tranzacții JDBC (2p)

- [x] **Operație tranzacțională**: [src/com/pao/project/banca/service/ContService.java](src/com/pao/project/banca/service/ContService.java) (metoda `transfera`)
  - Utilizează `connection.setAutoCommit(false)`, `commit()` și `rollback()`.
  - Garantează că banii nu dispar dacă transferul eșuează la jumătate (debitează sursa, creditează destinația, salvează 2 înregistrări de tranzacție).

---

## 3. Interogări avansate cu JOIN (2p)

Cele 3 interogări complexe sunt implementate în **`ClientRepository`**:

1. **Raport Sold Total per Client** (`getClientiCuSoldTotal`):
   - Unește `clienti` cu `conturi` și face `SUM(sold)`.
2. **Top Clienți după Numărul de Carduri** (`getTopClientiDupaNrCarduri`):
   - Unește `clienti` -> `conturi` -> `carduri` și face `COUNT(numar_card)`.
3. **Ultimele 5 Tranzacții ale unui Client** (`getTranzactiiRecenteClient`):
   - Unește `tranzactii` -> `conturi` -> `clienti` folosind condiția `OR` pe IBAN sursă/destinație.

---

## 4. Serviciu de Audit (1p)

- [x] **`AuditService`**: [src/com/pao/project/banca/service/AuditService.java](src/com/pao/project/banca/service/AuditService.java)
  - Singleton, scrie în `audit.csv`.
  - **Thread-safe**: Metoda `logAction` este `synchronized`.
  - **Apelat din toate acțiunile**: Integrat în `ClientService`, `ContService` și `CardService`.

---

## 5. Demonstrație CLI (1p)

- [x] **`Main.java`**: [src/com/pao/project/banca/Main.java](src/com/pao/project/banca/Main.java)
  - Meniu interactiv care permite testarea tuturor funcționalităților (CRUD, Tranzacții, Rapoarte, Carduri).
