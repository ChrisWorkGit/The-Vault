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

### 🔹 Referenz: [REF-ISSUE05-DECIBEL-BYPASS]
* **Datum:** 14.07.2026
* **Genutztes Tool:** Gemini (Android Studio AI Plugin)
* **Betroffene Dateien:** 
    * `app/src/main/AndroidManifest.xml`
    * `app/src/main/java/com/uniprojekt/thevault/ui/viewmodel/GameViewModel.kt`
    * `app/src/main/java/com/uniprojekt/thevault/ui/screens/MainApp.kt`
    * `app/src/main/java/com/uniprojekt/thevault/ui/screens/minigames/DecibelBypassScreen.kt`
* **Inhalt/Ziel:** Implementierung des kooperativen "Decibel Bypass"-Minispiels mit Mikrofon-Synchronisation, prozeduraler Wellengenerierung und intelligentem Game-Balancing.

#### Verwendeter Prompt (Phase 1: Core Implementation):
> Erstelle das kooperative Minispiel "Decibel Bypass" (Issue #5). Es nutzt das Mikrofon zur Synchronisation.
> 1. Vorbereitung: Prüfe STREAM_MUSIC Lautstärke. Zeige Warnung wenn nicht auf 100%. Button zum Maximieren via AudioManager.
> 2. Gameplay: Sinusförmige Audiokurve auf Canvas. Spieler muss entgegen der Wellenrichtung (Wischgeste) gegensteuern.
> 3. Koop-Mikrofon-Logik: Bei Fehlern wird ein Alarm-Sound abgespielt. Das Mikrofon des Partners muss diesen Ton erkennen (Amplituden-Peak > 25000) und ebenfalls einen Fehler werten.
> 4. Fehler-Limit: 3 Versuche ([X] [X] [X]).

#### Verwendeter Prompt (Phase 2: Visual Enhancements):
> Optimiere die grafische Darstellung im DecibelBypassScreen.kt:
> 1. Prozedurale Welle: Nutze Superposition aus zwei Sinus-Frequenzen für organisches Signalrauschen. Implementiere zufällige Amplituden-Spikes.
> 2. Antiphase-Visualisierung: Zeichne eine zweite, rote Welle (Feedback), deren Amplitude durch den Finger gesteuert wird.
> 3. Glow-Effekt: Füge der Spieler-Welle einen neon-roten Glow hinzu und zeichne eine gestrichelte Baseline zur Orientierung.

#### Verwendeter Prompt (Phase 3: Balancing & UX):
> Das Spiel ist zu schwer. Optimiere die Balance:
> 1. Toleranz-Fenster: Erlaube ca. 20% Abweichung auf der Y-Achse beim Neutralisieren der Welle.
> 2. Visuelles Feedback: Färbe die Spieler-Welle grün und lass sie pulsieren, wenn sie sich im Toleranzbereich ("Safe") befindet. Nur bei Fehlern wird sie rot.
> 3. Grace Period: Implementiere eine Gnadenfrist/Cooldown von 1,5s nach einem Fehler, um Kaskaden-Fehler zu vermeiden.
> 4. Smoothing: Nutze einen Low-Pass-Filter auf den Touch-Input, um Jitter zu vermeiden.

#### Erbrachte Eigenleistung des Teams nach Generierung:
Integration der `RECORD_AUDIO` Berechtigungen im Manifest. Das Team definierte den Amplituden-Schwellenwert (`25000`) basierend auf Hardware-Tests und implementierte eine 30-sekündige Überlebens-Siegbedingung für den kooperativen Loop. Zudem wurden Kompilierfehler in der `infiniteRepeatable`-Animation behoben und die mathematische Gewichtung des Low-Pass-Filters (`0.7/0.3`) für optimale Haptik feinjustiert. Ergänzend wurde das visuelle Feedback der Fehler-Matrix verfeinert (runder Glow statt vollflächigem Quadrat) und die globale "Shared Failure"-Logik über das Netzwerk implementiert, um sicherzustellen, dass ein Scheitern eines Spielers das Spiel für das gesamte Team beendet.

### 🔹 Referenz: [REF-ISSUE23-LOBBY-SYSTEM]
* **Datum:** 14.07.2026
* **Genutztes Tool:** Gemini (Android Studio AI Plugin)
* **Betroffene Dateien:** 
    * `app/src/main/java/com/uniprojekt/thevault/network/NetworkManager.kt`
    * `app/src/main/java/com/uniprojekt/thevault/ui/viewmodel/GameViewModel.kt`
    * `app/src/main/java/com/uniprojekt/thevault/ui/screens/MainApp.kt`
    * `app/src/main/java/com/uniprojekt/thevault/ui/screens/LobbyScreen.kt`
* **Inhalt/Ziel:** Implementierung eines dynamischen Multiplayer-Lobby-Systems für 2-4 Spieler mit Echtzeit-Namenssynchronisation und Host-Steuerung.

#### Verwendeter Prompt:
> Implementiere ein Multiplayer-Lobby-System für 2-4 Spieler.
> 1. NetworkManager: Erweitere auf Host-Multi-Client (bis zu 3 Clients). Implementiere Broadcast-Funktion für den Host.
> 2. GameViewModel: Neuer Zustand InLobby. Verwalte dynamische Spielerliste und synchronisiere diese bei jedem Beitritt oder Namensänderung.
> 3. LobbyScreen: Grid mit 4 Slots. Belegte Slots zeigen Namen und Status [CONNECTED]/[YOU]. Nur Host kann Spiel starten (INITIATE HEIST) wenn mind. 2 Spieler da sind. QR-Code in der Lobby vergrößern.
> 4. Flow: Lobby erst anzeigen wenn der erste Spieler verbunden ist (für Host).

#### Erbrachte Eigenleistung des Teams nach Generierung:
Vorgabe des synchronisierten P2P-Protokolls für die Spielerliste. Das Team passte den Workflow so an, dass der Host erst bei Verbindung des ersten Clients vom StartScreen in die Lobby wechselt, um eine bessere UX zu gewährleisten. Zudem wurde die Mindestanzahl von 2 Agenten für den Spielstart sowie die Skalierung des QR-Codes (size 300) und der Slots für optimale Lesbarkeit manuell optimiert.

### 🔹 Referenz: [REF-ISSUE03-ROOM-SETUP] & [REF-ISSUE28-HIGHSCORE-SCREEN]
* **Datum:** 15.07.2026
* **Genutztes Tool:** Gemini (Android Studio AI Plugin)
* **Betroffene Dateien:**
  * `app/src/main/java/com/uniprojekt/thevault/data/model/HeistStat.kt`
  * `app/src/main/java/com/uniprojekt/thevault/data/dao/HeistStatDao.kt`
  * `app/src/main/java/com/uniprojekt/thevault/data/VaultDatabase.kt`
  * `app/src/main/java/com/uniprojekt/thevault/data/VaultRepository.kt`
  * `app/src/main/java/com/uniprojekt/thevault/ui/viewmodel/GameViewModel.kt`
  * `app/src/main/java/com/uniprojekt/thevault/ui/screens/MainApp.kt`
  * `app/src/main/java/com/uniprojekt/thevault/ui/screens/StartScreen.kt`
  * `app/src/main/java/com/uniprojekt/thevault/ui/screens/HighscoreScreen.kt`
  * `app/src/main/AndroidManifest.xml`
  * `app/src/main/res/xml/file_paths.xml`
* **Inhalt/Ziel:** Implementierung der lokalen Room-Persistenz für Heist-Statistiken, globaler Spiel-Timer, P2P-Synchronisation von Spielergebnissen und der Highscore-Bildschirm inklusive Share-Funktion (Bitmap-Rendering).

#### Verwendeter Prompt:
> Setze die lokale Persistenz (Room), die globale Zeitmessung, das Daten-Sharing über das P2P-Netzwerk und den Highscore-Bildschirm inklusive Share-Funktion um.
> Anforderungen:
> 1. Datenbank-Setup: Entität HeistStat (Timestamp, Players, Duration, Sequence, Errors, isWin). DAO mit insert und ordered Queries.
> 2. Spiel-Timer: Ticking-Timer im GameViewModel, Anzeige in einer Neon-Leiste oben im GameScreen.
> 3. Netzwerk-Sync: Host generiert HeistStat-JSON und sendet HEIST_STAT_SUMMARY an alle Clients am Ende der Runde.
> 4. Highscore-Screen: Cyberpunk-Stil, Sektionen für alle Metriken, "Screenshot teilen"-Funktion via Intent.ACTION_SEND und FileProvider. "Breach Archive" Button im StartScreen.

#### Erbrachte Eigenleistung des Teams nach Generierung:
Definition des `HeistStat`-Schemas und Auswahl der relevanten Metriken zur Erfüllung des Uni-Kriteriums "Data Centricity". Das Team konfigurierte den `FileProvider` manuell und erstellte die `file_paths.xml`. Zudem wurde die manuelle JSON-Serialisierung (Regex-basiert) im ViewModel validiert, um den Overhead durch externe Bibliotheken gering zu halten. Die UI-Integration des Timers in die globale `MainApp`-Struktur wurde zur besseren UX feinjustiert.

### 🔹 Referenz: [REF-FIX-RANDOM-MINIGAME]
* **Datum:** 15.07.2026
* **Genutztes Tool:** Gemini (Android Studio AI Plugin)
* **Betroffene Dateien:**
  * `app/src/main/java/com/uniprojekt/thevault/ui/viewmodel/GameViewModel.kt`
* **Inhalt/Ziel:** Behebung der statischen Spielabfolge. Implementierung einer dynamischen Randomisierung der Minispiel-Sequenz bei jedem Heist-Start zur Erhöhung des Wiederspielwerts.

#### Verwendeter Prompt:
> Überprüfe kurz ohne was zu ändern ob die Reihenfolge an Minispielen wirklich random ist. Falls nicht, passe das kurz unter [REF-FIX-RANDOM-MINIGAME] an.

#### Erbrachte Eigenleistung des Teams nach Generierung:
Identifikation der linearen Index-Logik als Schwachstelle für das Gamedesign. Das Team entschied sich gegen eine rein zufällige Auswahl pro Level (um Dubletten zu vermeiden) und instruierte die KI, stattdessen die gesamte Liste (`defaultMinigames`) einmalig pro Spielstart zu mischen (`shuffled()`). Dies stellt sicher, dass jedes Minispiel genau einmal in variierender Reihenfolge vorkommt.

### 🔹 Referenz: [REF-FIX-RANDOM-MINIGAME]
* **Datum:** 15.07.2026
* **Genutztes Tool:** Gemini (Android Studio AI Plugin)
* **Betroffene Dateien:**
  * `app/src/main/java/com/uniprojekt/thevault/ui/viewmodel/GameViewModel.kt`
  * `app/src/main/java/com/uniprojekt/thevault/ui/screens/MainApp.kt`
* **Inhalt/Ziel:** Synchronisation der zufälligen Spielreihenfolge über das P2P-Netzwerk und Implementierung einer Barriere ("Waiting for Team"), damit alle Spieler gleichzeitig zum nächsten Minispiel voranschreiten.

#### Verwendeter Prompt:
> Passe das kurz unter [REF-FIX-RANDOM-MINIGAME] an: Die Reihenfolge muss bei jedem Spieler gleich sein. Wenn ein Spieler schneller fertig ist, muss er warten, bis alle fertig sind, bevor es zum nächsten Minispiel geht.

#### Erbrachte Eigenleistung des Teams nach Generierung:
Konzeption des Synchronisations-Protokolls: Der Host übernimmt die Rolle des "Masters" und verteilt die gewürfelte Sequenz. Das Team spezifizierte den "Waiting for Team"-Status als Teil der Game State Machine, um Race Conditions zu vermeiden, wenn ein Client schneller als der Host ist.

### 🔹 Referenz: [REF-ISSUE30-REAL-DEVICE-FIX]
Datum: 16.07.2026
Genutztes Tool: Gemini (Android Studio AI Plugin)
Betroffene Dateien:
app/src/main/java/com/uniprojekt/thevault/network/NetworkManager.kt
app/src/main/java/com/uniprojekt/thevault/ui/viewmodel/GameViewModel.kt

Inhalt/Ziel: Behebung von Deadlocks auf physischen Geräten. Umstellung auf IP-basiertes Player-Tracking und Implementierung eines Synchronisations-Cooldowns zur Vermeidung von Race-Conditions im P2P-Netzwerk.
Verwendeter Prompt:
Lies und beachte strikt unsere Projekt-Richtlinien aus der Datei 'AI_RULES.txt'.
Die neue Referenz-ID für diese Aufgabe lautet: [REF-ISSUE30-REAL-DEVICE-FIX]

KONTEXT:
Beim Testen auf physischen Geräten kommt es nach dem Abschluss eines Minigames zu einem Deadlock im "Waiting for Team"-Screen. Die Ursachen sind:
1. Eine ungenaue Zählung der fertigen Spieler im Host (doppelte Pakete oder String-Vergleiche ohne eindeutige ID-Zuordnung).
2. Ein Synchronisations-Loch: Clients erhalten das Weiterschalt-Signal (COMPLETE_MINIGAME_TRIGGER) vom Host zu schnell, während ihr lokaler UI-State noch im "Playing"- oder Übergangs-Zustand feststeckt, weshalb das Signal verworfen wird.

AUFGABE:
Repariere die Verbindungs- und Synchronisations-Logik im 'NetworkManager.kt' und 'GameViewModel.kt' für den stabilen Betrieb auf echten Geräten.

Bitte setze folgende technische Fixes um:

1. Eindeutige ID-Identifikation statt einfacher Strings:
  - Der Host darf fertige Spieler in `readyPlayers` nicht als einfache, anonyme "CLIENT_X" Strings speichern.
  - Verwende stattdessen eine `Set<String>` (anstelle einer Liste), um Duplikate durch eventuellen Paket-Jitter automatisch auszuschließen.
  - Identifiziere die Clients eindeutig über ihre IP-Adresse (z. B. `socket.inetAddress.hostAddress`) oder eine beim Handshake generierte, eindeutige `UUID`.

2. Beseitigung des Synchronisations-Lochs (State-Buffer & Queuing):
  - Wenn der Host ein `COMPLETE_MINIGAME_TRIGGER`-Signal sendet, dürfen die Clients dieses nicht verwerfen, nur weil ihre UI noch eine Millisekunde hinterherhinkt.
  - Implementiere im GameViewModel einen kurzen Empfangs-Puffer oder stelle sicher, dass Netzwerk-Signale für den Zustandswechsel asynchron verarbeitet werden (z. B. über eine Kotlin StateFlow-Kette, die den State erst aktualisiert, sobald das Gerät bereit ist).
  - Alternativ: Lass den Host eine kurze Verzögerung (z.B. 300-500ms Cooldown) einbauen, nachdem der letzte Spieler bereit war, bevor er den globalen Trigger an alle herausschickt. Dies gibt allen physischen Geräten genug Zeit, ihren lokalen UI-Wechsel in den "Waiting for Team"-Zustand abzuschließen.

3. Robustes Handling von Verbindungsabbrüchen:
  - Falls ein Socket auf einem echten Gerät kurzzeitig die Verbindung verliert, implementiere einen automatischen Bereinigungs-Mechanismus auf dem Host, der diesen Spieler aus der aktiven `readyPlayers`-Menge entfernt, damit die verbliebenen Spieler nicht im "Waiting"-Screen blockiert werden.

DENK AN DIE ENTWICKLUNGS-RICHTLINIEN AUS DER AI_RULES.txt:
- Kette die neue ID [REF-ISSUE30-REAL-DEVICE-FIX] im Header aller geänderten Dateien an.
- Nutze im neuen Code den Kommentar: // AI-Generated: Real Device Connection & Sync Patch
- Schreibe deutsche Kommentare, die das Set-basierte Tracking und die Paket-Verzögerung für den Präsentationstermin erklären.
Erbrachte Eigenleistung des Teams nach Generierung:
Analyse der Netzwerk-Logs auf physischen Testgeräten zur Identifikation des Synchronisations-Lochs (Signalverlust bei schnellen Zustandswechseln). Das Team entschied sich für einen deterministischen Ansatz: Die Umstellung von einer Liste auf ein Set im readyPlayers-Tracking stellt sicher, dass das System idempotent gegenüber Paket-Duplikaten ist. Zudem wurde die senderId-Logik in den NetworkManager integriert, um eine eindeutige Zuordnung der Readiness-Signale ohne zusätzlichen Protokoll-Overhead zu ermöglichen. Durch das manuelle Einfügen eines 400ms-Delays wurde die Hardware-Latenz physischer Displays erfolgreich ausgeglichen.

### 🔹 Referenz: [REF-ISSUE27-NOTIFICATION-OVERLOAD]
* **Datum:** 20.07.2026
* **Genutztes Tool:** Gemini (Android Studio AI Plugin)
* **Betroffene Dateien:**
    * `app/src/main/AndroidManifest.xml`
    * `app/src/main/java/com/uniprojekt/thevault/ui/viewmodel/GameViewModel.kt`
    * `app/src/main/java/com/uniprojekt/thevault/ui/screens/minigames/NotificationOverloadScreen.kt`
    * `app/src/main/java/com/uniprojekt/thevault/ui/screens/MainApp.kt`
    * `app/src/main/java/com/uniprojekt/thevault/ui/theme/CyberpunkUI.kt`
* **Inhalt/Ziel:** Implementierung des immersiven Minispiels "Notification Overload" unter Nutzung echter Android-Systembenachrichtigungen, inkl. dynamischer Rollenverteilung ("Neural Relay"), zirkulärer Informationsabhängigkeit und progressiver Schwierigkeitssteigerung.

#### Verwendeter Prompt:
> Lies und beachte strikt unsere Projekt-Richtlinien aus der Datei 'AI_RULES.txt'.
> Die neue Referenz-ID für diese Aufgabe lautet: [REF-ISSUE27-NOTIFICATION-OVERLOAD]
> 
> KONTEXT:
> Wir implementieren das Minispiel "Notification Overload" (Issue #27) für 2 bis 4 Spieler. Das Spiel nutzt echte Android-Systembenachrichtigungen (NotificationManager), um Reizüberflutung zu simulieren. Ein zufällig ausgewählter Spieler (Target Node) muss die Benachrichtigungen auf seinem Smartphone verwalten, während die verbleibenden Spieler (Analysten) ihm die richtigen Befehle und Keys zurufen müssen.
> 
> AUFGABE:
> Erstelle die Logik für das Minispiel `NotificationOverloadScreen` sowie die dazugehörigen Services im `GameViewModel` und dem Android-System.
> 
> Bitte implementiere folgende Kern-Komponenten für 2 bis 4 Spieler:
> 
> 1. Berechtigungen & Rollenverteilung (2-4 Spieler):
>    - Deklariere die Berechtigung `POST_NOTIFICATIONS` für Android 13+.
>    - Bestimme dynamisch beim Start des Minispiels per Zufall einen "Target Node" (Spieler X). Alle anderen aktiven Teilnehmer im Raum werden zu "Analysten".
>    - Nur auf dem Gerät des "Target Node" werden physische Android-System-Benachrichtigungen abgefeuert.
> 
> 2. Der Notification-Spam-Generator (Für den Target Node):
>    - Erstelle eine Schleife, die im Abstand von 1.5 bis 2.5 Sekunden echte System-Benachrichtigungen über den `NotificationManager` erzeugt.
>    - Jede Benachrichtigung erhält ein eindeutiges Tag/Label (z.B. "VAULT-SECURITY: Breach ID #1024", "CRITICAL_ERR: Sector #7742").
>    - Eine dieser Benachrichtigungen ist der "GOLDEN KEY". Wenn der Target-Spieler auf diese klickt, triggert der `PendingIntent` die Rückkehr zur App und meldet den Erfolg für das gesamte Team (`completeCurrentMinigame()`).
> 
> 3. Das verteilte Hacker-Terminal (Für die Analysten):
>    - Die Analysten sehen auf ihren Bildschirmen KEINE System-Benachrichtigungen, sondern neongrüne Cyberpunk-Konsolen.
>    - Teile die Information dynamisch auf die Analysten auf:
>      - Bei 2 Spielern: Analyst A sieht den vollständigen Ziel-Code (z.B. "TARGET: INTERCEPT BREACH ID #7742").
>      - Bei 3-4 Spielern: Teile den Code oder die Filter-Regeln auf (z.B. Analyst A sieht "TARGET SECTOR: #7000-#8000", Analyst B sieht "EXACT ID ENDS WITH: ...42"). Dies erzwingt aktive verbal Kommunikaton im gesamten Team!
> 
> 4. Fehler- und Abbruchbedingungen:
>    - Tracke die Anzahl der aktiven Benachrichtigungen. Wenn der Target Node zu langsam wischt und mehr als 10 Benachrichtigungen unserer App gleichzeitig in der Statusleiste aktiv sind, gilt das Spiel wegen "System-Überlastung" als fehlgeschlagen (`onFail()`).
>    - Wischt der Target Node die korrekte "Golden Key"-Benachrichtigung versehentlich weg (Delete-Intent via `getDeleteIntent`), wird sofort ein Alarm ausgelöst (`onMistake()`).
> 
> DENK AN DIE ENTWICKLUNGS-RICHTLINIEN AUS DER AI_RULES.txt:
> - Kette die neue ID [REF-ISSUE27-NOTIFICATION-OVERLOAD] im Datei-Header an.
> - Nutze im neuen Code den Kommentar: // AI-Generated: Immersive Android System Notification Overload Game
> - Schreibe verständliche deutsche Inline-Kommentare, insbesondere zur dynamischen Rollenverteilung für 2-4 Spieler, NotificationChannels und PendingIntents.

#### Erbrachte Eigenleistung des Teams nach Generierung:
Das Team konzipierte die Erweiterung zum "Neural Relay"-System, bei dem jeder Spieler gleichzeitig Informationen empfängt und für einen Partner sendet (zirkuläre Abhängigkeit). Dies steigerte die kooperative Tiefe im Vergleich zum ursprünglichen Target-Analyst-Modell erheblich. Zudem wurde die UI-Persistenz im Erfolgsfall optimiert: Spieler bleiben nach Abschluss ihrer Aufgabe als "Informations-Relais" aktiv, wobei die für Partner relevanten Daten weiterhin im Overlay angezeigt werden. Die progressive Schwierigkeitssteigerung (Speed Scaling) wurde durch manuelle Justierung des `speedFactor` (Reduktion um 3% pro Iteration) ausbalanciert.
