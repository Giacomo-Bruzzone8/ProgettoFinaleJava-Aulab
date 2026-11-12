-- =============================================================================
-- Aulab Chronicle — creazione del database con dati di esempio
--
-- ATTENZIONE: questo script CANCELLA tutte le tabelle e le ricrea da zero
-- con contenuti di esempio. Da eseguire su un database vuoto o di prova:
--
--   mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS progettofinalebruzzone CHARACTER SET utf8mb4"
--   mysql -u root -p --default-character-set=utf8mb4 progettofinalebruzzone < sql/setup.sql
--
-- Account di prova (password per tutti: Chronicle2026!)
--   admin@aulab.it       Admin      gestisce sezioni e candidature
--   revisore@aulab.it    Revisore   approva o rifiuta gli articoli
--   redazione@aulab.it   Redattore  scrive articoli
--   lettore@aulab.it     Lettore    può candidarsi in redazione
-- =============================================================================

SET NAMES utf8mb4;

DROP TABLE IF EXISTS users_roles;
DROP TABLE IF EXISTS career_request;
DROP TABLE IF EXISTS images;
DROP TABLE IF EXISTS articles;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100),
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50)
);

CREATE TABLE articles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100),
    subtitle VARCHAR(100),
    body TEXT,
    publish_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_accepted BOOLEAN DEFAULT NULL,
    user_id BIGINT,
    category_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE users_roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    role_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE images (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    path VARCHAR(255) NOT NULL,
    article_id BIGINT,
    FOREIGN KEY (article_id) REFERENCES articles(id)
);

CREATE TABLE career_request (
    id INT AUTO_INCREMENT PRIMARY KEY,
    body TEXT,
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id),
    role_id BIGINT,
    FOREIGN KEY (role_id) REFERENCES roles(id),
    is_checked BOOLEAN
);

-- ---- Ruoli e sezioni --------------------------------------------------------

INSERT INTO roles (id, name) VALUES
    (1, 'ROLE_ADMIN'), (2, 'ROLE_REVISOR'), (3, 'ROLE_WRITER'), (4, 'ROLE_USER');

INSERT INTO categories (id, name) VALUES
    (1, 'politica'), (2, 'economia'), (3, 'food&drink'), (4, 'sport'), (5, 'intrattenimento'), (6, 'tech');

-- ---- Utenti (password: Chronicle2026!) --------------------------------------

INSERT INTO users (id, username, email, password) VALUES
    (1, 'Direzione Chronicle', 'admin@aulab.it',     '$2a$10$LMK0cw8DwO59Roxin29ms.6dCBwPgZ0fo8HwYGGtytLRF5GgApYvS'),
    (2, 'Elena Sarti',         'revisore@aulab.it',  '$2a$10$LMK0cw8DwO59Roxin29ms.6dCBwPgZ0fo8HwYGGtytLRF5GgApYvS'),
    (3, 'Giulia Ferri',        'redazione@aulab.it', '$2a$10$LMK0cw8DwO59Roxin29ms.6dCBwPgZ0fo8HwYGGtytLRF5GgApYvS'),
    (4, 'Marco Riva',          'marco.riva@aulab.it','$2a$10$LMK0cw8DwO59Roxin29ms.6dCBwPgZ0fo8HwYGGtytLRF5GgApYvS'),
    (5, 'Luca Bianchi',        'lettore@aulab.it',   '$2a$10$LMK0cw8DwO59Roxin29ms.6dCBwPgZ0fo8HwYGGtytLRF5GgApYvS');

INSERT INTO users_roles (user_id, role_id) VALUES
    (1, 1), (2, 2), (3, 3), (4, 3), (5, 4);

-- ---- Articoli ---------------------------------------------------------------

INSERT INTO articles (title, subtitle, body, publish_date, is_accepted, user_id, category_id) VALUES

