var ip = "http://localhost:8080"

document.addEventListener("DOMContentLoaded", function() {
    // Finde den Button anhand seiner ID
    var button = document.getElementById("startButton");

    // Hänge die Funktion an das Klick-Ereignis des Buttons
    button.addEventListener("click", startGame);
});

function startGame(){
    var code = document.getElementById("code").innerHTML;
    var url = ip + "/qr?encryptedkey=XF98EG";
    window.location.href = url;
}

function start(){
    document.getElementById("startButton").addEventListener("click", function() {
        var code = document.getElementById("code").innerHTML;
        var url = ip + "/qr?encryptedkey=XF98EG";
        window.location.href = url;
    });
}