# Aulab Chronicle

Aulab Chronicle è un giornale online sviluppato con Spring Boot come progetto finale del corso di Specializzazione Java di Aulab. Gli articoli scritti dai redattori vengono pubblicati soltanto dopo l'approvazione di un revisore.

## Funzionalità principali

- **Lettura pubblica:** prima pagina, archivio, sezioni tematiche, ricerca per parola chiave e pagina di ciascun autore.
- **Ruoli e permessi:** quattro profili con aree riservate, gestiti tramite Spring Security.
- **Flusso editoriale:** i nuovi articoli restano in attesa finché un revisore non decide se pubblicarli o rifiutarli.
- **Candidature:** un utente registrato può chiedere di diventare redattore, revisore o amministratore. L'amministratore valuta la richiesta e, se la accetta, assegna il ruolo e invia una notifica via email.
- **Immagini:** le copertine vengono caricate su Supabase Storage. Gli articoli che non hanno un'immagine ricevono un'illustrazione generata automaticamente per la loro sezione.

| Ruolo      | Cosa può fare                                               |
|------------|-------------------------------------------------------------|
| Lettore    | Legge gli articoli e invia una candidatura                  |
| Redattore  | Scrive, modifica ed elimina i propri articoli               |
| Revisore   | Approva o rifiuta gli articoli in attesa                    |
| Admin      | Gestisce le sezioni e valuta le candidature                 |

## Tecnologie

Java 23, Spring Boot 3.4 (Web, Data JPA, Security, Validation, Mail), Thymeleaf, Bootstrap 5, MySQL 8, Lombok, ModelMapper.

## Avvio rapido

**Requisiti:** JDK 23 e MySQL 8. Maven non serve da installare: il progetto include il Maven Wrapper (`mvnw`).

1. **Creare il database e caricare i dati di esempio.** Lo script crea le tabelle e inserisce sezioni, articoli e utenti di prova.
   ```bash
   mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS progettofinalebruzzone CHARACTER SET utf8mb4"
   mysql -u root -p --default-character-set=utf8mb4 progettofinalebruzzone < sql/setup.sql
   ```
2. **Configurare le credenziali.** Copiare `.env.example` in `.env` e compilare i valori. Il file `.env` è escluso da Git. Le credenziali Supabase e Mailtrap servono solo per caricare immagini e inviare email: senza di esse il resto dell'applicazione funziona comunque.
3. **Avviare l'applicazione.**
   ```bash
   ./mvnw spring-boot:run
   ```
   Su Windows (cmd o PowerShell) il comando è `mvnw spring-boot:run`. L'applicazione risponde su http://localhost:8080.

### Variabili di configurazione

| Variabile        | Descrizione                    | Predefinito                                         |
|------------------|--------------------------------|-----------------------------------------------------|
| `DB_URL`         | URL JDBC del database          | `jdbc:mysql://localhost:3306/progettofinalebruzzone` |
| `DB_USERNAME`    | Utente MySQL                   | `root`                                              |
| `DB_PASSWORD`    | Password MySQL                 | —                                                   |
| `SUPABASE_URL`   | URL del progetto Supabase      | —                                                   |
| `SUPABASE_KEY`   | Chiave API di Supabase         | —                                                   |
| `MAIL_USERNAME`  | Utente SMTP di Mailtrap        | —                                                   |
| `MAIL_PASSWORD`  | Password SMTP di Mailtrap      | —                                                   |

## Account di prova

Lo script `sql/setup.sql` crea un account per ciascun ruolo. La password, uguale per tutti, è `Chronicle2026!`.

| Email                | Ruolo     |
|----------------------|-----------|
| `admin@aulab.it`     | Admin     |
| `revisore@aulab.it`  | Revisore  |
| `redazione@aulab.it` | Redattore |
| `lettore@aulab.it`   | Lettore   |

Due articoli e una candidatura sono già in attesa: si possono valutare entrando come revisore o come amministratore.

## Struttura del progetto

```
src/main/java/it/aulab/progetto_finale_bruzzone/
├── config/        Sicurezza, interceptor per le notifiche, configurazione MVC
├── controllers/   Rotte per articoli, categorie, utenti, candidature e copertine
├── dtos/          Oggetti di trasferimento verso le viste
├── models/        Entità JPA (Article, Category, User, Role, Image, CareerRequest)
├── repositories/  Interfacce Spring Data
├── services/      Logica applicativa, email e upload delle immagini
└── utils/         Funzioni di supporto per i template
src/main/resources/
├── templates/     Viste Thymeleaf (index.html contiene il layout condiviso)
├── static/css/    Foglio di stile dell'applicazione
└── application.properties
sql/
└── setup.sql      Creazione del database con dati di esempio
```

Gli altri file nella cartella `sql/` sono gli script incrementali usati durante lo sviluppo e non servono per l'avvio.
