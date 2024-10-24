$(document).ready(function(){
    $("#staffClear").on("click", function() {
       $("#staffSearch").val("").focus();
       return false;
    });

    $("#staffSearch")
        .autocomplete({
            serviceUrl: '/staff/suggest',
        onSelect: function (suggestion) {
            $('#staffSearch').val(suggestion.value);
            $('#staffSearchForm').submit();
        }})
        .select();

    $("#voucherStaffSearch")
        .autocomplete({
            serviceUrl: '/voucher/suggest',
        onSelect: function (suggestion) {
            $('#voucherStaffSearch').val(suggestion.value);
            $('#voucherStaffSearchForm').submit();
        }})
        .select();
});
