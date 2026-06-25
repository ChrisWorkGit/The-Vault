# 🤖 KI-Nutzung & Prompt-Dokumentation

In diesem Dokument wird die Nutzung von künstlicher Intelligenz (LLMs) im Entwicklungsprozess von *The Vault* lückenlos offengelegt und dokumentiert.

## 📝 Übersicht der Prompts & Code-Referenzen

### 🔹 Referenz: [REF-ISSUE09-CORE-ARCH]
* **Datum:** 15.6.2026
* **Genutztes Tool:** Gemini (Android Studio AI Plugin)
* **Betroffene Dateien:** 
    * `app/src/main/java/com/uniprojekt/thevault/ui/viewmodel/GameViewModel.kt`
    * `app/src/main/java/com/uniprojekt/thevault/MainActivity.kt`
    * `app/src/main/java/com/uniprojekt/thevault/ui/screens/MainApp.kt`
    * `app/src/main/java/com/uniprojekt/thevault/ui/screens/StartScreen.kt`
    * `app/src/main/java/com/uniprojekt/thevault/ui/screens/GameScreen.kt`
    * `app/src/main/java/com/uniprojekt/thevault/ui/screens/GameOverScreen.kt`
    * `app/src/main/java/com/uniprojekt/thevault/ui/theme/Color.kt`
    * `app/src/main/java/com/uniprojekt/thevault/ui/theme/Theme.kt`
    * `app/src/main/java/com/uniprojekt/thevault/ui/theme/Type.kt`
    * `app/build.gradle.kts`
    * `gradle/libs.versions.toml`
* **Inhalt/Ziel:** Setup der MVVM-Struktur, der Sealed-Class für den `GameState` und der zentralen State-Machine zur Steuerung der Minispiel-Kette sowie Refactoring des Package-Namens.

#### Verwendeter Prompt:
> Du bist ein erfahrener Android-Entwickler. Implementiere die Kern-Architektur und die Game State Machine für das Projekt "The Vault" in Kotlin und Jetpack Compose basierend auf Issue #9.
>
> PROJEKT-STRUKTUR & ANFORDERUNGEN:
> - Package-Name: com.uniprojekt.thevault
> - Architektur: MVVM (Model-View-ViewModel)
> - UI-Framework: Jetpack Compose (Material 3)
>
> AUFGABEN, DIE DU IMPLEMENTIEREN MUSST:
> 1. GameState: Eine Sealed Class/Interface im GameViewModel mit den Zuständen: Lobby, Playing (mit Parameter für den Spielexindex und Namen), FinalSwipe, GameOver (mit Parameter isWin).
> 2. GameViewModel: Beinhaltet die State Machine. Funktionen: startGame(), completeCurrentMinigame() (schaltet durch eine Liste von Dummy-Minispielen wie "Gyro-Lock", "Laser Barrier"), triggerGameOver(isWin: Boolean), resetToLobby().
> 3. MainApp (Navigation): Liest den State im Composable aus und schaltet via When-Statement die Screens um.
> 4. Dummy-Screens: Erstelle einfache Platzhalter-Composables für StartScreen (Lobby), GameScreen (der das aktuelle Minispiel als Text anzeigt), FinalSwipeScreen und GameOverScreen, jeweils mit temporären Buttons, um die State-Wechsel manuell zu triggern (für Testzwecke).
>
> STRIKTE ENTWICKLUNGS-RICHTLINIEN (FÜR UNI-DOKUMENTATION):
> 1. REFERENZ-KENNZEICHNUNG: Füge ganz oben in JEDER Datei einen Header-Kommentar ein, der exakt wie folgt aussieht, damit ich diesen Code meiner Prompt-Dokumentation zuordnen kann:
>    // PROMPT-REFERENZ: [REF-ISSUE09-CORE-ARCH]
>
> 2. KI-DISCLOSURE: Kennzeichne den gesamten generierten Logik-Block mit einem standardisierten Kommentar:
>    // AI-Generated: Core Architecture & State Machine Strategy
>
> 3. DOKUMENTATION: Verwende KDoc/JavaDoc für alle Klassen und öffentlichen Funktionen, um deren Zweck im Spiel-Loop zu erklären. Kommentiere komplexe Zeilen verständlich auf Deutsch.
>
> Bitte gib mir den fertigen, sauberen Code für das GameViewModel, die MainActivity (oder MainApp) und die Platzhalter-Screens aus.

