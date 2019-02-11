$(document).ready(function () {
    console.log("Hello login.js");

    $('#send').click(function () {
        $.ajax("/api/guest/forgotPassword", {
            type: 'POST',
            dataType: 'json',
            contentType: 'application/json',
            data: JSON.stringify({
                email: $('#inputEmail').val()
            }),
            success: function () {
                vewModel.isVisible(true);
                $('#inputEmail').val(null);
                },
            error: function (jqXHR) {
                alert(jqXHR.responseJSON.status +" - "+jqXHR.responseJSON.message);
            },
        })
    });

    var vewModel = {
        isVisible: ko.observable(false)
    };

    ko.applyBindings(vewModel);
});