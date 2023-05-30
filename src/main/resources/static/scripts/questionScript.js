function getHint(){
    url = "/hint?number=".concat(extractDigitsAndParseInt(document.getElementById("question_number").innerHTML))
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

function sendAnswer(){
    url = "/check?number=".concat(extractDigitsAndParseInt(document.getElementById("question_number").innerHTML))
        .concat("&answer=").concat(document.getElementById("antwort").value)
        .concat("&code=").concat(document.getElementById("code").value)
    console.log(url)
    fetch(url)
        .then(response => response.text())
        .then(data => {
            console.log(data);
            document.getElementById("answer_correct").innerHTML = data;
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

