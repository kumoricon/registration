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
    const inputSquareReceiptNumber = document.getElementById('inputSquareReceiptNumber');

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

    // square receipt number validity check
    if (inputSquareReceiptNumber != null) {
        if (!inputSquareReceiptNumber.value || inputSquareReceiptNumber.value.length < 4) {
            btnSave.attr("disabled", true);
            return;
        }
    }


    btnSave.attr("disabled", false);
}

function addListeners() {
    $('#inputAmount').keyup(setState);

    $('#inputSquareReceiptNumber').keyup(setState);
}
