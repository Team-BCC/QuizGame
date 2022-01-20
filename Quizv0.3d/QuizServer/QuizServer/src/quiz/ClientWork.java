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
package quiz;

/* Dichiarazione Moduli */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.dom.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;


/* Dichiarazione classe Gestione Client che estende Thread */
public class ClientWork extends Thread {

    protected final Socket client;			// Socket comunicazione con client
    protected String DatiRic;					// Stringa per contenere i dati ricevuti
    protected String DatiModif;				// Stringa per contenere i dati modificati
    final Callback decrementGet;            // Dichiarazione oggetto Callback per decrementare il n. atomico dei thread attivi
    protected String Nickname;                // Nickname
    protected int Points;                     // Punteggio
    protected ObjectOutputStream outVersoClient;  // Flussi di lettura e scrittura per oggetti
    protected ObjectInputStream inDalClient;
    protected Message answer;                 // Pacchetti di messaggi che il server e client si scambieranno
    protected Message question;
    protected Message handshake;
    protected Message response;
    protected int bestPoints;                 // Miglior punteggio raggiunto
    protected int boss1 = 0;
    protected int boss2 = 0;
    

    /* Costruttore con parametri */
    /* 

        Parametri:
        Socket client: Client creato ed accettato dal server.
        decrementGet: Metodo della classe AtomicInteger usata per conteggiare atomicamente i thread.

    */
    public ClientWork(Socket client, Callback decrementGet) {
        this.client = client;
        this.DatiRic="";
        this.DatiModif="";
        this.inDalClient=null;
        this.outVersoClient=null;
        this.decrementGet = decrementGet;
        this.Nickname ="";
        this.Points=100;
        this.bestPoints=100;
    }
    
    /* Metodi setter e getter */
    public String getDatiRic() {
        return DatiRic;
    }


    public void setDatiRic(String datiRic) {
        DatiRic = datiRic;
    }

    public String getNickname() {
        return Nickname;
    }

    public void setNickname(String nickname) {
        Nickname = nickname;
    }

    public int getPoints() {
        return Points;
    }

    public void setPoints(int points) {
        Points = points;
    }

    public int getbestPoints() {
        return bestPoints;
    }

    public void setbestPoints(int bestpoints) {
        bestPoints = bestpoints;
    }

    public void decPoints(Message message1) {
        Points = this.Points-message1.getValue();
    }

    public void incPoints(Message message1) {
        Points = this.Points+message1.getValue();
    }

    public String getDatiModif() {
        return DatiModif;
    }


    public void setDatiModif(String datiModif) {
        DatiModif = datiModif;
    }

    public Message prelevaDomanda(String nFile, int ntentativi) {

        List<Message> questions = new ArrayList<>();

        try {
            // Apertura file
            File file = new File(System.getProperty("user.dir")+"/src/quiz/"+nFile);
            //File file = new File("/home/quintab2122/public_html/sistemi/BCCOrientamento/Quizv0.3c/QuizServer/QuizServer/src/quiz/"+nFile);
            Scanner read = new Scanner (file);
            read.useDelimiter(",");
            String domanda, risposta, tentativi, punteggio;
            
            // Lettura dei campi sfruttando il limitatore impostato sopra
            while(read.hasNext())
            {
                domanda = read.next();
                risposta = read.next();
                tentativi = read.next();
                punteggio = read.next();
                int tent = Integer.parseInt(tentativi);
                int punt = Integer.parseInt(punteggio);
                //System.out.println(domanda+""+tentativi);

                //Creazione di un array di domande;
                Message packet = new Message(domanda, risposta, tent, punt);
                questions.add(packet);
            }
            read.close();
        }
        catch (Exception e) {
            System.out.println("\nErrore apertura file"+e);
        }
        

        Random rand = new Random();

        int int_random = rand.nextInt(ntentativi);
        
        // Ritorna una domanda casuale
        return questions.get(int_random);

    }

