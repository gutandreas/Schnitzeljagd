async function deleteAll() {

    var password = prompt("Bitte geben Sie das Passwort ein:");

    if (checkPassword(password)){
        url = "/admin/delete"
        console.log(url)
        fetch(url, {method: "DELETE"})
            .then(response => {
                if (response.ok) {
                    console.log('Daten erfolgreich gelöscht.');
                    var url = "/admin/load";
                    window.location.href = url;
                } else {
                    console.error('Fehler beim Löschen der Daten.');
                }
            })
            .catch(error => {
                console.error('Fehler beim Senden des DELETE-Requests:', error);
            });
    }
    else {
        alert("Das Passwort ist nicht korrekt!")
    }
}

async function deleteByCode(button) {

    var password = prompt("Bitte geben Sie das Passwort ein:");

    if (checkPassword(password)){
        var cell = button.parentNode; // Zugriff auf das übergeordnete td-Element (Zelle)
        var row = cell.parentNode; // Zugriff auf die Zeile (tr-Element)
        var cells = row.getElementsByTagName("td");
        var code = cells[2].textContent;
        console.log(code)
        url = "/admin/deleteByCode?code=".concat(code)
        console.log(url)
        fetch(url, {method: "DELETE"})
            .then(response => {
                if (response.ok) {
                    console.log('Daten erfolgreich gelöscht.');
                    var url = "/admin/load";
                    window.location.href = url;
                } else {
                    console.error('Fehler beim Löschen der Daten.');
                }
            })
            .catch(error => {
                console.error('Fehler beim Senden des DELETE-Requests:', error);
            });
    }
    else {
        alert("Das Passwort ist nicht korrekt!")
    }
}


async function changeModus(){

    var password = prompt("Bitte geben Sie das Passwort ein:");
    if (checkPassword(password)){
        url = "/admin/changeModus?modus=".concat(document.getElementById("modusText").value)
        fetch(url)
            .then(response => {
                if (response.ok) {
                    console.log('Modus erfolgreich geändert und alle User gelöscht.');
                    var url = "/admin/load";
                    window.location.href = url;
                } else {
                    console.error('Fehler beim Ändern des Modus.');
                }
            })
            .catch(error => {
                console.log(error);
            });
    }
    else {
        alert("Das Passwort ist nicht korrekt!")
    }

}



function checkPassword(password) {

    let correctHash = "OxJxEx?=?>"
    let hashedValue = '';

    for (let i = 0; i < password.length; i++) {
        const charCode = password.charCodeAt(i);
        const hashedCharCode = charCode ^ 13;

        hashedValue += String.fromCharCode(hashedCharCode);
    }

    return correctHash == hashedValue;
}

