
+ Code aus 2019

Innerhalb meines Studiums musste diese Teilleistung in Gruppen von 3-4 Personen innerhalb von ca. 3 Wochen in Java programmiert werden. Der Bot läuft durch ein Labyrinth, muss dabei fiktive Formulare einer Behörde in der korrekten Reihenfolge einsammeln und diese dann dem eigenen Sachbearbeiter abgeben. Die Karte ist dabei dem Bot unbekannt.

Der Bot kann jede Runde eine Aktion ausführen, beispielsweise einen Schritt in eine Richtung machen, Blatt/Formular aufheben, Blatt ablegen, Blatt/Formular zur Seite kicken, Formular aufheben, alle Formulare (den Antrag) beim Sachbearbeiter abgeben.

Dabei können die Bots gegen bis zu 3 andere Bots antreten.

Die KI bekommt beim Start folgende Informationen:
+ Breite des Labyrinth
+ Höhe des Labyrinth
+ Startposition auf der Karte (x/y)
+ seine eigene Nummer (1-4)
+ Anzahl der Blätter (können gelegt werden um andere Bots auszubremsen) 
+ Schwierigkeitsstufe (je Stufe kommen ausführbare Aktionen hinzu)

Bei jeder Runde bekommt der Bot
+ den Status des eigenen Feldes
+ den Status der direkt angrenzenden Feldes (Oben, Unten, Links, Rechts)
+ den Status der letzten ausgeführten Aktion



### Beispiel Video unter: [Youtube: Maze-running Bot](https://www.youtube.com/watch?v=cfkC3KOgis8 )