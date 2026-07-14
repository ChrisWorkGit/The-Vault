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

### 🔹 Referenz: [REF-ISSUE03-ROOM-SETUP]
* **Datum:** 26.6.2026
* **Genutztes Tool:** Gemini (Android Studio AI Plugin)
* **Betroffene Dateien:** 
    * `app/build.gradle.kts`
    * `gradle/libs.versions.toml`
    * `app/src/main/java/com/uniprojekt/thevault/data/model/GameSession.kt`
    * `app/src/main/java/com/uniprojekt/thevault/data/model/MinigameResult.kt`
    * `app/src/main/java/com/uniprojekt/thevault/data/model/GameSessionWithResults.kt`
    * `app/src/main/java/com/uniprojekt/thevault/data/dao/VaultDao.kt`
    * `app/src/main/java/com/uniprojekt/thevault/data/VaultDatabase.kt`
    * `app/src/main/java/com/uniprojekt/thevault/data/VaultRepository.kt`
    * `app/src/main/java/com/uniprojekt/thevault/ui/viewmodel/GameViewModel.kt`
* **Inhalt/Ziel:** Implementierung der Room-Datenbankstruktur zur lokalen Speicherung von Spiel-Statistiken mit einem hochflexiblen JSON-Metrikfeld für kooperative Minispiele.

