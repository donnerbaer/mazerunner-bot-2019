package de.challenge.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Pattern;

/**
 * Die Klasse Karte enthält die komplette Logik zum Bewegen des Bots. Nur die
 * Bewegung des eigenen Bots wird verarbeitet. Generische Bot werden nicht
 * beachtet. Grund dafür ist, dass durch Ausweichen oder Warten, damit die Bots
 * nicht aufeinander treffen, nur der eigene Bot einen Nachteil (1 Runde geht
 * verloren) bekommt. Lassen wir es aber darauf ankommen, erhalten beide Bots
 * den gleichen Nachteil.
 */
public class Karte {

	// Attribute
	/**
	 * Attribut das die letzte Aktion speichert.
	 */
	private String letzteAktion;

	/**
	 * Das Attribut groesseX gibt die Kartenbreite an.
	 */
	private final int groesseX;

	/**
	 * Das Attribut groesseY gibt die Kartenhoehe an.
	 */
	private final int groesseY;

	/**
	 * Das Attribut level gibt die Schwierigkeitsstufe an.
	 */
	private final int level;

	/**
	 * Das Attribut spielfeld beinhaltet alle Felder, die die Karte besitzt.
	 */
	private Feld[] spielfeld;

	/**
	 * Das Attribut formularListe enthält alle Formulare, die der Bot schon einmal
	 * gesehen hat.
	 */
	private List<Formular> formularListe;

	/**
	 * Das Attribut bot referenziert auf den eigenen Bot, der auf dem Spielfeld
	 * steht.
	 */
	private Bot bot;

	/**
	 * Das Attribut rundenzaehler gibt die aktuelle Runde an.
	 */
	private int rundenzaehler;

	// Konstruktor
	/**
	 * Konstruktor für die Instanz der Karte.
	 * 
	 * @param groesseX Erwartet die Kartenbreite.
	 * @param groesseY Erwartet die Kartenhöhe.
	 * @param level    Erwartet das Kartenlevel.
	 */
	public Karte(int groesseX, int groesseY, int level) {
		this.groesseX = groesseX;
		this.groesseY = groesseY;
		this.level = level;
		this.spielfeld = new Feld[groesseX * groesseY];
		this.formularListe = new ArrayList<>();
		this.rundenzaehler = 0;
		this.letzteAktion = "NullPointerExceptionBot";
	}

	// Methoden
	/**
	 * Die Methode generiereFelder weist einer Instanz der Klasse Karte ihre Felder
	 * zu.
	 * 
	 * Diese sollen erst einmal als Standard (aufgrund des Konstruktors) immer
	 * unbekannt (Bezeichnung = unbekannt, Kuerzel = ???, Pfadkosten = 9999) sein
	 * und unterscheiden sich lediglich in ihren x|y-Koordinaten.
	 */
	public void generiereFelder() {
		int index = 0;
		if (spielfeld == null) {
			System.err.println("ERROR: Es wurde kein Feld[] für das Attribute spielfeld erzeugt.");
		} else {
			while (index < getSpielfeld().length) {
				for (int y = 0; y < groesseY; y++) {
					for (int x = 0; x < groesseX; x++) {
						spielfeld[index] = new Feld(x, y);
						index++;
					}
				}
			}
		}
	}

	/**
	 * Methode zur Instanziierung des eigenen Bots.
	 * 
	 * Zudem wird anhand der Startkoordinaten der Bot dem Feld mit den gleichen
	 * Koordinaten zugeordnet und somit auf dem Spielfeld platziert.
	 * 
	 * @param playerId       Erwartet die Bot-ID.
	 * @param startX         Erwartet den Start-X-Wert.
	 * @param startY         Erwartet den Start-Y-Wert.
	 * @param anzahlBlaetter Erwartet die Anzahl der verfuegbaren Blaetter.
	 */
	public void erzeugeBot(int playerId, int startX, int startY, int anzahlBlaetter) {
		bot = new Bot(playerId, anzahlBlaetter);
		Feld startFeld = getFeld(startX, startY); // Ausgabe des Feldes, wo der Bot hinkommt
		bot.setFeld(startFeld); // Zuweisung des Feld zum Bot
		startFeld.setBot(bot);
		startFeld.setKuerzel(" o ");
		startFeld.setPfadkosten(0);
	}

	/**
	 * Eine Methode, die alle bisher gesammelten Spielfeld-Informationen
	 * zurücksetzt.
	 */
	public void hardReset() {
		for (Feld feld : this.spielfeld) {
			if (feld.getSachbearbeiter() == null) {
				feld.setBezeichnung("unbekannt");
				feld.setKuerzel("???");
			}
		}
	}

	/**
	 * Eine Methode, die alle bisher gesammelten Spielfeld-Informationen über Felder
	 * löscht.
	 * 
	 * Folgenden Bedingungen müssen zutreffen:
	 * <ul>
	 * <li>keinen Sachbearbeiter</li>
	 * <li>keine Mauern</li>
	 * <li>Pfadkosten != 1</li>
	 * <li>Feld nicht unbekannt</li>
	 * </ul>
	 */
	public void softReset() {
		for (Feld feld : this.spielfeld) {
			if (feld.getSachbearbeiter() == null && !feld.getBezeichnung().equals("WALL") && feld.getPfadkosten() != 1
					&& !feld.getBezeichnung().equals("unbekannt")) {
				feld.setBezeichnung("unbekannt");
				feld.setKuerzel("???");
			}
		}
	}

	/**
	 * Eine Methode, die nur in einem bestimmten Radius um die Position des Bots
	 * alle bisher gesammelten Spielfeld-Informationen über Felder löscht.
	 * 
	 * Folgenden Bedingungen müssen zutreffen:
	 * <ul>
	 * <li>keinen Sachbearbeiter</li>
	 * <li>keine Mauern</li>
	 * <li>Pfadkosten != 1</li>
	 * <li>Feld nicht unbekannt</li>
	 * </ul>
	 * 
	 * @param formular Erwartet ein Formular um dessen Position die Karte
	 *                 zurückgesetzt werden soll.
	 * 
	 */
	public void resetRadius(Formular formular) {
		for (Feld feld : this.spielfeld) {
			if (feld.getSachbearbeiter() == null && !feld.getBezeichnung().equals("WALL") && feld.getPfadkosten() > 1
					&& feld.getPfadkosten() <= 5 && !feld.getBezeichnung().equals("unbekannt")) {
				feld.setBezeichnung("unbekannt");
				feld.setKuerzel("???");
			}
		}
		formularListe.remove(formular);
	}

	/**
	 * Methode, die ein bestimmtes Feld anhand der eingegebenen Koordinaten
	 * zurückgibt.
	 * 
	 * @param koordinateX Erwartet die X-Koordinate.
	 * @param koordinateY Erwartet die Y-Koordinate.
	 * @return Ausgabe der Referenz als Datentyp Feld.
	 */
	public Feld getFeld(int koordinateX, int koordinateY) {
		if (koordinateX < 0) {
			koordinateX += groesseX;
		} else if (koordinateX >= groesseX) {
			koordinateX -= groesseX;
		}
		if (koordinateY < 0) {
			koordinateY += groesseY;
		} else if (koordinateY >= groesseY) {
			koordinateY -= groesseY;
		}
		return spielfeld[koordinateY * groesseX + koordinateX];
	}

