$(document).ready(function() {
    // Grab elements, create settings, etc.
    let canvas = document.getElementById('canvas');
    let context = canvas.getContext('2d');
    let errBack = function(e) {
        console.log('An error has occurred!', e)
    };


    // Trigger saving signature
    document.getElementById('submitForm').addEventListener('submit', function() {
        console.log("Submitting mah form");
        let data = canvas.toDataURL();
        document.getElementById('imageData').value = data;
    });
});
