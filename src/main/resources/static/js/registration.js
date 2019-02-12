$(document).ready(function () {

    console.log("Hello registration.js");

    $('#add_user').click(function () {

        var pas = $('#InputPassword').val().replace(/\s/g, '');
        var pas1 = $('#InputPassword2').val().replace(/\s/g, '');
        var name = $('#InputName').val().replace(/\s/g, '');
        var email = $('#InputEmail').val().replace(/\s/g, '');
        if (pas != pas1) {
            alert("Invalid password");
            return false;
        }
        if (pas == "" || name == "" || email == "") {
            alert("All fields must be filled!");
            return false;
        }

        $.ajax("/api/guest/registration", {
            type: "POST",
            dataType: 'json',
            contentType: 'application/json',
            data: JSON.stringify({
                name: name,
                email: email,
                password: pas
            }),
            success: function (data) {
                window.location = "../message.html";
            },
            error: function (jqXHR) {
                alert(jqXHR.responseJSON.status +" - "+jqXHR.responseJSON.message);
            },
        })
    });

});