var ip = "http://localhost:8080"

document.addEventListener("DOMContentLoaded", function() {
    var button = document.getElementById("startButton");
    button.addEventListener("click", startGame);
});

function startGame(){
    var code = document.getElementById("code").innerHTML;
    var url = ip + "/qr?encryptedkey=XF98EG";
    window.location.href = url;
}