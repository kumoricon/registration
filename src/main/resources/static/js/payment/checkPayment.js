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
    const inputCheckAuth = document.getElementById('inputCheckAuth');

    if (isNaN(amount) || isNaN(due)) {
        btnSave.attr("disabled", true);
        return;
    }

    // amount paid validity check
    if (amount <= 0) {
        inputAmount.val("").select();
        btnSave.attr("disabled", true);
        alert("Payments must be a positive number");
        return;
    }

    if (amount > due) {
        alert("Do not give change for credit cards or checks\nAmount taken must be less than or equal to amount due");
        btnSave.attr("disabled", true);
        return;
    }

    // check number validity check
    if (inputCheckAuth != null) {
        if (!inputCheckAuth.value || inputCheckAuth.value.length < 1) {
            btnSave.attr("disabled", true);
            return;
        }
    }

    btnSave.attr("disabled", false);
}

function addListeners() {
    $('#inputAmount').keyup(setState);

    $('#inputCheckAuth').keyup(setState);
}
