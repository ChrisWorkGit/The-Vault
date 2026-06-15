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
