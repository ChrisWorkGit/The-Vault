# 🔒 The Vault – Synchronized Heist

## 📱 Projektübersicht & Idee
**The Vault** ist ein kooperatives Multiplayer-Escape-Game für Android (3–4 Spieler). Das Ziel des Teams ist es, gemeinschaftlich einen digitalen Tresor zu knacken, indem verschiedene Hardware-Sensoren der Smartphones genutzt und Aufgaben im Team gelöst werden müssen.

## ⚙️ Gameplay-Mechanik
* **Lokaler Multiplayer (Offline-P2P):** Das Spiel läuft komplett ohne Cloud-Datenbanken. Ein Smartphone im Raum fungiert als lokaler Server (Host), die anderen Spieler verbinden sich direkt als Clients damit.
* **Dynamische Runden (Random Chain):** Das Spiel ist modular aufgebaut. Jedes Teammitglied steuert 1–2 eigenständige, sensorbasierte Minispiele bei. Bei jedem Start würfelt der Server die Minispiele der anwesenden Spieler in eine völlig zufällige Reihenfolge. 
* **Gewinnbedingung:** Um den Heist erfolgreich abzuschließen und das Spiel zu gewinnen, muss die gesamte Kette an Minispielen fehlerfrei und nacheinander durchlaufen werden.

## 🛠 Erfüllte Uni-Kriterien
* **Multi-Device:** Lokale Echtzeit-Synchronisation zwischen Host und Clients über ein Peer-to-Peer-Netzwerk (Sockets).
* **Sensoren & Gesten:** Die Minispiele nutzen die Hardware-Sensoren der Geräte (z. B. Gyroskop, Lichtsensor, Mikrofon) sowie spezielle Touch-Gesten.
* **Data Centricity:** Lokale Speicherung von Statistiken, Highscores und Bestzeiten über eine persistente **Room-Datenbank**.

## 🚀 Tech Stack
* **Sprache:** Kotlin
* **UI-Framework:** Jetpack Compose (Material 3)
* **Architektur:** MVVM (Model-View-ViewModel) mit einer zentralen State-Machine-Steuerung
* **Datenbank:** Room DB