    public void Leaderboard(){
        
        final String xmlFilePath = System.getProperty("user.dir")+"/src/quiz/leaderboard.xml";
        //final String xmlFilePath="/home/quintab2122/public_html/sistemi/BCCOrientamento/leaderboard.xml";
        
        try {
 
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
 
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
 
            Document document = documentBuilder.parse(xmlFilePath);
            Element root = document.getDocumentElement();

 
            // player element
            Element player = document.createElement("player");
 
            root.appendChild(player);
 
            //you can also use staff.setAttribute("id", "1") for this
 
            // nickname element
            Element nickname = document.createElement("nickname");
            nickname.appendChild(document.createTextNode(this.getNickname()));
            player.appendChild(nickname);
 
            // punteggio migliore element
            Element points = document.createElement("points");
            points.appendChild(document.createTextNode(Integer.toString(this.getbestPoints())));
            player.appendChild(points);
 
 
            // create the xml file
            //transform the DOM Object to an XML File
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(xmlFilePath);
 
 
            transformer.transform(domSource, streamResult);
 
            System.out.println("Done creating XML File");
 
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    // Override del metodo run della classe padre Thread
    @Override
    public void run() {
    
    
        // Apertura Canali di scrittura e lettura
        try {
            System.out.println("\nAvviato thread con nome:"+Thread.currentThread().getName());
            //apro i flussi per la comunicazione
            outVersoClient = new ObjectOutputStream(client.getOutputStream());
            inDalClient = new ObjectInputStream(client.getInputStream());

        } catch (IOException e) {
            System.out.println("in o out fallito");
            System.exit(-1);
        }

        // Handshake assegnazione Nickname
        try {
            // Lettura nickname 
            handshake = (Message) inDalClient.readObject();  //attendo dati dal client
            this.setNickname(handshake.getMessage1());
            System.out.println(handshake.toString());
        } catch (IOException e) {
            System.out.println("Lettura fallita");
            this.stop();
        }
        catch (ClassNotFoundException ex){
            System.err.println("YOUR PROBLEM WAS HERE...");
        }
        catch (NullPointerException ex){
            System.err.println("Segmentation Fault");
            ex.printStackTrace();
        }

        // Finchè hai punti
        while(this.Points > 0) {

            // Metodo per prelevare la domanda
            if(this.Points > 199 && boss1 == 0) {

                question = prelevaDomanda("boss.txt",3);
                boss1 = 1;

            }
            else if(this.Points > 200 && this.Points < 800 && boss1 == 1)
            {

                question = prelevaDomanda("domande2.txt",11);

            }
            else if(this.Points > 799 && boss2 == 0)
            {

                question = prelevaDomanda("boss2.txt",3);
                boss2 = 1;

            }
            else if(this.Points > 799 && boss2 == 1)
            {

                question = prelevaDomanda("domande2.txt",11);

            }
            else
            {

                question = prelevaDomanda("domande.txt",20);

            }

            

            // Finchè hai tentativi
            do  {
                try {
                    outVersoClient.reset();
                    System.out.println(question.toString());

                    System.out.println("Thread in esecuzione con nome:"+Thread.currentThread().getName());
                    outVersoClient.writeObject(question);	// Scrivo al client la domanda

                    System.out.println("Attesa di risposta da:"+Thread.currentThread().getName());
                    answer=(Message) inDalClient.readObject();  //attendo risposta dal client

                    System.out.println(answer.toString());

                    // Se hai terminato i tentativi
                    if(question.getAttempts() == 1) question.setAttempts(question.getAttempts()-1);

                    // Se la risposta è sbagliata e hai ancora tentativi
                    if((answer.getMessage1()).equals(question.getMessage2())==false && question.getAttempts() > 1) {
                        question.setAttempts(question.getAttempts()-1);
                        response = new Message("Risposta errata! Hai ancora "+question.getAttempts()+" tentativi!\n","",question.getAttempts(),0,this.Points);
                        System.out.println(response.toString());
                        outVersoClient.writeObject(response);	// Scrivo al cliente l'esito
                    }   

                    outVersoClient.flush();


                } catch (IOException e) {
                    System.out.println("Lettura fallita");
                    this.stop();
                } catch (ClassNotFoundException ex){
                    System.err.println("YOUR PROBLEM WAS HERE...");
                } catch (NullPointerException ex){
                    System.err.println("Segmentation Fault");
                    ex.printStackTrace();
                }



            }while((answer.getMessage1()).equals(question.getMessage2())==false && this.Points > 0 && question.getAttempts() > 0);

            //  Se hai sbagliato al termine dei tentativi, decrementa il punteggio
            if(answer.getMessage1().equals(question.getMessage2())==false) {
                this.decPoints(question);
                try {
                    Message response = new Message("Risposta errata! Tentativi esauriti! Punteggio diminuito di "+question.getValue()+"!\n");
                    response.setPoints(this.Points);
                    System.out.println(response.toString());
                    outVersoClient.writeObject(response);	// Scrivo il risultato al client
                }
                catch (IOException e){
                    System.err.println("YOUR PROBLEM WAS HERE...");
                }
            }
            //  Se hai indovinato al termine dei tentativi, aumenta il punteggio
            if(answer.getMessage1().equals(question.getMessage2())==true) {
                this.incPoints(question);
                try {
                    Message response = new Message("Risposta esatta! Punteggio aumentato di "+question.getValue()+"!\n");
                    response.setPoints(this.Points);
                    System.out.println(response.toString());
                    outVersoClient.writeObject(response);	// Scrivo il risultato al client
                }
                catch (IOException e){
                    System.err.println("YOUR PROBLEM WAS HERE...");
                }
            }
            System.out.println("Punti rimasti:"+this.getPoints());

            if(this.getPoints() > this.getbestPoints()) this.setbestPoints(this.Points);
            
        }

        // aggiorno leaderboard
        Leaderboard();
        try {
                System.out.println("\nDisconnessione "+Thread.currentThread().getName()+this.client.getInetAddress()+":"+this.client.getLocalPort()+'\n');
                decrementGet.call();    // Decremento n.Thread atomicamente
                client.close();         // Chiusura client
        } catch (IOException e) {
                e.printStackTrace();
        }
        
    }
}  

/* Interface per implementazione chiamata DecrementAndGet */
interface Callback {
	void call();
 }