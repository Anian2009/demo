$(document).ready(function () {

    console.log("Hello changePassword.js");

    var code = window.location.toString().split("code=")[1];

    var vewModel = {
        isVisible: ko.observable(false),
        isVisibleMes: ko.observable(false)
    };

    ko.applyBindings(vewModel);

    $.ajax('/changePasswordCode/'+code, {
        type: 'PUT',
        dataType: 'json',
        contentType: 'application/json',
        success: function () {
            vewModel.isVisible(true);
        },
        error: function (jqXHR) {
            alert(jqXHR.responseJSON.status +" - "+jqXHR.responseJSON.message);
            window.location = '/';
        }
    });

    $('#changePassword').click(function () {
        validate();
        $.ajax('api/guest/changePassword', {
            type: 'POST',
            dataType: 'json',
            contentType: 'application/json',
            data: JSON.stringify({
                password: $('#InputPassword').val(),
                code: code
            }),
            success: function () {
                vewModel.isVisibleMes(true);
                setTimeout(function () {
                    window.location = 'login.html';
                }, 2000)
            },
            error: function (jqXHR) {
                alert(jqXHR.responseJSON.status +" - "+jqXHR.responseJSON.message);
            }
        });
    });

    validate = function () {
        var pas1 = $('#InputPassword').val().replace(/\s/g, '');
        var pas2 = $('#InputPassword2').val().replace(/\s/g, '');

        if (pas1 === '' || pas1 != pas2){
            alert("Invalid password");
            return false;
        }


    }
});