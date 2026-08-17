// Blendet beim Klick auf "Name hinzufügen" das jeweils nächste Namensfeld ein
// (bis maximal 5) und aktiviert es erst dabei — vorher ist es "disabled" und
// wird darum nicht mitgeschickt. Die eigentliche 1-5-Grenze prüft der Server
// (ParticipantService.cleanMemberNames); das hier ist nur Komfort.
document.addEventListener("DOMContentLoaded", function () {
    var addButton = document.getElementById("addMemberButton");
    if (!addButton) {
        return;
    }

    var nextIndex = 2;
    var maxMembers = 5;

    addButton.addEventListener("click", function () {
        if (nextIndex > maxMembers) {
            return;
        }

        var field = document.getElementById("memberField" + nextIndex);
        var input = document.getElementById("member" + nextIndex);
        field.classList.remove("member-field-hidden");
        input.disabled = false;

        nextIndex++;
        if (nextIndex > maxMembers) {
            addButton.disabled = true;
            addButton.textContent = "Maximal 5 Namen";
        }
    });
});
