/**
 * Functions to make the at-con attendee form look nicer/work better
 */


$(document).ready(
    function(){
        setState();
        addListeners();
        updateAge(parseDate($('#inputBirthDate').val()));
        showHideLegalName($('#inputNameIsLegalName').is(':checked'));
    }
);


function addListeners() {
    $("input").blur(setState);
    $("#inputBirthDate").blur(onBirthdateUpdate);
    $('#inputNameIsLegalName').change(onNameIsLegalNameUpdate);
    $('#inputParentIsEmergencyContact').change(onParentIsEmergencyContactUpdate);
    $('#inputPhone').bind('keyup',onPhoneNumberUpdate);
    $('#inputEmergencyContactPhone').bind('keyup',onEmergencyPhoneNumberUpdate);
    $('#inputParentPhone').bind('keyup',onParentPhoneNumberUpdate);
}

function onPhoneNumberUpdate(eventObject) {
    var inputPhoneNumber = eventObject.target.value;
    inputPhoneNumber = inputPhoneNumber.replace(/\D/g, '');
    var length = inputPhoneNumber.length;
    if (length > 3 && length < 7) { inputPhoneNumber = inputPhoneNumber.slice(0,3) + "-" + inputPhoneNumber.slice(3,6); }
    if (length > 6) { inputPhoneNumber = inputPhoneNumber.slice(0,3) + "-" + inputPhoneNumber.slice(3,6) + "-" + inputPhoneNumber.slice(6,15); }
    $('#inputPhone').val(inputPhoneNumber);
}

function onEmergencyPhoneNumberUpdate(eventObject) {
    var inputPhoneNumber = eventObject.target.value;
    inputPhoneNumber = inputPhoneNumber.replace(/\D/g, '');
    var length = inputPhoneNumber.length;
    if (length > 3 && length < 7) { inputPhoneNumber = inputPhoneNumber.slice(0,3) + "-" + inputPhoneNumber.slice(3,6); }
    if (length > 6) { inputPhoneNumber = inputPhoneNumber.slice(0,3) + "-" + inputPhoneNumber.slice(3,6) + "-" + inputPhoneNumber.slice(6,15); }
    $('#inputEmergencyContactPhone').val(inputPhoneNumber);
}

function onParentPhoneNumberUpdate(eventObject) {
    var inputPhoneNumber = eventObject.target.value;
    inputPhoneNumber = inputPhoneNumber.replace(/\D/g, '');
    var length = inputPhoneNumber.length;
    if (length > 3 && length < 7) { inputPhoneNumber = inputPhoneNumber.slice(0,3) + "-" + inputPhoneNumber.slice(3,6); }
    if (length > 6) { inputPhoneNumber = inputPhoneNumber.slice(0,3) + "-" + inputPhoneNumber.slice(3,6) + "-" + inputPhoneNumber.slice(6,15); }
    $('#inputParentPhone').val(inputPhoneNumber);
}

function onBirthdateUpdate(eventObject) {
    var inputDateString = eventObject.target.value;
    updateAge(parseDate(inputDateString));
}

function onNameIsLegalNameUpdate(eventObject) {
    showHideLegalName(eventObject.target.checked);
}

function onParentIsEmergencyContactUpdate(eventObject) {
    if (eventObject.target.checked) {
        $('#inputParentFullName').val($('#inputEmergencyContactName').val());
        $('#inputParentPhone').val($('#inputEmergencyContactPhone').val());
    } else {
        $('#inputParentFullName').val('');
        $('#inputParentPhone').val('');
    }
}

function showHideLegalName(show) {
    if (show === false) {
        $('#inputNameIsLegalName').hide();
        $("label[for='inputNameIsLegalName']").hide();
        $("label[for='inputLegalFirstName']").show();
        $("label[for='inputLegalLastName']").show();
        $('#inputLegalFirstName').show();
        $('#inputLegalLastName').show();
    } else {
        $('#inputNameIsLegalName').show();
        $("label[for='inputNameIsLegalName']").show();
        $("label[for='inputLegalFirstName']").hide();
        $("label[for='inputLegalLastName']").hide();
        $('#inputLegalFirstName').hide();
        $('#inputLegalLastName').hide();
    }
}

function updateAge(inputDate) {
    if (inputDate == null) {
        showHideAgeFields(true);
        $('#age').text("");
        $('#inputBirthDate').val("").focus();
        return;
    }

    try {
        var age = calculateAge(inputDate);
        var yearString = age > 1 ? " years old" : " year old";
        $("#age").text('(' + age + yearString + ')');
        $("#inputBirthDate").val(inputDate.getMonth()+1 + '/' + inputDate.getDate() + '/' + inputDate.getFullYear());
        if (age >= 18) {
            showHideAgeFields(false);
        } else {
            showHideAgeFields(true);
        }
    } catch (e) {
        $("#age").text("");
        showHideAgeFields(true);
    }
}

function showHideAgeFields(isMinor) {
    if (isMinor) {
        $('#inputParentFormReceived').attr('disabled', false).attr('required', true);
        $('#inputParentFullName').attr('disabled', false).attr('required', true);
        $('#inputParentPhone').attr('disabled', false).attr('required', true);
        $('#inputParentIsEmergencyContact').attr('disabled', false).attr('required', false);
    } else {
        $('#inputParentFormReceived').attr('disabled', true).attr('required', false);
        $('#inputParentFullName').attr('disabled', true).attr('required', false);
        $('#inputParentPhone').attr('disabled', true).attr('required', false);
        $('#inputParentIsEmergencyContact').attr('disabled', true).attr('required', false);
    }

}


function setState() {
    if (readyToSave()) {
        $("#save").attr("disabled", false);
    } else {
        $("#save").attr("disabled", true);
    }
}

function readyToSave() {
    return $("#inputFirstName").val() !== "" &&
        $("#inputLastName").val() !== "" &&
        $("#inputZip").val() !== "" &&
        ($("#inputPhone").val() !== "" || $("#inputEmail").val() !== "") &&
        $("#inputEmergencyContactName").val() !== "" &&
        $("#inputEmergencyContactPhone").val() !== "" &&
        $("#inputBirthdate").val() !== ""
}