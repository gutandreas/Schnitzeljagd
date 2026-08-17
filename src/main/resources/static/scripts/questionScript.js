// Antworten und Tipps gehen als normales Formular (POST) an den Server — damit
// tragen sie den CSRF-Token automatisch mit, und der Tipptext kommt erst dann in
// die Seite, wenn er auch bezahlt ist.

// Zaehlt den Timer ab dem vom Server mitgelieferten Startwert hoch (Sekunden
// seit Anmeldung, inklusive bisheriger Tippzuschlaege). Laeuft rein im Client —
// kein Nachfragen beim Server noetig. Ein neuer, korrigierter Startwert kommt
// erst mit dem naechsten vollen Seitenaufbau (z.B. nach einem Tipp).
document.addEventListener("DOMContentLoaded", function () {
    var timer = document.getElementById("timer");
    if (!timer) {
        return;
    }

    var seconds = parseInt(timer.dataset.seconds, 10);
    var running = timer.dataset.running === "true";

    function render() {
        var h = Math.floor(seconds / 3600);
        var m = Math.floor((seconds % 3600) / 60);
        var s = seconds % 60;
        var pad = function (n) { return n < 10 ? "0" + n : "" + n; };
        timer.textContent = h > 0 ? (h + ":" + pad(m) + ":" + pad(s)) : (pad(m) + ":" + pad(s));
    }

    render();
    if (running) {
        setInterval(function () {
            seconds++;
            render();
        }, 1000);
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
