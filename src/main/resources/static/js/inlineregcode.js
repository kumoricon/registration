// In-Line Registration Code search box functions

$(document).ready(
    function(){
        inLineRegSearchSetState()
        inLineRegAddListeners();
    }
);

function inLineRegAddListeners() {
    $('#inLineCodeSearch').change(inLineRegSearchSetState);
    $('#inLineCodeSearch').bind('keyup', inLineRegSearchSetState);
}