package de.challenge.model;

/**
 * Die Klasse Sachbearbeiter stellt den Sachbearbeiter (FINISH) dar.
 */
public class Sachbearbeiter {

	// Attribute
	/**
	 * Das Attribut kuerzel gibt die Kurzbezeichnung des Formulars an. Das Format
	 * lautet: Z|BotID|anzahlFormulare.
	 */
	private final String kuerzel;
	
	/**
	 * Das Attribut botId gibt die Spielernummer an.
	 */
	private int botId;
	
	/**
	 * Das Attribut anzahlFormulare gibt die Anzahl der benötigten Formulare an.
	 */
	private int anzahlFormulare;

	// Konstruktor
	/**
	 * @param kuerzel         Erwartet die Kurzbezeichnung für das Formular.
	 * @param botId           Erwartet die playerId des Bots.
	 * 
	 * @param anzahlFormulare Erwartet die Anzahl der benötigten Formulare.
	 */
	public Sachbearbeiter(String kuerzel, int botId, int anzahlFormulare) {
		this.kuerzel = kuerzel;
		this.botId = botId;
		this.anzahlFormulare = anzahlFormulare;
	}

	// Getter & Setter Methoden
	public String getKuerzel() {
		return kuerzel;
	}

	public int getBotId() {
		return botId;
	}

	public void setBotId(int botId) {
		this.botId = botId;
	}

	public int getAnzahlFormulare() {
		return anzahlFormulare;
	}

	public void setAnzahlFormulare(int anzahlFormulare) {
		this.anzahlFormulare = anzahlFormulare;
	}

	// Sonstige Methoden
	/**
	 * Gibt die Referenz und alle Attribute aus.
	 */
	public void printInfos() {
		System.err.println("Sachbearbeiter: " + this);
		System.err.println("BotId: " + this.botId);
		System.err.println("Kuerzel: " + this.kuerzel);
	}

}
