var ip = "http://localhost:8080"

function start(){
    document.getElementById("startButton").addEventListener("click", function() {
        var code = document.getElementById("code").innerHTML;
        var url = ip + "/qr?encryptedkey=XF98EG";
        window.location.href = url;
    });
}