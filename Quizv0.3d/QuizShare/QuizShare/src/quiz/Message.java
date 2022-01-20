package quiz;

/* Dichiarazione Moduli */
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;

/* Dichiarazione classe Messaggio */
public class Message implements Serializable{
    private String Message1;					// Stringa per contenere i dati ricevuti
    private String Message2;				// Stringa per contenere i dati modificati
    private String Nickname;                // Nickname
    private int Value;                     // Punteggio
    private int Attempts;
    private int Points;                  

    /* Costruttore  */
    /* 

    */
    public Message(String message1) {
        this.Message1=message1;
        this.Message2="";
        this.Attempts=0;
        this.Nickname ="";
        this.Value=0;
        this.Points=100;
    }
    public Message(String message1, String message2) {
        this.Message1=message1;
        this.Message2=message2;
        this.Attempts=0;
        this.Nickname ="";
        this.Value=0;
        this.Points=100;
    }
    public Message(String message1, String message2, int attempt, int value) {
        this.Message1=message1;
        this.Message2=message2;
        this.Attempts=attempt;
        this.Nickname ="";
        this.Value=value;
        this.Points=100;
    }
    public Message(String message1, String message2, int attempt, int value, String nickname) {
        this.Message1=message1;
        this.Message2=message2;
        this.Attempts=attempt;
        this.Nickname =nickname;
        this.Value=value;
        this.Points=100;
    }
    public Message(String message1, String message2, int attempt, int value, int points) {
        this.Message1=message1;
        this.Message2=message2;
        this.Attempts=attempt;
        this.Nickname ="";
        this.Value=value;
        this.Points=points;
    }


    
    /* Metodi setter e getter */
    public String getMessage1() {
        return Message1;
    }

    public void setMessage1(String message1) {
        Message1 = message1;
    }

    public String getMessage2() {
        return Message2;
    }

    public void setMessage2(String message2) {
        Message2 = message2;
    }

    public String getNickname() {
        return Nickname;
    }

    public void setNickname(String nickname) {
        Nickname = nickname;
    }

    public int getValue() {
        return Value;
    }

    public void setValue(int value) {
        Value = value;
    }

    public int getAttempts() {
        return Attempts;
    }

    public void setAttempts(int attempt) {
        Attempts = attempt;
    }

    public int getPoints() {
        return Points;
    }

    public void setPoints(int points) {
        Points = points;
    }

    public String toString(){

        return ("Msg1:"+this.getMessage1()+" Msg2:"+this.getMessage2()+" Value:"+this.getValue()+" Attempts:"+this.getAttempts()+" Points:"+this.getPoints());

    }

}