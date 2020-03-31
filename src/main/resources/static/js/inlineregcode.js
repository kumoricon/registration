// In-Line Registration Code search box functions

$(document).ready(
    function(){
        inLineRegSearchSetState()
        inLineRegAddListeners();
    }
);

function inLineRegSearchSetState() {
    let btnCheckIn = $("#inLineCodeCheckIn");
    let txtCode = $("#inLineCodeSearch")

    if (txtCode.val().trim().length >= 6) {
        btnCheckIn.attr('disabled', false);
    } else {
        btnCheckIn.attr('disabled', true);
    }
}

function inLineRegAddListeners() {
    $('#inLineCodeSearch').change(inLineRegSearchSetState);
    $('#inLineCodeSearch').bind('keyup', inLineRegSearchSetState);
}