	/**
	 * Methode zur Ausgabe des noerdlichen Feldes von einem bestimmten Feld.
	 * 
	 * @param feld Erwartet das Feld von dem das noerdliche Feld ausgegeben werden
	 *             soll.
	 * @return Ausgabe der Referenz des nördlichen Feldes.
	 */
	public Feld getNordFeld(Feld feld) {
		return getFeld(feld.getKoordinateX(), feld.getKoordinateY() - 1);
	}

	/**
	 * Methode zur Ausgabe des oestlichen Feldes von einem bestimmten Feld.
	 * 
	 * @param feld Erwartet das Feld von dem das oestliche Feld ausgegeben werden
	 *             soll.
	 * @return Ausgabe der Referenz des oestlichen Feldes.
	 */
	public Feld getOstFeld(Feld feld) {
		return getFeld(feld.getKoordinateX() + 1, feld.getKoordinateY());
	}

	/**
	 * Methode zur Ausgabe des suedlichen Feldes von einem bestimmten Feld.
	 * 
	 * @param feld Erwartet das Feld von dem das suedliche Feld ausgegeben werden
	 *             soll.
	 * @return Ausgabe der Referenz des suedlichen Feldes.
	 */
	public Feld getSuedFeld(Feld feld) {
		return getFeld(feld.getKoordinateX(), feld.getKoordinateY() + 1);
	}

	/**
	 * Methode zur Ausgabe des westlichen Feldes von einem bestimmten Feld.
	 * 
	 * @param feld Erwartet das Feld von dem das westliche Feld ausgegeben werden
	 *             soll.
	 * @return Ausgabe der Referenz des westlichen Feldes.
	 */
	public Feld getWestFeld(Feld feld) {
		return getFeld(feld.getKoordinateX() - 1, feld.getKoordinateY());
	}

	/**
	 * Methode zur System.Out-Ausgabe des Befehls "go north". Diese Methode kann
	 * benutzt werden, um den Bot in die noerdliche Richtung laufen zu lassen.
	 */
	public void gehNord() {
		System.out.println("go north");
		this.letzteAktion = "go";
	}

	/**
	 * Methode zur System.Out-Ausgabe des Befehls "go east".Diese Methode kann
	 * benutzt werden, um den Bot in die oestliche Richtung laufen zu lassen.
	 */
	public void gehOst() {
		System.out.println("go east");
		this.letzteAktion = "go";
	}

	/**
	 * Methode zur System.Out-Ausgabe des Befehls "go south". Diese Methode kann
	 * benutzt werden, um den Bot in die suedliche Richtung laufen zu lassen.
	 */
	public void gehSued() {
		System.out.println("go south");
		this.letzteAktion = "go";
	}

	/**
	 * Methode zur System.Out-Ausgabe des Befehls "go west". Diese Methode kann
	 * benutzt werden, um den Bot in die westliche Richtung laufen zu lassen.
	 */
	public void gehWest() {
		System.out.println("go west");
		this.letzteAktion = "go";
	}

	/**
	 * Methode zur System.Out-Ausgabe des Befehls "take". Diese Methode kann benutzt
	 * werden, um ein Formular bzw. Blatt aufzuheben.
	 */
	public void nimm() {
		System.out.println("take");
		this.letzteAktion = "take";
	}

	/**
	 * Methode zur System.Out-Ausgabe des Befehls "take". Diese Methode kann benutzt
	 * werden, um ein Formular aufzuheben.
	 */
	public void nimmFormular() {
		System.out.println("take");
		System.err.println("methode nimm Formular");
		this.letzteAktion = "take form";
	}

	/**
	 * Methode zur System.Out-Ausgabe des Befehls "take". Diese Methode kann benutzt
	 * werden, um ein Blatt aufzuheben.
	 */
	public void nimmBlatt() {
		System.out.println("take");
		System.err.println("methode nimmbLatt");
		this.letzteAktion = "take sheet";
	}

	/**
	 * Methode zur System.Out-Ausgabe des Befehls "kick". Diese Methode kann benutzt
	 * werden, um ein Formular bzw. Blatt in eine bestimmte Richtung zu kicken.
	 * 
	 * @param richtung Erwartet die Richtung in die gekickt werden soll. (in
	 *                 englischer Sprache)
	 */
	public void kick(String richtung) {
		System.out.println("kick " + richtung);
		this.letzteAktion = "kick";
	}

	/**
	 * Methode zur System.Out-Ausgabe des Befehls "kick". Diese Methode kann benutzt
	 * werden, um ein Formular in eine bestimmte Richtung zu kicken.
	 * 
	 * @param richtung Erwartet die Richtung in die gekickt werden soll. (in
	 *                 englischer Sprache)
	 */
	public void kickFormular(String richtung) {
		System.err.println("methode kickFormular");
		System.out.println("kick " + richtung);
		this.letzteAktion = "kick form";
	}

	/**
	 * Methode zur System.Out-Ausgabe des Befehls "kick". Diese Methode kann benutzt
	 * werden, um ein Blatt in eine bestimmte Richtung zu kicken.
	 * 
	 * @param richtung Erwartet die Richtung in die gekickt werden soll. (in
	 *                 englischer Sprache)
	 */
	public void kickBlatt(String richtung) {
		System.out.println("kick " + richtung);
		System.err.println("methode kickBlatt");
		this.letzteAktion = "kick sheet";
	}

	/**
	 * Methode zur System.Out-Ausgabe des Befehls "finish". Diese Methode kann
	 * benutzt werden, damit der Bot seine Formulare beim Sachbearbeiter abgibt.
	 */
	public void gibAb() {
		System.out.println("finish");
		this.letzteAktion = "finish";
	}

	/**
	 * Methode zur System.Out-Ausgabe des Befehls "position". Diese Methode kann
	 * benutzt werden, damit der Bot seine aktuelle Position innerhalb der Karte
	 * abfragt.
	 */
	public void position() {
		System.out.println("position");
		this.letzteAktion = "position";
	}

	/**
	 * Methode zur System.Out-Ausgabe des Befehls "put". Diese Methode kann benutzt
	 * werden, damit der Bot ein Blatt Papier auf das aktuelle Feld legt.
	 */
	public void legAb() {
		System.out.println("put");
		this.letzteAktion = "put";
	}

