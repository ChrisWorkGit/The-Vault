# 🎨 Design-Richtlinien: "The Vault" - Cyberpunk Style

Diese Richtlinien definieren den einheitlichen visuellen Stil für die App "The Vault". Jede neue Komponente oder jeder neue Screen MUSS sich an diese Vorgaben halten.

## 1. Farbpalette (Cyberpunk / Matrix Look)
| Element | Farbe | Hex-Code | Beschreibung |
| :--- | :--- | :--- | :--- |
| **Background** | Cyber-Black | `#040805` | Extrem dunkles Grün-Schwarz |
| **Primary / Neon** | Neon-Green | `#00FF66` | "Radioaktives" Leuchten für Titel, Icons & Aktivelemente |
| **Text** | Text-Green | `#00AA44` | Gut lesbares Grün für Fließtext |
| **Border / Grid** | Dark-Green | `#003311` | Dezentes Dunkelgrün für Rahmen, Gitternetz & Scanlines |

## 2. Typografie
*   **Überschriften:** `SansSerif`, Fett (`Bold`), All-Caps bevorzugt.
*   **System-Texte:** `FontFamily.Monospace`. Alles, was "Terminal-Output", IP-Adressen, Statusmeldungen oder Code darstellt.
*   **Fließtext:** `SansSerif`, Normal-Schnitt, Farbe `TextGreen`.

## 3. Formsprache & Shapes
*   **Buttons:** Standard-Buttons nutzen die `CyberpunkShape` (schräges Parallelogramm). Keine Standard-Abrundungen verwenden!
*   **Container:** Scharfkantige Boxen mit dünner `DarkGreen` Umrandung (1.dp).
*   **Scanner:** Viewfinder mit betonten Fadenkreuz-Ecken (`NeonGreen`).

## 4. Visuelle Effekte (Modifiers)
*   **CRT-Effekt:** Alle Screens nutzen das `.crtOverlay()` (Gitternetz + horizontale Scanlines).
*   **Neon-Glow:** Wichtige interaktive Elemente oder Überschriften nutzen `.neonGlow()`.
*   **Animationen:** Lineare, technoid wirkende Animationen (z. B. der Laser-Sweep im Scanner).

---

*Hinweis: Wenn ein Design-Element nicht in diesen Richtlinien abgedeckt ist oder Unklarheiten bestehen, muss der User explizit nach einer Spezifikation gefragt werden.*