#### Verwendeter Prompt:
> Lies und beachte strikt unsere Projekt-Richtlinien aus der Datei 'AI_RULES.txt'.
> Die neue Referenz-ID für diese Aufgabe lautet: [REF-ISSUE03-ROOM-SETUP]
> 
> PROJEKT-KONTEXT & SPEZIFIKATION:
> Wir haben die P2P-Netzwerkbasis und das QR-Code-Onboarding (Issue #2 & #17) erfolgreich implementiert. Nun setzen wir die lokale Persistenzschicht (Room-Datenbank) um. 
> 
> "The Vault" ist ein rein kooperatives, asynchrones Multiplayer-Spiel (Prinzip: 'Spaceteam' / 'Keep Talking and Nobody Explodes'). Das bedeutet:
> 1. Minispiele sind NIEMALS isolierte Singleplayer-Aufgaben. Sie erfordern koordinierte, gleichzeitige Echtzeit-Aktionen des gesamten Teams (z.B. Spieler 1 sendet kontinuierlich Gyro-Werte, wodurch beim Partner ein akustisches Schloss-Klicken triggert).
> 2. Jedes Minispiel produziert völlig andere Datenstrukturen (Winkel, Dezibel-Peaks, Synchronisations-Deltas in ms, Fehlversuche).
> 3. Der Host fungiert als Authoritative Server im RAM: Er gleicht die Live-Events ab. Erst nach Rundenende (Sieg/Niederlage) broadcastet er ein zusammenfassendes Statistik-Paket an alle Clients. Jedes Gerät speichert diese Runden-Zusammenfassung dann dezentral in seiner eigenen Room-Datenbank für die lokale Historie ab.
> 
> AUFGABE:
> Implementiere die Room-Datenbankstruktur. Da die kooperativen Minispiele hochgradig unterschiedliche Metriken aufweisen, nutzen wir ein generisches Ansatzmodell über ein JSON/Text-Metrikfeld, um zukünftige Schema-Migrationen zu verhindern.
> 
> Bitte erstelle folgende Komponenten:
> 
> 1. Gradle-Konfiguration:
>    - Zeige mir kurz die benötigten Room-Abhängigkeiten (Runtime, KSP und Room-Ktx für Coroutines) für die build.gradle.kts.
> 
> 2. Die Entitäten (Entities):
>    - GameSession.kt: Speichert die globalen Heist-Metadaten (sessionId [PK, Auto-Increment], timestamp [Long], isWin [Boolean], totalDurationSeconds [Int]).
>    - MinigameResult.kt: Speichert die aggregierten Team-Ergebnisse pro gespieltem Minigame (resultId [PK, Auto-Increment], sessionId [FK zu GameSession mit CASCADE Delete], minigameTag [String, z.B. "COOP_LOCKPICK"], isSuccess [Boolean], timeSpentSeconds [Int]).
>    - additionalMetrics (String) in MinigameResult: Ein essenzielles TEXT/JSON-Feld. Hier legt jedes Koop-Minispiel am Ende seine maßgeschneiderten Team-Performance-Werte ab (z.B. {"desync_time_ms": 3400, "audio_clues_heard": 8} oder {"peak_noise_db": 65}).
> 
> 3. Data Access Object (VaultDao.kt) & Repository:
>    - Richte die @Insert-Methoden für eine GameSession sowie eine Liste von MinigameResult ein.
>    - Erstelle eine Query mit @Transaction, die eine vollständige Spielrunde inklusive aller dazugehörigen Minigame-Ergebnisse über eine relationale Hilfsklasse GameSessionWithResults abfragt.
>    - Kapsle die DAO-Aufrufe sauber in einem VaultRepository.kt.
> 
> 4. ViewModel-Integration (Vorbereitung für den Netzwerk-Loop):
>    - Implementiere im GameViewModel die Funktion saveFinalSession(session: GameSession, results: List<MinigameResult>) über das Repository.
>    - Füge ausführliche deutsche KDocs/Kommentare ein, die als logische Vorlage dienen: Erläutere im Code, wie der Host die transienten Echtzeit-Sensorwerte der Clients im RAM verarbeitet und nach dem Match zu einem kompakten String für das Feld additionalMetrics zusammenfasst, bevor die Speicherung getriggert wird.

#### Erbrachte Eigenleistung des Teams nach Generierung:
Vorgabe des Datenmodells (Session vs. Result) und des dezentralen Speicheransatzes. Festlegung des `additionalMetrics`-Feldes als JSON-String zur Vermeidung von Schema-Migrationen bei neuen kooperativen Minispielen. Zudem wurde eine Fehleranalyse bei der KSP-Kompilierung ("unexpected jvm signature V") durchgeführt und durch ein Upgrade auf Room 2.8.4 sowie die Anpassung der KSP-Version auf 2.2.10-2.0.2 erfolgreich behoben. Ergänzend wurden Manifest-Einträge für Hardware-Features hinzugefügt, um Lint-Fehler beim Kamera-Onboarding zu beseitigen.

### 🔹 Referenz: [REF-ISSUE20-CYBERPUNK-THEME]
* **Datum:** 26.6.2026
* **Genutztes Tool:** Gemini (Android Studio AI Plugin)
* **Betroffene Dateien:** 
    * `app/src/main/java/com/uniprojekt/thevault/ui/theme/Color.kt`
    * `app/src/main/java/com/uniprojekt/thevault/ui/theme/Type.kt`
    * `app/src/main/java/com/uniprojekt/thevault/ui/theme/Theme.kt`
    * `app/src/main/java/com/uniprojekt/thevault/ui/theme/CyberpunkUI.kt`
    * `app/src/main/java/com/uniprojekt/thevault/ui/screens/ScannerScreen.kt`
    * `app/src/main/java/com/uniprojekt/thevault/ui/screens/StartScreen.kt`
* **Inhalt/Ziel:** Umsetzung des globalen UI-Designsystems und einheitlichen Visual-Looks (Matrix- / Cyberpunk-Hacker-Look) für das gesamte Projekt, inklusive Anpassung des Start- und Scanner-Screens basierend auf Figma-Mockups.

#### Verwendeter Prompt:
> Lies und beachte strikt unsere Projekt-Richtlinien aus der Datei 'AI_RULES.txt'.
> Die neue Referenz-ID für diese Aufgabe lautet: [REF-ISSUE20-CYBERPUNK-THEME]
> 
> Setze nun das globale UI-Designsystem und den einheitlichen Visual-Look für unser Android-Projekt "The Vault" (com.uniprojekt.thevault) in Jetpack Compose um. Dieses Update beinhaltet die Definition des Themes sowie die exakte optische Anpassung des Start- und Scanner-Screens basierend auf unseren Figma-Mockups (Referenzdatei: image_3f2bea.jpg).
> 
> ANFORDERUNGEN & VISUELLER STIL (MATRIX- / CYBERPUNK-HACKER-LOOK):
> 1. Globale Farbpalette & Typografie (Color.kt, Theme.kt, Type.kt)
> 2. Cyberpunk-Komponenten (Shapes & Custom Modifiers)
> 3. Exakte Umsetzung des Scanners (ScannerScreen.kt / "INFILTRATE") basierend auf image_3f2bea.jpg
> 4. Anpassung des Startbildschirms (StartScreen.kt)

#### Erbrachte Eigenleistung des Teams nach Generierung:
Bereitstellung der Design-Referenz (Figma-Mockup `image_3f2bea.jpg`) und Definition des genauen Farbschemas. Das Team erstellte die `DESIGN_GUIDELINES.md` als verbindliche Vorgabe für die KI. Die KI wurde instruiert, diese Richtlinien strikt einzuhalten und bei gestalterischen Unklarheiten, die nicht in der Dokumentation abgedeckt sind, explizit Rücksprache mit dem Team zu halten. Zudem validierte das Team die Performance der Animationen und die Scannbarkeit des neongrünen QR-Codes auf schwarzem Hintergrund. Es wurden Kompilierfehler durch das Hinzufügen fehlender Material-Icons-Abhängigkeiten sowie die Aktualisierung veralteter Icon-Referenzen behoben.

### 🔹 Referenz: [REF-ISSUE23-INGAME-MENU]
* **Datum:** 14.7.2026
* **Genutztes Tool:** Gemini (Android Studio AI Plugin)
* **Betroffene Dateien:** 
    * `app/build.gradle.kts`
    * `app/src/main/java/com/uniprojekt/thevault/network/NetworkManager.kt`
    * `app/src/main/java/com/uniprojekt/thevault/ui/viewmodel/GameViewModel.kt`
    * `app/src/main/java/com/uniprojekt/thevault/ui/screens/MainApp.kt`
    * `app/src/main/java/com/uniprojekt/thevault/ui/screens/GameScreen.kt`
    * `app/src/main/java/com/uniprojekt/thevault/ui/screens/GameOverScreen.kt`
    * `app/src/main/java/com/uniprojekt/thevault/ui/screens/InGameMenu.kt`
* **Inhalt/Ziel:** Implementierung des In-Game-Menüs mit Cyberpunk-Styling und eines versteckten Debug-Overlays für Entwickler-Builds, inklusive P2P-Abbruch-Signalisierung.

#### Verwendeter Prompt:
> Erstelle ein ansprechendes, im Cyberpunk-Stil gehaltenes In-Game-Menü (z. B. über ein modales BottomSheet oder ein Overlay-Dialog) während der aktiven Spielphase (Playing).
> 
> 1. Reguläres In-Game-Menü (Für alle Builds verfügbar):
>    - Erreichbar über ein dezentes Zahnrad-Icon oben rechts.
>    - Button "SPIEL ABBRECHEN": Sendet via NetworkManager "GAME_OVER:DISCONNECTED_BY_USER" an den Partner.
>    - Implementiere die Empfänger-Logik: Partner wechselt in den GameOver-State mit Hinweis "VERBINDUNG VOM PARTNER ABGEBROCHEN".
> 
> 2. Debug-Optionen (Nur in DEBUG-Builds sichtbar!):
>    - Nutze BuildConfig.DEBUG zur Kapselung.
>    - Sektion "DEBUG INTERVENTIONS" mit rot-oranger Warn-Border.
>    - Buttons "MINISPIEL ERFOLGREICH BEENDEN" und "MINISPIEL FEHLGESCHLAGEN".
> 
> 3. UI-Styling (Cyberpunk-Konform):
>    - Nutzung der CyberpunkShape, Monospace-Optik und Farbcodes (#00FF66, #003311).

#### Erbrachte Eigenleistung des Teams nach Generierung:
Vorgabe der spezifischen Abbruch-Nachricht für das P2P-Protokoll und Definition der Debug-Interventionen zur Test-Beschleunigung. Das Team konfigurierte die Gradle-BuildFeatures manuell für den Zugriff auf `BuildConfig` und integrierte das Menü global in die `MainApp`-Struktur, um alle Minispiele abzudecken. Fehlerhafte Icon-Importe und veraltete Material3-Divider-Referenzen wurden manuell korrigiert.