	/**
	 * Methode um den Feld-Status (Cell-Status) des aktuellen Feldes und seiner
	 * direkten Nachbarfelder auszulesen und abzuspeichern.
	 * 
	 * Die Speicherung des Status geschieht mit der Methode "speichereUmgebung(Feld
	 * feld, String cellStatus);", die innerhalb dieser Methode aufgerufen wird.
	 * 
	 * @param currentCellStatus Erwartet den Feld-Status (CellStatus) vom aktuellen
	 *                          Feld.
	 * @param northCellStatus   Erwartet den Feld-Status (CellStatus) vom
	 *                          noerdlichen Feld.
	 * @param eastCellStatus    Erwartet den Feld-Status (CellStatus) vom oestlichen
	 *                          Feld.
	 * @param southCellStatus   Erwartet den Feld-Status (CellStatus) vom suedlichen
	 *                          Feld.
	 * @param westCellStatus    Erwartet den Feld-Status (CellStatus) vom westlichen
	 *                          Feld.
	 */
	public void seheUmgebung(String currentCellStatus, String northCellStatus, String eastCellStatus,
			String southCellStatus, String westCellStatus) {
		Feld aktuellesFeld = bot.getFeld();
		speichereUmgebung(aktuellesFeld, currentCellStatus);
		speichereUmgebung(getNordFeld(aktuellesFeld), northCellStatus);
		speichereUmgebung(getOstFeld(aktuellesFeld), eastCellStatus);
		speichereUmgebung(getSuedFeld(aktuellesFeld), southCellStatus);
		speichereUmgebung(getWestFeld(aktuellesFeld), westCellStatus);
	}

	/**
	 * Methode zum Speichern des Feld-Status (Cell-Status).
	 * 
	 * @param feld       Erwartet das Feld in dem der Feld-Status gespeichert werden
	 *                   soll.
	 * @param cellStatus Erwartet den Feld-Status (CellStatus).
	 */
	public void speichereUmgebung(Feld feld, String cellStatus) {
		if (cellStatus.equals("WALL")) {
			feld.setKuerzel("###");
			feld.setBezeichnung("WALL");
		} else if (cellStatus.contains("FLOOR")) {
			// if-else betrifft nur kuerzel
			if (feld.getBot() == null) {
				feld.setKuerzel("   ");
			} else {
				feld.setKuerzel(" o ");
			}
			feld.setFormular(null);
			feld.setBezeichnung("FLOOR");
			feld.setBlatt(null);
		} else if (cellStatus.contains("SHEET")) { // Contains -> "SHEET !" möglich
			// if-else betrifft nur kuerzel
			if (feld.getBot() == null) {
				feld.setKuerzel("sss");
			} else {
				feld.setKuerzel("sos");
			}
			feld.setBezeichnung("FLOOR");
			if (!istBlatt(feld)) {
				feld.setBlatt(new Blatt(this.rundenzaehler, false));
			}
		} else {
			komplexerInhalt(feld, cellStatus);
			feld.setBlatt(null);
		}
	}

	/**
	 * Methode, um den Feldinhalt eines bestimmtes Feldes abzuspeichern.
	 * 
	 * Dazu wird zunaechst der String cellStatus in Segmente aufgeteilt. Damit die
	 * Informationen getrennt in Variablen abgespeichert werden können. Diese können
	 * dann verwendet werden, um den Sachbearbeiter bzw. das Formular zu erstellen.
	 * Besonderheit beim Formular: Da dieses ggf. schon an einer anderen Position
	 * vorhanden sein kann, wird geprüft, ob das nun erkannte Formular schon mal
	 * instanziiert wurde. In diesem Fall werden nur die Referenzen angepasst.
	 * 
	 * @param feld       Erwartet das Feldes auf den sich der String cellStatus
	 *                   bezieht.
	 * @param cellStatus Erwartet die Information über den Feldinhalt.
	 */
	public void komplexerInhalt(Feld feld, String cellStatus) {
		feld.setBezeichnung("FLOOR");
		// Der cellStatus wird in mehrere Segmente aufgeteilt.
		String[] segmente = cellStatus.split(Pattern.quote(" "));
		String spielerId = segmente[1];
		int botId = Integer.valueOf(spielerId);
		/*
		 * Wenn das Feld einen Sachbearbeiter enthält, wird der Sachbearbeiter
		 * instanziiert und mit den entsprechenden Segmentdetails gefüllt.
		 */
		if (segmente[0].equals("FINISH") && segmente.length >= 3) {
			String anzahl = segmente[2];
			int anzahlFormulare = Integer.valueOf(anzahl);
			String sachbearbeiterKuerzel = "Z" + botId + anzahlFormulare;
			feld.setSachbearbeiter(new Sachbearbeiter(sachbearbeiterKuerzel, botId, anzahlFormulare));
			feld.setKuerzel(sachbearbeiterKuerzel);

		}

		/*
		 * Wenn auf dem Feld ein Formular liegt, wird das Formular instanziiert und mit
		 * den entsprechenden Segmentdetails gefüllt.
		 */
		else if (segmente[0].equals("FORM") && segmente.length >= 3) {
			String formId = segmente[2];
			int formularId = Integer.valueOf(formId);
			String neueFormularKuerzel = "F" + botId + formularId;
			Formular neuesFormular = null;

			/*
			 * Die folgenden Codezeilen prüfen, ob das neu erkannte Formular schon einmal
			 * aufgedeckt wurde. Falls dies bejaht wird, wird über das Spielfeld iteriert
			 * und beim entsprechenden Feld (alte Position) der Verweis auf das Formular
			 * entfernt.
			 */
			for (Formular form : this.formularListe) {
				if (form.getKuerzel().equals(neueFormularKuerzel)) {
					neuesFormular = form;
					neuesFormular.setLetzteBekannteFelde(feld);
					for (Feld feld1 : this.spielfeld) {
						if (feld1.getFormular() == form) {
							feld1.setKuerzel("   ");
							feld1.setFormular(null);
							break;
						}
					}
				}
			}
			if (neuesFormular == null) {
				neuesFormular = new Formular(neueFormularKuerzel, botId, formularId, feld);
				formularListe.add(neuesFormular);
			}
			feld.setFormular(neuesFormular);
			feld.setKuerzel(neueFormularKuerzel);
		}
	}

	/**
	 * Methode, damit die Position des Bot in der Karte aktualisiert wird.
	 * 
	 * Zudem werden die Pfadkosten angepasst.
	 * 
	 * @param lastActionsResult Erwartet die Information des letzten
	 *                          Rundenergebnisses.
	 */
	public void verarbeiteBotBewegung(String lastActionsResult) {
		Feld altePosition = bot.getFeld();
		String aktion = lastActionsResult;
		Feld neuePosition = null;
		if (aktion.equals("OK NORTH")) {
			neuePosition = getNordFeld(altePosition);
		} else if (aktion.equals("OK EAST")) {
			neuePosition = getOstFeld(altePosition);
		} else if (aktion.equals("OK SOUTH")) {
			neuePosition = getSuedFeld(altePosition);
		} else if (aktion.equals("OK WEST")) {
			neuePosition = getWestFeld(altePosition);
		}
		setzePfadkostenZurueck();
		bot.setFeld(neuePosition);
		neuePosition.setBot(bot);
		neuePosition.setKuerzel(" o ");
		neuePosition.setPfadkosten(0);
		altePosition.setBot(null);
		altePosition.setKuerzel("   ");
		altePosition.setPfadkosten(9999);
	}

