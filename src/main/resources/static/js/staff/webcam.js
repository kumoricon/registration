$(document).ready(function() {
    // Grab elements, create settings, etc.
    let canvas = document.getElementById('canvas');
    let context = canvas.getContext('2d');
    let video = document.getElementById('video');
    let mediaConfig =  { video: true };
    let errBack = function(e) {
        console.log('An error has occurred!', e)
    };

    // Put video listeners into place
    if(navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
        navigator.mediaDevices.getUserMedia(mediaConfig).then(function(stream) {
            //video.src = window.URL.createObjectURL(stream);
            video.srcObject = stream;
            video.play();
        });
    }

    /* Legacy code below! */
    else if(navigator.getUserMedia) { // Standard
        navigator.getUserMedia(mediaConfig, function(stream) {
            video.src = stream;
            video.play();
        }, errBack);
    } else if(navigator.webkitGetUserMedia) { // WebKit-prefixed
        navigator.webkitGetUserMedia(mediaConfig, function(stream){
            video.src = window.webkitURL.createObjectURL(stream);
            video.play();
        }, errBack);
    } else if(navigator.mozGetUserMedia) { // Mozilla-prefixed
        navigator.mozGetUserMedia(mediaConfig, function(stream){
            video.src = window.URL.createObjectURL(stream);
            video.play();
        }, errBack);
    } else {
        // Camera not detected/working
        document.getElementById('imageData').value = data;
        document.getElementById('save').disabled = false;
        document.getElementById('snap').innerText = "Webcam not detected";
    }

    // Trigger taking photo
    document.getElementById('snap').addEventListener('click', function() {
        context.drawImage(video, 0, 0, 800, 600);
        let data = canvas.toDataURL('image/jpeg', 0.9);
        document.getElementById('imageData').value = data;
        document.getElementById('save').disabled = false;
    });
});
