$(document).ready(function(){
    $("#attendeeClear").on("click", function() {
        $("#attendeeSearch").val("").focus();
        return false;
    });

    $("#attendeeSearch")
        .autocomplete({
            serviceUrl: '/search/suggest',
            onSelect: function (suggestion) {
                $('#attendeeSearch').val(suggestion.value);
                $('#attendeeSearchForm').submit();
            }})
        .select();
});
