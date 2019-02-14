$(document).ready(function () {

    console.log("Hello activation.js");

    $.ajax('/activation-code/' + window.location.toString().split("code=")[1], {
        type: 'PUT',
        dataType: 'json',
        contentType: 'application/json',
        success: function () {
            $('#myModalSuccess').modal('show');
            vewModel.isVisibleRegistration(false);
            vewModel.isVisibleLogin(true);

        },
        error: function () {
            $('#myModalError').modal('show');
            vewModel.isVisibleRegistration(true);
            vewModel.isVisibleLogin(false);
}
    });

    var vewModel = {
        isVisibleRegistration: ko.observable(false),
        isVisibleLogin: ko.observable(false),
    };

    ko.applyBindings(vewModel);
});