('Il bilancio partecipativo entra nei quartieri',
 'Sempre più comuni lasciano decidere ai cittadini una parte della spesa pubblica',
 'Una panchina nuova, un campetto illuminato, una biblioteca aperta la sera. Sono piccoli interventi, ma a sceglierli non è stata la giunta: sono stati i residenti, con un voto aperto a tutti dai sedici anni in su.\n\nIl bilancio partecipativo non è un\'idea recente, ma negli ultimi anni ha cambiato scala. I comuni che lo adottano destinano una quota fissa degli investimenti ai progetti proposti dai cittadini, li fanno valutare agli uffici tecnici e poi li mettono al voto.\n\nI critici parlano di cifre simboliche. I sostenitori rispondono che il valore sta altrove: chi propone un progetto impara come funziona un appalto, quanto costa davvero un marciapiede, perché i tempi della pubblica amministrazione sono quelli che sono. E torna a votare anche alle elezioni.',
 '2026-11-11', TRUE, 3, 1),

('Java ha più di trent\'anni e non sente l\'età',
 'Dalle applet al cloud: perché il linguaggio più dato per spacciato è ancora ovunque',
 'Ogni pochi anni qualcuno ne annuncia la fine, e ogni volta Java è ancora lì: nei sistemi delle banche, nei backend delle app, nei corsi che formano nuovi sviluppatori.\n\nIl segreto è un equilibrio raro tra stabilità e cambiamento. Il codice scritto vent\'anni fa compila ancora, ma il linguaggio di oggi ha record, pattern matching e thread virtuali che rendono semplice gestire migliaia di richieste contemporanee.\n\nAttorno al linguaggio è cresciuto un ecosistema enorme. Framework come Spring Boot permettono di avviare un\'applicazione web completa, con sicurezza e database, in pochi minuti. Non è un caso che sia ancora tra le prime scelte di chi deve costruire software destinato a durare.',
 '2026-11-10', TRUE, 4, 6),

('Il ritorno della trattoria di quartiere',
 'Menu corti, prezzi onesti e piatti del giorno: la ristorazione riscopre la semplicità',
 'Tovaglie a quadri, una lavagna con quattro primi e tre secondi, il vino della casa in caraffa. Dopo anni di locali concept e menu degustazione, la trattoria di quartiere sta vivendo una seconda giovinezza.\n\nA guidare il ritorno sono spesso giovani cuochi passati dalle cucine stellate. Portano tecnica e attenzione alle materie prime, ma rinunciano a tutto ciò che fa lievitare il conto: pochi piatti, cambiati ogni giorno in base al mercato, e niente sprechi.\n\nPer i clienti la formula funziona perché è leggibile. Si sa cosa si mangia e quanto si spende. Per i ristoratori è una scommessa sulla fedeltà: la trattoria vive di chi torna, non di chi passa una volta per la foto.',
 '2026-11-09', TRUE, 3, 3),

('Lo sport di base cerca allenatori',
 'Le società dilettantistiche faticano a trovare volontari: senza tecnici, le giovanili chiudono',
 'Il sabato mattina i campi di periferia sono pieni di bambini. Dietro ogni squadra, però, c\'è un adulto che ha rinunciato a qualcosa: il tempo libero, spesso una parte dello stipendio, a volte la pazienza.\n\nLe società sportive dilettantistiche denunciano da tempo una carenza di allenatori ed educatori. I corsi di formazione costano, le responsabilità crescono e il volontariato puro non basta più a coprire tutte le ore necessarie.\n\nAlcune realtà stanno sperimentando soluzioni nuove: collaborazioni con le università di scienze motorie, rimborsi trasparenti, squadre condivise tra più quartieri. L\'obiettivo è lo stesso per tutti: non dover dire a un bambino che quest\'anno la sua squadra non partirà.',
 '2026-11-08', TRUE, 4, 4),

('Affitti alle stelle, gli studenti scelgono la provincia',
 'Pendolarismo e stanze condivise: come cambia la vita dei fuori sede',
 'Una stanza singola vicino all\'università costa ormai quanto un intero appartamento in provincia. Così molti studenti fuori sede fanno due conti e scelgono di vivere a un\'ora di treno dalle lezioni.\n\nIl fenomeno ha effetti che vanno oltre i bilanci familiari. Le città universitarie perdono una parte della loro popolazione giovane, mentre i piccoli centri serviti dalla ferrovia vedono riaprire bar e copisterie.\n\nLe soluzioni proposte sono diverse: residenze pubbliche, incentivi a chi affitta a canone concordato, convenzioni con le aziende di trasporto. Nel frattempo, il pendolarismo diventa parte dell\'esperienza universitaria: si studia in treno e si fa amicizia sul binario.',
 '2026-11-07', TRUE, 3, 2),