#### Erbrachte Eigenleistung des Teams nach Generierung:
Das Team definierte die präzisen Anforderungen an die State Machine und die gewünschte Architektur. Nach der Generierung durch die KI wurden die Build-Konfigurationen (Gradle) manuell überprüft und korrigiert, um die Kompatibilität der Bibliotheken (insbesondere ViewModel-Compose) sicherzustellen. Zudem wurde die Integration in die `MainActivity` sowie die Bereinigung alter Package-Strukturen vorgenommen.

### 🔹 Referenz: [REF-ISSUE02-NET-BASE]
* **Datum:** 15.6.2026
* **Genutztes Tool:** Gemini (Android Studio AI Plugin)
* **Betroffene Dateien:** 
    * `app/src/main/java/com/uniprojekt/thevault/network/NetworkManager.kt`
    * `app/src/main/java/com/uniprojekt/thevault/ui/viewmodel/GameViewModel.kt`
    * `app/src/main/java/com/uniprojekt/thevault/ui/screens/StartScreen.kt`
    * `app/src/main/java/com/uniprojekt/thevault/ui/screens/MainApp.kt`
* **Inhalt/Ziel:** Implementierung der P2P-Socket-Basis für den lokalen Multiplayer, inkl. Host/Client-Logik und Handshake.

#### Verwendeter Prompt:
> Setze bitte folgende Aufgaben um:
> 1. StartScreen Erweiterung: Füge der bestehenden UI (Lobby/StartScreen) zwei klare Optionen hinzu: "Als Host starten" und "Als Client beitreten".
> 2. Netzwerk-Handler (Hintergrund): 
>    - Erstelle eine Klasse/Objekt (z.B. `NetworkManager`), die Coroutines (`Dispatchers.IO`) nutzt, um Netzwerkoperationen blockierungsfrei im Hintergrund auszuführen.
>    - Wenn "Host": Starte einen `ServerSocket` auf einem festen Port (z.B. 8888) und warte auf eingehende Verbindungen.
>    - Wenn "Client": Öffne einen `Socket` zu einer (vorerst hardcodierten oder per Textfeld eingegebenen) Host-IP-Adresse auf Port 8888.
> 3. Einfacher Text-Handshake:
>    - Sobald die Verbindung steht, sendet der Client sofort den String "Hello Vault" an den Server.
>    - Der Server liest diesen String, loggt ihn und sendet als Bestätigung "Access Granted" zurück.
> 4. GameViewModel-Anbindung:
>    - Der Verbindungsstatus (z. B. "Suche...", "Verbunden", "Handshake erfolgreich") muss als State im GameViewModel (oder einer separaten NetworkState-Klasse) gehalten und an den MainApp-Screen-Wechsler gemeldet werden.

#### Erbrachte Eigenleistung des Teams nach Generierung:
Vorgabe des Handshake-Protokolls ("Hello Vault" / "Access Granted") und Definition des Port 8888. Das Team hat die UI-Anforderungen für den StartScreen spezifiziert (IP-Eingabefeld).

### 🔹 Referenz: [REF-ISSUE13-LOCKPICK-DIETRICH-SHAPE]
* **Datum:** 23.6.2026
* **Genutztes Tool:** Claude (Claude.ai, Sonnet 4.6)
* **Betroffene Dateien:**
  * `app/src/main/java/com/uniprojekt/thevault/ui/screens/minigames/LockpickScreen.kt`
