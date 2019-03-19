/**
 * Functions for at-con payments (take cash / etc)
 */


$(document).ready(
    function(){
        console.log("doc ready");
        setState();
        addListeners();
    }
);


function setState() {
    var amount = parseFloat($('#inputAmount').val());
    var due = parseFloat($('#amountDue').val());

    $('#inputChange').val("");

    if (isNaN(amount) || isNaN(due)) {
        $('#inputChange').val("");
        $('#inputSave').attr('disabled', true)
        return;
    }

    if (amount <= 0) {
        alert("Payments must be a positive number");
        $('#inputChange').val("");
        $('#inputAmount').val("").select();
        return
    }

    var change = ((amount*100)-(due*100))/100;

    if (change < 0) { change = 0; }

    $('#inputChange').val(change);
    $('#inputSave').attr('disabled', false)
    console.log(change);
}

function addListeners() {
    $('#inputAmount').blur(setState);
}