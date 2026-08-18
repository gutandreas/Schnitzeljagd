// Antworten und Tipps gehen als normales Formular (POST) an den Server — damit
// tragen sie den CSRF-Token automatisch mit, und der Tipptext kommt erst dann in
// die Seite, wenn er auch bezahlt ist.

// Zeigt den Timer ab dem vom Server mitgelieferten Startwert (Sekunden seit
// Anmeldung, inklusive bisheriger Tippzuschlaege). Laeuft rein im Client —
// kein Nachfragen beim Server noetig. Ein neuer, korrigierter Startwert kommt
// erst mit dem naechsten vollen Seitenaufbau (z.B. nach einem Tipp).
//
// Bewusst NICHT hochgezaehlt (seconds++ pro Tick): der Bestaetigungsdialog vor
// einem Tipp (confirm()) blockiert JavaScript komplett, waehrenddessen faellt
// mindestens ein Tick aus. Bricht man den Dialog ab, gibt es keinen Seiten-
// neuaufbau, der das je korrigieren wuerde — verlorene Ticks blieben fuer immer
// verloren. Stattdessen wird bei jedem Tick aus der echten Systemzeit neu
// berechnet, wie viele Sekunden seit dem Laden vergangen sind; das holt einen
// uebersprungenen Tick beim naechsten von selbst wieder auf.
document.addEventListener("DOMContentLoaded", function () {
    var timer = document.getElementById("timer");
    if (!timer) {
        return;
    }

    var startSeconds = parseInt(timer.dataset.seconds, 10);
    var running = timer.dataset.running === "true";
    var loadedAt = Date.now();

    function render() {
        var seconds = running
            ? startSeconds + Math.floor((Date.now() - loadedAt) / 1000)
            : startSeconds;
        var h = Math.floor(seconds / 3600);
        var m = Math.floor((seconds % 3600) / 60);
        var s = seconds % 60;
        var pad = function (n) { return n < 10 ? "0" + n : "" + n; };
        timer.textContent = h > 0 ? (h + ":" + pad(m) + ":" + pad(s)) : (pad(m) + ":" + pad(s));
    }

    render();
    if (running) {
        setInterval(render, 1000);
    }
});

// Blendet das Codefeld ein, wenn jemand nicht die im Browser gemerkte Person ist.
function showCodeField() {
    var row = document.getElementById("code_row");
    if (row) {
        row.style.setProperty("display", "block");
        var code = document.getElementById("code");
        if (code) {
            code.value = "";
            code.required = true;
            code.focus();
        }
    }
}
