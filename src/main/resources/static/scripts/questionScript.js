var ip = "http://localhost:8080"

function getHint(){
    url = ip.concat("/hint?number=".concat(extractDigitsAndParseInt(document.getElementById("question_number").innerHTML)))
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



function extractDigitsAndParseInt(string) {
    var digits = string.match(/\d+/g);
    var digitString = digits.join('');
    var intValue = parseInt(digitString, 10);
    return intValue;
}

