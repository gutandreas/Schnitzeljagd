var ip = "http://localhost:8080"

function getHint(){
    url = ip.concat("/hint?number=1")
    fetch(url)
        .then(response => response.text())
        .then(data => {
            console.log(data);
            document.getElementById("hint_label").innerHTML = data; // Verwenden Sie innerHTML statt value
        })
        .catch(error => {
            console.log(error);
        });
}



function solve(){
    url = ip.concat("/hint?number=1")
    console.log(url)
    const xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
        console.log(xhttp.responseText)
        console.log(this.status)
        if (this.readyState == 4 && this.status == 200) {
            document.getElementById("hint_label").innerHTML = xhttp.responseText;
        }
    };
    xhttp.open("GET", url , true);
    xhttp.send();
}