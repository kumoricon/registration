$(document).ready(function(){
    $("#clear").on("click", function() {
        $("#search").val("").focus();
        return false;
    });

    $("#search")
        .autocomplete({
            serviceUrl: '/search/suggest',
            onSelect: function (suggestion) {
                $('#search').val(suggestion.value);
                $('#searchForm').submit();
            }})
        .select();
});
