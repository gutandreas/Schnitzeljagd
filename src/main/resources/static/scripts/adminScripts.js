async function deleteAll() {
    if (checkPassword()){
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
        console.log("Passwort nicht korrekt");
        document.getElementById("error").innerHTML="Passwort nicht korrekt!"
    }
}

async function changeModus(){
    if (checkPassword()){
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
        console.log("Passwort nicht korrekt");
        document.getElementById("error").innerHTML="Passwort nicht korrekt!"
    }

}



function checkPassword() {

    let correctHash = "OxJxEx?=?>"
    let input = document.getElementById("password").value;
    let hashedValue = '';

    for (let i = 0; i < input.length; i++) {
        const charCode = input.charCodeAt(i);
        const hashedCharCode = charCode ^ 13;

        hashedValue += String.fromCharCode(hashedCharCode);
    }

    return correctHash == hashedValue;
}