	/**
	 * Methode, damit die Pfadkosten der Felder aktualisiert werden. Dies muss nach
	 * jeder Runde durchgeführt werden.
	 */
	public void berechnePfadkosten() {

		Queue<Feld> zubearbeitendeFelder = new LinkedList<>();
		List<Feld> bearbeiteteFelder = new ArrayList<>();
		Feld aktuellesFeld = bot.getFeld();

		// alle Nachbarfelder der aktuellen Position sind zu bearbeitende Felder
		zubearbeitendeFelder.add(getNordFeld(aktuellesFeld));
		zubearbeitendeFelder.add(getOstFeld(aktuellesFeld));
		zubearbeitendeFelder.add(getSuedFeld(aktuellesFeld));
		zubearbeitendeFelder.add(getWestFeld(aktuellesFeld));

		bearbeiteteFelder.addAll(zubearbeitendeFelder);

		/*
		 * In der while-Schleife wird eine abgewandelte Variante des
		 * Dijkstra-Algorithmus ausgeführt. Die zu bearbeitenden Felder werden
		 * bearbeitet und danach werden deren Nachbarfelder zu zu bearbeitenden Feldern,
		 * wenn diese zuvor nicht schon bearbeitet wurden. Daher der Abgleich mit
		 * bearbeiteteFelder.
		 */
		while (zubearbeitendeFelder.size() > 0) {
			Feld feld = zubearbeitendeFelder.poll();
			if (!feld.getBezeichnung().equals("WALL")) {

				if (feld.getPfadkosten() > getNordFeld(feld).getPfadkosten()) {
					feld.setPfadkosten(getNordFeld(feld).getPfadkosten() + 1);
				}
				if (feld.getPfadkosten() > getOstFeld(feld).getPfadkosten()) {
					feld.setPfadkosten(getOstFeld(feld).getPfadkosten() + 1);
				}
				if (feld.getPfadkosten() > getSuedFeld(feld).getPfadkosten()) {
					feld.setPfadkosten(getSuedFeld(feld).getPfadkosten() + 1);
				}
				if (feld.getPfadkosten() > getWestFeld(feld).getPfadkosten()) {
					feld.setPfadkosten(getWestFeld(feld).getPfadkosten() + 1);
				}
				if (bearbeiteteFelder.contains(getNordFeld(feld)) == false) {
					zubearbeitendeFelder.add(getNordFeld(feld));
					bearbeiteteFelder.add(getNordFeld(feld));
				}
				if (bearbeiteteFelder.contains(getOstFeld(feld)) == false) {
					zubearbeitendeFelder.add(getOstFeld(feld));
					bearbeiteteFelder.add(getOstFeld(feld));
				}
				if (bearbeiteteFelder.contains(getSuedFeld(feld)) == false) {
					zubearbeitendeFelder.add(getSuedFeld(feld));
					bearbeiteteFelder.add(getSuedFeld(feld));
				}
				if (bearbeiteteFelder.contains(getWestFeld(feld)) == false) {
					zubearbeitendeFelder.add(getWestFeld(feld));
					bearbeiteteFelder.add(getWestFeld(feld));
				}
			}
		}
	}

	/**
	 * Methode, bei der die Pfadkosten jeden Feldes, ausgenommen der Botposition,
	 * auf den Standartwert 9999 gesetzt wird.
	 */
	public void setzePfadkostenZurueck() {
		for (Feld feld : spielfeld) {
			if (feld != bot.getFeld()) {
				feld.setPfadkosten(9999);
			}
		}
	}

	/**
	 * Methode bei dem die letzte Rundenaktion vom Bot verarbeitet wird.
	 * 
	 * Hat sich der Bot bewegt, wird diese Bewegung nun durch die Methode
	 * verarbeiteBotBewegung abgebildet.
	 * 
	 * @param lastActionsResult Erwartet das letzten Aktionsresultat.
	 */
	public void verarbeiteAktion(String lastActionsResult) {
		// verarbeite letzte Bewegung
		if (this.letzteAktion.equals("go") && lastActionsResult.startsWith("OK")) {
			verarbeiteBotBewegung(lastActionsResult);
		}
		// verarbeite letztes aufnehmen
		else if (this.letzteAktion.contains("take")) {
			if (this.letzteAktion.equals("take form") && lastActionsResult.equals("OK FORM")) {
				bot.setAnzahlFormulare(bot.getAnzahlFormulare() + 1);
			} else if (this.letzteAktion.equals("take sheet") && lastActionsResult.equals("OK SHEET")) {
				bot.setAnzahlBlaetter(bot.getAnzahlBlaetter() + 1);
				bot.getFeld().setBlatt(null);
			}
		}
		// verarbeite letztes schubsen
		else if (this.letzteAktion.contains("kick") && lastActionsResult.startsWith("OK")) {
			if (this.letzteAktion.equals("kick form")) {

			} else if (this.letzteAktion.equals("kick sheet")) {
				// Zur�cksetzen der alten Position des Blattes
				bot.getFeld().setBlatt(null);
				// Setzen der neuen Position des gekickten Blattes
				if (lastActionsResult.equals("OK NORTH")) {
					getNordFeld(bot.getFeld()).setBlatt(new Blatt(rundenzaehler, true));
				} else if (lastActionsResult.equals("OK EAST")) {
					getOstFeld(bot.getFeld()).setBlatt(new Blatt(rundenzaehler, true));
				} else if (lastActionsResult.equals("OK SOUTH")) {
					getSuedFeld(bot.getFeld()).setBlatt(new Blatt(rundenzaehler, true));
				} else if (lastActionsResult.equals("OK WEST")) {
					getWestFeld(bot.getFeld()).setBlatt(new Blatt(rundenzaehler, true));
				}
			}
		}
		// verarbeite letztes Blatt hinlegen
		else if (this.letzteAktion.contains("put") && lastActionsResult.startsWith("OK")) {
			bot.setAnzahlBlaetter(bot.getAnzahlBlaetter() - 1);
		}
	}

