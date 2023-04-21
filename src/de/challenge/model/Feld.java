package de.challenge.model;

/**
 * Die Klasse Feld stellt das Feld auf einem Spielfeld dar.
 */
public class Feld {

	// Attribute
	/**
	 * Das Attribut bezeichnung gibt die Typen-Bezeichnung (Wall oder Floor) des
	 * Feldes an.
	 */
	private String bezeichnung;

	/**
	 * Das Attribut kuerzel gibt die Kurzbezeichnung des Feldes an. Das Format
	 * lautet: F|BotID|FormularID .
	 */
	private String kuerzel;

	/**
	 * Das Attribut koordinateX gibt die Position auf der X-Achse des Spielfeldes
	 * an.
	 */
	private final int koordinateX;

	/**
	 * Das Attribut koordinateY gibt die Position auf der Y-Achse des Spielfeldes
	 * an.
	 */
	private final int koordinateY;

	/**
	 * Das Attribut pfadkosten gibt die Anzahl an Zügen, die gemacht werden müssen
	 * um dieses Feld zu erreichen, an.
	 */
	private int pfadkosten;

	/**
	 * Das Attribut sachbearbeiter ist die Referenz auf die Klasse Sachbearbeiter.
	 */
	private Sachbearbeiter sachbearbeiter;

	/**
	 * Das Attribut bot ist die Referenz auf die Klasse Bot.
	 */
	private Bot bot;

	/**
	 * Das Attribut formular ist die Referenz auf die Klasse Formular.
	 */
	private Formular formular;

	/**
	 * Das Attribute blatt ist die Referenz auf die Klasse Blatt.
	 */
	private Blatt blatt;

	// Konstruktor
	/**
	 * Ein Konstruktor für die Klasse Feld mit zwei Übergabeparametern.
	 *
	 * Das Kuerzel, die Pfadkosten und die Bezeichnung werden initial immer mit den
	 * Standardwerten befüllt. (kuerzel = "???"; pfadkosten = 9999; bezeichnung =
	 * "unbekannt")
	 * 
	 * @param koordinateX Erwartet die Position auf der X-Achse des Spielfeldes.
	 * @param koordinateY Erwartet die Position auf der Y-Achse des Spielfeldes.
	 */
	public Feld(int koordinateX, int koordinateY) {
		this.koordinateX = koordinateX;
		this.koordinateY = koordinateY;
		this.kuerzel = "???";
		this.pfadkosten = 9999;
		this.bezeichnung = "unbekannt";
	}

	// Getter & Setter Methoden
	public String getBezeichnung() {
		return bezeichnung;
	}

	public void setBezeichnung(String bezeichnung) {
		this.bezeichnung = bezeichnung;
	}

	public String getKuerzel() {
		return kuerzel;
	}

	public void setKuerzel(String kuerzel) {
		this.kuerzel = kuerzel;
	}

	public int getKoordinateX() {
		return koordinateX;
	}

	public int getKoordinateY() {
		return koordinateY;
	}

	public Sachbearbeiter getSachbearbeiter() {
		return sachbearbeiter;
	}

	public void setSachbearbeiter(Sachbearbeiter sachbearbeiter) {
		this.sachbearbeiter = sachbearbeiter;
	}

	public Bot getBot() {
		return bot;
	}

	public void setBot(Bot bot) {
		this.bot = bot;
	}

	public Formular getFormular() {
		return formular;
	}

	public void setFormular(Formular formular) {
		this.formular = formular;
	}

	public int getPfadkosten() {
		return pfadkosten;
	}

	public void setPfadkosten(int pfadkosten) {
		this.pfadkosten = pfadkosten;
	}

	public Blatt getBlatt() {
		return blatt;
	}

	public void setBlatt(Blatt blatt) {
		this.blatt = blatt;
	}

	// Sonstige Methoden
	/**
	 * Gibt die Referenz und alle Attribute aus.
	 */
	public void printInfos() {
		System.err.println("Feld: " + this);
		System.err.println("Bezeichnung: " + this.bezeichnung);
		System.err.println("Blatt: " + this.blatt);
		System.err.println("Bot: " + this.bot);
		System.err.println("Formular: " + this.formular);
		System.err.println("Koordinate X: " + this.koordinateX);
		System.err.println("Koordinate Y: " + this.koordinateY);
		System.err.println("Kuerzel: " + this.kuerzel);
		System.err.println("Pfadkosten: " + this.pfadkosten);
		System.err.println("Sachbearbeiter: " + this.sachbearbeiter);
	}

}
