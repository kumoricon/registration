$(document).ready(
    function(){
        setState();
        addListeners();
    }
);

function setState() {
    const btnSave = $('#btnSave');
    const inputAmount = $('#inputAmount');

    const amount = parseFloat(inputAmount.val());
    const due = parseFloat($('#amountDue').val());

    if (isNaN(amount) || isNaN(due)) {
        btnSave.attr("disabled", true);
        return;
    }

    if (amount <= 0) {
        inputAmount.val("").select();
        btnSave.attr("disabled", true);
        alert("Payments must be a positive number");
        return;
    }

    // Make sure auth number is filled in if the field exists (cash and check payment types)
    const inputAuth = document.getElementById('inputAuthNumber');

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

    btnSave.attr("disabled", false);
}

function addListeners() {
    $('#inputAmount').keyup(setState);

    $('#inputAuthNumber').keyup(setState);
}
