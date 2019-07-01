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
    $('#inputBadgeType').change(onBadgeTypeChange);
}

function onBirthdateUpdate(eventObject) {
    var inputDateString = eventObject.target.value;
    updateAge(parseDate(inputDateString));
    $('#inputPaidAmount').val(null);   // Clear paid amount, it should be recalculated based on age and badge type
}

function onNameIsLegalNameUpdate(eventObject) {
    showHideLegalName(eventObject.target.checked);
}

function onBadgeTypeChange() {
    $('#inputPaidAmount').val(null)
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
    if (inputDate === null) {
        showHideAgeFields(false);
        $('#age').text("");
        $('#inputBirthDate').val("").focus();
        return;
    }
    //date must be yyyy-mm-dd
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
    // Vastly reduced field enforcement -- we assume that Speciality coords have a reason for doing anything
    // For example, they can create badges that don't have first or last name at all, or a birthdate
    // It is required that you have either a firstname and lastname OR a fan name, though.
    return ($("#inputFirstName").val() !== "" && $("#inputLastName").val() !== "") ||
        $("#inputFanName").val() !== "";
}