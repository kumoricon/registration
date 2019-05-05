/**
 * Functions to make the at-con attendee form look nicer/work better
 */


$(document).ready(
    function(){
        console.log("doc ready");
        setState();
        addListeners();
        updateAge($('#inputBirthDate').val());
        showHideLegalName($('#inputNameIsLegalName').is(':checked'));
    }
);


function addListeners() {
    $("input").blur(setState);
    $("#inputBirthDate").blur(onBirthdateUpdate);
    $('#inputNameIsLegalName').change(onNameIsLegalNameUpdate);
}

function onBirthdateUpdate(eventObject) {
    var inputDateString = eventObject.target.value;
    updateAge(inputDateString);
}

function onNameIsLegalNameUpdate(eventObject) {
    showHideLegalName(eventObject.target.checked);
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
    if (inputDate === "") {
        showHideAgeFields(false);
        return;
    }
    //date must be yyyy-mm-dd
    try {
        var dob = new Date(inputDate.substring(0,4), inputDate.substring(5,7)-1, inputDate.substring(8,10));
        var age = calculateAge(dob);
        var yearString = age > 1 ? " years old" : " year old";
        $("#age").text('(' + age + yearString + ')');
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


/**
 * @param {Date} dob Date of birth
 * @returns {number} Age in years
 */
function calculateAge(dob) {
    var now = new Date();

    var yearNow = now.getFullYear();
    var monthNow = now.getMonth();
    var dateNow = now.getDate();


    var yearDob = dob.getFullYear();
    var monthDob = dob.getMonth();
    var dateDob = dob.getDate();

    var yearAge = yearNow - yearDob;

    if (monthNow >= monthDob)
        var monthAge = monthNow - monthDob;
    else {
        yearAge--;
        var monthAge = 12 + monthNow -monthDob;
    }

    if (dateNow >= dateDob)
        var dateAge = dateNow - dateDob;
    else {
        monthAge--;
        var dateAge = 31 + dateNow - dateDob;

        if (monthAge < 0) {
            monthAge = 11;
            yearAge--;
        }
    }

    return yearAge;
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