/* 

Samuele Camilletti - 5 INB, 28/10/21

1. Comunicazione continua client-server fino all'inserimento di una determinata stringa es.exit
2. Server Multithreading
3. Il server scrive un log dei client che si sono collegati
4  Port Number da riga di comando 
(Parametri del metodo main)
http://groups.di.unipi.it/~dipierro/Didattica/LIP-C/Classi2/Metodi/main.html
5. Realizzare nell'applicazione server un meccanismo di protezione da eventuali connessioni malevole 
(es. numero massimo di client, eventuale delay es. dopo aver accettato un client aspetto qualche secondo). 
Opzionalmente realizzare un'applicazione client che invia una serie di richieste in sequenza al server (simulazione DOS)

*/

/* APPLICAZIONE LATO SERVER */

/* Dichiarazione Moduli */
package quiz;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

/* Dichiarazione classe Server */
public class Server01 {
    protected ServerSocket server; 			// Dichiarazione oggetto serversocket 
    protected Socket client;					// Dichiarazione oggetto socket per ricezione client
    protected int port;						// Porta
    protected BufferedReader inDalClient;		// Dichiarazione stream-in
    protected BufferedWriter outVersoClient;  // Dichiarazione stream-out
    final AtomicInteger threadsCount = new AtomicInteger(0); 		// Allocazione nuovo oggetto AtomicInteger usato per il conteggio dei thread in modo atomico (Valore di partenza 0)

    /* Costruttore con parametri */
    /* 

        Parametri:
        porta: porta su cui verrà creato il server da inserire per riga di comando.

    */
    public Server01(int porta) {

        this.server=null;
        this.client=null;
        this.port=porta;
        this.inDalClient=null;
    this.outVersoClient=null;
    }

    /* Metodi getter e setter */
    public ServerSocket getServer() {
        return server;
    }

    public void setServer(ServerSocket server) {
        this.server = server;
    }

    /* Metodi getter e setter */
    public int getPort() {
        return port;
    }

    /* Metodi getter e setter */
    public void setServer(int porta) {
        this.port = porta;
    }


    public BufferedReader getInDalClient() {
        return inDalClient;
    }


    public void setInDalClient(BufferedReader inDalClient) {
        this.inDalClient = inDalClient;
    }

    public BufferedWriter getOutVersoClient() {
        return outVersoClient;
    }


    public void setOutVersoClient(BufferedWriter outVersoClient) {
        this.outVersoClient = outVersoClient;
    }

    public Socket attendi(){

        //creo il Server
        try{


            //con ServerSocket L'applicazione si dichiara server in ascolto sulla porta
            server = new ServerSocket(port);
            System.out.println("Server avviato...");
            while(true){         // Finchè il server è attivo

                //rimane in attesa di un client
                this.client=server.accept();	
                System.out.println("\n------------------------\nNuovo client connesso:"+this.client.getInetAddress()+":"+this.client.getLocalPort()+"\n------------------------\n");

                // Aumenta e controlla il NUMERO MASSIMO DI CONNESSIONI (SETTATO A 10)
                if(threadsCount.incrementAndGet() < 11){			
                    Thread thread = new ClientWork(client, threadsCount::decrementAndGet);           // Alloca nuova classe thread
                    thread.start();									  							     // Avvia thread	
                    System.out.println("Thread attivi:"+threadsCount.get());			
                }
                else{
                    try {


                        //lettura dal client
                        inDalClient= new BufferedReader(new InputStreamReader(client.getInputStream()));

                        //scrittura sul client
                        outVersoClient= new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                    } catch (IOException e) {
                        System.out.println("Errore: in o out fallito");
                        System.exit(-1);
                    }
                    outVersoClient.write("Numero massimo di connessioni raggiunte. Riprova più tardi.");	// Scrivo al cliente la nuova stringa sullo stream-out
                    outVersoClient.flush();
                    threadsCount.decrementAndGet();				// Decrementa atomicamente il numero dato che ha raggiunto il massimo
                    client.close();								// Chiude la connessione accettata.
                    System.out.println("Numero massimo di connessioni raggiunte. ");					
                }

                // PROVA ATTESA
                try{
                    Thread.sleep(2000);							// Attesa di 2 secondi prima di rimettersi in ascolto per evitare attacchi simil DDoS
                }
                catch(InterruptedException e){
                    e.printStackTrace();
                    System.exit(-2);
                }
            }
        } catch (IOException e) {
            System.out.println("Errore durante connessione");
            e.printStackTrace();
        }

        return client;
    }

    public void chiudi() {
    	try {
            client.close();				// Chiusura server
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* 

        FUNZIONE MAIN:
        Parametri:
		args: array di stringhe dei parametri passati per linea di comando.

		Valori di uscita:
		0 eseguito con successo
		-1 Fallita apertura stream
		-2 Fallita sleep
		-3 Fallito il passaggio parametri

    */
    public static void main(String[] args) {

        // Controlla se sono stati inseriti parametri.
        /*if(args.length == 0 || args.length > 1) {
                System.out.println("Utilizzo corretto essere tipo: java Server01 <port>");
                System.exit(-3);
        }*/
        int porta = 6706;//Integer.valueOf(args[0]).intValue();	// Cast ad intero della stringa inserita per riga di comando
        Server01 server = new Server01(porta);	// Istanzio un nuovo oggetto server
        server.attendi();		// Attesa e "binding" di nuove connessioni client
        server.chiudi();		// Chiusura connessione server
        System.exit(0);
    }
}

