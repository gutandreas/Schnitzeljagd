var ip = "localhost:8080"

function getHint(){
    fetch(ip+"/hint/" + parseInt(document.getElementById("question_number").value))
        .then(
        response => {
            console.log(response)
            document.getElementById("hint_label").value = response
        })
}