$(document).ready(
    function(){
        setState();
        addListeners();
    }
);

function setState() 
{
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

    btnSave.attr("disabled", false);
}

function addListeners()
{
    $('#inputAmount').keyup(setState);
}
