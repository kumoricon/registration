/**
 * Functions for at-con payments (take cash / etc)
 */


$(document).ready(
    function(){
        setState();
        addListeners();
    }
);


function setState() {
    var inputChange = $('#inputChange');
    var btnSave = $('#btnSave');
    var inputAmount = $('#inputAmount');

    var amount = parseFloat(inputAmount.val());
    var due = parseFloat($('#amountDue').val());

    inputChange.val("");

    if (isNaN(amount) || isNaN(due)) {
        inputChange.val("");
        btnSave.attr("disabled", true);
        return;
    }

    if (amount <= 0) {
        inputChange.val("");
        inputAmount.val("").select();
        btnSave.attr("disabled", true);
        alert("Payments must be a positive number");
        return;
    }

    var change = ((amount*100)-(due*100))/100;
    if (change < 0) { change = 0; }

    inputChange.val(change);

    // Make sure auth number is filled in if the field exists (cash and check payment types)
    var inputAuth = document.getElementById('inputCheckAuth');

    if (inputAuth == null) {
        inputAuth =  document.getElementById('inputAuthNumber');
    }
    if (inputAuth != null) {
        if (!inputAuth.value || inputAuth.value.length < 5) {
            btnSave.attr("disabled", true);
            return;
        } else {
            btnSave.attr("disabled", false);
        }
    }

    if (inputAuth != null) {
        if (amount > due) {
            alert("Do not give change for credit cards or checks\nAmount taken must be less than or equal to amount due");
            btnSave.attr("disabled", true);
            return;
        }
    }

    if (inputAuth.value.length > 10)
    {
        alert("Authorization number must be 10 or less characters.");
        btnSave.attr("disabled", true);
        return;
    }

    btnSave.attr("disabled", false);
}

function addListeners() {
    $('#inputAmount').keyup(setState);

    $('#inputCheckAuth').keyup(setState);
    $('#inputAuthNumber').keyup(setState);
}
