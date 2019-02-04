$(document).ready(function () {

    console.log("Hello activation.js");

    $.ajax('/activation-code/'+window.location.toString().split("code=")[1], {
        type: 'PUT',
        dataType: 'json',
        contentType: 'application/json',
        success: function () {
            $('#message').html("You have successfully completed the registration and you can log into the game using your name and password.");
        },
        error: function (jqXHR) {
            $('#message').html("Sorry something went wrong. Repeat registration will help resolve this error.");
            alert(jqXHR.responseJSON.status +" - "+jqXHR.responseJSON.message);
        }
    })
});