('Il cinema del lunedì sera',
 'Biglietti scontati e rassegne a tema: le sale riconquistano il pubblico nei giorni feriali',
 'Per anni il lunedì è stato il giorno peggiore per un cinema: sale semivuote, proiezioni cancellate. Oggi in molte città è diventato un appuntamento fisso.\n\nLa ricetta è semplice: prezzo ridotto, un film scelto con cura, spesso una breve introduzione in sala. Alcune rassegne propongono classici restaurati, altre opere prime che faticano a trovare spazio nella programmazione ordinaria.\n\nIl pubblico risponde, soprattutto quello più giovane, che ritrova nella sala un\'esperienza collettiva che lo streaming non offre. Per gli esercenti non è solo una questione di incassi: è il modo per costruire una comunità di spettatori che torneranno anche nel fine settimana.',
 '2026-11-06', TRUE, 4, 5),

('Assistenti AI in redazione: prima le regole',
 'Strumenti utili per trascrivere e riassumere, ma la verifica resta un lavoro umano',
 'Trascrivere un\'intervista di un\'ora, riassumere un documento di cento pagine, suggerire titoli alternativi: gli assistenti basati sull\'intelligenza artificiale sono entrati nelle redazioni con compiti molto concreti.\n\nLe testate più attente hanno scelto di scrivere le regole prima di adottare gli strumenti. Cosa si può delegare, cosa no, come segnalare ai lettori l\'uso di questi sistemi. Il principio comune è che la responsabilità di ciò che viene pubblicato resta di una persona.\n\nÈ anche il motivo per cui il ruolo del revisore non perde importanza, anzi. Verificare fonti, dati e citazioni diventa il cuore del mestiere, proprio quando produrre testo costa sempre meno.',
 '2026-11-05', TRUE, 3, 6),

('Vini senza alcol, curiosità o futuro?',
 'Crescono le etichette dealcolate: i produttori sperimentano, i sommelier restano cauti',
 'Fino a pochi anni fa erano una stranezza da scaffale. Oggi i vini dealcolati compaiono nelle carte dei ristoranti e nelle fiere di settore, spinti da chi guida, da chi fa sport e da chi semplicemente vuole bere meno.\n\nProdurli non è banale. Togliere l\'alcol significa togliere anche una parte di profumi e struttura, e le cantine stanno sperimentando tecniche diverse per recuperarli. I risultati sono ancora disomogenei, ma in miglioramento.\n\nI sommelier invitano a non confrontarli direttamente con i vini tradizionali: sono un prodotto diverso, con un pubblico diverso. Per molti produttori rappresentano soprattutto un modo per parlare a chi, altrimenti, il vino non lo comprerebbe affatto.',
 '2026-11-04', TRUE, 4, 3),

('Padel, da moda a movimento',
 'Campi pieni anche d\'inverno: lo sport della racchetta corta cerca una struttura stabile',
 'Fino a qualche stagione fa sembrava una moda destinata a passare. Invece i campi da padel continuano a riempirsi, anche nei mesi freddi e anche nelle ore meno comode.\n\nIl successo si spiega con la facilità di accesso: le regole si imparano in mezz\'ora, si gioca in quattro e le partite sono divertenti anche tra principianti. È uno sport sociale prima ancora che agonistico.\n\nLa sfida adesso è trasformare l\'entusiasmo in un movimento strutturato: istruttori qualificati, settori giovanili, tornei con un calendario chiaro. Senza questi passaggi il rischio è che il boom si sgonfi con la stessa velocità con cui è arrivato.',
 '2026-11-03', TRUE, 3, 4),

('Podcast narrativi, l\'audio che ci fa compagnia',
 'Inchieste, storie vere e racconti a puntate: perché ascoltiamo sempre di più',
 'In metropolitana, in palestra, mentre si cucina. I podcast narrativi hanno trovato il loro spazio nei momenti in cui gli occhi sono occupati ma la testa è libera.\n\nIl genere che cresce di più è quello dell\'inchiesta a puntate: storie vere ricostruite con pazienza, documenti, interviste e una voce che accompagna l\'ascoltatore episodio dopo episodio. È giornalismo che si prende il tempo che serve.\n\nPer chi li produce la sfida è economica. Un buon podcast richiede mesi di lavoro, e la pubblicità da sola raramente basta. Abbonamenti, sostegno degli ascoltatori e collaborazioni con le testate sono le strade che si stanno percorrendo.',
 '2026-11-02', TRUE, 4, 5),

