CREATE TABLE IF NOT EXISTS adrese (
    id VARCHAR(50) PRIMARY KEY,
    strada VARCHAR(100),
    numar VARCHAR(10),
    oras VARCHAR(50),
    judet VARCHAR(50),
    cod_postal VARCHAR(20),
    tara VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS clienti (
    id VARCHAR(50) PRIMARY KEY,
    nume VARCHAR(50),
    prenume VARCHAR(50),
    cnp VARCHAR(13) UNIQUE,
    email VARCHAR(100),
    telefon VARCHAR(20),
    adresa_id VARCHAR(50),
    FOREIGN KEY (adresa_id) REFERENCES adrese(id)
);

CREATE TABLE IF NOT EXISTS conturi (
    iban VARCHAR(34) PRIMARY KEY,
    client_id VARCHAR(50),
    tip_cont VARCHAR(20),
    moneda VARCHAR(10),
    sold REAL,
    activ BOOLEAN,
    nume_banca VARCHAR(100),
    FOREIGN KEY (client_id) REFERENCES clienti(id)
);

CREATE TABLE IF NOT EXISTS carduri (
    numar_card VARCHAR(16) PRIMARY KEY,
    iban VARCHAR(34),
    pin VARCHAR(4),
    tip_card VARCHAR(20),
    nume_detinator VARCHAR(100),
    status VARCHAR(20),
    FOREIGN KEY (iban) REFERENCES conturi(iban)
);

CREATE TABLE IF NOT EXISTS tranzactii (
    id VARCHAR(50) PRIMARY KEY,
    iban_sursa VARCHAR(34),
    iban_destinatie VARCHAR(34),
    suma REAL,
    tip_tranzactie VARCHAR(50),
    descriere VARCHAR(255),
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
);
