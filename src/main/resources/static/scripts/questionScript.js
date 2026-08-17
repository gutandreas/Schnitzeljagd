// Antworten und Tipps gehen als normales Formular (POST) an den Server — damit
// tragen sie den CSRF-Token automatisch mit, und der Tipptext kommt erst dann in
// die Seite, wenn er auch bezahlt ist.

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
