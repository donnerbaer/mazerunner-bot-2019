package de.challenge.model;

/**
 * Die Klasse Formular stellt Formulare (FORM) dar.
 */
public class Formular {

	// Attribute
	/**
	 * Das Attribut kuerzel gibt die Kurzbezeichnung des Formulars an. Das Format
	 * lautet: F|BotID|FormularID.
	 */
	private final String kuerzel;

	/**
	 * Das Attribut id gibt die Formular-ID an. Die ID ist wichtig um zu wissen, in
	 * welcher Reihenfolge die Formulare aufgenommen werden sollen.
	 */
	private int id;
	
	/**
	 * Das Attribut botId gibt die Zugehörigkeit des Formulars zu einen Bot an.
	 */
	private int botId;
	
	/**
	 * Das Attribut letzteBekannteFeld gibt die letzte bekannte Position
	 * (Feld-Referenz) an der das Formular gesehen wurde an.
	 */
	private Feld letzteBekannteFeld;

	// Konstruktor
	/**
	 * Ein Konstruktor für die Klasse Formular mit vier Übergabeparametern.
	 * 
	 * @param kuerzel            Erwartet die Kurzbezeichnung für das Formular.
	 * @param botId              Erwartet die playerId des Bots.
	 * @param id                 Erwartet die Id (Nummer / Reihenfolge) des
	 *                           Formulars.
	 * @param letzteBekannteFeld Erwartet das Feld auf dem das Formular zuletzt
	 *                           gesehen wurde.
	 */
	public Formular(String kuerzel, int botId, int id, Feld letzteBekannteFeld) {
		this.kuerzel = kuerzel;
		this.botId = botId;
		this.id = id;
		this.letzteBekannteFeld = letzteBekannteFeld;
	}

	// Getter & Setter Methoden
	public Feld getLetzteBekannteFeld() {
		return letzteBekannteFeld;
	}

	public void setLetzteBekannteFeld(Feld letzteBekannteFeld) {
		this.letzteBekannteFeld = letzteBekannteFeld;
	}

	public void setLetzteBekannteFelde(Feld letzteBekannteFeld) {
		this.letzteBekannteFeld = letzteBekannteFeld;
	}

	public String getKuerzel() {
		return kuerzel;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getBotId() {
		return botId;
	}

	public void setBotId(int botId) {
		this.botId = botId;
	}

	// Sonstige Methoden
	/**
	 * Gibt die Referenz und alle Attribute aus.
	 */
	public void printInfos() {
		System.err.println("Formular: " + this);
		System.err.println("Bot ID: " + this.botId);
		System.err.println("ID: " + this.id);
		System.err.println("Kuerzel: " + this.kuerzel);
		System.err.println("letzte bekannte Feld: " + this.letzteBekannteFeld);
	}

}
