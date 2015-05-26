Referenzprojekt JavaEE-Programmierung

Bei dem Referenzprojekt handelt es sich um den funktionalen Prototyp einer datenbankgestützten verteilten JavaEE-Anwendung zur Verwaltung einer kleineren Bücherei, die einen begrenzten Umfang an Büchern verwaltet.

Ziel der Anwendung ist es, dass die Kunden der Bücherei über Webmasken nach Büchern suchen können, anhand von Verfügbarkeitsanfragen feststellen können, ob das gewünschte Buch in der Bücherei verfügbar ist und es dann direkt ausleihen.  Ausgeliehene Bücher können über die passenden Masken auch wieder zurückgegeben werden.

Mitarbeiter der Bücherei haben zusätzlich zu den oben genannten Masken auch Zugriff auf den „administrativen“ Bereich, in dem sie neben der Kundenverwaltung auch die Stammdaten für die verwalteten Bücher (Autor, Titel, Verlag, Exemplare) anlegen und verwalten, Abfragen über den Buchbestand generieren, sowie die Ausleihe und Rückgabe von Büchern verwalten können.

Auswahlmöglichkeiten für Kunden:

  -	Suche nach Autor, Buch oder Verlag
  -	Verfügbarkeit / Ausleihe von Exemplaren nach Autor, Buchtitel oder Verlag
  -	Erstellen eines Leihscheins für die ausgeliehenen Bücher
  -	Rückgabe von ausgeliehenen Büchern

Zusätzliche Auswahlmöglichkeiten für Mitarbeiter:
  -	Stammdatenverwaltung 
      *	Autoren (mit Genre)
      * Bücher
      * Verlage
      * Exemplare

  -	Anlegen / Verwalten von Kunden
  -	Bestandslisten für 
      * Gesamtbestand
      * verfügbaren Bestand
      * ausgeliehenen Bestand ( auch nach Kunde ) 

Aufgrund des knappen zeitlichen Rahmens sind folgende Funktionen zwar in der Planung des Projektes berücksichtigt, konnten jedoch nicht komplett implementiert werden ( für ein späteres Release vorgesehen ):
  -	Leihfristüberwachung
  -	Verlängern der Ausleihdauer
  -	Reservieren von nicht verfügbaren Exemplaren
  -	Statistische Auswertungen
