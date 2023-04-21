package de.challenge.model;

public class Blatt {

	// Attribute
	/**
	 * Das Attribut rundenzaehler gibt die Runde an, in der das Blatt als fremdes
	 * Blatt erkannt wird.
	 */
	private int rundenzaehler;

	// Konstruktor
	/**
	 * Der Konstruktor erzeugt eine neue Blattinstanz mit den entsprechenden
	 * Parametern.
	 * 
	 * @param rundenzaehler Erwartet die aktuelle Runde.
	 * @param eigenesBlatt  Erwartet die Eingabe, ob das Blatt gerade vom eigenen
	 *                      Bot gekickt oder abgelegt wurde (entspricht true, sonst
	 *                      false).
	 * 
	 */
	public Blatt(int rundenzaehler, boolean eigenesBlatt) {
		if (eigenesBlatt == true) {
			/*
			 * Wenn wir selbst ein Blatt legen oder kicken, dann wollen wir es f�r eine gewisse Anzahl
			 * an Runden nicht "sehen" bzw. etwas damit tun, daher +x
			 */
			this.rundenzaehler = rundenzaehler + 10;
		} else
			this.rundenzaehler = rundenzaehler;
	}

	// Getter & Setter Methoden
	public int getRundenzaehler() {
		return rundenzaehler;
	}

	public void setRundenzaehler(int rundenzaehler) {
		this.rundenzaehler = rundenzaehler + 3;
	}

	// Sonstige Methoden
	/**
	 * Gibt die Referenz und alle Attribute aus.
	 */
	public void printInfos() {
		System.err.println("Blatt: " + this);
		System.err.println("Rundenzähler" + this.rundenzaehler);
	}
}
