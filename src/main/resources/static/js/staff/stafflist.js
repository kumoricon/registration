$(document).ready(function(){
    $("#clear").on("click", function() {
       $("#search").val("").focus();
       return false;
    });
    $("#search").select();
});