	/**
	 * Die Methode prüft, ob das übergebene Feld keine Wand, kein Sachbearbeiter und
	 * kein Formular ist, womit dort ein Formular hingekickt werden kann.
	 * 
	 * @param feld das zu prüfende Feld
	 * @return Gibt wenn dies zutrifft true, wenn nicht false zur�ck
	 */
	public boolean checkFeldFreiFormularkick(Feld feld) {
		if (!istWall(feld) && !istSachbearbeiter(feld) && !istFormular(feld)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Die Methode prüft, ob das übergebene Feld keine Wand, kein Sachbearbeiter,
	 * kein eigenes Formular und kein Blatt ist, womit dort ein Blatt hingekickt
	 * werden kann.
	 * 
	 * @param feld Erwartet das zu prüfende Feld.
	 * @return Gibt wenn dies zutrifft true und wenn nicht false zurück.
	 */
	public boolean checkFeldFreiBlattKick(Feld feld) {
		if (!istWall(feld) && !istSachbearbeiter(feld) && !istEigenesFormular(feld) && !istBlatt(feld)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Die Methode überprüft, ob das Formular auf dem Feld gekickt werden kann.
	 * 
	 * @param feld Erwartet das Feld von dem das Formular gekickt werden soll.
	 * @return Gibt wenn dies zutrifft true und wenn nicht false zurück.
	 */
	public boolean checkFormularKickBar(Feld feld) {
		if (checkFeldFreiFormularkick(getNordFeld(feld)) || checkFeldFreiFormularkick(getOstFeld(feld))
				|| checkFeldFreiFormularkick(getSuedFeld(feld)) || checkFeldFreiFormularkick(getWestFeld(feld))) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Die Methode überprüft, ob das Blatt auf dem Feld gekickt werden kann.
	 * 
	 * @param feld Erwartet das Feld von dem das Blatt gekickt wird.
	 * @return Gibt wenn dies zutrifft true, wenn nicht false zurück
	 */
	public boolean checkBlattKickbar(Feld feld) {
		if (checkFeldFreiBlattKick(getNordFeld(feld)) || checkFeldFreiBlattKick(getOstFeld(feld))
				|| checkFeldFreiBlattKick(getSuedFeld(feld)) || checkFeldFreiBlattKick(getWestFeld(feld))) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Methode in der der Bot ein fremdes Formular in eine Himmelsrichtung kickt,
	 * wenn dieses Feld auch frei ist.
	 * 
	 * @param aktuellesFeld Erwartet das Feld auf dem sich das fremde Formular
	 *                      befindet.
	 */
	public void kickFremdesFormular(Feld aktuellesFeld) {
		if (istFremdesFormular(aktuellesFeld)) {
			/*
			 * Es wird immer nach Norden gekickt, wenn frei (entsprechend Uhrzeigersinn geht
			 * es sonst weiter).
			 */
			if (checkFeldFreiFormularkick(getNordFeld(aktuellesFeld))) {
				kickFormular("north");
			} else if (checkFeldFreiFormularkick(getOstFeld(aktuellesFeld))) {
				kickFormular("east");
			} else if (checkFeldFreiFormularkick(getSuedFeld(aktuellesFeld))) {
				kickFormular("south");
			} else if (checkFeldFreiFormularkick(getWestFeld(aktuellesFeld))) {
				kickFormular("west");
			}
		}
	}

	/**
	 * Methode in der der Bot ein fremdes bzw. unbekanntes Blatt in eine
	 * Himmelsrichtung kickt, wenn diese Feld auch frei ist.
	 * 
	 * @param aktuellesFeld Erwartet das Feld auf dem sich das fremde Blatt
	 *                      befindet.
	 */
	public void kickFremdesBlatt(Feld aktuellesFeld) {

		if (istBlatt(aktuellesFeld) && aktuellesFeld.getBlatt().getRundenzaehler() <= rundenzaehler) {
			/*
			 * es wird immer nach Norden gekickt, wenn frei (entsprechend Uhrzeigersinn geht
			 * es sonst weiter
			 */

			if (checkFeldFreiBlattKick(getNordFeld(aktuellesFeld))) {
				kickBlatt("north");
			} else if (checkFeldFreiBlattKick(getOstFeld(aktuellesFeld))) {
				kickBlatt("east");
			} else if (checkFeldFreiBlattKick(getSuedFeld(aktuellesFeld))) {
				kickBlatt("south");
			} else if (checkFeldFreiBlattKick(getWestFeld(aktuellesFeld))) {
				kickBlatt("west");
			}
		}
	}

	/**
	 * Methode bei dem alle noch betretbaren unbekannten Felder einer Liste
	 * hinzugefügt werden.
	 * 
	 * @return Ausgabe einer Liste mit allen betretbaren unbekannten Feldern.
	 */
	public List<Feld> sucheBetretbareUnbekannteFelder() {
		List<Feld> lauffelderListe = new ArrayList<>();
		for (Feld feld : spielfeld) {
			if (feld.getBezeichnung().equals("FLOOR") && (getNordFeld(feld).getBezeichnung().equals("unbekannt")
					| getOstFeld(feld).getBezeichnung().equals("unbekannt")
					| getSuedFeld(feld).getBezeichnung().equals("unbekannt")
					| getWestFeld(feld).getBezeichnung().equals("unbekannt"))) {

				lauffelderListe.add(feld);
			}
		}
		return lauffelderListe;
	}

	/**
	 * Die Methode prüft, ob das Feld eine Wand ist.
	 * 
	 * @param feld Erwartet das zu überprüfende Feld.
	 * @return Gibt wenn dies zutrifft true und wenn nicht false zurück.
	 */
	public boolean istWall(Feld feld) {
		if (feld.getBezeichnung().equals("WALL")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Die Methode prüft, ob das Feld ein Sachbearbeiter ist.
	 * 
	 * @param feld Erwartet das zu überprüfende Feld.
	 * @return Gibt wenn dies zutrifft true und wenn nicht false zurück.
	 */
	public boolean istSachbearbeiter(Feld feld) {
		if (feld.getSachbearbeiter() != null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Die Methode prüft, ob auf dem Feld ein Formular liegt.
	 * 
	 * @param feld Erwartet das zu überprüfende Feld.
	 * @return Gibt wenn dies zutrifft true und wenn nicht false zurück.
	 */
	public boolean istFormular(Feld feld) {
		if (feld.getFormular() != null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Die Methode prüft, ob auf dem Feld ein fremdes Formular liegt.
	 * 
	 * @param feld Erwartet das zu überprüfende Feld.
	 * @return Gibt wenn dies zutrifft true und wenn nicht false zurück.
	 */
	public boolean istFremdesFormular(Feld feld) {
		if (istFormular(feld) && feld.getFormular().getBotId() != bot.getId()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Die Methode prüft, ob auf dem Feld ein eigenes Formular liegt.
	 * 
	 * Die Methoden wurde neben der Methode istFremdesFormular implementiert, um den
	 * Rueckgabewert einfacher verständlich zu machen.
	 * 
	 * @param feld Erwartet das zu überprüfende Feld
	 * @return Gibt wenn dies zutrifft true, wenn nicht false zurück
	 */
	public boolean istEigenesFormular(Feld feld) {
		if (istFormular(feld) && feld.getFormular().getBotId() == bot.getId()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Die Methode prüft, ob der Antrag beim Sachbearbeiter abgegeben werden kann.
	 * 
	 * @param feld Erwartet das zu überprüfende Feld.
	 * @return Gibt wenn dies zutrifft true und wenn nicht false zurück.
	 */
	public boolean istAntragAbgabeBereit(Feld feld) {
		if (feld.getSachbearbeiter().getAnzahlFormulare() == bot.getAnzahlFormulare()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Die Methode prüft, ob es sich bei dem Sachbearbeiter um den eigenen handelt.
	 * 
	 * @param feld Erwartet das zu überprüfende Feld.
	 * @return Gibt wenn dies zutrifft true und wenn nicht false zurück.
	 */
	public boolean istEigenerSachbearbeiter(Feld feld) {
		if (istSachbearbeiter(feld) && feld.getSachbearbeiter().getBotId() == bot.getId()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Methode prüft, ob auf dem Feld ein Blatt liegt.
	 * 
	 * @param feld Erwartet das zu überprüfende Feld.
	 * @return Gibt wenn dies zutrifft true und wenn nicht false zurück.
	 */
	public boolean istBlatt(Feld feld) {
		if (feld.getBlatt() != null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Methode, die alle Aktionen des Bots steuert.
	 * 
	 * Anhand der Vorhandenheit der Referenzen in den Variablen naechstenFormular,
	 * naechstenFormularFeld und zielFeld werden entsprechend Aktionen ausgeführt.
	 * M�glichen Aktionen sind: Blaetter hinlegen, Blaetter kicken, Blaetter
	 * aufnehmen, Formulare kicken, Formulare aufnehmen, Laufbewegungen und das
	 * Finish.
	 * 
	 */
	public void zufallBewegungsAlgorithmus() {
		Feld aktuellesFeld = bot.getFeld();
		Feld zielFeld = null;
		Feld naechstesFormularFeld = null;
		// Die Liste soll alle Felder zu denen der Bot hingehen könnte enthalten.
		List<Feld> lauffelderListe = new ArrayList<>();
		/*
		 * Die Liste soll das Feld bzw. die Felder, in deren Richtung gegangen werden
		 * soll, enthalten.
		 */
		List<Feld> richtungsListe = new ArrayList<>();

		// Referenzierung Ziel-Feld
		for (Feld feld : spielfeld) {
			if (istEigenerSachbearbeiter(feld) && istAntragAbgabeBereit(feld)) {
				zielFeld = feld;
				break;
			}
		}
		// Referenzierung nächstes Formular-Feld
		Formular naechstesFormular = null;
		if (formularListe.size() > 0) {
			for (Formular formular : formularListe) {
				if (formular.getBotId() == bot.getId() && formular.getId() == getBot().getAnzahlFormulare() + 1) {
					naechstesFormular = formular;
					break;
				}
			}
		}

		// Sucht nach der Referenz des naechsten Formulars im Spielfeld.
		if (naechstesFormular != null) {
			for (Feld feld : spielfeld) {
				if (feld.getFormular() == naechstesFormular) {
					naechstesFormularFeld = feld;
					break;
				}
			}
			/*
			 * Sollte das naechste Formular schon erkannt worden sein, liegt aber nicht dort
			 * wo es erwartet wird (Referenz wurde bereits geloescht), so wird die letzte
			 * bekannte Feldposition des Formulars als naechstes Formularfeld definiert.
			 */
			if (naechstesFormularFeld == null) {
				naechstesFormularFeld = naechstesFormular.getLetzteBekannteFeld();
			}
			/*
			 * Sollte sowohl im Spielfeld keine Referenz des naechsten Formulares und im
			 * Formular keine Referenz zum Feld (letzteBekanntePosition) vorhanden sein...
			 */
			if (naechstesFormularFeld == null) {
				resetRadius(naechstesFormular);
			}
		}
		// Zufallszahl fuer das kicken eines fremden Formulares
		int kickZufallFormular = (int) (Math.random() * 3 + 1);

		/*
		 * Blätter auf fremde Formulare legen (Level 5): wenn das aktuelle Feld ein
		 * fremdes Formular enthält und wir noch Blätter übrig haben, wird ein Blatt auf
		 * das fremde Formular gelegt.
		 */
		if (level == 5 && bot.getAnzahlBlaetter() > 0 && istFormular(aktuellesFeld) && istFremdesFormular(aktuellesFeld)
				&& !istBlatt(aktuellesFeld)) {
			legAb();
			aktuellesFeld.setBlatt(new Blatt(this.rundenzaehler, true));
		}
		/*
		 * Mit einer 1/3 Wahrscheinlichkeit wird ein fremdes Formular, auf dem der Bot
		 * steht, gekickt.
		 */
		else if (level >= 4 && istFremdesFormular(aktuellesFeld) && kickZufallFormular % 10 == 1
				&& !istBlatt(aktuellesFeld) && checkFormularKickBar(aktuellesFeld)
				&& (naechstesFormularFeld != null || zielFeld != null)) {
			kickFremdesFormular(aktuellesFeld);
		}
		/*
		 * Wenn ein naechstes Formularfeld vorhanden ist, dann geht der Bot dort hin
		 * bzw. wenn dieser drauf steht wird das Formular aufgenommen.
		 */
		else if (naechstesFormularFeld != null) {
			richtungsListe.add(naechstesFormularFeld);
			if (aktuellesFeld.getFormular() != null && naechstesFormularFeld == aktuellesFeld) {
				if (!istBlatt(aktuellesFeld)) {
					nimmFormular();
				} else {
					nimmBlatt();
				}
			} else {
				richtungsListe.add(naechstesFormularFeld);
				bewegungsmuster(richtungsListe, aktuellesFeld, naechstesFormular);
			}
		}
		/*
		 * Wenn ein Zielfeld(Sachbearbeiter + alle Formulare(eingesammelt)) vorhanden
		 * ist, dann geht der Bot dorthin bzw. wenn dieser darauf steht, wird der Antrag
		 * abgegeben.
		 */
		else if (zielFeld != null) {
			if (zielFeld == aktuellesFeld) {
				gibAb();
			} else {
				richtungsListe.add(zielFeld);
				bewegungsmuster(richtungsListe, aktuellesFeld, naechstesFormular);
			}
		}
		/*
		 * wenn kein zielFeld(Sachbearbeiter) und kein naechstes Formularfeld bekannt
		 * sind...
		 */
		else if (zielFeld == null && naechstesFormularFeld == null) {
			lauffelderListe.addAll(sucheBetretbareUnbekannteFelder());
			/*
			 * keine unbekannten betretbaren Felder mehr vorhanden sind, dann setze die
			 * Karte zurueck
			 */
			if (lauffelderListe.isEmpty()) {
				softReset();
				lauffelderListe.addAll(sucheBetretbareUnbekannteFelder());
			}
			/*
			 * alle naechsten betretbare Felder mit unbekannten Nachbarfeldern werden der
			 * richtungsListe hinzugefuegt
			 */
			int min = 0;
			for (Feld feld : lauffelderListe) {
				if (min == 0) {
					min = feld.getPfadkosten();
				} else if (min > feld.getPfadkosten()) {
					min = feld.getPfadkosten();
				}
			}
			for (Feld feld : lauffelderListe) {
				if (feld.getPfadkosten() <= min) {
					richtungsListe.add(feld);
				}
			}
			bewegungsmuster(richtungsListe, aktuellesFeld, naechstesFormular);
		}
	}

	/**
	 * Methode bei der aus einer Liste von betretbaren Feldern ein Feld als zielFeld
	 * definiert wird (ausgelagert in den Methoden normalBewegung, maximalBewegung,
	 * minimalBewegung). Sollten wir auf Blaetter treffen wird entschieden, ob wir
	 * es kicken bzw. aufnehmen. Sollte der Bot auf kein Blatt treffen, so wird er
	 * seine Laufaktion entsprechend ausführen.
	 * 
	 * @param richtungsListe    Erwartet die Liste von Feldern, die der Bot betreten
	 *                          kann.
	 * @param aktuellesFeld     Erwartet das aktuelle Feld auf dem der Bot steht.
	 * @param naechstesFormular Erwartet das nächste aufzusammelnde Formular.
	 */
	public void bewegungsmuster(List<Feld> richtungsListe, Feld aktuellesFeld, Formular naechstesFormular) {

		/*
		 * Wenn wir auf einem Blatt stehen, das wir nicht selbst dorthin gelegt haben
		 * (oder es nicht mehr wissen), dann schauen wir, ob wir das Blatt kicken können
		 * und kicken es. Wenn nicht nehmen wir es auf.
		 */
		if (istBlatt(aktuellesFeld) && aktuellesFeld.getBlatt().getRundenzaehler() <= this.rundenzaehler) {
			if (checkBlattKickbar(aktuellesFeld)) {
				kickFremdesBlatt(aktuellesFeld);
			} else {
				nimmBlatt();
			}
		} else if (istBlatt(getNordFeld(aktuellesFeld))
				&& getNordFeld(aktuellesFeld).getBlatt().getRundenzaehler() <= this.rundenzaehler) {
			this.gehNord();
		} else if (istBlatt(getOstFeld(aktuellesFeld))
				&& getOstFeld(aktuellesFeld).getBlatt().getRundenzaehler() <= this.rundenzaehler) {
			gehOst();
		} else if (istBlatt(getSuedFeld(aktuellesFeld))
				&& getSuedFeld(aktuellesFeld).getBlatt().getRundenzaehler() <= this.rundenzaehler) {
			gehSued();
		} else if (istBlatt(getWestFeld(aktuellesFeld))
				&& getWestFeld(aktuellesFeld).getBlatt().getRundenzaehler() <= this.rundenzaehler) {
			gehWest();
		} else {
			/*
			 * Neueste Teil-Variante der Methode bewegungsmuster() - Auslagerung der
			 * Feldwahl in die Methoden: normalBewegung, maximalBewegung, minimalBewegung
			 */
			Feld zielFeld = normalBewegung(richtungsListe);
			List<Feld> lauffelderListe = new LinkedList<>();
			lauffelderListe.add(zielFeld);
			/*
			 * Die lauffelderListe wird mit allen Feldern befüllt, die sich zwischen dem Bot
			 * und seinem Ziel befinden. Ausgangsposition ist dabei das zielFeld. Dabei wird
			 * noch darauf geachtet, dass der kuerzeste Weg benutzt wird, um zum Bot zu
			 * gelangen. Sollte ein Nachbarfeld des zielFeldes kleinere Pfadkosten besitzen,
			 * so wird dieses Feld als neues zielFeld fuer den naechsten Rundendurchlauf
			 * definiert.
			 */
			int lauffelderIndex = 0;
			while (lauffelderListe.get(lauffelderIndex).getPfadkosten() > 1) {
				if (getNordFeld(zielFeld).getPfadkosten() < lauffelderListe.get(lauffelderIndex).getPfadkosten()) {
					lauffelderListe.add(getNordFeld(zielFeld));
					lauffelderIndex++;
					zielFeld = getNordFeld(zielFeld);
				} else if (getOstFeld(zielFeld).getPfadkosten() < lauffelderListe.get(lauffelderIndex)
						.getPfadkosten()) {
					lauffelderListe.add(getOstFeld(zielFeld));
					lauffelderIndex++;
					zielFeld = getOstFeld(zielFeld);
				} else if (getSuedFeld(zielFeld).getPfadkosten() < lauffelderListe.get(lauffelderIndex)
						.getPfadkosten()) {
					lauffelderListe.add(getSuedFeld(zielFeld));
					lauffelderIndex++;
					zielFeld = getSuedFeld(zielFeld);
				} else if (getWestFeld(zielFeld).getPfadkosten() < lauffelderListe.get(lauffelderIndex)
						.getPfadkosten()) {
					lauffelderListe.add(getWestFeld(zielFeld));
					lauffelderIndex++;
					zielFeld = getWestFeld(zielFeld);
				}
			}
			/*
			 * Nun wird geguckt, in welcher Himmelsrichtung sich das ermittelte zielFeld mit
			 * den Pfadkosten == 1 befindet und dann die entsprechende Aktion ausgeführt.
			 */
			if (zielFeld == getOstFeld(aktuellesFeld)) {
				gehOst();
			} else if (zielFeld == getNordFeld(aktuellesFeld)) {
				gehNord();
			} else if (zielFeld == getSuedFeld(aktuellesFeld)) {
				gehSued();
			} else if (zielFeld == getWestFeld(aktuellesFeld)) {
				gehWest();
			}
			/*
			 * Im dem Fall, dass ein Formular verschoben wurde, geht der Bot immer zur
			 * letzten bekannten Position des Formulars (setLetzteBekannteFeld). Stellt er
			 * nun fest, dass dort kein Formular mehr ist, setzt er die letzte bekannte
			 * Position auf null und ruft die Methode resetRadius auf. Danach berechnet er
			 * seine Laufbewegung neu.
			 */
			else if (zielFeld == aktuellesFeld && aktuellesFeld == naechstesFormular.getLetzteBekannteFeld()) {
				naechstesFormular.setLetzteBekannteFeld(null);
				resetRadius(naechstesFormular);
				zufallBewegungsAlgorithmus();
			}
		}
	}

	/**
	 * Hierbei wird aus einer Liste ein zufälliges Feld ausgewählt und übergeben.
	 * 
	 * @param richtungsListe Erwartet eine Liste aller Felder, die als nächstes
	 *                       Zielfeld in Frage kommen.
	 * @return Gibt das ausgewählte Zielfeld zurück.
	 */
	public Feld normalBewegung(List<Feld> richtungsListe) {
		int zufallIndex = (int) (Math.random() * richtungsListe.size());
		Feld laufRichtung = richtungsListe.get(zufallIndex);
		return laufRichtung;
	}

	/**
	 * Hierbei wird aus einer Liste das Feld mit den wenigsten angrenzenden
	 * unbekannten Feldern ausgewählt und übergeben.
	 * 
	 * @param richtungsListe Erwartet eine Liste aller Felder, die als nächstes
	 *                       Zielfeld in Frage kommen.
	 * @return Gibt das ausgewählte Zielfeld zurück.
	 */
	public Feld minimalBewegung(List<Feld> richtungsListe) {
		Feld laufRichtung = null;
		int min = 4; // speichert den momentanen Minimalwert

		for (Feld feld : richtungsListe) {
			int zaehler = anzahlUnbekannteAngrenzendeFelderErmitteln(feld);
			if (zaehler <= min) {
				// Zaehler fuer die angrenzenden unbekannten Felder
				min = zaehler;
				/*
				 * Setzt die Laufrichtung auf das Feld, das bis jetzt (im Schleifendurchlauf)
				 * den kleinsten Wert an angrenzenden unbekannten Feldern hat. Der Einfachheit
				 * halber wird zum Schluss immer das Feld als zielfeld gewählt, welches als
				 * letztes in der Liste stand und den max-Wert besitzt.
				 */
				laufRichtung = feld;
			}
		}
		return laufRichtung;
	}

	/**
	 * Hierbei wird aus einer Liste das Feld mit den meisten angrenzenden
	 * unbekannten Feldern ausgewählt und übergeben.
	 * 
	 * @param richtungsListe Erwartet eine Liste aller Felder, die als nächstes
	 *                       Zielfeld in Frage kommen.
	 * @return Gibt das ausgewählte Zielfeld zurück.
	 */
	public Feld maximalBewegung(List<Feld> richtungsListe) {
		Feld laufRichtung = null;
		int max = 0; // speichert den momentanen Maximalwert

		for (Feld feld : richtungsListe) {
			// Zaehler fuer die angrenzenden unbekannten Felder
			int zaehler = anzahlUnbekannteAngrenzendeFelderErmitteln(feld);
			if (zaehler >= max) {
				max = zaehler;
				/*
				 * Setzt die Laufrichtung auf das Feld, das bis jetzt den größten Maximalwert
				 * hat. Der Einfachheit halber wird zum Schluss immer das Feld als zielfeld
				 * gewählt, welches als letztes in der Liste stand und den min-Wert besitzt.
				 */
				laufRichtung = feld;
			}
		}
		return laufRichtung;
	}

	/**
	 * Die Methode prüft, wie viele an ein Feld angrenzende Felder unbekannt sind.
	 * 
	 * @param feld Erwartet das Feld, dessen Nachbarfelder überprüft werden sollen.
	 * @return Gibt die Anzahl der angrenzenden unbekannten Felder zurueck.
	 */
	public int anzahlUnbekannteAngrenzendeFelderErmitteln(Feld feld) {
		int zaehler = 0; // ist der Zähler für die angrenzen unbekannten Felder
		if (!getNordFeld(feld).getBezeichnung().equals("unbekannt")) {
			zaehler++;
		}
		if (!getOstFeld(feld).getBezeichnung().equals("unbekannt")) {
			zaehler++;
		}
		if (!getSuedFeld(feld).getBezeichnung().equals("unbekannt")) {
			zaehler++;
		}
		if (!getWestFeld(feld).getBezeichnung().equals("unbekannt")) {
			zaehler++;
		}
		return zaehler;
	}

	/**
	 * Methode, um die komplette Karte im Standard Error ausgeben zu lassen. Dabei
	 * werden die Kuerzel der Felder als Mini-Map angezeigt.
	 */
	public void zeigeMiniMap() {
		String ausgabe = "";
		for (int koordinateY = 0; koordinateY < groesseY; koordinateY++) {
			ausgabe = "";
			for (int koordinateX = 0; koordinateX < groesseX; koordinateX++) {
				Feld feld = getFeld(koordinateX, koordinateY);
				System.err.print(feld.getKuerzel() + " ");
			}
			System.err.println(ausgabe);
		}
	}

	/**
	 * Methode, um die komplette Karte im Standard Error ausgeben zu lassen. Dabei
	 * werden nur die Pfadkosten der Felder als Mini-Map angezeigt. Ausgangsposition
	 * - sprich, das Feld mit den Pfadkosten=1, ist die aktuelle Bot-Position.
	 */
	public void zeigeMiniMapRundenkosten() {
		for (int koordinateY = 0; koordinateY < groesseY; koordinateY++) {
			for (int koordinateX = 0; koordinateX < groesseX; koordinateX++) {
				Feld feld = getFeld(koordinateX, koordinateY);
				System.err.print(feld.getPfadkosten() + "      ");
			}
			System.err.println();
		}
	}

	/**
	 * Methode, um die komplette Karte im Standard Error ausgeben zu lassen. Dabei
	 * wird nur die Bezeichnung der Felder als Mini-Map angezeigt.
	 */
	public void zeigeMiniMapBezeichnung() {
		for (int koordinateY = 0; koordinateY < groesseY; koordinateY++) {
			for (int koordinateX = 0; koordinateX < groesseX; koordinateX++) {
				Feld feld = getFeld(koordinateX, koordinateY);
				System.err.print(feld.getBezeichnung() + "   ");
			}
			System.err.println();
		}
	}

	/**
	 * Methode, um die komplette Karte im Standard Error ausgeben zu lassen. Dabei
	 * werden nur die Koordinaten der Felder als Mini-Map angezeigt.
	 */
	public void zeigeMiniMapKoordinaten() {
		for (int koordinateY = 0; koordinateY < groesseY; koordinateY++) {
			for (int koordinateX = 0; koordinateX < groesseX; koordinateX++) {
				Feld feld = getFeld(koordinateX, koordinateY);
				System.err.print(feld.getKoordinateX() + ":" + feld.getKoordinateY() + "   ");
			}
			System.err.println();
		}
	}

	/**
	 * Die Methode erhöht die Rundenanzahl um 1.
	 */
	public void erhoeheRundenzaehler() {
		this.rundenzaehler++;
	}

	// Getter & Setter Methoden
	public int getGroesseX() {
		return groesseX;
	}

	public int getGroesseY() {
		return groesseY;
	}

	public int getLevel() {
		return level;
	}

	public Feld[] getSpielfeld() {
		return spielfeld;
	}

	public void setSpielfeld(Feld[] spielfeld) {
		this.spielfeld = spielfeld;
	}

	public List<Formular> getFormularListe() {
		return formularListe;
	}

	public void setFormularListe(List<Formular> formularListe) {
		this.formularListe = formularListe;
	}

	public Bot getBot() {
		return bot;
	}

	public void setBot(Bot bot) {
		this.bot = bot;
	}

	public int getRundenzaehler() {
		return rundenzaehler;
	}

	public void setRundenzaehler(int rundenzaehler) {
		this.rundenzaehler = rundenzaehler;
	}

	public String getLetzteAktion() {
		return letzteAktion;
	}

	public void setLetzteAktion(String letzteAktion) {
		this.letzteAktion = letzteAktion;
	}

	// Sonstige Methoden
	/**
	 * Gibt die Referenz und alle Attribute aus
	 */
	public void printInfos() {
		System.err.println("Karte: " + this);
		System.err.println("Bot: " + this.bot);
		System.err.println("formularListe: " + this.formularListe);
		System.err.println("groesseX: " + this.groesseX);
		System.err.println("groesse>: " + this.groesseY);
		System.err.println("letzte Aktion: " + this.letzteAktion);
		System.err.println("Level: " + this.level);
		System.err.println("rundenzaehler: " + this.rundenzaehler);
		System.err.println("spielfeld: " + this.spielfeld);
	}

}
