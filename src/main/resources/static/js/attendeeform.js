/**
 * Functions to make the at-con attendee form look nicer/work better. Used by both the regular and speciality form
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
    $('#inputBadgeType').change(onBadgeTypeChange);
    $('#inputPhone').bind('keyup', onPhoneNumberUpdate);
    $('#inputEmergencyContactPhone').bind('keyup', onPhoneNumberUpdate);
    $('#inputParentPhone').bind('keyup', onPhoneNumberUpdate);
    $('#inputLastName').bind('keyup', updateLastName);
    $('#inputLegalLastName').bind('keyup', updateLastName);
}


function onPhoneNumberUpdate(eventObject) {
    let phoneNumber = eventObject.target.value;
    phoneNumber = phoneNumber.replace(/\D/g, '');
    let length = phoneNumber.length;
    if (length > 3 && length < 7) { phoneNumber = phoneNumber.slice(0,3) + "-" + phoneNumber.slice(3,6); }
    if (length > 6) { phoneNumber = phoneNumber.slice(0,3) + "-" + phoneNumber.slice(3,6) + "-" + phoneNumber.slice(6,15); }
    eventObject.target.value = phoneNumber;
}

function onBirthdateUpdate(eventObject) {
    let dateString = eventObject.target.value;
    updateAge(parseDate(dateString));
    clearPaidAmountIfControlExists();
}

function onBadgeTypeChange() {
    clearPaidAmountIfControlExists();
}

function clearPaidAmountIfControlExists() {
    let inputPaidAmount = $('#inputPaidAmount');
    if( inputPaidAmount.length ) {   // inputPaidAmount only exists on the speciality form
        inputPaidAmount.val(null);   // Clear paid amount, it should be recalculated based on age and badge type
    }
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
    if (inputDate === null) {
        showHideAgeFields(true);
        $('#age').text("");
        return;
    }
    //date must be yyyy-mm-dd
    try {
        let age = calculateAge(inputDate);
        let yearString = age > 1 ? " years old" : " year old";
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
    let btnSave = $("#save");
    if ($('#inputPaidAmount').length) {     // On speciality form, because inputPaidAmount only exists on that page
        if (readyToSaveSpeciality()) {
            btnSave.attr("disabled", false);
        } else {
            btnSave.attr("disabled", true);
        }
    } else {
        if (shouldRequirePhone()) {
            $('#inputPhoneLabel').text('Phone Number*');
            $('#inputEmailLabel').text('or Email*');
            $('#inputPhone').attr('disabled', false);
            $('#inputEmail').attr('disabled', false);
        } else {
            $('#inputPhoneLabel').text('Phone Number');
            $('#inputEmailLabel').text('or Email');
            $('#inputPhone').attr('disabled', true);
            $('#inputEmail').attr('disabled', true);
        }
        if (readyToSave()) {
            btnSave.attr("disabled", false);
        } else {
            btnSave.attr("disabled", true);
        }
    }
}

function shouldRequirePhone() {
    let birthDate = $('#inputBirthDate').val();
    if (birthDate == null || birthDate.trim() === "") return true;
    let age = calculateAge(parseDate(birthDate));
    return age >= 13;
}

function capitalizeLastName(name) {
  return name.replace(/(\w{1})(\w*)(\-)?(\w{1})?(\w*)?/g, (m, c1, c2, c3, c4, c5) => {
    let string = `${c1.toUpperCase()}${/^mc/i.test(c1+c2) ? c2[0]+c2[1].toUpperCase()+c2.substring(2,) : c2}`;
    string += c3 === '-' ? `-${c4 ? c4.toUpperCase() : ''}${c5 ? c5 : ''}` : '';
    return string;
  });
}

function updateLastName(event) {
    event.target.value = capitalizeLastName(event.target.value);
}

function readyToSave() {
    return $("#inputFirstName").val() !== "" &&
        $("#inputLastName").val() !== "" &&
        $("#inputZip").val() !== "" &&
        // Only require phone number for youth and adults
        (($("#inputPhone").val() !== "" || $("#inputEmail").val() !== "") || !shouldRequirePhone()) &&
        $("#inputEmergencyContactName").val() !== "" &&
        $("#inputEmergencyContactPhone").val() !== "" &&
        $("#inputBirthdate").val() !== ""
}

function readyToSaveSpeciality() {
    // Vastly reduced field enforcement -- we assume that Speciality coords have a reason for doing anything
    // For example, they can create badges that don't have first or last name at all, or a birthdate
    // It is required that you have either a firstname and lastname OR a fan name, though.
    return ($("#inputFirstName").val() !== "" && $("#inputLastName").val() !== "") ||
        $("#inputFanName").val() !== "";
}

