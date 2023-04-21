package de.challenge.model;

/**
 * Die Klasse Bot stellt den eigenen Bot innerhalb der Karte dar. Dieser sammelt
 * Formulare und Blaetter ein.
 */
public class Bot {

	// Attribute
	/**
	 * Das Attribut id gibt die ID des Bots an.
	 */
	private int id;
	
	/**
	 * Das Attribut anzahlFormulare gibt die Anzahl der Formulare an, die der
	 * Bot schon aufgenommen hat.
	 */
	private int anzahlFormulare;
	
	/**
	 * Das Attribut feld gibt die Referenz zum Feld an, auf dem der Bot sich
	 * befindet.
	 */
	private Feld feld;

	/**
	 * Das Attribut anzahlBlaetter gibt an, wie viele Blaetter Papier der Bot
	 * besitzt.
	 */
	private int anzahlBlaetter;

	// Konstruktor
	/**
	 * Konstruktor f�r den Bot mit zwei Übergabeparametern.
	 * 
	 * @param id             Erwartet die playerId (Init-Wert) des Bot.
	 * @param anzahlBlaetter Erwartet die Anzahl der Blaetter, die der Bot initial erh�lt.
	 */
	public Bot(int id, int anzahlBlaetter) {
		this.id = id;
		this.anzahlFormulare = 0; // FormularZähler
		this.anzahlBlaetter = anzahlBlaetter;
	}

	// Getter & Setter Methoden
	public int getAnzahlBlaetter() {
		return anzahlBlaetter;
	}

	public void setAnzahlBlaetter(int anzahlBlaetter) {
		this.anzahlBlaetter = anzahlBlaetter;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAnzahlFormulare() {
		return anzahlFormulare;
	}

	public void setAnzahlFormulare(int anzahlFormulare) {
		this.anzahlFormulare = anzahlFormulare;
	}

	public Feld getFeld() {
		return feld;
	}

	public void setFeld(Feld feld) {
		this.feld = feld;
	}

	// Sonstige Methoden
	/**
	 * Gibt die Referenz und alle Attribute aus.
	 */
	public void printInfos() {
		System.err.println("Bot: " + this);
		System.err.println("Anzahl Blaetter: " + this.anzahlBlaetter);
		System.err.println("Anzahl Formulare: " + this.anzahlFormulare);
		System.err.println("Feld: " + this.feld);
		System.err.println("ID: " + this.id);
	}

}