('Il lavoro ibrido tre anni dopo',
 'Uffici più piccoli e giorni in presenza concordati: cosa è rimasto dello smart working',
 'Doveva essere una parentesi, è diventato un modello. Molte aziende hanno trovato un equilibrio tra casa e ufficio, con due o tre giorni in presenza concordati con i team.\n\nGli effetti si vedono anche sulle città. Gli uffici si sono ridotti e trasformati in spazi per riunioni e lavoro di gruppo, mentre i quartieri residenziali hanno visto aprire spazi di coworking e caffè attrezzati.\n\nRestano aperte alcune questioni: il diritto alla disconnessione, la formazione dei più giovani, che in ufficio imparano anche osservando i colleghi, e le differenze tra chi può lavorare da remoto e chi no. Temi su cui contratti e normative stanno ancora cercando un assetto.',
 '2026-10-30', TRUE, 3, 2),

('Le assemblee civiche alla prova',
 'Cittadini estratti a sorte per discutere temi complessi: un esperimento che fa discutere',
 'Cento persone scelte a sorte, rappresentative della popolazione per età, genere e provenienza. Si incontrano per alcuni fine settimana, ascoltano esperti, discutono e alla fine consegnano raccomandazioni alle istituzioni.\n\nLe assemblee civiche sono state sperimentate su temi come il clima, la mobilità e l\'uso del suolo. Chi le ha organizzate racconta di discussioni sorprendentemente pacate e di proposte concrete.\n\nIl punto critico è il seguito. Se le raccomandazioni restano nel cassetto, la fiducia dei partecipanti si trasforma in delusione. Per questo i promotori chiedono che le istituzioni si impegnino in anticipo a rispondere pubblicamente, punto per punto.',
 '2026-10-28', TRUE, 4, 1),

-- In attesa di revisione (visibili nella dashboard del revisore)
('Passkey, addio password?',
 'Accedere con l\'impronta o il volto è più sicuro: ecco come funziona',
 'Le password sono il punto debole della sicurezza online: si dimenticano, si riutilizzano, si rubano. Le passkey promettono di sostituirle con qualcosa che non si può indovinare.\n\nIl principio è semplice. Al posto di una parola segreta, il dispositivo crea una coppia di chiavi: una resta sul telefono o sul computer, l\'altra viene registrata dal sito. Per accedere basta sbloccare il dispositivo con l\'impronta, il volto o un PIN.\n\nIl vantaggio principale è che non c\'è nulla da rubare sul server e nulla da digitare su un sito falso. La diffusione è ancora graduale, ma i principali servizi le supportano già.',
 '2026-11-12', NULL, 3, 6),

('La maratona cambia percorso',
 'Più parchi e meno curve: il nuovo tracciato promette tempi più veloci',
 'Il percorso della maratona cittadina cambia volto. Il nuovo tracciato attraversa tre parchi, elimina due dei tratti più tortuosi e riduce i saliscendi del finale.\n\nGli organizzatori puntano a migliorare i tempi e a rendere la gara più piacevole anche per chi corre per la prima volta. Sono previsti più punti di ristoro e zone dedicate al tifo lungo il percorso.\n\nRestano da definire le modifiche alla viabilità. I residenti chiedono informazioni chiare con largo anticipo, e i negozianti delle vie interessate sperano in un indotto che compensi le chiusure della mattina.',
 '2026-11-12', NULL, 4, 4);

-- ---- Una candidatura in attesa (visibile nella dashboard admin) -------------

INSERT INTO career_request (body, user_id, role_id, is_checked) VALUES
('Leggo l\'Aulab Chronicle da quando è nato e mi piacerebbe contribuire.\n\nHo scritto per il giornale della mia università e collaboro con un blog di cinema. Vorrei proporre recensioni e approfondimenti per la sezione intrattenimento.',
 5, 3, FALSE);
