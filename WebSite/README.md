# QuizGame

## Modalità di gioco
L’utente dopo aver scelto il suo nickname in-game, viene posto di fronte a una
serie di domande alla quale dovrà rispondere correttamente inserendo la
risposta nell’apposito contenitore testuale. L’utente inizia la partita con un
punteggio standard di 100 punti. Ogni domanda ha un suo valore in termini di
punteggio ed un numero di tentativi massimi. Se l’utente indovinerà la
domanda il punteggio salirà del valore di essa, se invece al termine dei tentativi
non ci sarà riuscito il suo punteggio decrementerà del medesimo valore. Il
termine della partita viene sancito nel momento in cui il giocatore esaurisce i
suoi punti (0 punti) e il punteggio massimo raggiunto verrà registrato, insieme
al suo nickname, in una classifica globale con tutti gli altri giocatori.

## Architettura dell’applicazione
Al fine di realizzare un’applicazione che fosse indipendente dalla piattaforma su
cui viene avviata e che potesse mostrare le caratteristiche dei socket, threading
e oggetti è stata utilizzata un’architettura di comunicazione client/server.
Il server implementa la tecnica del multi-threading per consentire la
connessione di più client connessi contemporaneamente. La connessione e il
trasferimento dei dati per ogni thread è implementata attraverso l’IPC dei
Socket i quali, in questo caso, lavorano sul protocollo TCP/IP.
Le informazioni che il server e i client si scambiano vengono racchiuse
attraverso una classe condivisa Messaggio la quale contiene tutti i dati relativi
al funzionamento del gioco (vedere progettazione ad oggetti).
Il server mette a disposizione questo servizio sulla porta 6076, la quale è stata
definita in maniera standard per semplificare la connessione.

## Implementazioni di sicurezza lato server
Per evitare un affollamento di troppe connessioni contemporanee è stato scelto
preventivamente di limitare il numero di thread creabili a 10. E’ stata inoltre
implementata una sleep di 5 secondi dopo l’allocazione di un thread per
evitare flooding di richieste.

## Interfacciamento con Web Server
Al termine della partita ogni thread accede in scrittura ad un file XML in cui
scrive i migliori punteggi degli utenti. Tale file è letto dall’interprete php nel
momento in cui viene richiesta la pagina web relativa al gioco, la quale è
disponibile in rete attraverso un Web Server Apache in esecuzione sulla stessa
macchina sulla porta 8289.
