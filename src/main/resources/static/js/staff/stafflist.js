$(document).ready(function(){
    $("#search").on("keyup", function() {
        let value = $(this).val().toLowerCase();
        filter(value);
    });

    $("#clear").on("click", function() {
       $("#search").val("").focus();
       filter("");
    });
});

function filter(value) {
    $("#staffList tbody tr").filter(function() {
        let name = $(this).children().get(0).innerText.toLowerCase();
        let department = $(this).children().get(1).innerText.toLowerCase();
        let visible = name.indexOf(value) > -1 || department.indexOf(value) > -1;
        $(this).toggle(visible)
    });

}