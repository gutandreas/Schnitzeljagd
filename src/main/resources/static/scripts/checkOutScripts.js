function sendCheckout(){
    url = "/finish?code=".concat(document.getElementById("code").value)
    console.log(url)
    fetch(url)
        .then(response => response.text())
        .then(data => {
            console.log(data);
            document.getElementById("answer").innerHTML = data;
        })
        .catch(error => {
            console.log(error);
        });
}