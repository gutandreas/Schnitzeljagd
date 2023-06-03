async function deleteAll() {
    if (await checkPassword()){
        url = "/admin/delete"
        console.log(url)
        fetch(url, {method: "DELETE"})
            .then(response => {
                if (response.ok) {
                    console.log('Daten erfolgreich gelöscht.');
                    var url = "/admin/loadPage";
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
    if (await checkPassword()){
        url = "/admin/changeModus?modus=".concat(document.getElementById("modusText").value)
        fetch(url)
            .then(response => {
                if (response.ok) {
                    console.log('Modus erfolgreich geändert und alle User gelöscht.');
                    var url = "/admin/loadPage";
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

async function checkPassword() {

    const hashedPassword = "6308D330240351DF1A05E686D05C7F5207F6293B73460D1B5065841A003047F6"
    const encoder = new TextEncoder();
    const data = encoder.encode(document.getElementById("password").value);
    const hashBuffer = await crypto.subtle.digest('SHA-256', data);
    const hashArray = Array.from(new Uint8Array(hashBuffer));
    const hashHex = hashArray.map(byte => byte.toString(16).padStart(2, '0'))
    const hashString = hashHex.reduce((result, current) => result + current);
    const hashUpperCase = hashString.toUpperCase();
    console.log(hashUpperCase)


    if(hashedPassword == hashUpperCase){
        return true;
    }
    else {
        return false
    }
}

