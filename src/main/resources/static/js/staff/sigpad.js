var extensionDataElement;
var msg;
var saveBtn;
var requireSignature;

$(document).ready(function() {
    extensionDataElement = document.createElement("MyExtensionDataElement");
    requireSignature = document.getElementById('REQUIRE_SIGNATURE').value === 'true';
    msg = document.getElementById("message");
    saveBtn = document.getElementById("save");
    initializeSigpad();
});



function initializeSigpad() {
    let isInstalled = document.documentElement.getAttribute('SigPlusExtLiteExtension-installed');
    if (!isInstalled) {
        msg.innerText = "SigPlusExtLite extension is either not installed or disabled. Please install or enable extension.";
        document.getElementById("openSigWindow").disabled = true;
        saveBtn.disabled = requireSignature;  // Allow continuing without signature since SigPlus Extension isn't installed

        // If the signature pad isn't detected as installed, add an observer to watch for
        // attribute changes (that could indicate that it's been installed and run this function again.
        // Sometimes the Chrome plugin hasn't finished initializing when this function is run for the
        // first time.
        let observer = new MutationObserver(function(mutations) {
            mutations.forEach(function(mutation) {
                if (mutation.type === "attributes") {
                    console.log("attributes changed");
                    initializeSigpad();
                }
            });
        });
        observer.observe(document.documentElement, {
            attributes: true //configure it to listen to attribute changes
        });
    } else {
        msg.innerText = 'Signature pad detected';
        if (requireSignature) {
            saveBtn.disabled = true;    // Disable Save button until signature is entered
        }
        document.getElementById("openSigWindow").addEventListener('click', openSignatureWindow);

        let message = {
            "firstName":"",
            "lastName": "",
            "eMail": "",
            "location": "",
            "imageFormat": 1,
            "imageX": 1000,
            "imageY":  400,   "imageTransparency":   false,   "imageScaling":   false,   "maxUpScalePercent":   0.0,
            "rawDataFormat": "ENC", "minSigPoints": 25 };
        top.document.addEventListener('SignResponse', SignResponse, false);
        let messageData = JSON.stringify(message);

        extensionDataElement.setAttribute("messageAttribute", messageData);
        document.documentElement.appendChild(extensionDataElement);
        openSignatureWindow();
    }
}

function openSignatureWindow() {
    let evt = document.createEvent("Events");
    evt.initEvent("SignStartEvent", true, false);
    extensionDataElement.dispatchEvent(evt);
}

function SignResponse(event) {
    let str = event.target.getAttribute("msgAttribute");
    let obj = JSON.parse(str);
    if (obj["isSigned"] === false) {
        msg.innerText = "No signature detected";
        saveBtn.disabled = true;
    } else {
        msg.innerText = "Signature Captured";
        document.getElementById("imageData").value = obj["imageData"];
        saveBtn.disabled = false;
    }
}