* **Inhalt/Ziel:** Anpassung der Zeichnung des Dietrich-Hakens im Lockpicking-Minispiel: statt einer geraden/gebogenen Linie mit Pfeilspitze wird nun die charakteristische L-Form eines echten Dietrichs (langer Griff + kurze abgewinkelte Spitze) nachgebildet.

#### Verwendeter Prompt:
> Ändere den gezeichneten Dietrich von dem geraden Strich in die Form des bereitgestellten Bildes.
>
> *(Anhang: Referenzfoto eines echten Dietrich-/Spannhakens, `LockPick.png`)*
> 
> *(Anhang: Dokumentations Richtlinien für KI Nutzung, `AI_RULES.txt`)*

#### Erbrachte Eigenleistung des Teams nach Generierung:
Bereitstellung des Referenzfotos zur Formvorgabe. Visuelle Prüfung des Ergebnisses auf einem Testgerät und Feinjustierung der Konstanten `bendX`, `bendY` und `handleX` (Winkel/Länge des Knicks) zur besseren Annäherung an die Vorlage.

### 🔹 Referenz: [REF-ISSUE02-NET-BASE]
* **Datum:** 15.6.2026
* **Genutztes Tool:** Gemini (Android Studio AI Plugin)
* **Betroffene Dateien:** 
    * `app/src/main/java/com/uniprojekt/thevault/network/NetworkManager.kt`
    * `app/src/main/java/com/uniprojekt/thevault/ui/viewmodel/GameViewModel.kt`
    * `app/src/main/java/com/uniprojekt/thevault/ui/screens/StartScreen.kt`
    * `app/src/main/java/com/uniprojekt/thevault/ui/screens/MainApp.kt`
* **Inhalt/Ziel:** Implementierung der P2P-Socket-Basis für den lokalen Multiplayer, inkl. Host/Client-Logik und Handshake.

### 🔹 Referenz: [REF-ISSUE17-QR-CONNECT]
* **Datum:** 25.6.2026
* **Genutztes Tool:** Gemini (Android Studio AI Plugin)
* **Betroffene Dateien:** 
    * `app/build.gradle.kts`
    * `gradle/libs.versions.toml`
    * `app/src/main/AndroidManifest.xml`
    * `app/src/main/java/com/uniprojekt/thevault/network/NetworkUtils.kt`
    * `app/src/main/java/com/uniprojekt/thevault/network/NetworkManager.kt`
    * `app/src/main/java/com/uniprojekt/thevault/ui/viewmodel/GameViewModel.kt`
    * `app/src/main/java/com/uniprojekt/thevault/ui/screens/StartScreen.kt`
    * `app/src/main/java/com/uniprojekt/thevault/ui/screens/ScannerScreen.kt`
* **Inhalt/Ziel:** QR-Code Onboarding für P2P-Verbindungen (Generierung & Scanning) mit manuellem IP-Fallback.

#### Verwendeter Prompt:
> Erweitere das bestehende Netzwerk-Fundament um ein komfortables QR-Code-Onboarding für Host und Client inklusive eines manuellen IP-Fallbacks.
>
> 1. Gradle-Abhängigkeiten: 'com.google.zxing:core', CameraX & ML Kit Barcode Scanning.
> 2. Host-Erweiterung: IPv4-Ermittlung, QR-Code Generierung & Clipboard-Funktion.
> 3. Client-Erweiterung: Kamera-Scanner-Screen (CameraX & ML Kit) mit manuellem Fallback-Umschalter.
> 4. Berechtigungen: CAMERA-Permission hinzufügen.

#### Erbrachte Eigenleistung des Teams nach Generierung:
Integration der Kamera-Vorschau in die Compose-UI via `AndroidView`. Definition des Fallback-Workflows zwischen automatischem Scan und manueller Eingabe für Emulator-Kompatibilität.
