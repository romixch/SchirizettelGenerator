# Schirizettel Generator

### Screenshot
![Screenshot](Screenshot.png)

### Ausgabe

![Ausgabe](ZettelExample.png)

## Beschreibung

Diese Applikation hilft dir beim Erstellen von Korbball-Resultate-Zettel.
Als Voraussetzung muss Java ab der Version 8 installiert sein. Um die
Resultate-Zettel zu erstellen, lade das neuste Release hier herunter:
(https://github.com/romixch/SchirizettelGenerator/releases).
Entpacke die Zip-Datei und starte die Applikation im `bin`-Verzeichnis.

Ein Fenster öffnet sich. Darin kannst du folgende drei Dinge
auswählen:

- Vorlage:  Vorlage, wie die Zettel aussehen sollen. Ich habe bereits zwei Vorlagen erstellt. 
  Du kannst aber gerne deine eigene Vorlage erstellen. Beispiele findest du hier:
  [Vorlagen](https://github.com/romixch/SchirizettelGenerator/raw/master/src/main/resources/)
  Erstelle im Tool deiner Wahl einfach ein PDF-Formular, das dieser Generator dann ausfüllt. 
  Falls du hilfe dabei brauchst, meldest du dich bei der unten erwähnten Mail-Adresse.
- Daten:   Eine CSV-Datei, welche die zu druckenden Daten enthällt. Ein Beispiel dafür findest du auch hier:
  [BeispielDaten.csv](https://github.com/romixch/SchirizettelGenerator/raw/master/BeispielDaten.csv)
- Ausgabe: in diese PDF-Datei wird das Resultat geschrieben.

Eine detaillierte Beschreibung, wie du deine Schirizettel damit generieren kannst,
hat Stefan Etter auf Youtube gestellt: [Schiedsrichterzettel Generator](https://www.youtube.com/watch?v=OAVRBeTbJpw&feature=youtu.be)

## Download

Gehe dazu in den [Relese-Bereich](https://github.com/romixch/SchirizettelGenerator/releases) von GitHub.
Dort findest du eine Zip-Datei zum Download. Diese kannst du auf deiner Maschine entpacken und das 
darin enthaltene exe-File ausführen. Windows wird dich fragen, ob du der Datei wirklich vertrauen
möchtest. Klicke auf *Weitere Informationen* und *Trotzdem ausführen*:

![Unbekannte Applikation](windows_unknown_app.png)

Für mehr Infos kontaktiere roman.schaller@gmail.com

## Selber Kompilieren

Für geübte Java-Entwickler:

Starten:
```shell script
./gradlew run
```

Distribution erstellen:
```shell script
./gradlew packageWindows
```