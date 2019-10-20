var extensionDataElement;
var msg;
var saveBtn;

const REQUIRE_SIGNATURE = true;    // If true, AND the SigPlus Lite Extension is detected, a signature will
                                   // be required before the save button is enabled.

$(document).ready(function() {
    extensionDataElement = document.createElement("MyExtensionDataElement");
    msg = document.getElementById("message");
    saveBtn = document.getElementById("save");
    initializeSigpad();
});

function initializeSigpad() {
    let isInstalled = document.documentElement.getAttribute('SigPlusExtLiteExtension-installed');
    if (!isInstalled) {
        msg.innerText = "SigPlusExtLite extension is either not installed or disabled. Please install or enable extension.";
        document.getElementById("openSigWindow").disabled = true;
        saveBtn.disabled = false;   // Allow continuing without signature since SigPlus Extension isn't installed
        return
    } else {
        if (REQUIRE_SIGNATURE) {
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
