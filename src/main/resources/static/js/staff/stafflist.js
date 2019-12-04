$(document).ready(function(){
    $("#clear").on("click", function() {
       $("#search").val("").focus();
       return false;
    });

    $("#search")
        .autocomplete({
            serviceUrl: '/staff/suggest',
        onSelect: function (suggestion) {
            $('#search').val(suggestion.value);
            $('#searchForm').submit();
        }})
        .select();
});
