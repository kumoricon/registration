$(document).ready(function(){
    let btnSave = $("#save");
    let checkbox = $("#servicePinCheckbox");
    btnSave.prop("disabled", !checkbox.is(":checked"));
    if (checkbox.length > 0) {
        checkbox.change(function() {
            btnSave.prop("disabled", !this.checked);
        });
    } else {
        btnSave.prop("disabled", false);
    }
});