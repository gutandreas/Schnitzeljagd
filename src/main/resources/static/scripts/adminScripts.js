function deleteAll() {
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

function changeModus(){
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

