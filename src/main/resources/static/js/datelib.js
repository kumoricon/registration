/**
 * Returns a date parsed from letious string formats, including:
 * YYYY-MM-DD
 * MMDDYYYY
 * MMDDYY
 * MM-DD-YYYY and MM/DD/YYYY and MM\DD\YYYY
 * MM-DD-YY and MM/DD/YY and MM\DD\YY
 * @param dateString
 * @returns {Date}
 */
function parseDate(dateString) {
    let testString = (dateString.trim());
    let MMDDYYYY = /^(\d{2})(\d{2})(\d{4})$/;
    let MMdashDDdashYYYY = /^(\d{1,2})[-/\\](\d{1,2})[-/\\](\d{4})$/;
    let YYYYdashMMdashDD = /^(\d{4})[-/\\](\d{1,2})[-/\\](\d{1,2})$/;

    let MMDDYY = /^(\d{2})(\d{2})(\d{2})$/;
    let MMdashDDdashYY = /^(\d{1,2})[-/\\](\d{1,2})[-/\\](\d{2})$/;

    let re = MMDDYYYY.exec(testString);
    if (re) {
        return new Date(parseInt(re[3]), parseInt(re[1])-1, parseInt(re[2]));
    }

    re = MMdashDDdashYYYY.exec(testString);
    if (re) {
        return new Date(parseInt(re[3]), parseInt(re[1])-1, parseInt(re[2]));
    }

    re = YYYYdashMMdashDD.exec(testString);
    if (re) {
        return new Date(parseInt(re[1]), parseInt(re[2])-1, parseInt(re[3]));
    }

    re = MMDDYY.exec(testString);
    if (re) {
        let year = toFullYear(parseInt(re[3]));
        return new Date(year, parseInt(re[1])-1, parseInt(re[2]));
    }

    re = MMdashDDdashYY.exec(testString);
    if (re) {
        let year = toFullYear(parseInt(re[3]));
        return new Date(year, re[1]-1, parseInt(re[2]));
    }

    return null;
}


/**
 * Returns full year (19xx or 20xx) given a partial year. Eg, 19 -> 2019, 20 -> 1920, 83 -> 1983
 * @param  year integer 0-99
 * @returns {Number} 4 digit year
 */
function toFullYear(year) {
    if (year <= new Date().getFullYear()-2000) {
        return year + 2000;
    } else {
        return year + 1900;
    }
}


/**
 * @param {Date} dob Date of birth
 * @returns {number} Age in years
 */
function calculateAge(dob) {
    let now = new Date();

    let yearNow = now.getFullYear();
    let monthNow = now.getMonth();
    let dateNow = now.getDate();


    let yearDob = dob.getFullYear();
    let monthDob = dob.getMonth();
    let dateDob = dob.getDate();

    let yearAge = yearNow - yearDob;

    let monthAge;
    let dateAge;
    if (monthNow >= monthDob)
        monthAge = monthNow - monthDob;
    else {
        yearAge--;
        monthAge = 12 + monthNow - monthDob;
    }

    if (dateNow >= dateDob)
        dateAge = dateNow - dateDob;
    else {
        monthAge--;
        dateAge = 31 + dateNow - dateDob;

        if (monthAge < 0) {
            monthAge = 11;
            yearAge--;
        }
    }

    return yearAge;
}


/* Tests:
var dates = [
    {"str": "1979-03-08", "expected": new Date(1979, 2, 8)},
    {"str": "1979-3-8", "expected": new Date(1979, 2, 8)},
    {"str": "03-08-1979", "expected": new Date(1979, 2, 8)},
    {"str": "03/08/1979", "expected": new Date(1979, 2, 8)},
    {"str": "03081979", "expected": new Date(1979, 2, 8)},
    {"str": "3/8/1979", "expected": new Date(1979, 2, 8)},
    {"str": "3/8/79", "expected": new Date(1979, 2, 8)},
    {"str": "3-8-79", "expected": new Date(1979, 2, 8)},
    {"str": "02282012", "expected": new Date(2012, 1, 28)},
    {"str": "02/28/2012", "expected": new Date(2012, 1, 28)},
    {"str": "2012-02-05", "expected": new Date(2012, 1, 5)},
    {"str": "030879", "expected": new Date(1979, 2, 8)},
    {"str": "022812", "expected": new Date(2012, 1, 28)},
    {"str": "2/28/12", "expected": new Date(2012, 1, 28)}
];

for (let d in dates) {
    let test = dates[d];
    let result = parseDate(test['str']);
    if (result.toString() === test['expected'].toString()) {
        console.log("Success! " + test['str'] + " returned " + result);
    } else {
        console.log("Failure  " + test['str'] + " returned " + result + " Expected " + test['expected']);
    }
}
*/
