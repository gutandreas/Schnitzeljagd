# Schnitzeljagd

QR-Schnitzeljagd für den Informatikunterricht. Die Klasse meldet sich an, bekommt
je einen persönlichen Code und läuft die Posten in eigener Reihenfolge ab; an
jedem Posten hängt ein QR-Code, der zur Frage führt. Wer alle Posten gelöst hat,
schliesst ab — die Zeit landet in der Rangliste.

Spring Boot 3.4 · Java 21 · Thymeleaf · H2 (Datei)

## Lokal starten

Über IntelliJ (`SchnitzeljagdApplication`) oder:

```bash
mvn spring-boot:run
```

Dann `http://localhost:8080` — Adminbereich unter `/admin`, Benutzername `admin`,
Passwort `admin` (nur lokal, siehe unten).

Die Datenbank liegt als Datei unter `./data/schnitzeljagd.mv.db`.

## Einstellungen

Alles über Umgebungsvariablen, damit auf dem Server nichts im Quelltext steht:

| Variable | Bedeutung | Standard |
|---|---|---|
| `SCHNITZELJAGD_BASE_URL` | Adresse, die **in den QR-Codes** steht | `http://localhost:8080` |
| `SCHNITZELJAGD_ADMIN_PASSWORD` | Passwort für `/admin` | `admin` |
| `SCHNITZELJAGD_TITLE` | Titel auf der Startseite | `Schnitzeljagd` |
| `SCHNITZELJAGD_DB_PATH` | Pfad der H2-Datei | `./data/schnitzeljagd` |

`SCHNITZELJAGD_BASE_URL` ist die einzige Einstellung, die man nicht vergessen
darf: Sie wird in die gedruckten QR-Codes eingebrannt. Stimmt sie nicht, zeigen
alle Zettel ins Leere und müssen neu gedruckt werden.

## Ablauf für die Lehrperson

1. Im Adminbereich eine **Jagd** anlegen (`/admin`).
2. **Posten** hinzufügen: Titel, Ort, Frage, Tipp, akzeptierte Antworten
   (mehrere mit Semikolon; Gross-/Kleinschreibung egal).
3. Jagd **aktiv setzen** — es ist immer genau eine aktiv.
4. **QR-Codes drucken** (Knopf bei der Jagd). Die erste Seite ist der
   Anmelde-Code, danach folgt ein Posten pro Seite.
5. Nach dem Durchgang unter **Teilnehmende** aufräumen.

Posten 1 ist der Einstieg und bleibt bei allen zuerst; die übrigen werden pro
Person gemischt, damit sich nicht alle am selben Ort drängen.

## Für die Klasse

Anmelde-QR scannen → Name eintragen → Code erscheint → los. Der Code wird im
Browser gemerkt und muss normalerweise nie eingetippt werden. Nötig wird er nur,
wenn jemand das Gerät wechselt oder der QR-Scanner die Seite in einem anderen
Browser öffnet — deshalb steht er auf der Willkommensseite zum Notieren.

## Deployment

Push auf `master` baut das Image und legt es unter
`ghcr.io/gutandreas/schnitzeljagd:latest` ab (siehe `.github/workflows/build.yml`).
Auf dem Server:

```bash
cd /opt/schnitzeljagd && sudo docker compose pull && sudo docker compose up -d
```

Vorlagen dafür liegen in [`deploy/`](deploy): `docker-compose.yml` und der
Caddy-Block. Gebaut wird **nie auf dem Server** (zu wenig Arbeitsspeicher),
sondern immer in der GitHub-Action.

## Herkunft

Ursprünglich 2023 aus einem Thymeleaf-Beispielprojekt entstanden und mehrfach im
Unterricht eingesetzt. 2026 überarbeitet: Fragen aus dem Quelltext in die
Datenbank, Adminbereich mit echter Anmeldung, QR-Codes werden selbst erzeugt.
Die alten Fragensätze werden beim ersten Start automatisch importiert
(`LegacySeedData`) und können danach im Adminbereich bearbeitet oder gelöscht
werden.
