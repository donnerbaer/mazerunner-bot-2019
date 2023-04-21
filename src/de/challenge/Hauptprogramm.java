package de.challenge;

import java.util.Scanner;
import de.challenge.model.Karte;

/**
 * Die Klasse Hauptprogramm enthaelt unsere main-Methode.
 * @author F
 * @author G
 * @author B
 */
public class Hauptprogramm {
	/**
	 * Hauptmethode (main-Methode) zum Ausführen des Bots.
	 * 
	 * @param args (Argumente)
	 */
	public static void main(String[] args) {
		/*
		 * Scanner zum Auslesen der Standardeingabe, welche Initialisierungs- und
		 * Rundendaten liefert.
		 */
		Scanner input = new Scanner(System.in);
		int sheetCount = 0; // anzahl der Blaetter des Bots
		String lastActionsResult;
		String currentCellStatus;
		String northCellStatus;
		String eastCellStatus;
		String southCellStatus;
		String westCellStatus;

		// INIT - Auslesen der Initialdaten
		// 1. Zeile: Maze Infos
		int sizeX = input.nextInt();
		int sizeY = input.nextInt();
		int level = input.nextInt();
		input.nextLine(); // Beenden der ersten Zeile

		// 2. Zeile: Player Infos
		int playerId = input.nextInt();
		int startX = input.nextInt();
		int startY = input.nextInt();
		if (level == 5) { // Nur in Level 5 wird die Eingabe des sheetCount erwartet
			sheetCount = input.nextInt();
		}
		input.nextLine(); // Beenden der zweiten Zeile

		// Karte Instanziieren
		Karte karte = new Karte(sizeX, sizeY, level);
		karte.generiereFelder();
		karte.erzeugeBot(playerId, startX, startY, sheetCount);

		System.err.println("Breite der Karte: " + sizeX);
		System.err.println("Hoehe der Karte: " + sizeY);
		System.err.println("Level:" + level);
		System.err.println("Bot-ID: " + playerId);
		System.err.println("Startposition: " + startX + "|" + startY);
		System.err.println("Anzahl der Blaetter: " + sheetCount);
		System.err.println("############################################");

		boolean finished = false;
		// TURN (Wiederholung je Runde notwendig)
		while (!finished) {

			// Zuerst wird der Rundenzähler erhöht
			karte.erhoeheRundenzaehler();

			long startZeit = System.currentTimeMillis();

			// Rundeninformationen auslesen
			lastActionsResult = input.nextLine();
			currentCellStatus = input.nextLine();
			northCellStatus = input.nextLine();
			eastCellStatus = input.nextLine();
			southCellStatus = input.nextLine();
			westCellStatus = input.nextLine();

			// PROGRAMM
			// Methodenaufruf, um die letzte Aktion zu verarbeiten
			karte.verarbeiteAktion(lastActionsResult);
			/*
			 * Methodenaufruf, um die Umgebung des eigenen Players auszulesen und in der
			 * Karte abzuspeichern
			 */
			karte.seheUmgebung(currentCellStatus, northCellStatus, eastCellStatus, southCellStatus, westCellStatus);

			karte.setzePfadkostenZurueck(); // Methodenaufruf, um die Pfadkosten zurückzusetzen
			karte.berechnePfadkosten(); // Methodenaufruf, um die Pfadkosten neu zu berechnen

			System.err.println("Ergebnis Vorrunde: " + lastActionsResult);
			System.err.println("Nord: " + northCellStatus);
			System.err.println("Osten: " + eastCellStatus);
			System.err.println("Sueden: " + southCellStatus);
			System.err.println("Westen: " + westCellStatus);
			System.err.println("aktuelles: " + currentCellStatus);
			System.err.println("Anzahl Blaetter" + karte.getBot().getAnzahlBlaetter());
			System.err.println("Runde: " + karte.getRundenzaehler());
			// Die Karte soll nur bei kleineren Karten als Error ausgegeben werden
			if (sizeX * sizeY <= 625) {
				karte.zeigeMiniMap();
			}

			// Rundenaktion ausgeben
			karte.zufallBewegungsAlgorithmus();

			// Ausgabe der Rechenzeit von den Runden
			System.err.println("Rundenzeit:" + (System.currentTimeMillis() - startZeit));
		}
		// Eingabe schliessen (letzte Aktion)
		input.close();
	}

}
